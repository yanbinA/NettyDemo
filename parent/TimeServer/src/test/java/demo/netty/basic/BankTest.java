package demo.netty.basic;

/**
 * @author yanbin
 * @date 2017/10/29 17:09
 */
public class BankTest {

    public static final double INITIAL_BALANCE = 1000d;
    public static final double MAX_AMOUNT = 1000d;
    public static final int NACCOUNTS = 100;
    public static final int DELAY = 10;

    public static void main(String[] args) {
        Bank bank = new Bank(NACCOUNTS, INITIAL_BALANCE);

        for (int i = 0; i < NACCOUNTS; i++) {
            int from = i;
            Runnable r = () -> {
              try {
                  while(true) {
                      int to = (int) (bank.getSize() * Math.random());
                      double amount = MAX_AMOUNT * Math.random();
                      bank.transfer(from, to, amount);
                      Thread.sleep(3000);
                  }


              } catch (Exception e) {

              }
            };
            new Thread(r).start();
        }

    }

}
