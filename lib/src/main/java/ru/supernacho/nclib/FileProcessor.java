package ru.supernacho.nclib;

import java.io.*;
import java.util.Arrays;
import java.util.List;

public class FileProcessor implements ClientFileInterface, ServerFileInterface {
    private User user;
    private File path;
    private List<File> fileList;
    private FileInputStream fileInputStream;
    private FileOutputStream fileOutputStream;

    public FileProcessor() {
    }

    public FileModel uploadFile(File path){
        FileModel fileModel = new FileModel();
        try {
            byte[] fileContent = new byte[(int) path.length()];
            fileInputStream = new FileInputStream(path);
            fileInputStream.read(fileContent);
            fileModel.setFile(path);
            fileModel.setFileContent(fileContent);
            return fileModel;
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
        return fileModel;
    }

    public void saveFile(String userStorage, FileModel incomingFile){
        try {
            fileOutputStream = new FileOutputStream(new File(userStorage, incomingFile.getFile().getName()));
            fileOutputStream.write(incomingFile.getFileContent());
            fileOutputStream.flush();
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

    public void setUser(User user) {
        this.user = user;
        String userRepository = user.getRepository();
        path = new File(userRepository);
        if (!path.exists()) {
            if (path.mkdirs()) System.out.println("User repository created at " + user.getRepository());
        } else {
            System.out.println("User repository exist, going on...");
        }
    }

    public FileModel getFile(String fileName) {
        for (File file : fileList) {
            if (file.getName().equals(fileName)) {
                FileModel fileModel = new FileModel();
                byte[] fileContent = new byte[(int) file.length()];
                try {
                    fileInputStream = new FileInputStream(file);
                    fileInputStream.read(fileContent);
                    fileModel.setFile(file);
                    fileModel.setFileContent(fileContent);
                    return fileModel;
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (fileInputStream != null) {
                        try {
                            fileInputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        return null;
    }

    public List<File> getFileList() {
        File[] tmpList = path.listFiles();
        fileList = Arrays.asList(tmpList != null ? tmpList : new File[0]);
        for (File file1 : fileList) {
            System.out.println("Files: " + file1.getName());
        }
        System.out.println("Files qaty: " + fileList.size());
        user.setFileList(fileList);
        return fileList;
    }

    public void deleteFile(String fileName){
        for (File file : fileList) {
            if (file.getName().equals(fileName)) {
                if (file.delete()) {
                    System.out.println("Deletion succes");
                } else {
                    System.out.println("Deletion fail");
                }
            }
        }
    }

}
