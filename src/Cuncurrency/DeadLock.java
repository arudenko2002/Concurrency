package Cuncurrency;

public class DeadLock {
	//public class Cell{
	int value=0;
	synchronized void setValue(int val) {value=val;}
	synchronized int getValue() {return value;}
		
	synchronized void swapValue(DeadLock other) {
		int t = getValue();
		System.out.println("current value="+t+"   hash="+ System.identityHashCode(other));
		// wait for another thread to start
		try {Thread.sleep(20l);} catch (InterruptedException e) {}
		int v = other.getValue();
		setValue(v);
		other.setValue(t);
	}
	
	public DeadLock(int val) {
		setValue(val);
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		final DeadLock c1 = new DeadLock(5);
		final DeadLock c2 = new DeadLock(6);
		new Thread(){
	        public void run() {
	          System.out.println("intent c2->c1, c1="+c1.value+" c2="+c2.value);
	          c1.swapValue(c2);
	          System.out.println("RESULT c2->c1, c1="+c1.value+" c2="+c2.value);
	        }
	     }.start();
	     new Thread(){
		    public void run() {
		     	System.out.println("intent c1->c2, c1="+c1.value+" c2="+c2.value);
		        c2.swapValue(c1);
		        System.out.println("RESULT c1->c2, c1="+c1.value+" c2="+c2.value);
		    }
         }.start();
	}

}
