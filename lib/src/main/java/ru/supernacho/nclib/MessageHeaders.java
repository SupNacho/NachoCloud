package ru.supernacho.nclib;

public class MessageHeaders {

    public static final String DELIMITER =          ";";
    public static final String REGISTER =           "/register";
    public static final String REGISTER_ERROR =     "/register_error";
    public static final String AUTH_REQUEST =       "/auth_request";
    public static final String AUTH_ACCEPT =        "/auth_accept";
    public static final String AUTH_ERROR =         "/auth_error";
    public static final String FILE_LIST =          "/file_list";
    public static final String GET_USER_DATA =      "/get_user_data";
    public static final String FILE_UPLOAD =        "/file_upload";
    public static final String GET_FILE =           "/get_file";
    public static final String FILE_DELETE =        "/file_delete";
    public static final String RECONNECT =          "/reconnect";
    public static final String MSG_FORMAT_ERROR =   "/msg_format_error";

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
