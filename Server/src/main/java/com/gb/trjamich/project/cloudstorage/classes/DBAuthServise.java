package com.gb.trjamich.project.cloudstorage.classes;

import com.gb.trjamich.project.cloudstorage.handlers.SQLHandler;
import com.gb.trjamich.project.cloudstorage.interfaces.AuthService;

public class DBAuthServise implements AuthService {
    @Override
    public String getNicknameByLoginAndPassword(String login, String password) {
        return SQLHandler.getNicknameByLoginAndPassword(login, password);
    }

    @Override
    public boolean registration(String login, String password, String nickname) {
        return SQLHandler.registration(login, password, nickname);
    }


    @Override
    public boolean changeNick(String oldNickname, String newNickname) {
        return SQLHandler.changeNick(oldNickname, newNickname);
    }

    @Override
    public boolean checkPassword(String password, String login) {
        return SQLHandler.checkPassword(password, login);
    }
}
