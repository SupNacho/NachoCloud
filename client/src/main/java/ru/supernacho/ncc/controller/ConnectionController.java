package ru.supernacho.ncc.controller;

import javafx.application.Platform;
import ru.supernacho.ncc.ClientMain;
import ru.supernacho.nclib.*;
import ru.supernacho.ncnet.SocketThread;
import ru.supernacho.ncnet.SocketThreadListener;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.List;

public class ConnectionController implements SocketThreadListener{

    private static final int PORT = 8189;
    private static final String HOST = "localhost";
    private SocketThread socketThread;
    private String login;
    private String password;
    private String newLogin;
    private String newPassword;
    private String name;
    private List<File> fileList;
    private StringBuilder errMessages = new StringBuilder();
    private boolean isRegistration;
    private ClientMain clientMain;
    private ReceivedDataController receiveDataController;


    public ConnectionController(ClientMain main) {
        this.clientMain = main;
        this.receiveDataController = new ReceivedDataController(this, clientMain);
    }

    public void connect(String login, String password) {
        try {
            this.login = login;
            this.password = password;
            Socket socket = new Socket(HOST, PORT);
            socketThread = new SocketThread(this, "socketThread", socket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void registration(String newLogin, String newPassword, String name, boolean isRegistration) {
        try {
            this.isRegistration = isRegistration;
            this.newLogin = newLogin;
            this.newPassword = newPassword;
            this.name = name;
            Socket socket = new Socket(HOST, PORT);
            socketThread = new SocketThread(this, "socketThread", socket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void disconnect() {
        if (socketThread != null) socketThread.close();
    }

    @Override
    public void onStartSocketThread(SocketThread socketThread) {
    }

    @Override
    public void onStopSocketThread(SocketThread socketThread) {
        Platform.runLater(() -> {
            errMessages.append("Connection closed");
            clientMain.showLoginPage(errMessages.toString());
            errMessages.setLength(0);
        });
    }

    public void onReadySocketThread(SocketThread socketThread, Socket socket) {
        Platform.runLater(() -> {
            if (isRegistration) {
                socketThread.sendMsg(Request.getRegistrationRequest(newLogin, newPassword, name));
                isRegistration = false;
            } else {
                String authRequest = Request.getAuthRequest(login, password);
                socketThread.sendMsg(authRequest);
            }
        });
    }

    public void sendRequest(Object request) {
        System.out.println("Client Request: " + request.toString());
        socketThread.sendMsg(request);
    }

    @Override
    public void onReceiveData(SocketThread socketThread, Socket socket, Object objectData) {
        Platform.runLater(() -> receiveDataController.parse(objectData));
    }

    @Override
    public void onExceptionSocketThread(SocketThread socketThread, Socket socket, Exception e) {
        Platform.runLater(e::printStackTrace);
    }

    public List<File> getFileList() {
        return fileList;
    }

    public void setFileList(List<File> fileList) {
        this.fileList = fileList;
    }
}
