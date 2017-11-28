package ru.supernacho.ncc.view;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import ru.supernacho.ncc.Main;
import ru.supernacho.ncc.controller.MessageHeaders;

public class RegistrationDialogController {
    private Main main;
    private Stage dialogStage;
    private boolean okClicked = false;

    @FXML
    private TextField textFieldNewLogin;

    @FXML
    private PasswordField passFieldNewPassword;

    @FXML
    private PasswordField passFieldConfirmPassword;

    @FXML
    private TextField textFiledName;

    @FXML
    private Button buttonRegister;

    @FXML
    private Button buttonCancel;

    @FXML
    private Label labelRegisterStatus;

    public void setMain(Main main){
        this.main = main;
    }

    @FXML
    private void initialize(){
    }

    public void setDialogStage(Stage dialogStage){
        this.dialogStage = dialogStage;
    }

    public boolean isOkClicked(){
        return okClicked;
    }

    @FXML
    private void handleOk(){
        if (isInputValid()){
            System.out.println(String.format("Register -> Login: %s | Pass: %s | Name: %s", textFieldNewLogin.getText(),
                    passFieldNewPassword.getText(), textFiledName.getText()));
            labelRegisterStatus.setText("Registration data send to server, waiting response...");
            okClicked = true;
            main.registration(textFieldNewLogin.getText(), passFieldNewPassword.getText(), textFiledName.getText(), true);
//            main.showBrowsePage();
//            main.sendRequest(MessageHeaders.FILE_LIST_REFRESH);
            dialogStage.close();
        }

    }

    @FXML
    private void handleCancel(){
        dialogStage.close();
    }

    private boolean isInputValid(){
        StringBuilder errorMesage = new StringBuilder();

        if (textFieldNewLogin == null || textFieldNewLogin.getText().length() == 0) {
            errorMesage.append("Login is empty");
        }

        if (passFieldNewPassword == null || passFieldNewPassword.getText().length() == 0){
            errorMesage.append("Password is empty");
        }
         if (!passFieldNewPassword.getText().equals(passFieldConfirmPassword.getText())) {
            errorMesage.append("Passwords not equals");
         }

         if (errorMesage.length() == 0){
            return true;
         } else {
             labelRegisterStatus.setText(errorMesage.toString());
             return false;
         }
    }
}
