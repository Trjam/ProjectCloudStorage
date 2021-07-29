package com.gb.trjamich.project.cloudstorage.client;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ClientApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        //FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/main.fxml"));
        //MainControler controller = fxmlLoader.getController();
        //primaryStage.setOnCloseRequest(event -> controller.exitAction());
        primaryStage.setTitle("La'Nuage");
        primaryStage.setScene(new Scene( root,1200, 700));
        primaryStage.show();
    }

    public static void startClient(String[] args) {
        launch(args);

    }
}

