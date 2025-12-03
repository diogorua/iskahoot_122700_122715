package jogo;

import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import coordenacao.CyclicBarrier;
import coordenacao.ModifiedCountdownLatch;
import perguntas.Pergunta;
import servidor.ClientHandler;

/**
 * Classe central que gere o estado de uma sala de jogo.
 */

public class GameState {

    private final int idSala;
    private final int numEquipasEsperadas;
    private final int jogadoresPorEquipa;
    private Map<String, Equipa> equipas;
    private Map<String, Jogador> jogadores;
    private List<Pergunta> listaDePerguntas;
    private int rondaAtual;
    private List<ClientHandler> listeners;
    
    private ModifiedCountdownLatch modifiedLatch;
    private Map<String, CyclicBarrier> barreiraEquipa;
    private ModifiedCountdownLatch fimDaRondaEquipaLatch;
    private CaixaDeRespostas caixaDeRespostas;
    
    private long tempoInicioRonda;
    
    
    public GameState(int idSala, int numEquipas, int numJogadores) {
        this.idSala = idSala;
        this.numEquipasEsperadas = numEquipas;
        this.jogadoresPorEquipa = numJogadores;
        this.equipas = new HashMap<>();
        this.jogadores = new HashMap<>();
        this.barreiraEquipa = new HashMap<>();
        this.listeners = new ArrayList<>();
        this.rondaAtual = 0;
        this.caixaDeRespostas = new CaixaDeRespostas();
    }

   
    public void carregarPerguntas(List<Pergunta> perguntas) {
        this.listaDePerguntas = perguntas;
    }
    
    
    // Seccao critica: metodo sincronizado para garantir exclusao mutua 
    public synchronized boolean adicionarJogador(String idEquipa, String username, ClientHandler handler) {
        if (jogadores.containsKey(username)) {
            System.out.println("Erro (Sala " + idSala + "): Username " + username + " já existe.");
            return false;
        }
        
        int totalJogadoresEsperados = numEquipasEsperadas * jogadoresPorEquipa;
        
        if (jogadores.size() >= totalJogadoresEsperados) {
            System.out.println("Erro (Sala " + idSala + "): Sala está cheia.");
            return false;
        }
        
        Equipa equipa = equipas.get(idEquipa);
        
        if (equipa == null) {
            if (equipas.size() >= numEquipasEsperadas) {
                System.out.println("Erro (Sala " + idSala + "): Número máximo de equipas atingido.");
                return false;
            }
            equipa = new Equipa(idEquipa);
            equipas.put(idEquipa, equipa);
        }
        
        if (equipa.getNumeroMembros() >= jogadoresPorEquipa) {
            System.out.println("Erro (Sala " + idSala + "): Equipa " + idEquipa + " está cheia.");
            return false;
        }
        
        Jogador novoJogador = new Jogador(username, idEquipa);
        jogadores.put(username, novoJogador);
        equipa.adicionarJogadores(novoJogador);
        listeners.add(handler);
        
        System.out.println("Sucesso (Sala " + idSala + "): Jogador " + username + " entrou na equipa " + idEquipa);
        
        if (jogadores.size() == totalJogadoresEsperados) {
            System.out.println("SALA " + idSala + " COMPLETA. O jogo vai começar!");
            
            // o ultimo jogador ao entrar nesta sala, representado pela thread DealWithCliente, vai criar outra thread que vai correr em paralelo (ciclo do jogo)
            new Thread(this::iniciarCicloDeJogo).start();
            
            // a thread do ultimo jogador acorda todas as outras que estavam a espera que o jogo comecasse
            synchronized (this) {
                this.notifyAll();
            }
        }
        
        return true;
    }
    
    
    
