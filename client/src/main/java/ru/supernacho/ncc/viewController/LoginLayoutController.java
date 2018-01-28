package ru.supernacho.ncc.viewController;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import ru.supernacho.ncc.ClientMain;

public class LoginLayoutController {
    private ClientMain clientMain;

    public void setClientMain(ClientMain clientMain){
        this.clientMain = clientMain;
    }

    @FXML
    private Label labelErrors;

    @FXML
    private TextField loginField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button buttonSignIn;

    @FXML
    private Button buttonRegister;

    @FXML
    private void handleSignIn(){
        clientMain.connect(loginField.getText(), passwordField.getText());
    }

    @FXML
    private void handleRegister(){
        clientMain.showRegisterDialog();
    }

    public void setErrMessage(String msg){
        labelErrors.setText(msg);
        labelErrors.setVisible(true);
    }

}
