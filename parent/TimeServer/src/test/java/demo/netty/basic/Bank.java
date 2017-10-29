package demo.netty.basic;


import java.util.Arrays;

/**
 * @author yanbin
 * @date 2017/10/29 16:32
 */
public class Bank {

    private double[] accounts;

    private final int size;

    private final static int DEFAULT_SIZE = 50;

    public Bank() {
        this(DEFAULT_SIZE);
    }

    public Bank(int size) {
        this(size, 100.0d);
    }

    public Bank(int size, double amount) {
        if (size <= 0) {
            this.size = DEFAULT_SIZE;
        } else {
            this.size = size;
        }

        this.accounts = new double[this.size];
        Arrays.fill(this.accounts, amount);
    }

    public double[] getAccounts() {
        return accounts;
    }

    public void setAccounts(double[] accounts) {
        this.accounts = accounts;
    }

    public double get(int index) {
        if (index >= size) {
            return -1;
        }
        return accounts[index];
    }

    public int getSize() {
        return this.size;
    }

    public double getTotalBalances() {
        double sum = 0;
        for (double account : this.accounts) {
            sum += account;
        }
        return sum;
    }

    public void transfer(int from, int to, double amount) {
        if (this.accounts[from] < amount) {
            return;
        }
        System.out.print(Thread.currentThread());
        this.accounts[from] -= amount;
        System.out.printf(" %10.2f from %d to %d", amount, from, to);
        this.accounts[to] += amount;
        System.out.printf(" Total Balance : %10.2f%n", this.getTotalBalances());
    }
}
