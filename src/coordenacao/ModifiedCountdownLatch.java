package coordenacao;

public class ModifiedCountdownLatch {

	// numero de respostas que faltam receber
	private int count;
	
	// fator de bonificacao (duplicar os pontos para os primeiros 2 jogadores)
	private int bonusFactor;
	
	// quantas respostas ainda recebem bonus
	private int bonusCount;
	
	// tempo de espera em segundos
	private long deadline;
	
	
	public ModifiedCountdownLatch(int bonusFactor, int bonusCount, int waitPeriod, int count) {
		this.count = count;
		this.bonusFactor = bonusFactor;
		this.bonusCount = bonusCount;
		this.deadline = System.currentTimeMillis() + (waitPeriod * 1000);
	}
	
	public synchronized int countdown() {
		count--;
		
		int fatorAtual = 1;
		
		if (bonusCount > 0) {
			fatorAtual = bonusFactor;
			bonusCount--;
		}
		
		if (count == 0) {
			notifyAll();
		}
		
		return fatorAtual;
	}
	
	
	// Thread do GameState vai bloquear a espera que o countdownLatch seja aberto
	public synchronized void await() throws InterruptedException {
		while (count > 0) {
			long now = System.currentTimeMillis();
			long timeRemaining = deadline - now;
			
			if (timeRemaining <= 0) {
				break;
			}
			
			wait(timeRemaining);
		}
		
	}
	
}
