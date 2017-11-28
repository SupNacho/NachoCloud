package ru.supernacho.ncc.model;

import javafx.beans.property.*;

import java.time.LocalDate;

public class RemoteStorage {
    private final StringProperty fileName;
    private final LongProperty fileSize;
    private final ObjectProperty<LocalDate> lastChangeDate;

    public RemoteStorage(){
        this(null, 0, null);
    }

    public RemoteStorage(String fileName, long fileSize, LocalDate date){
        this.fileName = new SimpleStringProperty(fileName);
        this.fileSize = new SimpleLongProperty(fileSize);
        this.lastChangeDate = new SimpleObjectProperty<LocalDate>(date);
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

    public LocalDate getLastChangeDate() {
        return lastChangeDate.get();
    }

    public ObjectProperty<LocalDate> lastChangeDateProperty() {
        return lastChangeDate;
    }

    public void setLastChangeDate(LocalDate lastChangeDate) {
        this.lastChangeDate.set(lastChangeDate);
    }
}
