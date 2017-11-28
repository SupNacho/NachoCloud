package ru.supernacho.rf.controller;

public interface AuthService {
    void start();
    String getRepository(String login, String password);
    void stop();
}
