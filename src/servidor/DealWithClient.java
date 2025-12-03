package servidor;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Map;

import jogo.GameState;

public class DealWithClient extends Thread implements ClientHandler {

	
	private Socket connection;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private Map<Integer, GameState> salas;
    private String username;

    public DealWithClient(Socket connection, Map<Integer, GameState> salas) {
        this.connection = connection;
        this.salas = salas;
    }

    @Override
    public void run() {
        try {
            setStreams();
            processConnection();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
		} finally {
            closeConnection();
        }
    }
    

    
    private void setStreams() throws IOException {
        out = new ObjectOutputStream(connection.getOutputStream());
        in = new ObjectInputStream(connection.getInputStream());
    }
    
    

    private void processConnection() throws IOException, ClassNotFoundException {
      
        String msg = (String) in.readObject();

        String[] parts = msg.split(" ");
        int salaID = Integer.parseInt(parts[0]);

        if (!salas.containsKey(salaID)) {
            out.writeObject("ERRO: Sala não existe.");
            return;
        }

        GameState gs = salas.get(salaID);
        
        String equipa = parts[1];
        this.username = parts[2];
        
        boolean jogadorAceite = gs.adicionarJogador(equipa, username, this);
        
        if (!jogadorAceite) {
        	out.writeObject("ERRO: Não foi possível entrar (Sala cheia ou nome duplicado).");
        	return;
        }

        out.writeObject("Ligado com sucesso à sala " + salaID);
        
        // obtem o cadeado do objeto GameState
        synchronized (gs) {
            while (!gs.salaCompleta()) {
                try {
                	//cada thread DealWithClient fica bloqueada a espera que o jogue comece (sala completa)
                    gs.wait();
                } catch (InterruptedException e) {
                    return;
                }
            }
        }

        out.writeObject("JOGO_A_COMECAR");

        // Receber Respostas
        System.out.println("[DealWithClient " + username + "] À espera de respostas...");
        
        while (true) {
            try {
                Object obj = in.readObject();
                
                // O cliente envia um Integer com o indice da resposta escolhida
                if (obj instanceof Integer) {
                    int indiceResposta = (Integer) obj;
                    gs.processarResposta(username, indiceResposta);
                }
                
            } catch (IOException e) {
                System.out.println("[DealWithClient " + username + "] Cliente desligou-se.");
                break;
            }
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
	
	
    public String getUsername() {
        return username;
    }
    
    

    // Metodo publico para o GameState enviar objetos (Perguntas, Placares) para este cliente
    @Override
    public void enviarObjeto(Object obj) {
        try {
            out.writeObject(obj);
            out.reset(); // Importante para evitar cache de objetos repetidos
        } catch (IOException e) {
            System.err.println("Erro a enviar objeto para " + username);
        }
    }
    
    
    @Override
    public void onFimDeJogo(String placarFinal) {
        enviarObjeto("FIM:" + placarFinal);
 
        closeConnection();

        this.interrupt();
    }
    
	
}
