package org.apache.rocketmq.example.quickstart.my;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.OpenOption;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.TimeUnit;

public class TestWriteByNIO {

    static int length = 100000000;

    public static void main(String[] args) throws InterruptedException, IOException {

//        mapWrite();
        channelWrite();

    }

    public static void mapWrite() throws InterruptedException {
        long start = System.currentTimeMillis();

        String filePath = "/Users/shijiapeng/store/a.txt";
        try (FileChannel channel = FileChannel.open(Paths.get(filePath),
                StandardOpenOption.READ, StandardOpenOption.WRITE)) {
            MappedByteBuffer map = channel.map(FileChannel.MapMode.READ_WRITE, 0, length);

            for (int i = 0; i < length; i++) {
                map.put((byte)0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("Elapsed Time:" + (System.currentTimeMillis() - start));
        TimeUnit.SECONDS.sleep(10);
        System.out.println("MapWrite finished");
    }



    public static ByteBuffer channelWrite() throws InterruptedException, IOException {
        long start = System.currentTimeMillis();


        ByteBuffer buf = ByteBuffer.allocateDirect(length);
        for (int i = 0; i < length; i++) {
            buf.put((byte)0);
        }
        buf.flip();

        long start1 = System.currentTimeMillis();
        System.out.println("buf prepared time:" + (start1 - start));




        String filePath = "/Users/shijiapeng/store/a.txt";
        RandomAccessFile aFile = new RandomAccessFile(filePath, "rw");
        FileChannel channel = aFile.getChannel();

        while (buf.hasRemaining()) {
            channel.write(buf);
        }
        long start2 = System.currentTimeMillis();
        System.out.println("channel write time:" + (start2 - start1));



        System.out.println("Elapsed Time:" + (System.currentTimeMillis() - start));
        TimeUnit.SECONDS.sleep(10);
        System.out.println("MapWrite finished");
        channel.force(true);

        return buf;
    }
}
