package ru.supernacho.ncserver.controller;

import ru.supernacho.nclib.Request;
import ru.supernacho.ncnet.SocketThread;
import ru.supernacho.ncnet.SocketThreadListener;

import java.net.Socket;

public class CloudSocketThread extends SocketThread {

    private boolean isAuthorized;
    private boolean isReconnected;
    private String login;


    CloudSocketThread(SocketThreadListener eventListener, String name, Socket socket) {
        super(eventListener, name, socket);
    }

    boolean isAuthorized() {
        return isAuthorized;
    }

    boolean isReconnected() {
        return isReconnected;
    }

    String getLogin() {
        return login;
    }

    void authAccept(String login) {
        this.isAuthorized = true;
        this.login = login;
        sendMsg(Request.getAuthAccept(login));
        System.out.println(login);
    }

    void authError() {
        sendMsg(Request.getAuthError());
        close();
    }

    void reconnected() {
        isReconnected = true;
        sendMsg(Request.getReconnect());
        close();
    }

    void messageFormatError(String msg) {
        sendMsg(Request.getMsgFormatError(msg));
        close();
    }
}
