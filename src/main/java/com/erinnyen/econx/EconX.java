package com.erinnyen.econx;

import com.erinnyen.econx.DBInteraction.DBCredentials;
import com.erinnyen.econx.Listeners.ConnectionListeners;
import com.erinnyen.econx.econCommands.getCreditCommand;
import com.erinnyen.econx.econCommands.recentTransactionsCommand;
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

    private final File path = new File(String.valueOf(this.getDataFolder()));

    @Override
    public void onEnable() {
        // Plugin startup logic

        DBCredentials dbCreds = getDBcredsFromJSON();

        final PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new ConnectionListeners(dbCreds), this);
        Objects.requireNonNull(this.getCommand("send")).setExecutor(new sendCommand(dbCreds));
        Objects.requireNonNull(this.getCommand("getcredit")).setExecutor(new getCreditCommand(dbCreds));
        Objects.requireNonNull(this.getCommand("recent")).setExecutor(new recentTransactionsCommand(dbCreds));



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

                return new DBCredentials(username, password, url);

            }catch(ParseException | IOException e){

                e.printStackTrace();
                return null;
            }

        }
        return null;
    }
}
