package com.erinnyen.econx;

import com.erinnyen.econx.DBInteraction.DBCredentials;
import com.erinnyen.econx.DBInteraction.PlayerDBInteraction;
import com.erinnyen.econx.Listeners.ConnectionListeners;
import com.erinnyen.econx.econCommands.getCreditCommand;
import com.erinnyen.econx.econCommands.recentTransactionsCommand;
import com.erinnyen.econx.econCommands.sellCommand;
import com.erinnyen.econx.econCommands.sendCommand;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public final class EconX extends JavaPlugin {

    private final File path = new File(String.valueOf(this.getDataFolder()));

    @Override
    public void onEnable() {
        // Plugin startup logic

        DBCredentials dbCreds = new DBCredentials(path);

        final PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new ConnectionListeners(dbCreds), this);
        Objects.requireNonNull(this.getCommand("send")).setExecutor(new sendCommand(dbCreds));
        Objects.requireNonNull(this.getCommand("getcredit")).setExecutor(new getCreditCommand(dbCreds));
        Objects.requireNonNull(this.getCommand("recent")).setExecutor(new recentTransactionsCommand(dbCreds));
        Objects.requireNonNull(this.getCommand("sell")).setExecutor(new sellCommand());



    }

    @Override
    public void onLoad(){

        //plugin on load logic

        if(getDataFolder().mkdirs()){
            getLogger().info("Created Plugin directory");
            createDBCredsFile();
            Bukkit.getPluginManager().disablePlugin(this);

        }else {
            getLogger().info("Plugin directory already exists.");
            for(File file : Objects.requireNonNull(path.listFiles())) {
                if (!file.getName().equals("dbcreds.json")) {
                    getLogger().warning("Couldn't fetch credentials for Database connections.");
                    getLogger().warning("dbcreds.json doesn't exists yet.");

                    createDBCredsFile();
                    Bukkit.getPluginManager().disablePlugin(this);
                }
                getLogger().info("Found dbcreds.json");
                DBCredentials test_conn_creds = new DBCredentials(path);

                if (test_conn_creds.getUsername() != null || test_conn_creds.getPassword() != null || test_conn_creds.getUrl() != null) {
                    if (test_conn_creds.getUsername().equals("")) {
                        getLogger().warning("Please specify a username for the DB connection in dbcreds.json");
                        Bukkit.getPluginManager().disablePlugin(this);
                    }
                    if (test_conn_creds.getPassword().equals("")) {
                        getLogger().warning("Please specify a password for the DB connection in dbcreds.json");
                        Bukkit.getPluginManager().disablePlugin(this);
                    }
                    if (test_conn_creds.getUrl().equals("")) {
                        getLogger().warning("Please specify a url for the DB connection in dbcreds.json");
                        Bukkit.getPluginManager().disablePlugin(this);
                    }
                    PlayerDBInteraction test_conn = new PlayerDBInteraction(test_conn_creds);
                    if (test_conn.testConnection()) {
                        getLogger().info("Database connection established.");
                        getLogger().info("All set good to go");
                        return;
                    }
                    getLogger().warning("Something went wrong with the database connection!");
                    getLogger().warning("Please check your database credentials.");
                    Bukkit.getPluginManager().disablePlugin(this);
                }
                getLogger().warning("Couldn't extract username, password and url from dbcreds.json");
                Bukkit.getPluginManager().disablePlugin(this);
            }
        }
    }

    public void createDBCredsFile(){

        File dbCredsJSONFIle = new File(path + "dbcreds.json");

        try {
            if(dbCredsJSONFIle.createNewFile()){
                getLogger().info("Created the dbcreds.json file");
                getLogger().info("Please enter your database credentials in the file");
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

}
