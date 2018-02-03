package ru.supernacho.ncc.viewController;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import ru.supernacho.ncc.ClientMain;
import ru.supernacho.ncc.controller.ConnectionController;

public class LoginLayoutController {
    private ClientMain clientMain;
    private ConnectionController connection;

    public void setClientMain(ClientMain clientMain){
        this.clientMain = clientMain;
        connection = clientMain.getConnection();
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
        connection.connect(loginField.getText(), passwordField.getText());
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
