package com.erinnyen.econx.DBInteraction;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Objects;

public class DBCredentials {

    private String username;
    private String password;
    private String url;

    public DBCredentials(File path){

        for(File file : Objects.requireNonNull(path.listFiles())){
            if(!file.getName().equals("dbcreds.json")){
                System.out.println("Error: file not found");
                return;
            }

            try {
                JSONParser jsonParser = new JSONParser();
                Object parsed = jsonParser.parse(new FileReader(file.getPath()));
                JSONObject jsonObject = (JSONObject) parsed;

                username = (String) jsonObject.get("username");
                password = (String) jsonObject.get("password");
                url = (String) jsonObject.get("url");


            }catch(ParseException | IOException e){
                e.printStackTrace();
                return;
            }

        }
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
