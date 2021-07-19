package com.gb.trjamich.project.cloudstorage.client.controllers;

import com.gb.trjamich.project.cloudstorage.client.classes.Network;
import javafx.fxml.FXML;
import javafx.scene.control.*;


public class LoginController {
    private MainController controller;

    @FXML
    public TextField loginField;

    @FXML
    public PasswordField passwordField;

    @FXML
    public Button okButton;

    @FXML
    public Button cancelButton;

    public void tryToLogin(){
        //???
        new Network();

    }

    public void setController(MainController controller) {
        this.controller = controller;
    }
}
