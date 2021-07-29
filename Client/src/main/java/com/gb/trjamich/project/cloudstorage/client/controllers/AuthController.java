package com.gb.trjamich.project.cloudstorage.client.controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import static com.gb.trjamich.project.cloudstorage.client.classes.Network.authenticated;

//import static com.gb.trjamich.project.cloudstorage.client.classes.Network.login;

//import com.gb.trjamich.project.cloudstorage.client.classes.Network.login;


public class AuthController {
    //private Network network;
    private MainController controller;

    @FXML
    public TextField loginField;

    @FXML
    public PasswordField passwordField;

    @FXML
    public Button okButton;

    @FXML
    public Button cancelButton;

    @FXML
    private void tryToLogin() {
        String login = loginField.getText().trim();
        String password = passwordField.getText().trim();

        //assert network!=null;
        controller.login(login,password);
        passwordField.clear();
        loginField.clear();
    }

    protected void setController(MainController controller) {
        this.controller = controller;
    }

    public void closeLogin() {
        passwordField.clear();
        loginField.clear();
        controller.loginStage.close();
    }
}
