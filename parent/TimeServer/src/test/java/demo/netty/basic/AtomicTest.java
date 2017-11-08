package demo.netty.basic;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 原子性测试
 * @author yanbin
 * @date 2017/11/8 14:25
 */
public class AtomicTest {

    public static AtomicInteger atomic = new AtomicInteger(1);

    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {

                        int c = AtomicTest.atomic.incrementAndGet();

                        System.out.print(" " + c);
                        try {
                            Thread.sleep(3000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                }
            }).start();
        }
    }

}
