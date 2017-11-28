package ru.supernacho.rf.controller;

public interface CloudServerListener {
    void onLogCloudServer(CloudServer cloudServer, String msg);
}
