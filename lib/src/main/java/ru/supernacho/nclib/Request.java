package ru.supernacho.nclib;

public class Request implements RequestHeaders{

    public static String getRegistrationError(String newLogin){
        return REGISTER_ERROR + DELIMITER + newLogin + " is already taken, pls choose another...";

    }

    public static String getFileDownload(String fileName){
        return GET_FILE + DELIMITER + fileName;
    }

    public static String getRegistrationRequest(String newLogin, String newPassword, String name){
        return REGISTER + DELIMITER + newLogin + DELIMITER + newPassword + DELIMITER + name;
    }

    public static String getFileDelete(String file){
        return FILE_DELETE + DELIMITER + file;
    }

    // /auth_request login password
    public static String getAuthRequest(String login, String password) {
        return AUTH_REQUEST + DELIMITER + login + DELIMITER + password;
    }

    // /auth_accept login
    public static String getAuthAccept(String login){
        return AUTH_ACCEPT + DELIMITER + login;
    }

    // /file_list file1 file2 file3
    public static String getFileList(String files){
        return FILE_LIST + DELIMITER + files;
    }

    // AUTH_ERROR time message
    public static String getAuthError(){
        return AUTH_ERROR;
    }

    public static String getReconnect(){
        return RECONNECT;
    }

    // /msg_format_error time value
    public static String getMsgFormatError(String value){
        return MSG_FORMAT_ERROR + DELIMITER + value;
    }
}
