package ru.supernacho.rf.controller;

import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Vector;

public class CloudServer implements ServerSocketThreadListener, SocketThreadListener {

    private final AuthService authService;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
    private final CloudServerListener eventListener;
    private ServerSocketThread serverSocketThread;
    private final Vector<SocketThread> clients = new Vector<>();
    private DataBase dataBase;

    public CloudServer(CloudServerListener eventListener, AuthService authService, DataBase dataBase) {
        this.eventListener = eventListener;
        this.authService = authService;
        this.dataBase = dataBase;
    }

    public void startListening(int port) {
        if(serverSocketThread != null && serverSocketThread.isAlive()) {
            putLog("Поток сервера уже запущен.");
            return;
        }
        serverSocketThread = new ServerSocketThread(this, "ServerSocketThread", port, 2000);
        authService.start();
    }

    public synchronized void dropAllClients() {
        for (int i = 0; i < clients.size(); i++) {
            clients.get(i).close();
        }
        putLog("dropAllClients");
    }

    public void stopListening() {
        if(serverSocketThread == null || !serverSocketThread.isAlive()) {
            putLog("Поток сервера не запущен.");
            return;
        }
        serverSocketThread.interrupt();
    }

    private synchronized void putLog(String msg) {
        String msgLog = dateFormat.format(System.currentTimeMillis());
        msgLog += Thread.currentThread().getName() + ": " + msg;
        eventListener.onLogCloudServer(this, msgLog);
    }


    @Override
    public void onStartServerSocketThread(ServerSocketThread thread) {
        putLog("started...");
    }

    @Override
    public void onStopServerSocketThread(ServerSocketThread thread) {
        putLog("stopped.");
    }

    @Override
    public void onReadyServerSocketThread(ServerSocketThread thread, ServerSocket serverSocket) {
        putLog("ServerSocket is ready...");
    }

    @Override
    public void onTimeOutAccept(ServerSocketThread thread, ServerSocket serverSocket) {
        putLog("accept() timeout");
    }

    @Override
    public void onAcceptedSocket(ServerSocketThread thread, ServerSocket serverSocket, Socket socket) {
        putLog("Client connected: " + socket);
        String threadName = "Socket thread: " + socket.getInetAddress() + ":" + socket.getPort();
        new CloudSocketThread(this, threadName, socket);
    }

    @Override
    public void onExceptionServerSocketThread(ServerSocketThread thread, Exception e) {
        putLog("Exception: " + e.getClass().getName() + ": " + e.getMessage());
    }


    @Override
    public synchronized void onStartSocketThread(SocketThread socketThread) {
        putLog("started...");
    }

    @Override
    public synchronized void onStopSocketThread(SocketThread socketThread) {
        putLog("stopped.");
        clients.remove(socketThread);
        CloudSocketThread client = (CloudSocketThread) socketThread;
    }

    private CloudSocketThread getClientByNickname(String nickname) {
        final  int cnt = clients.size();
        for (int i = 0; i < cnt; i++) {
            CloudSocketThread client = (CloudSocketThread) clients.get(i);
            if (!client.isAuthorized()) continue;
            if (client.getLogin().equals(nickname)) return client;
        }
        return null;
    }

    @Override
    public synchronized void onReadySocketThread(SocketThread socketThread, Socket socket) {
        putLog("Socket is ready.");
        clients.add(socketThread);
    }

    @Override
    public synchronized void onReceiveString(SocketThread socketThread, Socket socket, String value) {

        CloudSocketThread client = (CloudSocketThread) socketThread;

        if ( client.isAuthorized()) {
            handleAuthorizeClient(client, value);
        } else {
            handleNonAuthorizeClient(client, value);
        }
    }

    private void sendToAllAuthorizedClients(String msg) {
        for (int i = 0; i < clients.size(); i++) {
            CloudSocketThread client = (CloudSocketThread) clients.get(i);
            if(client.isAuthorized()) client.sendMsg(msg);
        }
    }

    private void handleAuthorizeClient(CloudSocketThread client, String msg) {
        String[] msgArr = msg.split(MessageHeaders.DELIMITER);
        switch (msgArr[0]){
            case MessageHeaders.FILE_UPLOAD:
                System.out.println("UPLOADING FILE");
                break;
            case MessageHeaders.FILE_DOWNLOAD:
                System.out.println("Downloading file");
                break;
            case MessageHeaders.FILE_LIST:
                System.out.println("Sending fileList from: " + dataBase.getUserRepository());
                break;
            case MessageHeaders.FILE_LIST_REFRESH:
                client.sendMsg(MessageHeaders.FILE_LIST + MessageHeaders.DELIMITER + dataBase.getUserRepository() + MessageHeaders.DELIMITER);
                break;
            default:
                throw new RuntimeException("Unknown msg + " + msg);
        }
    }

    private void handleNonAuthorizeClient(CloudSocketThread newClient, String msg) {
        String tokens[] = msg.split(MessageHeaders.DELIMITER);
//        if((tokens.length != 3 || !tokens[0].equals(MessageHeaders.AUTH_REQUEST)) || !tokens[0].equals(MessageHeaders.REGISTER)){
//            newClient.messageFormatError(msg);
//            return;
//        }
        if (tokens[0].equals(MessageHeaders.AUTH_REQUEST)) {
            String login = tokens[1];
            String password = tokens[2];
            String nickname = authService.getRepository(login, password);
            if (nickname == null) {
                newClient.authError();
                return;
            }

            CloudSocketThread oldClient = getClientByNickname(nickname);
            newClient.authAccept(nickname);
            if (oldClient == null) {
                System.out.println(("Server " + newClient.getLogin() + " connected. Auth Block"));

            } else {
                oldClient.reconnected();
            }
        }
        if (tokens[0].equals(MessageHeaders.REGISTER)){
            if(dataBase.checkAviablity(tokens[1])){
                dataBase.registerUser(tokens[1], tokens[2], tokens[3]);
                String login = tokens[1];
                String password = tokens[2];
                String nickname = authService.getRepository(login, password);
                if (nickname == null) {
                    newClient.authError();
                    return;
                }

                CloudSocketThread oldClient = getClientByNickname(nickname);
                newClient.authAccept(nickname);
                if (oldClient == null) {
                    System.out.println(("Server " + newClient.getLogin() + " connected."));
                } else {
                    oldClient.reconnected();
                }
            } else {
                newClient.sendMsg(MessageHeaders.getRegistrationError(tokens[1]));
            }
        }
    }


    @Override
    public synchronized void onExceptionSocketThread(SocketThread socketThread, Socket socket, Exception e) {
        putLog("Exception: " + e.getClass().getName() + ": " + e.getMessage());
    }
}
