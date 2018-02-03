package ru.supernacho.ncserver.AuthService;

import ru.supernacho.ncserver.controller.CloudServer;
import ru.supernacho.ncserver.controller.CloudSocketThread;

public interface AuthService {
    void start();
    String getRepository(String login, String password);
    void stop();
    void register(String newLogin, String newPassword, String name,
                  CloudServer server, CloudSocketThread newClient);
    void authorize(String newLogin, String newPassword, CloudServer server, CloudSocketThread newClient);
}
