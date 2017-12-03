package ru.supernacho.ncc;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ru.supernacho.nclib.FileModel;
import ru.supernacho.nclib.FileProcessor;
import ru.supernacho.nclib.MessageHeaders;
import ru.supernacho.nclib.User;
import ru.supernacho.ncnet.SocketThread;
import ru.supernacho.ncnet.SocketThreadListener;
import ru.supernacho.ncc.model.RemoteStorage;
import ru.supernacho.ncc.view.BrowseLayoutController;
import ru.supernacho.ncc.view.LoginLayoutController;
import ru.supernacho.ncc.view.RegistrationDialogController;
import ru.supernacho.ncc.view.RootLayoutController;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;

public class ClientMain extends Application implements SocketThreadListener {

    private Stage primaryStage;
    private BorderPane rootLayout;
    private SocketThread socketThread;
    private String login;
    private String password;
    private String newLogin;
    private String newPassword;
    private String name;
    private User user;
    private List<File> fileList;
    private StringBuilder errMessages = new StringBuilder();
    private boolean isRegistration;
    private FileProcessor fileProcessor = new FileProcessor();

    private ObservableList<RemoteStorage> remoteStorage = FXCollections.observableArrayList();
    private String saveFileLocation;

    public ClientMain() {
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
            loader.setLocation(ClientMain.class.getResource("/rootLayout.fxml"));
            rootLayout = (BorderPane) loader.load();

            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);
            RootLayoutController controller = loader.getController();
            controller.setClientMain(this);
            primaryStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showLoginPage(String message) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(ClientMain.class.getResource("/loginPage.fxml"));
            AnchorPane loginPage = (AnchorPane) loader.load();

            rootLayout.setCenter(loginPage);

            LoginLayoutController controller = loader.getController();
            controller.setClientMain(this);
            if (message != null) controller.setErrMessage(message);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showBrowsePage() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(ClientMain.class.getResource("/browseLayout.fxml"));
            AnchorPane browsePage = (AnchorPane) loader.load();

            rootLayout.setCenter(browsePage);
            rootLayout.setOnDragOver(new EventHandler<DragEvent>() {
                @Override
                public void handle(DragEvent event) {
                    Dragboard dragboard = event.getDragboard();
                    if (dragboard.hasFiles()){
                        event.acceptTransferModes(TransferMode.COPY);
                    } else {
                        event.consume();
                    }
                }
            });
            rootLayout.setOnDragDropped(new EventHandler<DragEvent>() {
                @Override
                public void handle(DragEvent event) {
                    Dragboard dragboard = event.getDragboard();
                    boolean succes = false;
                    if (dragboard.hasFiles()){
                        succes = true;
                        for (File file: dragboard.getFiles()){
                            FileModel fileModel = fileProcessor.uploadFile(file);
                            sendRequest(fileModel);
                            System.out.println("File: " + file.getName() + " File size: " + file.length() + "File path: " + file.getAbsolutePath());
                        }
                    }
                    event.setDropCompleted(succes);
                    event.consume();
                }
            });
            BrowseLayoutController controller = loader.getController();
            controller.setClientMain(this);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean showRegisterDialog() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(ClientMain.class.getResource("/RegistrationDialog.fxml"));
            AnchorPane page = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Registration");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            RegistrationDialogController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setClientMain(this);
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

    public FileProcessor getFileProcessor() {
        return fileProcessor;
    }

    public void setSaveFileLocation(String saveFileLocation) {
        this.saveFileLocation = saveFileLocation;
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
                    String authRequest = MessageHeaders.getAuthRequest(login, password);
                    socketThread.sendMsg(authRequest);
                }
            }
        });
    }

    public void sendRequest(Object request) {
        System.out.println("Client Request: " + request.toString());
        socketThread.sendMsg(request);
    }

    @Override
    public void onReceiveData(SocketThread socketThread, Socket socket, Object objectData) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                if (objectData instanceof FileModel){
                    FileModel fileModel = (FileModel) objectData;
                    fileProcessor.saveFile(saveFileLocation, fileModel);
                    System.out.println("Received file " + fileModel.getName());
                }

                if (objectData instanceof List){
                    remoteStorage.clear();
                    fileList = (List<File>) objectData;
                    user.setFileList(fileList);
                    for (File file : fileList) {
                        System.out.println("File: " + file.getName());
                    }
                    System.out.println("Files qantity: " + fileList.size());
                    for (File file : fileList) {
                        remoteStorage.add(new RemoteStorage(file.getName(), file.length(), file.lastModified()));
                    }
                }
                if (objectData instanceof User){
                    user = (User) objectData;
                    System.out.println("User data: " + user.toString());
                    for (File file : user.getFileList()) {
                        System.out.println("File: " + file.getName());
                    }
                    System.out.println("Files qaty: " + user.getFileList().size());
                    for (File file : user.getFileList()) {
                        remoteStorage.add(new RemoteStorage(file.getName(), file.length(), file.lastModified()));
                    }
                }

                if (objectData instanceof String) {
                    String value = objectData.toString();
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
//                            for (String file : files) {
//                                remoteStorage.add(new RemoteStorage(file, 0L, LocalDate.now()));
//                            }
                            break;
                        case MessageHeaders.AUTH_ACCEPT:
                            System.out.println(" - [ Успешная авторизация ] -> " + tokens[1] + "\n");
                            showBrowsePage();
                            sendRequest(MessageHeaders.GET_USER_DATA);
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