    public void iniciarCicloDeJogo() {
        System.out.println("[GameState " + idSala + "] Início do ciclo de jogo.");

        for (Pergunta pergunta : listaDePerguntas) {
            rondaAtual++;
            System.out.println("\n--- [GameState " + idSala + "] Ronda " + rondaAtual + " ---");
            System.out.println("Pergunta: " + pergunta.getQuestion());
            
            this.tempoInicioRonda = System.currentTimeMillis();

            // Limpar a caixa de respostas a cada ronda de perguntas
            caixaDeRespostas.limpar();
         
            
            //alterna entre perguntas individuais ou de equipa em cada ronda (impar=Individual, par=Equipa)
            boolean isRondaEquipa = (rondaAtual % 2 == 0); 

            // PERGUNTA DE EQUIPA
            if (isRondaEquipa) {
                System.out.println(">> Tipo: PERGUNTA DE EQUIPA");
                
                // CountDownLatch simplificado para o GameState
                fimDaRondaEquipaLatch = new ModifiedCountdownLatch(1, 0, 40, numEquipasEsperadas);
                
                barreiraEquipa.clear();
                
                
                for(Equipa equipa: equipas.values()) {
                	// Flag usada para garantir que o Runnable so e executado uma vez se ocorrer timeout
                	// sem esta flag no caso do primeiro membro responder e ficar a espera do outro,
                	// se ocorresse timeout a thread do primeiro jogador acordava sozinha e a executava o Runnable e a thread do jogador
                	// que nao respondeu executava igualmente o Runnable
                	final boolean[] acaoJaExecutada = {false};
                	Runnable barrierAction = () -> {
                		synchronized (acaoJaExecutada) {
                            if (acaoJaExecutada[0]) {
                                return;
                            }
                            acaoJaExecutada[0] = true;
                        }
                        
                        System.out.println("[Barreira " + equipa.getIdEquipa() + "] Equipa terminou. A calcular pontos...");
                        calcularPontosEquipa(equipa, pergunta);
                        fimDaRondaEquipaLatch.countdown();
                    };
                    
                    CyclicBarrier barrier = new CyclicBarrier(jogadoresPorEquipa, barrierAction);
                    barreiraEquipa.put(equipa.getIdEquipa(), barrier);
                }

                this.modifiedLatch = null; 

            // PERGUNTA INDIVIDUAL
            } else {
                System.out.println(">> Tipo: PERGUNTA INDIVIDUAL");
                this.modifiedLatch = new ModifiedCountdownLatch(2, 2, 30, jogadores.size());
                this.barreiraEquipa.clear(); 
            }

            // Enviar Pergunta
            for (ClientHandler handler : listeners) {
                handler.enviarObjeto(pergunta);
            }

            // Esperar
            try {
            	// GameState espera (um pouco mais do que 30s) que a barreira acabe depois ser aberta e executado o Runnable para o calculo dos pontos de cada equipa
                // Pergunta EQUIPA
            	if (isRondaEquipa) {
                    fimDaRondaEquipaLatch.await();
                } else {
                	// GameState espera pelo ultimo jogador a responder (Pergunta INDIVIDUAL)
                	modifiedLatch.await();
                }
                System.out.println("[GameState] Ronda terminada.");

            } catch (InterruptedException e) {
                return;
            }
            
            
            String textoPlacar = "PLACAR:" + gerarTextoPlacar();
            for (ClientHandler handler : listeners) {
                handler.enviarObjeto(textoPlacar);
            }
            
            //Delay para passar entre perguntas
            try { Thread.sleep(3000); } catch (InterruptedException e) {}
        }

        System.out.println("[GameState " + idSala + "] Fim do Jogo!");
        
        String msgFinal = "FIM:" + gerarTextoPlacar();
        for (ClientHandler handler : listeners) {
            handler.onFimDeJogo(msgFinal);
        }
        
    }
    
    

