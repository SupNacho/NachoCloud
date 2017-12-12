package ru.supernacho.ncserver.controller;

import ru.supernacho.nclib.FileModel;
import ru.supernacho.nclib.FileProcessor;
import ru.supernacho.nclib.MessageHeaders;
import ru.supernacho.nclib.User;
import ru.supernacho.ncnet.ServerSocketThread;
import ru.supernacho.ncnet.ServerSocketThreadListener;
import ru.supernacho.ncnet.SocketThread;
import ru.supernacho.ncnet.SocketThreadListener;
import ru.supernacho.ncserver.dbase.AuthService;
import ru.supernacho.ncserver.dbase.DataBase;
import ru.supernacho.ncserver.model.ServerFilesProcessor;

import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

public class CloudServer implements ServerSocketThreadListener, SocketThreadListener {

    private final AuthService authService;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
    private final CloudServerListener eventListener;
    private ServerSocketThread serverSocketThread;
    private final Vector<SocketThread> clients = new Vector<>();
    private DataBase dataBase;
    private User user;
    private ServerFilesProcessor serverFilesProcessor;
    private FileProcessor fileProcessor;
    private ConcurrentHashMap<CloudSocketThread, User> userMap;

    public CloudServer(CloudServerListener eventListener, AuthService authService, DataBase dataBase) {
        this.eventListener = eventListener;
        this.authService = authService;
        this.dataBase = dataBase;
        this.userMap = new ConcurrentHashMap<>();
        this.serverFilesProcessor = new ServerFilesProcessor();
        this.fileProcessor = new FileProcessor();
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
        userMap.remove(socketThread);
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
    public synchronized void onReceiveData(SocketThread socketThread, Socket socket, Object value) {

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

    private void handleAuthorizeClient(CloudSocketThread client, Object objectData) {
        user = userMap.get(client);
        serverFilesProcessor.setUser(user);
        if (objectData instanceof FileModel){
            FileModel fileModel = (FileModel) objectData;
            System.out.println("Receive file: " + fileModel.getName() + " File size: " + fileModel.getSize());
            fileProcessor.saveFile(user.getRepository(), fileModel);
            client.sendMsg(serverFilesProcessor.getFileList());
        }
        if (objectData instanceof String) {
            String msg = objectData.toString();
            String[] msgArr = msg.split(MessageHeaders.DELIMITER);
            switch (msgArr[0]) {
                case MessageHeaders.FILE_UPLOAD:
                    System.out.println("UPLOADING FILE");
                    break;
                case MessageHeaders.GET_FILE:
                    System.out.println("Downloading file " + msgArr[1]);
                    client.sendMsg(serverFilesProcessor.getFile(msgArr[1]));
                    break;
                case MessageHeaders.FILE_LIST:
                    System.out.println("Sending fileList from: " + dataBase.getUserRepository());
                    client.sendMsg(serverFilesProcessor.getFileList());
                    break;
                case MessageHeaders.FILE_DELETE:
                    System.out.println("Perform delete file: " + msgArr[1]);
                    serverFilesProcessor.deleteFile(msgArr[1]);
                    client.sendMsg(serverFilesProcessor.getFileList());
                    break;
                case MessageHeaders.GET_USER_DATA:
                    serverFilesProcessor.getFileList();
                    client.sendMsg(user);
                    break;
                default:
                    throw new RuntimeException("Unknown msg + " + msg);
            }
        }
    }

    private void handleNonAuthorizeClient(CloudSocketThread newClient, Object objectData) {
        if (objectData instanceof String) {
            String msg = objectData.toString();
            String tokens[] = msg.split(MessageHeaders.DELIMITER);
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
                    userMap.put(newClient, new User(login, dataBase.getName(), dataBase.getUserRepository()));
                } else {
                    oldClient.reconnected();
                }
            }
            if (tokens[0].equals(MessageHeaders.REGISTER)) {
                if (dataBase.checkAviablity(tokens[1])) {
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
                        userMap.put(newClient, new User(login, dataBase.getName(), dataBase.getUserRepository()));
                    } else {
                        oldClient.reconnected();
                    }
                } else {
                    newClient.sendMsg(MessageHeaders.getRegistrationError(tokens[1]));
                }
            }
        }
    }


    @Override
    public synchronized void onExceptionSocketThread(SocketThread socketThread, Socket socket, Exception e) {
        putLog("Exception: " + e.getClass().getName() + ": " + e.getMessage());
    }
}
