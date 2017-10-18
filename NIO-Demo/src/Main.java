
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;

public class Main {

    public static void main(String[] args) throws Exception{

        Charset charset = Charset.forName("UTF-8");

        CharsetDecoder decoder = charset.newDecoder();
        CharsetEncoder encoder = charset.newEncoder();

        RandomAccessFile accessFile = new RandomAccessFile("C:\\Users\\Administrator\\Desktop\\new 1.txt", "rw");
        RandomAccessFile accessFile2 = new RandomAccessFile("C:\\Users\\Administrator\\Desktop\\new2.txt", "rw");

        FileChannel channel = accessFile.getChannel();

        FileChannel writeChannel = accessFile2.getChannel();

        ByteBuffer buffer = ByteBuffer.allocate(48);

        CharBuffer cb = CharBuffer.allocate(48);

        int bytesRead = channel.read(buffer);

        while (bytesRead != -1) {
            System.out.println("Read:" + bytesRead);
            //改变成读模式
            buffer.flip();
            writeChannel.write(buffer);
            //将buffer改变成写模式
            cb.clear();
            buffer.clear();
            bytesRead = channel.read(buffer);
        }
        accessFile.close();
        accessFile2.close();

    }
}
