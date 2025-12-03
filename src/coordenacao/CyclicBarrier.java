package coordenacao;

import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class CyclicBarrier {

	private Lock lock = new ReentrantLock();
	// variavel condicional que e negacao da condicao de bloqueio (a barreira bloqueia ate todas as threads chegarem)
	private Condition allArrived = lock.newCondition();
	private Runnable barrierAction;
	private int numThreads;
	private int currentThreads = 0;
	// assegura a reutilizacao da barreira
	private int generation = 0;
	
	public CyclicBarrier(int numThreads, Runnable barrierAction) {
		this.numThreads = numThreads;
		this.barrierAction = barrierAction;
	}
	
	
	public void await(int waitPeriod) throws InterruptedException {
		lock.lock();
		
        try {
            int myGeneration = generation;
            
            currentThreads++;
            
            if (currentThreads == numThreads) {
                if (barrierAction != null) {
                    barrierAction.run();
                }
                
                nextGeneration();
                return;
            }

   
            long wait = System.currentTimeMillis() + (waitPeriod * 1000);
            
            
            while (currentThreads < numThreads && myGeneration == generation) {
                
                long timeRemaining = wait - System.currentTimeMillis();
                
                //caso de timeout
                if (timeRemaining <= 0) {
                    breakBarrier();  
                    return;
                }
                
                allArrived.await(timeRemaining, TimeUnit.MILLISECONDS);
            }
      

        } finally {
            lock.unlock();
        }
    }

    private void nextGeneration() {
        currentThreads = 0;
        generation++;
        allArrived.signalAll(); 
    }

    private void breakBarrier() {
        if (barrierAction != null) {
             barrierAction.run();
        }
        generation++;
        currentThreads = 0;
        allArrived.signalAll();
    }
	

	
}
