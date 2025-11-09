package servidor;

import java.util.ArrayList;
import java.util.List;

public class Equipa {

	private final String idEquipa;
	private final List<Jogador> membrosEquipa;
	private int pontuacaoTotal;
	
	public Equipa(String idEquipa) {
		this.idEquipa = idEquipa;
		this.membrosEquipa = new ArrayList<>();
		this.pontuacaoTotal = 0;
	}
	
	
	public String getIdEquipa() {
		return idEquipa;
	}
	
	public List<Jogador> getMembrosEquipa() {
		return membrosEquipa;
	}
	
	public int getPontuacaoTotal() {
		return pontuacaoTotal;
	}
	
	public int getNumeroMembros() {
		return this.membrosEquipa.size();
	}
	
	
	public void adicionarJogadores(Jogador jogador) {
		if(membrosEquipa.size() < 2) {
			membrosEquipa.add(jogador);
		}
	}
	
	
	
	/**
	 * Numa fase mais avançada este método terá de ser synchronized para evitar que duas threads
	 * de jogadores da mesma equipa alterem os pontos ao mesmo tempo
	 */
	public void adicionarPontos(int pontos) {
		this.pontuacaoTotal += pontos;
	}
	
	
	
	
}
