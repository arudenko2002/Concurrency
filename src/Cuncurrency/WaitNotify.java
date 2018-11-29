package Cuncurrency;

import java.util.Vector;

class Producer extends Thread {
 
    static final int MAXQUEUE = 5;
    private Vector<String> messages = new Vector<String>();
    int counter=0;
    boolean readWrite=true;
    void setFalse(){readWrite=false;}
    boolean getFalse(){return readWrite;}
 
    @Override
    public void run() {
        try {
            while (true) {
                putMessage();
                //sleep(5000);
            }
        } catch (InterruptedException e) {
        }
    }
 
    private synchronized void putMessage() throws InterruptedException {
        while (messages.size() == MAXQUEUE) {
        	System.out.println("WAIT WWW");
            //wait();
            System.out.println("WAIT WWW2");
            setFalse();
            notify();
        }
        messages.addElement((counter++)+"  "+new java.util.Date().toString());
        System.out.println("put message "+counter);
        notify();
        //Later, when the necessary event happens, the thread that is running it calls notify() from a block synchronized on the same object.
    }
 
    // Called by Consumer
    public synchronized String getMessage() throws InterruptedException {
        notify();
        while (messages.size() == 0) {
            wait();//By executing wait() from a synchronized block, a thread gives up its hold on the lock and goes to sleep.
        }
        String message = (String) messages.firstElement();
        messages.removeElement(message);
        return message;
    }
}
 
class WaitNotify extends Thread {
 
    Producer producer;
    
 
    WaitNotify(Producer p) {
        producer = p;
        try {
        	
        	synchronized(producer) {
        		
        		while(producer.getFalse()) {
        			System.out.println("wait stoi");
        			producer.wait();
        		}
        	}
        } catch (InterruptedException e) {}
    }
 
    @Override
    public void run() {
        try {
            while (true) {
                String message = producer.getMessage();
                System.out.println("Got message: " + message);
                //sleep(200);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
 
    public static void main(String args[]) {
        Producer producer = new Producer();
        producer.start();
        new WaitNotify(producer).start();
    }
}
