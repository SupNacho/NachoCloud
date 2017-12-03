package ru.supernacho.ncserver.dbase;

import java.io.File;
import java.sql.*;

public class DataBase {
    private Connection connection;
    private Statement statement;
    private PreparedStatement preparedStatement;
    private ResultSet resultSet;

    public void startBase() {
        try {
            connectDB();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void connectDB() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:UsersDB.db");
            statement = connection.createStatement();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        File file = new File("UsersDB.db");
//        if (!file.exists()) {
            statement.execute("CREATE TABLE IF NOT EXISTS users (\n" +
                    "    id           INTEGER PRIMARY KEY AUTOINCREMENT\n" +
                    "                         UNIQUE\n" +
                    "                         NOT NULL,\n" +
                    "    login        STRING  UNIQUE,\n" +
                    "    password     STRING,\n" +
                    "    storage_path STRING  UNIQUE,\n" +
                    "    name                 NOT NULL,\n" +
                    "    is_loggined  BOOLEAN NOT NULL\n" +
                    ");");
//        }
    }

    public boolean login(String login, String password) throws SQLException {
        preparedStatement = connection.prepareStatement("SELECT login, password, name, storage_path FROM users WHERE login = ? AND password = ?;");
        preparedStatement.setString(1, login);
        preparedStatement.setString(2, password);
        resultSet = preparedStatement.executeQuery();
        if (resultSet.isClosed()) {
            return false;
        }
        return true;
    }

    public String getUserRepository() {
        try {
            return resultSet.getString("storage_path");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "Something wrong";
    }

    public boolean registerUser(String newLogin, String newPassword, String name) {

            try {
                preparedStatement = connection.prepareStatement("INSERT INTO users (\n" +
                        "                      id,\n" +
                        "                      login,\n" +
                        "                      password,\n" +
                        "                      storage_path,\n" +
                        "                      name,\n" +
                        "                      is_loggined\n" +
                        "                  )\n" +
                        "                  VALUES (\n" +
                        "                      ?,\n" +
                        "                      ?,\n" +
                        "                      ?,\n" +
                        "                      ?,\n" +
                        "                      ?,\n" +
                        "                      ?\n" +
                        "                  );");
                preparedStatement.setString(1, null);
                preparedStatement.setString(2, newLogin);
                preparedStatement.setString(3, newPassword);
                preparedStatement.setString(4, "cloud_storage/" + newLogin + "/");
                preparedStatement.setString(5, name);
                preparedStatement.setBoolean(6, true);
                return preparedStatement.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        return false;
    }

    public boolean checkAviablity(String login) {
        try {
            System.out.println("Check aviablity!");
            preparedStatement = connection.prepareStatement("SELECT login\n" +
                    "  FROM users\n" +
                    " WHERE login = ?;");
            preparedStatement.setString(1, login);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.isClosed()) {
                System.out.println("login is aviable");
                return true;
            } else {
                System.out.println("login is occupied");
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public String getName() {
        try {
            return resultSet.getString("name");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void disconect() {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
