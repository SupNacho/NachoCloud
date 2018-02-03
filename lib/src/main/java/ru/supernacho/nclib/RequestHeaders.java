package ru.supernacho.nclib;

public interface RequestHeaders {
    String DELIMITER =          ";";
    String REGISTER =           "/register";
    String REGISTER_ERROR =     "/register_error";
    String AUTH_REQUEST =       "/auth_request";
    String AUTH_ACCEPT =        "/auth_accept";
    String AUTH_ERROR =         "/auth_error";
    String FILE_LIST =          "/file_list";
    String GET_USER_DATA =      "/get_user_data";
    String FILE_UPLOAD =        "/file_upload";
    String GET_FILE =           "/get_file";
    String FILE_DELETE =        "/file_delete";
    String RECONNECT =          "/reconnect";
    String MSG_FORMAT_ERROR =   "/msg_format_error";
}
