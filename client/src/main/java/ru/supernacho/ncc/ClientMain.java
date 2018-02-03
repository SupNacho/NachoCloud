package ru.supernacho.ncc;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ru.supernacho.ncc.controller.ConnectionController;
import ru.supernacho.ncc.viewController.*;
import ru.supernacho.nclib.*;
import ru.supernacho.ncc.model.RemoteStorage;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class ClientMain extends Application {

    private Stage primaryStage;
    private BorderPane rootLayout;
    private ClientFileInterface fileProcessor = new FileProcessor();
    private ObservableList<RemoteStorage> remoteStorage = FXCollections.observableArrayList();
    private String saveFileLocation;
    private ConnectionController connection = new ConnectionController(this);

    public ClientMain() {
    }

    @Override
    public void start(Stage primaryStage){
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("NachoCloud");
        primaryStage.setOnCloseRequest(e -> {
            connection.disconnect();
            Platform.exit();
        });
        initRootLayout();
        showLoginPage(null);
    }

    private void initRootLayout() {
        try {

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(ClientMain.class.getResource("/rootLayout.fxml"));
            rootLayout = loader.load();

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
            AnchorPane loginPage =  loader.load();

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
            AnchorPane browsePage = loader.load();

            rootLayout.setCenter(browsePage);
            rootLayout.setOnDragOver(event -> {
                Dragboard dragboard = event.getDragboard();
                if (dragboard.hasFiles()){
                    event.acceptTransferModes(TransferMode.COPY);
                } else {
                    event.consume();
                }
            });
            rootLayout.setOnDragDropped(event -> {
                Dragboard dragboard = event.getDragboard();
                boolean succes = false;
                if (dragboard.hasFiles()){
                    succes = true;
                    for (File file: dragboard.getFiles()){
                        FileModel fileModel = fileProcessor.uploadFile(file);
                        if (checkFileOverwrite(fileModel)) {
                            connection.sendRequest(fileModel);
                            System.out.println("File: " + file.getName() + " File size: " + file.length() + "File path: " + file.getAbsolutePath());
                        }
                    }
                }
                event.setDropCompleted(succes);
                event.consume();
            });
            BrowseLayoutController controller = loader.getController();
            controller.setClientMain(this);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean checkFileOverwrite(FileModel fileModel){
        List<File> fileList = connection.getFileList();
        for (File file : fileList) {
            if (fileModel.getName().equals(file.getName())){
                String newName = showOverwriteDialog(fileModel.getName());
                if (newName.equals("drop")) return false;
                fileModel.setFile(new File(newName));
                connection.sendRequest(fileModel);
                System.out.println("File: " + file.getName() + " File size: " + file.length() + "File path: " + file.getAbsolutePath());
                return false;
            }
        }
        return true;
    }

    private String showOverwriteDialog(String fileName){
        try{
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(ClientMain.class.getResource("/overWriteLayout.fxml"));
            AnchorPane page = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("File " + fileName + " exists on server...");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            OverWriteViewController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setClientMain(this);
            controller.setOldFileName(fileName);
            dialogStage.showAndWait();
            return controller.getNewFileName();
        } catch (IOException e){
            e.printStackTrace();
        }
        return fileName;
    }

    public void showRegisterDialog() {
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

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ObservableList<RemoteStorage> getRemoteStorage() {
        return remoteStorage;
    }

    public ClientFileInterface getFileProcessor() {
        return fileProcessor;
    }

    public void setSaveFileLocation(String saveFileLocation) {
        this.saveFileLocation = saveFileLocation;
    }

    public String getSaveFileLocation() {
        return saveFileLocation;
    }



    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public ConnectionController getConnection() {
        return connection;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
