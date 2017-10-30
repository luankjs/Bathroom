package br.ufrn.bathroom;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Bathroom {
	int capacity;
	char gender;
	int maxTime;
	
	public Bathroom(int capacity, int maxTime) {
		this.capacity = capacity;
		this.maxTime = maxTime;
	}
	
	public void setGender(char gender) {
		this.gender = gender;
	}
	
	public static void main(String args[]) throws InterruptedException {
		Random random = new Random();
		Bathroom bathroom = new Bathroom(5, 10000);
		ThreadFactory threadFactory = Executors.defaultThreadFactory();
		MyRejectExecutionHandler rejectionHandler = new MyRejectExecutionHandler();
		ThreadPoolExecutor executor = new ThreadPoolExecutor(bathroom.capacity, bathroom.capacity, 
				bathroom.maxTime, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(bathroom.capacity), threadFactory, rejectionHandler);
		int count = 0;
		
		Thread personCounter = new Thread(){
			public void run(){
				while(!executor.isShutdown()) {
					System.out.println("Pessoas no banheiro: " + executor.getActiveCount());
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		};
		
		personCounter.start();

		while (count <= 20) {
			char gender = random.nextInt(2) == 1 ? 'M' : 'F';
			Person person = new Person(gender, bathroom);
			
			if (canEntry(person, executor)) {
				bathroom.gender = person.gender;
				executor.execute(person);
			} else {
				if (person.gender == 'M') {
					System.out.println("Homem Rejeitado");
				} else {
					System.out.println("Mulher Rejeitada");
				}
			}

	        Thread.sleep(random.nextInt(5000));

	        count++;
		}
		
		System.out.println("Banheiro fechado para limpeza!");
	}
	
	private static boolean canEntry(Person person, ThreadPoolExecutor executor) {
		if ((executor.getActiveCount() == 0) || (person.gender == person.bathroom.gender)) {
			return true;
		} else {
			return false;
		}
	}
}
