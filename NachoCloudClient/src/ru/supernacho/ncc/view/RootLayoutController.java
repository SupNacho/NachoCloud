package ru.supernacho.ncc.view;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import ru.supernacho.ncc.Main;

public class RootLayoutController {
    private Main main;

    public void setMain(Main main){
        this.main = main;
    }


    @FXML
    private void handleExit(){
        //todo Добавить код входя выхода
        System.exit(0);
    }

    @FXML
    private void handleAbout(){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.initOwner(main.getPrimaryStage());
        alert.setTitle("About NachoCloud");
        alert.setHeaderText("Blah blah blah");
        alert.setContentText("Some instructions...");
        alert.showAndWait();
    }
}
