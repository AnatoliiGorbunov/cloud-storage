package ru.geekbrains.cloud.service;

public class UserNameService {//

    private static int userId = 0;


    public String getUserName(){
        userId++;
        return "user" + userId;
    }
}
