package servidor;

import java.util.ArrayList;
import java.util.List; 

/**
 * Este será o nosso recurso partilhado pelas threads, uma caixa de respostas que implementa o que vimos nas aulas
 * o padrão Produtor Consumidor
 * Produtor: threads Jogadores que colocam as respostas na caixa
 * Consumidor: A thread principal do jogo (no servidor, thread GameState), que retira as respostas
 */


public class CaixaDeRespostas {
	
	private List<String> respostasDaRonda;
	
	public CaixaDeRespostas() {
		this.respostasDaRonda = new ArrayList<>();
	}
	
	
	public void submeterResposta(String username, String resposta) {
        System.out.println("[CaixaDeRespostas] " + username + " respondeu: " + resposta);
        respostasDaRonda.add(resposta);
    }

	
    public List<String> armazenarTodasAsRespostas(int numJogadoresEsperados) {
        System.out.println("[CaixaDeRespostas] A aguardar " + numJogadoresEsperados + " respostas...");
        
        
        return new ArrayList<>();
    }
    
    public void limparParaProximaRonda() {
        respostasDaRonda.clear();
    }
	
	
}
