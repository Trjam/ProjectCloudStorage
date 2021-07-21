package com.gb.trjamich.project.cloudstorage.client.controllers;

import com.gb.trjamich.project.cloudstorage.client.classes.Network.*;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import static com.gb.trjamich.project.cloudstorage.client.classes.Network.login;


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
        login(login, password);
    }

    protected void setController(MainController controller) {
        this.controller = controller;
    }
}
