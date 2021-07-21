package com.gb.trjamich.project.cloudstorage.client.classes;

import com.google.gson.Gson;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class Network {
    private static final String IP_ADDRESS = "localhost";
    private static final int PORT = 5000;
    private static Socket socket;
    private static DataInputStream in;
    private static DataOutputStream out;

    private static boolean authenticated;
    private String nickname;
    private String login;

    private static void connect() {
        try {
            socket = new Socket(IP_ADDRESS, PORT);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

            new Thread(() -> {
                System.out.println("поток стартовал");
                try {
                    //цикл аутентификации
                    //int i = 0;
                    while (true) {

                            Response response = new Gson().fromJson(readString(), Response.class);

                            if (response!=null&&response.getReqType().equals("auth")) {

                                if ("login".equals(response.getOperation())) {
                                    switch (response.getStatus()) {
                                        case "ok":
                                            System.out.println("login ок");
                                            setAuthenticated(true);
                                            break;
                                        case "fault":
                                            System.out.println("Login fault: " + response.getInfo());
                                            break;
                                    }
                                } else if (response.getOperation().equals("register")) {
                                    switch (response.getStatus()) {
                                        case "ок":
                                            System.out.println("reg ок");
                                            break;
                                        case "fault":
                                            System.out.println("reg fault: " + response.getInfo());
                                            break;
                                    }
                                }else if (response.getOperation().equals("logout") && response.getStatus().equals("ok")) {
                                    setAuthenticated(false);
                                    break;
                                }
                            }else {
                                ;
                                break;
                            }
                        }
                    while (authenticated){
                        System.out.println("основной цикл");

                        break;
                    }
                   // }
                } catch (Exception e){
                    e.printStackTrace();
                }finally {
                    try {

                        socket.close();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void setAuthenticated(boolean authenticated) {
        Network.authenticated = authenticated;

        if (!authenticated) {
            //nickname = "";
        }
        //setTitle(nickname);
        //textArea.clear();
    }

    public static String readString() throws IOException {
        return new String(in.readAllBytes());
    }

    public static void writeString(String str) throws IOException {
        out.write(str.getBytes(StandardCharsets.UTF_8));
        out.flush();
    }

    public static Socket getSocket(){
        return socket;
    }

    public static void login(String login, String password) {
        if (socket== null || socket.isClosed()) {
            connect();
        }
        Request request= Request.builder()
                .reqType("auth")
                .operation("login")
                .user(User.builder()
                        .login(login)
                        .password(password)
                        .build())
                .build();
        try {
            writeString(new Gson().toJson(request,Request.class));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}