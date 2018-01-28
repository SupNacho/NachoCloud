package ru.supernacho.ncc.model;

import javafx.beans.property.*;

public class RemoteStorage {
    private  StringProperty fileName;
    private  LongProperty fileSize;
    private  StringProperty lastChangeDate;

    public RemoteStorage(String fileName, long fileSize, String date){
        this.fileName = new SimpleStringProperty(fileName);
        this.fileSize = new SimpleLongProperty(fileSize);
        this.lastChangeDate = new SimpleStringProperty(date);
    }

    public String getFileName() {
        return fileName.get();
    }

    public StringProperty fileNameProperty() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName.set(fileName);
    }

    public Long getFileSize() {
        return fileSize.get();
    }

    public LongProperty fileSizeProperty() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize.set(fileSize);
    }


    public StringProperty lastChangeDateProperty() {
        return lastChangeDate;
    }

    public void setLastChangeDate(String lastChangeDate) {
        this.lastChangeDate.set(lastChangeDate);
    }
}
