package com.gb.trjamich.project.cloudstorage.client;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ClientApp {
    private final Socket socket;
    private final DataOutputStream out;
    private final DataInputStream in;
    private final ByteArrayOutputStream bos;

    public ClientApp() throws IOException {
        socket = new Socket("localhost", 5678);
        out = new DataOutputStream(socket.getOutputStream());
        bos = new ByteArrayOutputStream();
        in = new DataInputStream(socket.getInputStream());


        int i =0;
        while (i<5) {
            socket.setTcpNoDelay(true);
            i++;
            sendMessage("priyom zadacha");
            //String str = in.readUTF();

            long size = in.;
            byte[] buffer = new byte[8 * 1024];

            for (int j = 0; j < (size + (buffer.length - 1)) / (buffer.length); j++) {
                bos.write(buffer, 0,in.read(buffer));
            }
            byte[] result = bos.toByteArray();
            String s = new String(result, StandardCharsets.UTF_8);
            System.out.println(s);
        }
    }

    private void sendMessage(String message) {
        try {
            out.writeUTF(message);
            //out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        new ClientApp();
    }
}
