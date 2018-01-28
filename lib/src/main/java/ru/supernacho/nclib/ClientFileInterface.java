package ru.supernacho.nclib;

import java.io.File;

public interface ClientFileInterface {
    FileModel uploadFile(File path);
    void saveFile(String userStorage, FileModel incomingFile);
}