    public void processarResposta(String username, int indiceResposta) {
        
        boolean isRondaEquipa = !barreiraEquipa.isEmpty();

        if (isRondaEquipa) {
        	
            caixaDeRespostas.adicionarResposta(username, indiceResposta);

            Jogador jogador = jogadores.get(username);
            String idEquipa = jogador.getIdEquipa();
            
            CyclicBarrier barreiraDaEquipa = barreiraEquipa.get(idEquipa);

            if (indiceResposta == -1) {
                System.out.println("[GameState] Jogador " + username + " Não respondeu (Timeout)");
            } else {
                System.out.println("[GameState] Jogador " + username + " (Equipa " + idEquipa + ") respondeu. A esperar na barreira...");
            }
           

            try {
                // Esperar apenas pelos colegas de equipa
                if (barreiraDaEquipa != null) {
                	
                	long tempoPassado = System.currentTimeMillis() - this.tempoInicioRonda;
                    int tempoPassadoSegundos = (int) (tempoPassado / 1000);
                    
                    // Calcula quanto tempo falta para atingir 30s (timeout entre cada ronda)
                    int tempoParaEsperar = 30 - tempoPassadoSegundos;
                    
                    // Segurança: se o tempo já acabou, mete 0
                    if (tempoParaEsperar < 0) tempoParaEsperar = 0;

                    barreiraDaEquipa.await(tempoParaEsperar); 
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        } else {
        	//Calcular pontos de cada jogador quando a pergunta e do tipo individual
            Jogador jogador = jogadores.get(username);
            Equipa equipa = equipas.get(jogador.getIdEquipa());
            Pergunta perguntaAtual = listaDePerguntas.get(rondaAtual - 1);

            boolean acertou = (indiceResposta == perguntaAtual.getCorrectOptionIndex());
            int pontosGanhos = 0;

            int fatorBonus = modifiedLatch.countdown(); 

            if (acertou) {
                pontosGanhos = perguntaAtual.getPoints() * fatorBonus;
                equipa.adicionarPontos(pontosGanhos);
                System.out.println("Jogador " + username + " ACERTOU! (Bónus: " + fatorBonus + "x). Pontos: " + pontosGanhos);
            } else {
                System.out.println("Jogador " + username + " errou.");
            }
        }
    }

    private void calcularPontosEquipa(Equipa equipa, Pergunta pergunta) {
        int pontosBase = pergunta.getPoints();

        // calcula a pontuacao da equipa (se todos os membros da equipa acertaram tem um bonus duplicado, caso contrario sera considerada a melhor pontuacao de entre eles)
       
        boolean todosAcertaram = true;
        boolean alguemAcertou = false;
        
        List<Jogador> membrosEquipa = equipa.getMembrosEquipa();


        for (Jogador membro : membrosEquipa) {
            String username = membro.getUsername();
            
            //se um dos membros dessa equipa nao respondeu por algum motivo coloca se a variavel a false e salta para o proximo membro dessa equipa
            if (!caixaDeRespostas.temResposta(username)) {
                todosAcertaram = false;
                continue;
            }
            
            int resp = caixaDeRespostas.obterResposta(username);
            
            if (resp == pergunta.getCorrectOptionIndex()) {
                alguemAcertou = true;
            } else {
                todosAcertaram = false;
            }
        }

        if (todosAcertaram ) {
            int bonus = pontosBase * 2;
            equipa.adicionarPontos(bonus);
            System.out.println(">> Equipa " + equipa.getIdEquipa() + ": Todos acertaram! (Pontos: " + bonus + ")");
        } else if (alguemAcertou) {
        	//considerada a melhor pontuacao entre eles (cotacao da pergunta sem bonificacao)
            equipa.adicionarPontos(pontosBase);
            System.out.println(">> Equipa " + equipa.getIdEquipa() + ": Parcial. (Pontos: " + pontosBase + ")");
        } else {
            System.out.println(">> Equipa " + equipa.getIdEquipa() + ": Errou.");
        }
        
    }
    
    private String gerarTextoPlacar() {
    	String texto = "";
        List<Equipa> listaEquipas = new ArrayList<>(equipas.values());
        // Ordenar por pontuação descrescente
        listaEquipas.sort((e1, e2) -> Integer.compare(e2.getPontuacaoTotal(), e1.getPontuacaoTotal()));

        for (Equipa e : listaEquipas) { 
            texto += e.getIdEquipa() + ": " + e.getPontuacaoTotal() + " pts\n";
        }
        return texto;
    }
    
    public boolean salaCompleta() {
        return jogadores.size() == numEquipasEsperadas * jogadoresPorEquipa;
    }

    
}