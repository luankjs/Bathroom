package br.ufrn.bathroom;

import java.util.Random;

public class Person implements Runnable {
  
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
    			msg_end = "Homem saiu no banheiro";
    		} else {
    			msg = "Mulher entrou no banheiro";
    			msg_end = "Mulher saiu no banheiro";
    		}

        System.out.println(msg);
        usingBathroom();
        System.out.println(msg_end);
    }

    private void usingBathroom() {
    		Random random = new Random();

        try {
            Thread.sleep(random.nextInt(this.bathroom.maxTime));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}