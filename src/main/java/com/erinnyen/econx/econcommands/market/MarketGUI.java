package com.erinnyen.econx.econcommands.market;

import com.erinnyen.econx.dbinteraction.DatabaseCredentials;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;


public class MarketGUI implements CommandExecutor {

    String header = ChatColor.LIGHT_PURPLE + "[Market]" + ChatColor.RESET;
    DatabaseCredentials dbCreds;
    Inventory marketGuiInventory;

    public MarketGUI(DatabaseCredentials pDbCreds, Inventory pInventory){
        dbCreds = pDbCreds;
        marketGuiInventory = pInventory;

    }
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if(!label.equalsIgnoreCase("gui")){
            return false;
        }
        if(!(sender instanceof Player)){
            sender.sendMessage(header + ChatColor.RED + "You have to be a Player to use this command!");
            return false;
        }

        Player player = (Player) sender;
        player.openInventory(marketGuiInventory);

        return true;
    }


}
