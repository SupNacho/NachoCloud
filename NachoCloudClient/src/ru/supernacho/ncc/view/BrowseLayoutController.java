package ru.supernacho.ncc.view;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import ru.supernacho.ncc.Main;
import ru.supernacho.ncc.controller.MessageHeaders;
import ru.supernacho.ncc.model.RemoteStorage;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;


public class BrowseLayoutController {
    @FXML
    private TableView<RemoteStorage> remoteStorageTable;

    @FXML
    private TableColumn<RemoteStorage, String> fileNameColumn;

    @FXML
    private TableColumn<RemoteStorage, Long> fileSizeColumn;

    @FXML
    private TableColumn<RemoteStorage, LocalDate> fileDateColumn;

    @FXML
    private Button buttonAdd;
    @FXML
    private Button buttonDel;
    @FXML
    private Button buttonRefresh;

    private Main main;

    public BrowseLayoutController() {

    }

    @FXML
    private void initialize() {
        fileNameColumn.setCellValueFactory(cellData -> cellData.getValue().fileNameProperty());
        fileSizeColumn.setCellValueFactory(cellData -> cellData.getValue().fileSizeProperty().asObject());
        fileDateColumn.setCellValueFactory(cellData -> cellData.getValue().lastChangeDateProperty());
        buttonDel.setDisable(true);
    }

    public void setMain(Main main) {
        this.main = main;
        remoteStorageTable.setItems(main.getRemoteStorage());
    }

    @FXML
    private void handleAddFile() throws IOException {
        //todo: добавить запрос на добавление файла;
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(main.getPrimaryStage());
        if (file != null && file.isFile()){
            file = new File(file.getPath());
            System.out.println("Добавляем файл: " + file.getName() + " at path " + file.getCanonicalPath());
        }

    }

    @FXML
    private void handleDeleteFile() {
        int selectedIndex = remoteStorageTable.getSelectionModel().getSelectedIndex();
        System.out.println("selected index: " + selectedIndex);
        if (selectedIndex >= 0) {
            System.out.println("Удаляем файл: " + remoteStorageTable.getItems().get(selectedIndex).getFileName());
            remoteStorageTable.getItems().remove(selectedIndex);
            //todo: добавить запрос на удаление файла;

        } else if (selectedIndex == -1) {
            buttonDel.setDisable(true);
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.initOwner(main.getPrimaryStage());
            alert.setTitle("No Selection");
            alert.setHeaderText("No remote file selected");
            alert.setContentText("Pls select a file in the table.");
            alert.showAndWait();
        }
    }

    @FXML
    private void handleRefreshFiles() {
        main.sendRequest(MessageHeaders.FILE_LIST_REFRESH);
        System.out.println("Обновляем");
    }

    @FXML
    private void handleSelectFiles() {
        int selectedIndex = remoteStorageTable.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0){
            buttonDel.setDisable(false);
        } else {
            buttonDel.setDisable(true);
        }
        System.out.println("Выбрали " + selectedIndex);
    }

}
