package com.erinnyen.econx;

import com.erinnyen.econx.DBInteraction.DBCredentials;
import com.erinnyen.econx.Listeners.ConnectionListeners;
import com.erinnyen.econx.econCommands.getCreditCommand;
import com.erinnyen.econx.econCommands.sendCommand;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Objects;

public final class EconX extends JavaPlugin {

    private File path = new File(this.getDataFolder() + "/econX");

    @Override
    public void onEnable() {
        // Plugin startup logic


        final PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new ConnectionListeners(), this);
        this.getCommand("send").setExecutor(new sendCommand());
        this.getCommand("getcredit").setExecutor(new getCreditCommand());
        System.out.println(path);
        System.out.println(path.listFiles());

        //DBCredentials dbCreds = getDBcredsFromJSON();


    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public void onFirstStartup(){

    }

    public DBCredentials getDBcredsFromJSON(){

        for(File file : Objects.requireNonNull(path.listFiles())){
            if(!file.getName().equals("dbcreds.json")){
                System.out.println("Error: file not found");
                return null;

            }

            try {
                JSONParser jsonParser = new JSONParser();
                Object parsed = jsonParser.parse(new FileReader(file.getPath()));
                JSONObject jsonObject = (JSONObject) parsed;

                String username = (String) jsonObject.get("username");
                String password = (String) jsonObject.get("password");
                String url = (String) jsonObject.get("url");

                System.out.println(username);
                System.out.println(password);
                System.out.println(url);

                DBCredentials dbCreds = new DBCredentials(username, password, url);

                return dbCreds;

            }catch(ParseException | IOException e){

                e.printStackTrace();
                return null;
            }

        }
        return null;
    }
}
