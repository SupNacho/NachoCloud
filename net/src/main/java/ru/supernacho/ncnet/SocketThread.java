package ru.supernacho.ncnet;

import java.io.*;
import java.net.Socket;

public class SocketThread extends Thread {

    private final SocketThreadListener eventListener;
    private final Socket socket;
    private ObjectOutputStream outObjStream;

    public SocketThread(SocketThreadListener eventListener, String name, Socket socket) {
        super(name);
        this.eventListener = eventListener;
        this.socket = socket;
        start();
    }


    @Override
    public void run() {
        eventListener.onStartSocketThread(this);
        try {
            InputStream sis = socket.getInputStream();
            OutputStream sos = socket.getOutputStream();
            outObjStream = new ObjectOutputStream(sos);
            ObjectInputStream in = new ObjectInputStream(sis);
            eventListener.onReadySocketThread(this, socket);
            while(!isInterrupted()) {
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