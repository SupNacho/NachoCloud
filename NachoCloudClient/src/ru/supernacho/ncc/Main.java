package ru.supernacho.ncc;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ru.supernacho.ncc.controller.MessageHeaders;
import ru.supernacho.ncc.controller.SocketThread;
import ru.supernacho.ncc.controller.SocketThreadListener;
import ru.supernacho.ncc.model.RemoteStorage;
import ru.supernacho.ncc.view.BrowseLayoutController;
import ru.supernacho.ncc.view.LoginLayoutController;
import ru.supernacho.ncc.view.RegistrationDialogController;
import ru.supernacho.ncc.view.RootLayoutController;
import sun.net.www.content.text.PlainTextInputStream;

import java.io.IOException;
import java.net.Socket;
import java.time.LocalDate;
import java.util.Arrays;

public class Main extends Application implements SocketThreadListener {

    private Stage primaryStage;
    private BorderPane rootLayout;
    private SocketThread socketThread;
    private String login;
    private String password;
    private String newLogin;
    private String newPassword;
    private String name;
    private StringBuilder errMessages = new StringBuilder();
    private boolean isRegistration;

    private ObservableList<RemoteStorage> remoteStorage = FXCollections.observableArrayList();

    public Main() {
        remoteStorage.add(new RemoteStorage("file1.omg", 5416l, LocalDate.now()));
        remoteStorage.add(new RemoteStorage("file2.omg", 54456l, LocalDate.now()));
        remoteStorage.add(new RemoteStorage("file5.omg", 5446l, LocalDate.now()));
        remoteStorage.add(new RemoteStorage("file32.omg", 546l, LocalDate.now()));
        remoteStorage.add(new RemoteStorage("file6.omg", 54634l, LocalDate.now()));
        remoteStorage.add(new RemoteStorage("file12.omg", 5346l, LocalDate.now()));
        remoteStorage.add(new RemoteStorage("file3.omg", 54526l, LocalDate.now()));
        remoteStorage.add(new RemoteStorage("file8.omg", 5462l, LocalDate.now()));
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("NachoCloud");

        initRootLayout();

        showLoginPage(null);
    }

    public void initRootLayout() {
        try {

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("view/rootLayout.fxml"));
            rootLayout = (BorderPane) loader.load();

            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);
            RootLayoutController controller = loader.getController();
            controller.setMain(this);
            primaryStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showLoginPage(String message) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("view/loginPage.fxml"));
            AnchorPane loginPage = (AnchorPane) loader.load();

            rootLayout.setCenter(loginPage);

            LoginLayoutController controller = loader.getController();
            controller.setMain(this);
            if (message != null) controller.setErrMessage(message);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showBrowsePage() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("view/browseLayout.fxml"));
            AnchorPane browsePage = (AnchorPane) loader.load();

            rootLayout.setCenter(browsePage);
            BrowseLayoutController controller = loader.getController();
            controller.setMain(this);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean showRegisterDialog() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("view/RegistrationDialog.fxml"));
            AnchorPane page = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Registration");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            RegistrationDialogController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setMain(this);
            dialogStage.showAndWait();
            return controller.isOkClicked();

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public ObservableList<RemoteStorage> getRemoteStorage() {
        return remoteStorage;
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public void connect(String login, String password) {
        try {
            this.login = login;
            this.password = password;
            Socket socket = new Socket("localhost", 8189);
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
            Socket socket = new Socket("localhost", 8189);
            socketThread = new SocketThread(this, "socketThread", socket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void disconect() {
        socketThread.close();
    }

    @Override
    public void onStartSocketThread(SocketThread socketThread) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                System.out.println("Socket Start");
            }
        });
    }

    @Override
    public void onStopSocketThread(SocketThread socketThread) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                System.out.println("Connection closed.");
                errMessages.append("Connection closed");
                showLoginPage(errMessages.toString());
                errMessages.setLength(0);
            }
        });
    }

    //todo сделать ключ на проверку логин или регистрация и в обработке ниже сделать иф с выбором сообщения на сервер:
    @Override
    public void onReadySocketThread(SocketThread socketThread, Socket socket) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                if (isRegistration) {
                    System.out.println("Start REGISTRATION process");
                    socketThread.sendMsg(MessageHeaders.getRegistrationRequest(newLogin, newPassword, name));
                    isRegistration = false;
                } else {
                    System.out.println("Start AUTH process");
                    socketThread.sendMsg(MessageHeaders.getAuthRequest(login, password));
                }
            }
        });
    }

    public void sendRequest(String request) {
//        StringBuilder sb = new StringBuilder();
//        switch (request){
//            case MessageHeaders.REGISTER:
//                sb.append(request);
//                break;
//            case MessageHeaders.FILE_LIST_REFRESH:
//                sb.append(MessageHeaders.FILE_LIST_REFRESH);
//                break;
//            case MessageHeaders.FILE_LIST:
//                sb.append(MessageHeaders.FILE_LIST);
//                break;
//            case MessageHeaders.FILE_UPLOAD:
//                sb.append(MessageHeaders.FILE_UPLOAD);
//                break;
//            case MessageHeaders.FILE_DOWNLOAD:
//                sb.append(MessageHeaders.FILE_DOWNLOAD);
//                break;
//            default:
//                throw new RuntimeException("Unknown Request for sending: " + request);
//        }
        System.out.println(request);
        socketThread.sendMsg(request);
    }

    @Override
    public void onReceiveString(SocketThread socketThread, Socket socket, String value) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                String tokens[] = value.split(MessageHeaders.DELIMITER);
                switch (tokens[0]) {
                    case MessageHeaders.REGISTER_ERROR:
                        String err = "Reg err: " + tokens[1];
                        System.out.println(err);
                        errMessages.append(err);
                        break;
                    case MessageHeaders.FILE_LIST:
                        int msgHeaderCut = MessageHeaders.FILE_LIST.length() + MessageHeaders.DELIMITER.length();
                        String files[] = value.substring(msgHeaderCut).split(MessageHeaders.DELIMITER);
                        Arrays.sort(files);
                        for (String file : files) {
                            remoteStorage.add(new RemoteStorage(file, 0L, LocalDate.now()));
                        }
//                        remoteStorage.addAll(users);
                        break;
                    case MessageHeaders.AUTH_ACCEPT:
                        System.out.println(" - [ Успешная авторизация ] -> " + tokens[1] + "\n");
                        showBrowsePage();
                        sendRequest(MessageHeaders.FILE_LIST_REFRESH);
                        break;
                    case MessageHeaders.AUTH_ERROR:
                        String msg = "Ошибка авторизации ";
                        System.out.println(msg);
                        errMessages.append(msg);
                        break;
                    case MessageHeaders.MSG_FORMAT_ERROR:
                        System.out.println("- [Ошибка формата сообщения] - > " + tokens[0] + tokens[1] + "\n");
                        break;
                    case MessageHeaders.RECONNECT:
                        System.out.println(" - [Переподключение с другого устройства] \n");
                        break;
                    default:
                        throw new RuntimeException("Неизвестный заголовок сообщения: " + value);
                }
            }
        });
    }

    @Override
    public void onExceptionSocketThread(SocketThread socketThread, Socket socket, Exception e) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                e.printStackTrace();
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
