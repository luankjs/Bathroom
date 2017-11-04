package br.ufrn.bathroomSemaphore;

import java.util.Random;
import java.util.concurrent.Semaphore;

public class Bathroom {
	int capacity;
	char gender;
	int maxTime;
	static Semaphore semaphore;
	static boolean flag = true;
	
	public Bathroom(int capacity, int maxTime) {
		this.capacity = capacity;
		Bathroom.semaphore = new Semaphore(this.capacity);
		this.maxTime = maxTime;
	}
	
	public void setGender(char gender) {
		this.gender = gender;
	}
	
	static class Person extends Thread{
		  
	    public char gender;
	    public Bathroom bathroom;
	    
	    public Person(char g, Bathroom bath){
	        this.gender = g;
	        this.bathroom = bath;
	    }

	    @Override
	    public void run() {
	    		String msg;
			String msg_end;
	    		if (this.gender == 'M') {
	    			msg = "Homem entrou no banheiro";
	    			msg_end = "Homem saiu do banheiro";
	    		} else {
	    			msg = "Mulher entrou no banheiro";
	    			msg_end = "Mulher saiu do banheiro";
	    		}

	        try {
	        		semaphore.acquire(); //Is it possible to use bathroom?      
	            System.out.println(msg);
	            usingBathroom(); // Using bathroom
	        } catch (InterruptedException e) {
	            e.printStackTrace();
	        } finally {
		        	System.out.println(msg_end);
	        		semaphore.release(); // Go out bathroom
	        }
	    }

	    private void usingBathroom() { // Critical Region
	    		Random random = new Random();

	        try {
	            Thread.sleep(random.nextInt(this.bathroom.maxTime));
	        } catch (InterruptedException e) {
	            e.printStackTrace();
	        }
	    }
	}
	
	public static void main(String args[]) throws InterruptedException {
		Random random = new Random();
		Bathroom bathroom = new Bathroom(5, 10000);
		
		// People in bathroom counter
		Thread personCounter = new Thread(){
			int inBath = 0;

			public void run(){
				while(flag) {
					inBath = bathroom.capacity - semaphore.availablePermits();
					System.out.println("Pessoas no banheiro: " + inBath);
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				
				this.interrupt();
			}
		};
		
		personCounter.start();

		int count = 0;
		while (count <= 20) {
			char gender = random.nextInt(2) == 1 ? 'M' : 'F';
			Person person = new Person(gender, bathroom);
			
			if(canEntry(person, bathroom.capacity)){
				bathroom.gender = person.gender;
				person.start();
			} else {
	    			if (person.gender == 'M' && semaphore.availablePermits() == 0) {
					System.out.println("Homem Rejeitado, banheiro lotado!");
				} else if(person.gender == 'F' && semaphore.availablePermits() == 0) {
					System.out.println("Mulher Rejeitada, banheiro lotado!");
				} else if(person.gender == 'M') {
					System.out.println("Homem Rejeitado!");
				} else {
					System.out.println("Mulher Rejeitada!");
				}
			}

	        Thread.sleep(random.nextInt(5000));

	        count++;
		}
		
		flag = false;
		System.out.println("Banheiro fechado para limpeza!");
	}
	
	private static boolean canEntry(Person person, int capacity) {
		int inBath = capacity - semaphore.availablePermits();
		
		return (inBath == 0) || (person.gender == person.bathroom.gender);
	}
}
