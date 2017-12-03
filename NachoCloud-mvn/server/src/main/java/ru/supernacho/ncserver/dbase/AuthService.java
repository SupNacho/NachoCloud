package ru.supernacho.ncserver.dbase;

public interface AuthService {
    void start();
    String getRepository(String login, String password);
    void stop();
}
