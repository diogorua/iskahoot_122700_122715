package cliente;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

import perguntas.Pergunta;

public class Cliente {
	
	private Socket connection;
	private ObjectInputStream in;
	private ObjectOutputStream out;
	
	private String jogoID;
    private String equipa;
    private String username;
    private GUI gui;
    
    
    public Cliente(String jogoID, String equipa, String username) {
        this.jogoID = jogoID;
        this.equipa = equipa;
        this.username = username;
        
        //Instanciamos a gui para conseguir saber que resposta o utilizador escolheu na interface grafica e para invocar os metodos necessarios
        this.gui = new GUI();
        this.gui.setCliente(this);
    }
    
	
	public void runClient(String ip, int port) throws ClassNotFoundException {
		try {
			gui.open();
            gui.setTextPlayerAndTeam(username, equipa);
			
			connectToServer(ip, port);
			setStreams();
			processConnection();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			closeConnection();
		}
	}
	
	
	
	private void connectToServer(String ip, int port) throws IOException {
        InetAddress endereco = InetAddress.getByName(ip);
        System.out.println("Endereco: " + endereco);
        connection = new Socket(endereco, port);
        System.out.println("Socket criado!");
    }
	
	
	private void setStreams() throws IOException {
		out = new ObjectOutputStream(connection.getOutputStream());
        in = new ObjectInputStream(connection.getInputStream());
	}
	
	
	private void processConnection() throws IOException, ClassNotFoundException {
		out.writeObject(jogoID + " " + equipa + " " + username);
		
		String resposta = (String) in.readObject();
        System.out.println("[SERVIDOR] " + resposta);

        if (resposta.startsWith("ERRO")) {
            System.out.println("Ligação terminada.");
            return;
        }

        System.out.println("Cliente aceite! Aguardando início de jogo...");
        
        while (true) {
        	try {

        		// o cliente le uma mensagem ou uma pergunta
                Object obj = in.readObject();

                if (obj instanceof String) {
                	
                	String msg = (String) obj;
                	
                	if (msg.equals("JOGO_A_COMECAR")) {
                        System.out.println("O JOGO COMEÇOU!");
                    } 
                    else if (msg.startsWith("PLACAR:")) {
                    	// Remove o prefixo PlACAR: para enviar para a GUI apenas a equipa e a pontuacao em cada ronda
                        String placar = msg.substring(7);
                        gui.atualizarPlacar(placar);
                    }
                    else if (msg.startsWith("FIM:")) {
                    	// Remove o prefixo FIM: para enviar para a GUI apenas o placar final com as pontuacoes
                        String placarFinal = msg.substring(4);
                        gui.mostrarFimDeJogo(placarFinal);
                        break;
                    }
                    else {
                        System.out.println("[SERVIDOR] " + msg);
                    }
                	
                } 
                else if (obj instanceof Pergunta) {
                	Pergunta p = (Pergunta) obj;
                	System.out.println("Recebi pergunta: " + p.getQuestion());
                	gui.showQuestion(p);
                }
                
        
        	} catch (EOFException e) {
                System.out.println("[CLIENTE] Servidor fechou a ligação.");
                break;
            }
        }
	}
	

	
	public void closeConnection() {
		try {
			if(connection != null) connection.close();
			
			if(in != null) in.close();
			
			if(out != null) out.close();
			
			if(gui != null) gui.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	//manda a resposta para o servidor (metodo invocado na gui)
	public void sendAnswer(int indice) {
		try {
			out.writeObject(indice);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	
	public static void main(String[] args) throws ClassNotFoundException {
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
