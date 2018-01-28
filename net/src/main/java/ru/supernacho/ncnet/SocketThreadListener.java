package ru.supernacho.ncnet;

import java.net.Socket;

public interface SocketThreadListener {

    void onStartSocketThread(SocketThread socketThread);
    void onStopSocketThread(SocketThread socketThread);

    void onReadySocketThread(SocketThread socketThread, Socket socket);
    void onReceiveData(SocketThread socketThread, Socket socket, Object value);

    void onExceptionSocketThread(SocketThread socketThread, Socket socket, Exception e);
}
