package NokiaGrabber;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadTracker {
	private AtomicInteger counter = new AtomicInteger();
	private ExecutorService pool;
	private int MaxThread;
	private int HasDone=0;
	private int CountNE;
	
	public void incrementCounter(){
		int incrementAndGet = counter.incrementAndGet();
		HasDone++;
		System.out.println("Thread counter : "+incrementAndGet);
	}
	
	public void decreaseCounter(){
		if(counter.get()<0)
			{
			throw new ArithmeticException("counter is < 0");			
			}
		int decrementAndGet = counter.decrementAndGet();
		System.out.println("Thread counter : "+decrementAndGet);
		checkIfThreadDead();
	}

	protected int getCounter() {
		return counter.get();
	}
	
	protected void checkIfThreadDead(){
		if((getCounter()==0 && MaxThread!=1) || (getCounter()==0 && MaxThread==1 && CountNE == HasDone)){
			allThreadsHaveFinished();
		}
	}
	
//	@Override
	public void allThreadsHaveFinished(){
	}

	public void setPool(ExecutorService pool) {
		this.pool = pool;
	}

	public ExecutorService getPool() {
		return pool;
	}
	
	public void setMaxthread(int MaxThread){
		this.MaxThread=MaxThread;
	}
	
	public void setCountNE(int CountNE){
		this.CountNE=CountNE;
	}
}
