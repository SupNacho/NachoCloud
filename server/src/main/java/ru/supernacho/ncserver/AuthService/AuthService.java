package ru.supernacho.ncserver.AuthService;

public interface AuthService {
    void start();
    String getRepository(String login, String password);
    void stop();
}
