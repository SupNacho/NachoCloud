package ru.supernacho.ncnet;

import java.io.*;
import java.net.Socket;

public class SocketThread extends Thread {

    private final SocketThreadListener eventListener;
    private final Socket socket;
    private ObjectOutputStream outObjStream;
    private InputStream sis;
    private OutputStream sos;

    public SocketThread(SocketThreadListener eventListener, String name, Socket socket) {
        super(name);
        this.eventListener = eventListener;
        this.socket = socket;
        start();
    }


    @Override
    public void run() {
        eventListener.onStartSocketThread(this);
        System.out.println("1 - ST run");
        try {
            sis = socket.getInputStream();
            sos = socket.getOutputStream();
            System.out.println("2 - Socket IOstreams");
            outObjStream = new ObjectOutputStream(sos);
            ObjectInputStream in = new ObjectInputStream(sis);
            System.out.println("3 - oiStream set");
            eventListener.onReadySocketThread(this, socket);
            System.out.println("5 - eList on ready set");
            while(!isInterrupted()) {
                System.out.println("6 - in.READ");
                Object dataMsg = in.readObject();
                System.out.println(dataMsg.toString());
                eventListener.onReceiveData(this, socket, dataMsg);
            }
        } catch (IOException e){
            eventListener.onExceptionSocketThread(this, socket, e);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                eventListener.onExceptionSocketThread(this, socket, e);
            }
            eventListener.onStopSocketThread(this);
        }
    }

    public synchronized void sendMsg(Object msg) {
        try {
            outObjStream.writeObject(msg);
            outObjStream.flush();
        } catch (IOException e) {
            eventListener.onExceptionSocketThread(this, socket, e);
            close();
        }
    }

    public synchronized void close() {
        interrupt();
        try {
            socket.close();
        } catch (IOException e) {
            eventListener.onExceptionSocketThread(this, socket, e);
        }
    }
}