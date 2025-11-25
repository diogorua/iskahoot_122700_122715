package cliente;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class Cliente {
	
	private Socket connection;
	private Scanner in;
	private PrintWriter out;
	
	private String jogoID;
    private String equipa;
    private String username;
    
    public Cliente(String jogoID, String equipa, String username) {
        this.jogoID = jogoID;
        this.equipa = equipa;
        this.username = username;
    }
	
	public void runClient(String ip, int port) {
		try {
			connectToServer(ip, port);
			setStreams();
			processConnection();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			closeConnection();
		}
	}
	
	private void processConnection() {
		out.println(jogoID + " " + equipa + " " + username);
		
		String resposta = in.nextLine();
        System.out.println("[SERVIDOR] " + resposta);

        if (resposta.startsWith("ERRO")) {
            System.out.println("Ligação terminada.");
            return;
        }

        System.out.println("Cliente aceite! Aguardando início de jogo...");
        
        while (true) {
        	try {
                if (!in.hasNextLine()) {
                    System.out.println("[CLIENTE] Servidor fechou a ligação.");
                    break;
                }

                String msg = in.nextLine();

                if (msg.equals("JOGO_A_COMECAR")) {
                	System.out.println("O JOGO COMEÇOU!");
                	//ciclo de jogo TODO
                	break;
                }

                System.out.println("[SERVIDOR] " + msg);
        	} catch (Exception e) {
                System.out.println("[CLIENTE] Erro de ligação.");
                break;
            }
        }
	}
	
	private void connectToServer(String ip, int port) throws IOException {
        InetAddress endereco = InetAddress.getByName(ip);
        System.out.println("Endereco: " + endereco);
        connection = new Socket(endereco, port);
        System.out.println("Socket criado!");
    }
	
	private void setStreams() throws IOException {
		in  = new Scanner(connection.getInputStream());
        out = new PrintWriter(connection.getOutputStream(), true);
	}
	
	public void closeConnection() {
		try {
			if(connection != null)
				connection.close();
			if(in != null)
				in.close();
			if(out != null)
				out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
        if (args.length != 5) {
            System.out.println("USO CORRETO:");
            System.out.println("java Cliente <IP> <PORT> <JogoID> <Equipa> <Username>");
            System.out.println("Exemplo: java Cliente localhost 3000 0 equipa1 user1");
            return;
        }

        String ip = args[0];
        int port = Integer.parseInt(args[1]);

        Cliente cliente = new Cliente(args[2], args[3], args[4]);
        cliente.runClient(ip, port);
    }
}
