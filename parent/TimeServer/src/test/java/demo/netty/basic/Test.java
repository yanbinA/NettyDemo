package demo.netty.basic;

import java.io.File;
import java.io.FileInputStream;

/**
 * @author yanbin
 * @date 2017/10/29 14:21
 */
public class Test {

    public static void main(String[] args) {



        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                String num = "111";

                Integer integer = Integer.valueOf(num);

                File file = new File("sadasd");
                try {

                    FileInputStream fileInputStream = new FileInputStream(file);
                } catch (Exception e) {
                    throw new RuntimeException("找不到文件", e);
                }

                int a = 10 / 0;
            }
        });



/*        thread1.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                System.out.println("异常捕获器中处理异常");
                e.printStackTrace();
            }
        });*/
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                System.out.println("默认异常捕获器中处理异常\n" + e.getMessage());
                e.printStackTrace();
            }
        });
        thread1.start();

    }

}
