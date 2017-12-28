package ru.supernacho.ncc.view;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import ru.supernacho.ncc.ClientMain;

public class RootLayoutController {
    private ClientMain clientMain;

    public void setClientMain(ClientMain clientMain){
        this.clientMain = clientMain;
    }


    @FXML
    private void handleExit(){
        System.exit(0);
    }

    @FXML
    private void handleAbout(){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.initOwner(clientMain.getPrimaryStage());
        alert.setTitle("About NachoCloud");
        alert.setHeaderText("Blah blah blah");
        alert.setContentText("Some instructions...");
        alert.showAndWait();
    }
}
