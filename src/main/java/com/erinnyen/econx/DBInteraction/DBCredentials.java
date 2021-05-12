package com.erinnyen.econx.DBInteraction;

public class DBCredentials {

    private String username;
    private String password;
    private String url;

    public DBCredentials(String pUsername, String pPassword, String pUrl){
        username = pUsername;
        password = pPassword;
        url = pUrl;
    }


    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getUrl() {
        return url;
    }
}
