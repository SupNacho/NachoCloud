package ru.supernacho.nclib;

import java.io.File;
import java.util.List;

public interface ServerFileInterface {
    void setUser(User user);
    FileModel getFile(String fileName);
    List<File> getFileList();
    void deleteFile(String fileName);
}
