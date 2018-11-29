package Cuncurrency;

public class DeadLockResolved {
	//public class Cell{
	int value=0;
	synchronized void setValue(int val) {value=val;System.out.println("SET="+value);}
	synchronized int getValue() {System.out.println("GET="+value);return value;}
		
	synchronized void swapValue(DeadLockResolved other) {
		int t = getValue();
		System.out.println("current value="+t+"   hash="+ System.identityHashCode(other));
		// wait for another thread to start
		try {Thread.sleep(20l);} catch (InterruptedException e) {}
		int v = other.getValue();
		setValue(v);
		other.setValue(t);
	}
	
	synchronized void swapValueResolved(DeadLockResolved other) {
		System.out.println("this="+System.identityHashCode(this)+"   other="+ System.identityHashCode(other));
		if(other == this) {
			return;
		} else if (System.identityHashCode(this) < System.identityHashCode(other)) {
			System.out.println("OTHER "+other.value+ "  "+System.identityHashCode(other));
			this.swapValue(other);
		}
		else {
			System.out.println("THIS "+this.value+"  "+System.identityHashCode(this));
			other.swapValue(this);
		}
	}
	
	public DeadLockResolved(int val) {
		setValue(val);
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		final DeadLockResolved c1 = new DeadLockResolved(5);
		final DeadLockResolved c2 = new DeadLockResolved(6);
		new Thread(){
	        public void run() {
	          System.out.println("intent c2->c1, c1="+c1.value+" c2="+c2.value);
	          c1.swapValueResolved(c2);
	          System.out.println("RESULT c2->c1, c1="+c1.value+" c2="+c2.value);
	        }
	     }.start();
	     new Thread(){
		    public void run() {
		     	System.out.println("intent c1->c2, c1="+c1.value+" c2="+c2.value);
		        c2.swapValueResolved(c1);
		        System.out.println("RESULT c1->c2, c1="+c1.value+" c2="+c2.value);
		    }
         }.start();
	}

}
