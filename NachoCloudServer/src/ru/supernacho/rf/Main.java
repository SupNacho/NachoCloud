package ru.supernacho.rf;

import ru.supernacho.rf.controller.CloudServer;
import ru.supernacho.rf.controller.CloudServerListener;
import ru.supernacho.rf.controller.DataBase;
import ru.supernacho.rf.controller.SimpleAuthService;

import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main implements CloudServerListener, Runnable {

    private final DataBase dataBase = new DataBase();
    private static final String START = "start";
    private static final String STOP = "stop";
    private static final String QUIT = "quit";
    private static final String DROP = "drop";
    private final CloudServer cloudServer = new CloudServer(this, new SimpleAuthService(dataBase), dataBase);

    public static void main(String[] args) {

        ExecutorService executor = Executors.newCachedThreadPool();
        executor.submit(new Main());
    }

    @Override
    public void run() {
        Scanner scanner = new Scanner(System.in);
        boolean exit = false;
        while (!exit) {
            String command = scanner.next();
            switch (command) {
                case START:
                    cloudServer.startListening(8189);
                    break;
                case STOP:
                    cloudServer.stopListening();
                    break;
                case DROP:
                    cloudServer.dropAllClients();
                    break;
                case QUIT:
                    exit = true;
                    System.exit(0);
                    break;
                default:
                    System.out.println("Unknown command");
            }
        }
    }

    @Override
    public void onLogCloudServer(CloudServer cloudServer, String msg) {
        System.out.println(msg + "\n");
    }
}
