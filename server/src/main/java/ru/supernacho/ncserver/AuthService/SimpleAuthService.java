package ru.supernacho.ncserver.AuthService;

import ru.supernacho.ncserver.dbase.DataBase;

import java.sql.SQLException;

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
        dataBase.disconnect();
    }
}
