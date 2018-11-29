package Cuncurrency;

public class LockStarvation {
    private double balance;
    int id;
 
    LockStarvation(int id, double balance) {
        this.id = id;
        this.balance = balance;
    }
     
    synchronized double getBalance() {
        // Wait to simulate io like database access ...
        try {Thread.sleep(100l);} catch (InterruptedException e) {}
        return balance;
    }
     
    synchronized void withdraw(double amount) {
        balance -= amount;
        System.out.println("AAAAAA ="+balance);
    }
 
    synchronized void deposit(double amount) {
        balance += amount;
    }
 
    synchronized void transfer(LockStarvation to, double amount) {
            withdraw(amount);
            to.deposit(amount);
    }
 
    public static void main(String[] args) {
        final LockStarvation fooAccount = new LockStarvation(1, 500d);
        final LockStarvation barAccount = new LockStarvation(2, 500d);
         
        Thread balanceMonitorThread1 = new Thread(new BalanceMonitor(fooAccount), "BalanceMonitor");
        Thread transactionThread1 = new Thread(new Transaction2(fooAccount, barAccount, 250d), "Transaction-1");
        Thread transactionThread2 = new Thread(new Transaction2(fooAccount, barAccount, 249d), "Transaction-2");
         
        balanceMonitorThread1.setPriority(Thread.MAX_PRIORITY);
        transactionThread1.setPriority(Thread.MIN_PRIORITY);
        transactionThread2.setPriority(Thread.MIN_PRIORITY);
         System.out.println(balanceMonitorThread1.getPriority());
         System.out.println(transactionThread1.getPriority());
         System.out.println(transactionThread2.getPriority());
        // Start the monitor
        balanceMonitorThread1.start();
         
        // And later, transaction threads tries to execute.
        try {Thread.sleep(100l);} catch (InterruptedException e) {}
        transactionThread1.start();
        transactionThread2.start();
 
    }
 
}
class BalanceMonitor implements Runnable {
    private LockStarvation account;
    BalanceMonitor(LockStarvation account) { this.account = account;}
    boolean alreadyNotified = false;
     
    @Override
    public void run() {
        System.out.format("%s started execution%n", Thread.currentThread().getName());
        while (true) {
        	double bal = account.getBalance();
        	System.out.println("Balance="+bal);
        	
            if(bal <= 0) {
                // send email, or sms, clouds of smoke ...
            	System.out.println("Balance2="+bal);
                break;
            }
        }
        System.out.format("%s : account has gone too low, email sent, exiting.", Thread.currentThread().getName());
    }
     
}
class Transaction2 implements Runnable {
    private LockStarvation sourceAccount, destinationAccount;
    private double amount;
 
    Transaction2(LockStarvation sourceAccount, LockStarvation destinationAccount, double amount) {
        this.sourceAccount = sourceAccount;
        this.destinationAccount = destinationAccount;
        this.amount = amount;
    }
 
    public void run() {
        System.out.format("%s started execution%n", Thread.currentThread().getName());
        sourceAccount.transfer(destinationAccount, amount);
        System.out.printf("%s completed execution%n", Thread.currentThread().getName());
    }
 
}