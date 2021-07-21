package com.gb.trjamich.project.cloudstorage.client.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    private Stage stage;
    private Stage loginStage;
    private AuthController authController;

    private Socket socket;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Platform.runLater(() -> {
            stage = new Stage();
            stage.setOnCloseRequest(event -> {
                System.out.println("bye");
                if (socket != null && !socket.isClosed()) {

                }
            });
        });
        //setAuthenticated(false);
    }

    private void createLoginWindow() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            Parent root = fxmlLoader.load();
            loginStage = new Stage();
            loginStage.setTitle("Login");
            loginStage.setScene(new Scene(root, 300, 150));
            loginStage.setResizable(false);

            loginStage.initModality(Modality.APPLICATION_MODAL);
            loginStage.initStyle(StageStyle.UTILITY);

            authController = fxmlLoader.getController();
            authController.setController(this);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void startLogin() {
        if (loginStage == null) {
            createLoginWindow();
        }
        Platform.runLater(() -> {
            loginStage.show();
        });
    }

}
