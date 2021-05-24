package com.erinnyen.econx;

import com.erinnyen.econx.dbinteraction.DatabaseCredentials;
import com.erinnyen.econx.dbinteraction.PlayerDatabaseUtil;
import com.erinnyen.econx.econcommands.market.MarketGUI;
import com.erinnyen.econx.listeners.ConnectionListeners;
import com.erinnyen.econx.econcommands.banking.getCreditCommand;
import com.erinnyen.econx.econcommands.banking.recentTransactionsCommand;
import com.erinnyen.econx.econcommands.market.BuyCommand;
import com.erinnyen.econx.econcommands.market.sellCommand;
import com.erinnyen.econx.econcommands.banking.sendCommand;
import com.erinnyen.econx.econcommands.market.ViewSellOrdersCommand;
import com.erinnyen.econx.listeners.InventoryClickListener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class EconX extends JavaPlugin {

    private final File path = new File(String.valueOf(this.getDataFolder()));
    public Inventory marketGuiInventory;


    @Override
    public void onEnable() {
        // Plugin startup logic

        DatabaseCredentials dbCreds = new DatabaseCredentials(path);
        createInv();

        final PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new ConnectionListeners(dbCreds), this);
        pluginManager.registerEvents(new InventoryClickListener(dbCreds, marketGuiInventory), this);
        Objects.requireNonNull(this.getCommand("send")).setExecutor(new sendCommand(dbCreds));
        Objects.requireNonNull(this.getCommand("getcredit")).setExecutor(new getCreditCommand(dbCreds));
        Objects.requireNonNull(this.getCommand("recent")).setExecutor(new recentTransactionsCommand(dbCreds));
        Objects.requireNonNull(this.getCommand("sell")).setExecutor(new sellCommand(dbCreds));
        Objects.requireNonNull(this.getCommand("viewsellorders")).setExecutor(new ViewSellOrdersCommand(dbCreds));
        Objects.requireNonNull(this.getCommand("buy")).setExecutor(new BuyCommand(dbCreds));
        Objects.requireNonNull(this.getCommand("gui")).setExecutor(new MarketGUI(dbCreds, marketGuiInventory));




    }

    @Override
    public void onLoad(){

        //plugin on load logic

        if(getDataFolder().mkdirs()){
            getLogger().info("Created Plugin directory");
            createDatabaseCredentialsFile();
            Bukkit.getPluginManager().disablePlugin(this);

        }else {
            getLogger().info("Plugin directory already exists.");
            for(File file : Objects.requireNonNull(path.listFiles())) {
                if (!file.getName().equals("dbcreds.json")) {
                    getLogger().warning("Couldn't fetch credentials for Database connections.");
                    getLogger().warning("dbcreds.json doesn't exists yet.");

                    createDatabaseCredentialsFile();
                    Bukkit.getPluginManager().disablePlugin(this);
                }
                getLogger().info("Found dbcreds.json");
                DatabaseCredentials test_conn_creds = new DatabaseCredentials(path);

                if (test_conn_creds.getUsername() != null || test_conn_creds.getPassword() != null || test_conn_creds.getUrl() != null) {
                    // I know I am kinda of checking twice here, but i don't have a better way to do it.
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
                    PlayerDatabaseUtil test_conn = new PlayerDatabaseUtil(test_conn_creds);
                    if (test_conn.testConnection()) {
                        getLogger().info("Database connection established.");
                        getLogger().info("All set good to go.");
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

    public void createDatabaseCredentialsFile(){

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


    public void createInv(){

        marketGuiInventory = Bukkit.createInventory(null, 54, ChatColor.LIGHT_PURPLE + "Items for Sale:");

        ItemStack item = new ItemStack(Material.COAL);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("(Id: " + "3" + ") " + ChatColor.GOLD + "40.0" + "C");
        List<String> lore = new ArrayList<String>();
        lore.add(ChatColor.GRAY + "(" + "490" + "C per item)");
        meta.setLore(lore);
        item.setItemMeta(meta);
        item.setAmount(32);

        marketGuiInventory.setItem(0, item);

        item = new ItemStack(Material.BARRIER);
        meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.RED + "Close Shop");
        item.setItemMeta(meta);

        marketGuiInventory.setItem(53, item);

    }

}
