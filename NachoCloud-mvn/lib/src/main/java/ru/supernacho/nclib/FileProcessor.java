package ru.supernacho.nclib;

import java.io.*;

public class FileProcessor {
    private FileInputStream fileInputStream;
    private FileOutputStream fileOutputStream;
    private byte[] fileContent;

    public FileProcessor() {
    }

    public FileModel uploadFile(File path){
        FileModel fileModel = new FileModel();
        try {
            fileContent = new byte[(int) path.length()];
            fileInputStream = new FileInputStream(path);
            fileInputStream.read(fileContent);
            fileModel.setFile(path);
            fileModel.setFileContent(fileContent);
            return fileModel;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public void saveFile(String userStorage, FileModel incomingFile){
        try {
            fileOutputStream = new FileOutputStream(new File(userStorage, incomingFile.getFile().getName()));
            fileOutputStream.write(incomingFile.getFileContent());
            fileOutputStream.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileOutputStream != null){
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}