package Cuncurrency;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
 
public class DeadLock2 {
    double balance;
    final int id;
    final Lock lock = new ReentrantLock();
     
    DeadLock2(int id, double balance) {
        this.id = id;
        this.balance = balance;
    }
     
    void withdraw(double amount) {
        // Wait to simulate io like database access ...
        try {Thread.sleep(10l);} catch (InterruptedException e) {}
        balance -= amount;
    }
     
    void deposit(double amount) {
        // Wait to simulate io like database access ...
        try {Thread.sleep(10l);} catch (InterruptedException e) {}
        balance += amount;
    }
     
    static void transfer(DeadLock2 from, DeadLock2 to, double amount) {
        from.lock.lock();
            from.withdraw(amount);
            System.out.println("Balance from ="+from.balance+"  to="+to.balance);
            to.lock.lock();
            System.out.println("Balance2 from ="+from.balance+"  to="+to.balance);
                to.deposit(amount);
                System.out.println("Balance3 from ="+from.balance+"  to="+to.balance);
            to.lock.unlock();
            System.out.println("Balance4 from ="+from.balance+"  to="+to.balance);
        from.lock.unlock();
        System.out.println("Balance5 from ="+from.balance+"  to="+to.balance);
    }
     
    public static void main(String[] args) {
        final DeadLock2 fooAccount = new DeadLock2(1, 100d);
        final DeadLock2 barAccount = new DeadLock2(2, 100d);
         
        new Thread() {
            public void run() {
            	DeadLock2.transfer(fooAccount, barAccount, 10d);
            }
        }.start();
         
        new Thread() {
            public void run() {
            	DeadLock2.transfer(barAccount, fooAccount, 10d);
            }
        }.start();
         
    }
}
