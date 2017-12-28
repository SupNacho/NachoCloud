package com.nicolas.lesson3;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class Main {

    static class Timer {
        static long t;

        static void start() {
            t = System.currentTimeMillis();
        }

        static void stopAndPrint() {
            System.out.println("time: " + (System.currentTimeMillis() - t));
        }
    }

    public static void main(String[] args) throws Exception {
        Cat cat = new Cat("Barsik");
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("cat.ser"));

        Ball ball = new Ball();
        cat.setBall(ball);

        oos.writeObject(cat);
        oos.close();

        ObjectInputStream ois = new ObjectInputStream(new FileInputStream("cat.ser"));
        Cat cat2 = (Cat) ois.readObject();
        ois.close();

        cat2.info();
        System.out.println(cat2.getBall() == null);

    }

    public static void main10(String[] args) throws Exception{
        RandomAccessFile raf = new RandomAccessFile("1.txt", "rw");
        raf.seek(5);

        //System.out.println((char) raf.read());
        raf.write(65);
        raf.close();
    }

    public static void main9(String[] args) throws Exception {
        ArrayList<FileInputStream> ali = new ArrayList<>();
        ali.add(new FileInputStream("a.txt"));
        ali.add(new FileInputStream("b.txt"));
        ali.add(new FileInputStream("c.txt"));

        SequenceInputStream sin = new SequenceInputStream(Collections.enumeration(ali));

        int x;
        while ((x = sin.read()) != -1) {
            System.out.print((char) x);
        }

        sin.close();

    }

    public static void main8(String[] args) throws Exception {
        BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream("1.txt"), StandardCharsets.UTF_8)
        );
    }

    public static void main7(String[] args) throws Exception {
        PipedInputStream in = new PipedInputStream();
        PipedOutputStream out = new PipedOutputStream();

        in.connect(out);

        out.write(10);
        out.write(20);

        System.out.println(in.read());
        System.out.println(in.read());
    }

    public static void main6(String[] args) throws Exception {
        byte[] b = {1, 2, 3, 4, 5};

        ByteArrayInputStream in = new ByteArrayInputStream(b);
        int x;
        while ((x = in.read()) != -1) {
            System.out.print(x + " ");
        }

        in.close();

        System.out.println();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        for (int i = 0; i < 100; i++) {
            out.write(i);
        }

        out.close();
        System.out.println(Arrays.toString(out.toByteArray()));

        CharArrayWriter caw = new CharArrayWriter();
        caw.write(65);
        char[] q = caw.toCharArray();
        System.out.println(q[0]);

    }

    public static void main5(String[] args) throws Exception {
        long a = 1000_000_000L;
        DataOutputStream out = new DataOutputStream(
                new BufferedOutputStream(new FileOutputStream("1.txt"))
        );

        out.writeLong(a);
        out.close();

        DataInputStream in = new DataInputStream(new FileInputStream("1.txt"));
        //System.out.println(in.readLong());
        System.out.println(in.readShort());
        System.out.println(in.readShort());
        System.out.println(in.readShort());
        System.out.println(in.readShort());

        in.close();
    }

    public static void main4(String[] args) throws Exception {

        BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream("2.txt"));
        out.write(65);

        //out.flush();
        out.close();
    }

    public static void main3(String[] args) throws Exception {
        Timer.start();

        FileInputStream in = new FileInputStream("str.txt");
        StringBuilder sb = new StringBuilder("");
        int x;
        while ((x = in.read()) != -1) {
            sb.append((char) x);
        }

        in.close();

        Timer.stopAndPrint();

        Timer.start();
        BufferedInputStream bin = new BufferedInputStream(new FileInputStream("str.txt"));
        sb = new StringBuilder("");

        while ((x = bin.read()) != -1) {
            sb.append((char) x);
        }

        bin.close();

        Timer.stopAndPrint();


        Timer.start();

        in = new FileInputStream("str.txt");
        byte[] bytes = new byte[in.available()];

        in.read(bytes);

        String aaa = new String(bytes);

//        sb = new StringBuilder("");
//
//        for (byte bt: bytes) {
//            sb.append(bt);
//        }
//        in.close();

        Timer.stopAndPrint();
    }

    public static void main2(String[] args) throws Exception {

        FileOutputStream out = new FileOutputStream("1.txt");

        for (int i = 0; i < 5; i++) {
            System.out.print(65 + i);
            out.write(65 + i);
        }

        out.close();

        System.out.println();

        FileInputStream in = new FileInputStream("1.txt");
        int x;
        while ((x = in.read()) != -1) {
            System.out.print((char) x);
        }

        in.close();
    }

    public static void main1(String[] args) throws Exception {
        File f = new File("1");

        String[] fileNames = f.list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                if (name.startsWith("1") && name.endsWith(".txt")) return true;
                return false;
            }
        });

        for (String fn : fileNames) {
            System.out.println(fn);
        }

    }


}
