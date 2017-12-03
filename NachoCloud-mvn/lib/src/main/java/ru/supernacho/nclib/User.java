package ru.supernacho.nclib;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class User implements Serializable{
    private String login;
    private String name;
    private String repository;
    private List<File> fileList;

    public User(String login, String name, String repository) {
        this.login = login;
        this.name = name;
        this.repository = repository;
        this.fileList = new ArrayList<>();
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRepository() {
        return repository;
    }

    public void setRepository(String repository) {
        this.repository = repository;
    }

    public List<File> getFileList() {
        return fileList;
    }

    public void setFileList(List<File> fileList) {
        this.fileList = fileList;
    }

    @Override
    public String toString() {
        return "Login: " + login + " Name: " + name + " Repos: " + repository + " File List: " + fileList.size();
    }
}
