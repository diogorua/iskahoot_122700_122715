package jogo;

import java.util.HashMap;
import java.util.Map;

/**
 * Recurso Partilhado para guardar as respostas da ronda atual
 */


public class CaixaDeRespostas {
    
    // Mapa: Username -> Indice da Resposta
    private Map<String, Integer> respostas;
    
    
    public CaixaDeRespostas() {
        this.respostas = new HashMap<>();
    }
    
    
    
    public synchronized void adicionarResposta(String username, int resposta) {
        respostas.put(username, resposta);
    }
    

  
    public synchronized boolean temResposta(String username) {
        return respostas.containsKey(username);
    }
    

    public synchronized int obterResposta(String username) {
        return respostas.get(username);
    }
    
    
    public synchronized void limpar() {
        respostas.clear();
    }
}
