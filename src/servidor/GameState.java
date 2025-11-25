package servidor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Classe central que gere o estado de uma sala de jogo.
 * (Versão para a Fase 1-2-3, sem threads de cronómetro)
 */

public class GameState {

    private final int idSala;
    private final int numEquipasEsperadas;
    private final int jogadoresPorEquipa;
    private Map<String, Equipa> equipas;
    private Map<String, Jogador> jogadores;
    //private CaixaDeRespostas caixaDeRespostas;
    private List<Pergunta> listaDePerguntas;
    private int rondaAtual;

    public GameState(int idSala, int numEquipas, int numJogadores) {
        this.idSala = idSala;
        this.numEquipasEsperadas = numEquipas;
        this.jogadoresPorEquipa = numJogadores;
        this.equipas = new HashMap<>();
        this.jogadores = new HashMap<>();
        //this.caixaDeRespostas = new CaixaDeRespostas();
        this.rondaAtual = 0;
    }

   
    public void carregarPerguntas(List<Pergunta> perguntas) {
        this.listaDePerguntas = perguntas;
    }

    
    public Pergunta getProximaPergunta() {
    	
        if (listaDePerguntas != null && rondaAtual < listaDePerguntas.size()) {
            System.out.println("[GameState] A preparar ronda " + (rondaAtual + 1));
            Pergunta p = listaDePerguntas.get(rondaAtual);
            rondaAtual++;
            return p;
        }
        
        System.out.println("[GameState] Fim do Jogo. Não há mais perguntas.");
        return null;
    }
    
  
    public boolean adicionarJogador(String idEquipa, String username) {
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
        System.out.println("Sucesso (Sala " + idSala + "): Jogador " + username + " entrou na equipa " + idEquipa);
        
        if (jogadores.size() == totalJogadoresEsperados) {
            System.out.println("SALA " + idSala + " COMPLETA. O jogo vai começar!");
            synchronized (this) {
                this.notifyAll();
            }
        }
        
        return true;
    }
    
    public boolean salaCompleta() {
        return jogadores.size() == numEquipasEsperadas * jogadoresPorEquipa;
    }

    public int getNumJogadoresLigados() {
        return jogadores.size();
    }
}