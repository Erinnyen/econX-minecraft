package com.erinnyen.econx.DBInteraction;

public class marketDBInteraction {


    private final String uname;
    private final String password;
    private  final String url;

    public marketDBInteraction(DBCredentials pDBcreds){


        uname = pDBcreds.getUsername();
        password = pDBcreds.getPassword();
        url = pDBcreds.getUrl();

    }
}
