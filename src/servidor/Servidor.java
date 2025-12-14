package servidor;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import jogo.GameState;
import perguntas.LerPerguntas;
import perguntas.Quiz;

public class Servidor {

    public static final int PORT = 3000;

    private ServerSocket server;
    private Map<Integer, GameState> salas = new HashMap<>();
    private int proximoID = 0;
    private ExecutorService poolSalas = Executors.newFixedThreadPool(5);


    public void runServer() {
        try {
            server = new ServerSocket(PORT);
            System.out.println("[SERVIDOR] A escutar na porta " + PORT + "...");
            
            new Thread(this::menuAdmin).start();
            while(true) {
            	waitForConnection();
            }	
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (server != null) {
                try {
                    server.close();
                    System.out.println("[SERVIDOR] ServerSocket fechado.");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    
    private void waitForConnection() throws IOException {
		Socket connection = server.accept();
		DealWithClient handler = new DealWithClient(connection, salas, poolSalas);
		handler.start();
		System.out.println("[SERVIDOR] Nova Conexão...");
	}

    
    private void menuAdmin() {
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\n=== ADMIN ===");
            System.out.println("Escreve:  new <equipas> <jogadores>");
            System.out.print("> ");
            String cmd = sc.nextLine();

            if (cmd.startsWith("new")) {
                String[] parts = cmd.split(" ");
                if (parts.length == 3) {
                    int nEquipas = Integer.parseInt(parts[1]);
                    int nJogadores = Integer.parseInt(parts[2]);
                    criarSala(nEquipas, nJogadores);
                } else {
                    System.out.println("Formato inválido! Ex: new 2 3");
                }
            } 
        }
    }

    private void criarSala(int equipas, int jogadores) {
        GameState gs = new GameState(proximoID, equipas, jogadores);
        
        List<Quiz> quizzes = LerPerguntas.carregarQuizzesDoFicheiro("perguntas.json");
        
        if (!quizzes.isEmpty()) {
            gs.carregarPerguntas(quizzes.get(0).getQuestions());
            System.out.println("[SERVIDOR] Perguntas carregadas para a sala " + proximoID);
        } else {
            System.err.println("[SERVIDOR] ERRO CRÍTICO: Não foi possível carregar perguntas! O ficheiro está vazio");
        }
        
        
        salas.put(proximoID, gs);
        System.out.println("[SERVIDOR] Sala criada com ID = " + proximoID);
        proximoID++;
    }


    public static void main(String[] args) {
        Servidor server = new Servidor();
        server.runServer();
    }
}
