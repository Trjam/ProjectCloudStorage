package com.gb.trjamich.project.cloudstorage.client.handlers;

import io.netty.channel.ChannelInboundHandlerAdapter;

import java.io.*;
import java.net.Socket;

public class ClientHandler extends ChannelInboundHandlerAdapter {
    private final Socket socket;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }


}
