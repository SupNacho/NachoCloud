package ru.supernacho.ncc.controller;

import javafx.collections.ObservableList;
import ru.supernacho.ncc.ClientMain;
import ru.supernacho.ncc.model.RemoteStorage;
import ru.supernacho.nclib.*;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class ReceiveDataController {

    private ClientFileInterface fileProcessor;
    private ClientMain clientMain;
    private ConnectionController connection;
    private ObservableList<RemoteStorage> remoteStorage;
    private User user;
    private DateFormat timeStamp = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
    private StringBuilder errMessages = new StringBuilder();

    ReceiveDataController(ConnectionController connection, ClientMain main) {
        this.clientMain = main;
        this.connection = connection;
        this.remoteStorage = clientMain.getRemoteStorage();
        this.fileProcessor = new FileProcessor();
    }

    public void parse(Object objectData) {

        if (objectData instanceof FileModel) {
            FileModel fileModel = (FileModel) objectData;
            fileProcessor.saveFile(clientMain.getSaveFileLocation(), fileModel);
            System.out.println("Received file " + fileModel.getName());
        }

        if (objectData instanceof List) {
            remoteStorage.clear();
            List<File> fileList = (List<File>) objectData;
            user.setFileList(fileList);
            connection.setFileList(fileList);
            for (File file : fileList) {
                System.out.println("File: " + file.getName());
            }
            System.out.println("Files qantity: " + fileList.size());
            for (File file : fileList) {
                remoteStorage.add(new RemoteStorage(file.getName(), file.length(), timeStamp.format(new Date(file.lastModified()))));
            }
        }

        if (objectData instanceof User) {
            user = (User) objectData;
            connection.sendRequest(Request.FILE_LIST);
        }

        if (objectData instanceof String) {
            String value = objectData.toString();
            String tokens[] = value.split(Request.DELIMITER);
            switch (tokens[0]) {
                case Request.REGISTER_ERROR:
                    String err = "Reg err: " + tokens[1];
                    System.out.println(err);
                    errMessages.append(err);
                    break;
                case Request.FILE_LIST:
                    int msgHeaderCut = Request.FILE_LIST.length() + Request.DELIMITER.length();
                    String files[] = value.substring(msgHeaderCut).split(Request.DELIMITER);
                    Arrays.sort(files);
                    break;
                case Request.AUTH_ACCEPT:
                    clientMain.showBrowsePage();
                    connection.sendRequest(Request.GET_USER_DATA);
                    break;
                case Request.AUTH_ERROR:
                    String msg = "Ошибка авторизации ";
                    System.out.println(msg);
                    errMessages.append(msg);
                    break;
                case Request.MSG_FORMAT_ERROR:
                    String msg2 = "Ошибка формата сообщения - > " + tokens[0] + tokens[1] + "\n";
                    errMessages.append(msg2);
                    break;
                case Request.RECONNECT:
                    String msg3 = "Переподключение с другого устройства] \n";
                    errMessages.append(msg3);
                    break;
                default:
                    throw new RuntimeException("Неизвестный заголовок сообщения: " + value);
            }
        }
    }
}

