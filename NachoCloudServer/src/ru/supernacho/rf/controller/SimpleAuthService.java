package ru.supernacho.rf.controller;

import java.sql.SQLException;
import java.util.ArrayList;

public class SimpleAuthService implements AuthService {

    private DataBase dataBase;

    public SimpleAuthService(DataBase dataBase) {
        this.dataBase = dataBase;
    }

    @Override
    public void start() {
        dataBase.startBase();
    }

    @Override
    public String getRepository(String login, String password) {
        try {
            if (dataBase.login(login,password)){
                return dataBase.getName();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void stop() {
        dataBase.disconect();
    }
}
