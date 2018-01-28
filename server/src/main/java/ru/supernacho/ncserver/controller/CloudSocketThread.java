package ru.supernacho.ncserver.controller;

import ru.supernacho.nclib.MessageHeaders;
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
        sendMsg(MessageHeaders.getAuthAccept(login));
        System.out.println(login);
    }

    void authError() {
        sendMsg(MessageHeaders.getAuthError());
        close();
    }

    void reconnected() {
        isReconnected = true;
        sendMsg(MessageHeaders.getReconnect());
        close();
    }

    void messageFormatError(String msg) {
        sendMsg(MessageHeaders.getMsgFormatError(msg));
        close();
    }
}
