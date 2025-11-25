package servidor;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Servidor {

    public static final int PORT = 3000;

    private ServerSocket server;
    private Map<Integer, GameState> salas = new HashMap<>();
    private int proximoID = 0;

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
		ConnectionHandler handler = new ConnectionHandler (connection, salas);
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
        salas.put(proximoID, gs);
        System.out.println("[SERVIDOR] Sala criada com ID = " + proximoID);
        proximoID++;
    }

    private class ConnectionHandler extends Thread {
        private Socket connection;
        private Scanner in;
        private PrintWriter out;
        private Map<Integer, GameState> salas;

        public ConnectionHandler(Socket connection, Map<Integer, GameState> salas) {
            this.connection = connection;
            this.salas = salas;
        }

        @Override
        public void run() {
            try {
                setStreams();
                processConnection();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                closeConnection();
            }
        }

        private void setStreams() throws IOException {
            out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(connection.getOutputStream())), true);
            in = new Scanner(connection.getInputStream());
        }

        private void processConnection() {
            try {
                String msg = in.nextLine();

                String[] parts = msg.split(" ");
                int salaID = Integer.parseInt(parts[0]);

                if (!salas.containsKey(salaID)) {
                    out.println("ERRO: Sala não existe.");
                    return;
                }

                GameState gs = salas.get(salaID);
                gs.adicionarJogador(parts[1], parts[2]);

                out.println("Ligado com sucesso à sala " + salaID);
                
                synchronized (gs) {
                    while (!gs.salaCompleta()) {
                        gs.wait();
                    }
                }

                out.println("JOGO_A_COMECAR");

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void closeConnection() {
            try {
                if (connection != null) connection.close();
                if (in != null) in.close();
                if (out != null) out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        Servidor server = new Servidor();
        server.runServer();
    }
}
