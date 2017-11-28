package ru.supernacho.ncc.view;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import ru.supernacho.ncc.Main;

public class LoginLayoutController {
    private Main main;

    public void setMain(Main main){
        this.main = main;
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
        System.out.println(String.format("Login: %s | pass: %s", loginField.getText(), passwordField.getText()));
        //todo запрос на сервер для установки соеденения если ОК то запуск БроузВиев
        main.connect(loginField.getText(), passwordField.getText());
//        main.showBrowsePage();
    }

    @FXML
    private void handleRegister(){
        main.showRegisterDialog();
    }

    public void setErrMessage(String msg){
        labelErrors.setText(msg);
        labelErrors.setVisible(true);
    }

}
