package com.erinnyen.econx;

import com.erinnyen.econx.DBInteraction.DBCredentials;
import com.erinnyen.econx.DBInteraction.DBInteraction;
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

        if(true){

        }

        DBCredentials dbCreds = getDBcredsFromJSON();

        final PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new ConnectionListeners(dbCreds), this);
        Objects.requireNonNull(this.getCommand("send")).setExecutor(new sendCommand(dbCreds));
        Objects.requireNonNull(this.getCommand("getcredit")).setExecutor(new getCreditCommand(dbCreds));
        Objects.requireNonNull(this.getCommand("recent")).setExecutor(new recentTransactionsCommand(dbCreds));



    }

    @Override
    public void onLoad(){

        //plugin on load logic

        if(getDataFolder().mkdirs()){
            getLogger().info("Created Plugin directory");
            createDBCredsFile();

        }else {
            getLogger().info("Plugin directory already exists.");
            for(File file : Objects.requireNonNull(path.listFiles())){
                if(!file.getName().equals("dbcreds.json")){
                    getLogger().warning("Couldn't fetch credentials for Database connections.");
                    getLogger().warning("dbcreds.json doesn't exists yet.");

                    createDBCredsFile();
                    return;
                }
                DBCredentials test_conn_creds = getDBcredsFromJSON();
                if(test_conn_creds.getUsername().equals("")){
                    getLogger().warning("Please specify a username for the DB connection in dbcreds.json");
                    return;
                }
                if(test_conn_creds.getPassword().equals("")){
                    getLogger().warning("Please specify a password for the DB connection in dbcreds.json");
                    return;
                }
                if(test_conn_creds.getUrl().equals("")){
                    getLogger().warning("Please specify a url for the DB connection in dbcreds.json");
                    return;
                }
                DBInteraction test_conn = new DBInteraction(test_conn_creds);
                if(test_conn.testConnection()){
                    getLogger().info("Database connection established.");
                    getLogger().info("All set good to go");
                    return;
                }
                getLogger().warning("Something went wrong with the database connection!");
                getLogger().warning("Please check your database credentials.");
            }
        }
    }

    public void createDBCredsFile(){

        File dbCredsJSONFIle = new File(path + "dbcreds.json");

        try {
            if(dbCredsJSONFIle.createNewFile()){
                getLogger().info("Created the dbcreds.json file");
                getLogger().info("Please enter your database credentials in the file");
                return;
            }else{
                getLogger().warning("Somehow dbcreds.json already exist. I have no clue why though");
            }

        }catch (IOException e){
            getLogger().warning("An error occurred while creating the dbcreds.json file");
            e.printStackTrace();
        }



    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
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
