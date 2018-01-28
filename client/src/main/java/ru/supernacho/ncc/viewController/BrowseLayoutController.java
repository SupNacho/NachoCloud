package ru.supernacho.ncc.viewController;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import ru.supernacho.ncc.ClientMain;
import ru.supernacho.nclib.FileModel;
import ru.supernacho.nclib.FileProcessor;
import ru.supernacho.nclib.MessageHeaders;
import ru.supernacho.ncc.model.RemoteStorage;

import java.io.File;
import java.io.IOException;


public class BrowseLayoutController {
    @FXML
    private TableView<RemoteStorage> remoteStorageTable;

    @FXML
    private TableColumn<RemoteStorage, String> fileNameColumn;

    @FXML
    private TableColumn<RemoteStorage, Long> fileSizeColumn;

    @FXML
    private TableColumn<RemoteStorage, String> fileDateColumn;

    @FXML
    private Button buttonAdd;
    @FXML
    private Button buttonDownLoad;
    @FXML
    private Button buttonDel;
    @FXML
    private Button buttonRefresh;

    private ClientMain clientMain;

    public BrowseLayoutController() {

    }

    @FXML
    private void initialize() {
        fileNameColumn.setCellValueFactory(cellData -> cellData.getValue().fileNameProperty());
        fileSizeColumn.setCellValueFactory(cellData -> cellData.getValue().fileSizeProperty().asObject());
        fileDateColumn.setCellValueFactory(cellData -> cellData.getValue().lastChangeDateProperty());
        buttonDel.setDisable(true);
        buttonDownLoad.setDisable(true);
        remoteStorageTable.setOnMouseClicked(event -> {
            if (event.getClickCount() > 1){
                handleDownloadFile();
            } else {
                handleSelectFiles();
            }
        });
    }

    public void setClientMain(ClientMain clientMain) {
        this.clientMain = clientMain;
        remoteStorageTable.setItems(clientMain.getRemoteStorage());
    }

    @FXML
    private void handleAddFile() throws IOException {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(clientMain.getPrimaryStage());
        if (file != null && file.isFile()){
            file = new File(file.getPath());
            System.out.println("Добавляем файл: " + file.getName() + " at path " + file.getCanonicalPath());
            FileProcessor fileProcessor = clientMain.getFileProcessor();
            FileModel fileModel = fileProcessor.uploadFile(file);
            if (clientMain.checkFileOverwrite(fileModel)) clientMain.sendRequest(fileModel);
        }

    }

    @FXML
    private void handleDownloadFile(){
        int selectedIndex = remoteStorageTable.getSelectionModel().getSelectedIndex();
        saveLocationChooser();
        System.out.println("Download selected index: " + selectedIndex);
        if (selectedIndex >= 0) {
            String fileName = remoteStorageTable.getItems().get(selectedIndex).getFileName();
            System.out.println("Скачиваем файл: " + fileName);
            clientMain.sendRequest(MessageHeaders.getFileDownload(fileName));
        } else {
            buttonDownLoad.setDisable(true);
        }
    }

    private void saveLocationChooser() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File saveLocation = directoryChooser.showDialog(clientMain.getPrimaryStage());
        if (saveLocation != null && saveLocation.isDirectory()){
            clientMain.setSaveFileLocation(saveLocation.getPath());
        } else {
            String defPath = "download_from_cloud/";
            File path = new File(defPath);
            if (!path.exists()){
                path.mkdirs();
            }
            clientMain.setSaveFileLocation(defPath);
        }
    }

    @FXML
    private void handleDeleteFile() {
        int selectedIndex = remoteStorageTable.getSelectionModel().getSelectedIndex();
        System.out.println("Delete selected index: " + selectedIndex);
        if (selectedIndex >= 0) {
            System.out.println("Удаляем файл: " + remoteStorageTable.getItems().get(selectedIndex).getFileName());
            clientMain.sendRequest(MessageHeaders.getFileDelete(remoteStorageTable.getItems().get(selectedIndex).getFileName()));
            remoteStorageTable.getItems().remove(selectedIndex);
        } else if (selectedIndex == -1) {
            buttonDel.setDisable(true);
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.initOwner(clientMain.getPrimaryStage());
            alert.setTitle("No Selection");
            alert.setHeaderText("No remote file selected");
            alert.setContentText("Pls select a file in the table.");
            alert.showAndWait();
        }
    }

    @FXML
    private void handleRefreshFiles() {
        clientMain.sendRequest(MessageHeaders.FILE_LIST);
        System.out.println("Обновляем");
    }

    @FXML
    private void handleSelectFiles() {
        int selectedIndex = remoteStorageTable.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0){
            buttonDel.setDisable(false);
            buttonDownLoad.setDisable(false);
        } else {
            buttonDel.setDisable(true);
            buttonDownLoad.setDisable(true);
        }
        System.out.println("Выбрали " + selectedIndex);
    }

}
