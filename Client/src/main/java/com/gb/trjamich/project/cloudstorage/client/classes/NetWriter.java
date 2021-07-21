package com.gb.trjamich.project.cloudstorage.client.classes;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class NetWriter implements Runnable {

    private Socket socket;
    private final String IP_ADDRESS = "localhost";
    private final int PORT = 5000;

    Socket wsocket = new Socket(IP_ADDRESS, PORT);

    DataInputStream in =new DataInputStream(socket.getInputStream());
    DataOutputStream out =new DataOutputStream(socket.getOutputStream());

    public NetWriter() throws IOException {
    }

    @Override
    public void run() {
        try {
            while (true) {

            }
        } catch (Exception e){
            e.printStackTrace();
        }

    }
}
