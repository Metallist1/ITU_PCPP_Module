package exercise61;

import java.util.Random;

import benchmarking.Benchmark;

import static benchmarking.Benchmark.Mark7;

public class AccountExperiments {
    static final int N = 10; // Number of accounts
    static final int NO_TRANSACTION = 5;

    static final Account[] accounts = new Account[N];
    static final Random rnd = new Random();

    public static void main(String[] args) {
        new AccountExperiments();
    }

    public AccountExperiments() {
        // Create empty accounts
        for (int i = 0; i < N; i++) {
            accounts[i] = new Account(i);
        }
        //insert code using Mark7 to measure execution time
        Mark7("NTrans", AccountExperiments::doNTransactions);
    }

    private static double doNTransactions(int noTransactions) {
        for (int i = 0; i < noTransactions; i++) {
            long amount = rnd.nextInt(5000) + 100; // Just a random possitive amount
            int source = rnd.nextInt(N);
            int target = (source + rnd.nextInt(N - 2) + 1) % N; // make sure target <> source
            doTransaction(new Transaction(amount, accounts[source], accounts[target]));
        }
        return 0.0;
    }

    private static void doTransaction(Transaction t) {
        t.transfer();
    }

    static class Transaction {
        final Account source, target;
        final long amount;

        Transaction(long amount, Account source, Account target) {
            this.amount = amount;
            this.source = source;
            this.target = target;
        }

        public void transfer() {
            source.withdraw(amount);
            try {
                Thread.sleep(50);
            } catch (Exception e) {
                System.out.println(e);
            }
            ; // Simulate transaction time
            target.deposit(amount);
        }

        public String toString() {
            return "Transfer " + amount + " from " + source.id + " to " + target.id;
        }
    }

    static class Account {
        // should have transaction history, owners, account-type, and 100 other real things
        public final int id;
        private long balance = 0;

        Account(int id) {
            this.id = id;
        }

        public void deposit(long sum) {
            balance += sum;
        }

        public void withdraw(long sum) {
            balance -= sum;
        }

        public long getBalance() {
            return balance;
        }
    }

}
