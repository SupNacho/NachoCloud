package ru.supernacho.ncserver.model;

import ru.supernacho.nclib.FileModel;
import ru.supernacho.nclib.User;

import java.io.*;
import java.util.Arrays;
import java.util.List;

public class ServerFilesProcessor {
    private String userRepository;
    private User user;
    private File path;
    private List<File> fileList;
    private FileInputStream fileInputStream;

    public ServerFilesProcessor() {
    }

    public void setUser(User user) {
        this.user = user;
        userRepository = user.getRepository();
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
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
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

//    public boolean addFile(File file) {
//        if (fileList.add(file)) return true;
//        return false;
//    }

    public List<File> getFileList() {
        File[] tmpList = path.listFiles();
        fileList = Arrays.asList(tmpList);
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
