package main.se.kth.id1212.util;

import java.io.File;
import java.util.Scanner;

public class UserCredentials {
    private String username;
    private  String password;

    public UserCredentials() {
        String filePath = "Task_3/src/Main/se/kth/id1212/util/credentials.txt";
        try {
            Scanner file_content = new Scanner(new File(filePath));
            username = file_content.nextLine();
            password = file_content.nextLine();
            file_content.close();
        } catch (Exception ioException) {
            ioException.printStackTrace();
        }
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
