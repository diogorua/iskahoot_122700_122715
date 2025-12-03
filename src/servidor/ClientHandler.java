package servidor;

/**
 * Interface que define o contrato de comunicação com um cliente.
 * Permite que o GameState envie mensagens e sinalize o fim do jogo
 * sem conhecer detalhes de rede ou threads.
 */

public interface ClientHandler {
    
    void enviarObjeto(Object obj);

    void onFimDeJogo(String placarFinal);
}