package com.gb.trjamich.project.cloudstorage.client.classes;

import com.gb.trjamich.project.cloudstorage.client.controllers.MainController;
import com.google.gson.Gson;
import common.FileList;
import common.Request;
import common.Response;
import common.User;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

import static com.gb.trjamich.project.cloudstorage.client.controllers.MainController.socket;

public class Network {
    public static final String IP_ADDRESS = "localhost";
    public static final int PORT = 5000;

    public static DataInputStream in;
    public static DataOutputStream out;
    private static MainController controller;

    public static boolean authenticated;
    private static String nickname;
    public static String clientLogin;
    public static UUID clientUuid;



    public static void setAuthenticated(boolean auth) {
        authenticated = auth;


        if (!authenticated) {
            nickname = "";
            clientLogin = "";
            clientUuid = null;
        }

        //setTitle(nickname);
        //textArea.clear();
    }

    public static String readString() throws IOException {
        Scanner scanner = new Scanner(new InputStreamReader(socket.getInputStream(), "UTF-8"));
        scanner.useDelimiter("\r\n");
        String response = scanner.nextLine();
        return response;
    }

    public static void writeString(String str) throws IOException {
        System.out.println(str);
        out.write((str+"\r\n").getBytes(StandardCharsets.UTF_8));
        out.flush();
    }

    public static Socket getSocket() {
        return socket;
    }



    public void setController(MainController controller) {
        this.controller = controller;
    }

}