package ru.supernacho.ncc.view;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import ru.supernacho.ncc.ClientMain;


public class OverWriteViewController {
    private ClientMain clientMain;
    private Stage dialogStage;
    private boolean isOverWrite = false;
    private String oldFileName;
    private String newFileName;


    @FXML
    private TextField textFieldNewFileName;

    @FXML
    private Label labelFileExists;

    @FXML
    private Button buttonOverWrite;

    @FXML
    private Button buttonSaveNewName;

    @FXML
    private Button buttonCancel;

    public void setClientMain(ClientMain clientMain){
        this.clientMain = clientMain;
    }

    @FXML
    private void initialize(){
    }

    public void setDialogStage(Stage dialogStage){
        this.dialogStage = dialogStage;
    }

    public void setOldFileName(String oldFileName) {
        this.oldFileName = oldFileName;
        labelFileExists.setText(new String().format("File %s already exists on server", oldFileName));
    }

    public boolean isOverWrite(){
        return isOverWrite;
    }

    @FXML
    private void handleCancel(){
        newFileName = "drop";
        dialogStage.close();
    }

    @FXML
    private void handleOverWrite(){
        newFileName = oldFileName;
        dialogStage.close();
    }

    @FXML
    private void handleRename(){
        if (isFileNameValid()){
            newFileName = textFieldNewFileName.getText();
            isOverWrite = true;
            dialogStage.close();
        }
    }

    private boolean isFileNameValid(){
        if (textFieldNewFileName.getText() == null || textFieldNewFileName.getText().isEmpty()){
            labelFileExists.setText("Empty file name not accepted.");
            return false;
        }
        return true;
    }

    public String getNewFileName() {
        return newFileName;
    }
}
