package Cuncurrency;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
 
public class LiveLock {
    double balance;
    int id;
    Lock lock = new ReentrantLock();
 
    LiveLock(int id, double balance) {
        this.id = id;
        this.balance = balance;
    }
 
    boolean withdraw(double amount) {
        if (this.lock.tryLock()) {
            // Wait to simulate io like database access ...
            try {Thread.sleep(10l);} catch (InterruptedException e) {}
            balance -= amount;
            return true;
        }
        return false;
    }
 
    boolean deposit(double amount) {
        if (this.lock.tryLock()) {
            // Wait to simulate io like database access ...
            try {Thread.sleep(10l);} catch (InterruptedException e) {}
            balance += amount;
            return true;
        }
        return false;
    }
 
    public boolean tryTransfer(LiveLock destinationAccount, double amount) {
        if (this.withdraw(amount)) {
        	System.out.println("Balance1="+balance);
            if (destinationAccount.deposit(amount)) {
            	System.out.println("Balance2="+balance);
                return true;
            } else {
                // destination account busy, refund source account.
                this.deposit(amount);
                System.out.println("Balance3="+balance);
            }
        }
 
        return false;
    }
 
    public static void main(String[] args) {
        final LiveLock fooAccount = new LiveLock(1, 500d);
        final LiveLock barAccount = new LiveLock(2, 500d);
 
        new Thread(new Transaction(fooAccount, barAccount, 10d), "transaction-1").start();
        new Thread(new Transaction(barAccount, fooAccount, 10d), "transaction-2").start();
 
    }
 
}
class Transaction implements Runnable {
    private LiveLock sourceAccount, destinationAccount;
    private double amount;
 
    Transaction(LiveLock sourceAccount, LiveLock destinationAccount, double amount) {
        this.sourceAccount = sourceAccount;
        this.destinationAccount = destinationAccount;
        this.amount = amount;
    }
 
    public void run() {
        while (!sourceAccount.tryTransfer(destinationAccount, amount))
            continue;
        System.out.printf("%s completed ", Thread.currentThread().getName());
    }
 
}
