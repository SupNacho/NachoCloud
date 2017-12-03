package ru.supernacho.ncserver.controller;

public interface CloudServerListener {
    void onLogCloudServer(CloudServer cloudServer, String msg);
}
