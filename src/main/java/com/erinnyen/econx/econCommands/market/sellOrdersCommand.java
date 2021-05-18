package com.erinnyen.econx.econCommands.market;

import com.erinnyen.econx.DBInteraction.DBCredentials;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class sellOrdersCommand implements CommandExecutor {

    String header = ChatColor.LIGHT_PURPLE + "[Market]" + ChatColor.RESET;
    DBCredentials dbCreds;

    public sellOrdersCommand(DBCredentials pDBcreds){
        dbCreds = pDBcreds;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        /*
            This is going to be a temporary command, that only will be used for console applications.
            (This command can be executed as a non-player.
            In the long run I want to implement a chest gui to see the open orders.
         */

        if(!label.equalsIgnoreCase("sellorders")){
            return false;
        }
        if(args.length != 1){
            sender.sendMessage("Usage:");
            return false;
        }


        return false;
    }
}
