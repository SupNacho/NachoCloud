package ru.supernacho.ncserver.AuthService;

import ru.supernacho.nclib.Request;
import ru.supernacho.nclib.User;
import ru.supernacho.ncserver.controller.CloudServer;
import ru.supernacho.ncserver.controller.CloudSocketThread;
import ru.supernacho.ncserver.dbase.DataBase;

import java.sql.SQLException;
import java.util.concurrent.ConcurrentHashMap;

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
    public void register(String newLogin, String newPassword, String name,
                         CloudServer server, CloudSocketThread newClient){
        ConcurrentHashMap<CloudSocketThread, User> userMap = server.getUserMap();
        if (dataBase.checkAviablity(newLogin)) {
            dataBase.registerUser(newLogin, newPassword, name);
            String nickname = getRepository(newLogin, newPassword);
            if (nickname == null) {
                newClient.authError();
                return;
            }

            CloudSocketThread oldClient = server.getClientByNickname(nickname);
            newClient.authAccept(nickname);
            if (oldClient == null) {
                System.out.println(("Server " + newClient.getLogin() + " connected."));
                userMap.put(newClient, new User(newLogin, dataBase.getName(), dataBase.getUserRepository()));
            } else {
                oldClient.reconnected();
            }
        } else {
            newClient.sendMsg(Request.getRegistrationError(newLogin));
        }
    }

    @Override
    public void authorize(String newLogin, String newPassword, CloudServer server, CloudSocketThread newClient) {
        String nickname = getRepository(newLogin, newPassword);
        if (nickname == null) {
            newClient.authError();
            System.out.println("AUTH ERROR");
            return;
        }

        CloudSocketThread oldClient = server.getClientByNickname(nickname);
        newClient.authAccept(nickname);
        if (oldClient == null) {
            System.out.println(("Server " + newClient.getLogin() + " connected."));
            server.getUserMap().put(newClient, new User(newLogin, dataBase.getName(), dataBase.getUserRepository()));
        } else {
            oldClient.reconnected();
        }
    }

    @Override
    public void stop() {
        dataBase.disconnect();
    }
}
