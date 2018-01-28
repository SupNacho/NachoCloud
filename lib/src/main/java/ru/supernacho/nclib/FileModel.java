package ru.supernacho.nclib;

import java.io.File;
import java.io.Serializable;

public class FileModel implements Serializable{
    private File file;
    private byte[] fileContent;

    public String getName() {
        return file.getName();
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }


    public long getLastModified() {
        return file.lastModified();
    }

    public byte[] getFileContent() {
        return fileContent;
    }

    public void setFileContent(byte[] fileContent) {
        this.fileContent = fileContent;
    }

    public long getSize(){
        return fileContent.length;
    }
}
