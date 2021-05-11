package com.erinnyen.econx;

import com.erinnyen.econx.Listeners.ConnectionListeners;
import com.erinnyen.econx.econCommands.getCreditCommand;
import com.erinnyen.econx.econCommands.sendCommand;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class EconX extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic



        final PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new ConnectionListeners(), this);
        this.getCommand("send").setExecutor(new sendCommand());
        this.getCommand("getcredit").setExecutor(new getCreditCommand());

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
