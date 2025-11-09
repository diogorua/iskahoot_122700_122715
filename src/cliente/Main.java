package cliente;

import servidor.*; 
import java.util.List;
import javax.swing.JOptionPane; 

public class Main {

 private GUI gui;
 private GameState sala;

 
 
 public static void main(String[] args) {
     Main teste = new Main();
     teste.start();
 }
 
 

 public void start() {
     System.out.println("--- INICIANDO TESTE LOCAL ---");

     
     System.out.println("[Servidor] A carregar perguntas de 'perguntas.json'...");
     List<Quiz> quizzes = LerPerguntas.carregarQuizzesDoFicheiro("perguntas.json");
     
     if (quizzes.isEmpty()) {
         System.err.println("ERRO: Ficheiro 'perguntas.json' não encontrado ou vazio.");
         return;
     }
     
     List<Pergunta> perguntasDoJogo = quizzes.get(0).getQuestions();
     System.out.println("[Servidor] " + perguntasDoJogo.size() + " perguntas carregadas.");

     System.out.println("[Servidor] A criar sala 'salaTeste' para 1 equipa...");
     sala = new GameState("salaTeste", 1);
     sala.carregarPerguntas(perguntasDoJogo);
     sala.adicionarJogador("jogador1", "equipaA");
     sala.adicionarJogador("jogador2", "equipaA");


    
     System.out.println("[Cliente] A abrir a GUI para 'jogador1'...");
     gui = new GUI();
     
     gui.setMain(this); 
     
     gui.open();
     gui.setTextPlayer("jogador1");
     gui.setTextPoints("Equipa: equipaA | Pontos: 0");

     simularProximaRonda();
     
     System.out.println("--- TESTE INICIADO ---");
     System.out.println("A GUI está aberta. O ciclo de jogo está ativo.");
     
 }
 
 
 private void simularProximaRonda() {
     Pergunta pergunta = sala.getProximaPergunta();
     
     if (pergunta != null) {
         System.out.println("[Interligação] A enviar pergunta '" + pergunta.getQuestion() + "' para a GUI...");
         
         gui.setTextQuestion(pergunta.getQuestion());
         gui.setTextButtons(pergunta.getOptions());
         gui.startTimer(30);
         
     } else {
         System.out.println("[Interligação] Fim do Jogo. Não há mais perguntas.");
         JOptionPane.showMessageDialog(null, "Fim do Jogo! (Teste)");
         gui.close();
         System.exit(0);
         
     }
 }
 

 public void onRespostaSubmetida() {
     System.out.println("[TesteLocal] GUI submeteu uma resposta. A carregar próxima ronda...");
     
     simularProximaRonda();
 }
}

