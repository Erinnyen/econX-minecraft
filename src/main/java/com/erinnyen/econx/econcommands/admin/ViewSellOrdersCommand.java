package com.erinnyen.econx.econcommands.admin;

import com.erinnyen.econx.dbinteraction.DatabaseCredentials;
import com.erinnyen.econx.dbinteraction.MarketDatabaseUtil;
import com.erinnyen.econx.util.Util;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class ViewSellOrdersCommand implements CommandExecutor {

    DatabaseCredentials dbCreds;

    public ViewSellOrdersCommand(DatabaseCredentials pDBcreds){
        dbCreds = pDBcreds;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        /*
            This is going to be a temporary command, that only will be used for console applications.
            (This command can be executed as a non-player.
            In the long run I want to implement a chest gui to see the open orders.
         */

        if(!label.equalsIgnoreCase("viewsellorders")){
            return false;
        }

        if(!sender.hasPermission("econx.admin")){
            sender.sendMessage(Util.PERM_ERR);
            return true;
        }

        if(args.length != 0){
            sender.sendMessage("Usage:");
            return false;
        }

        ArrayList<String> openOrders = new MarketDatabaseUtil(dbCreds).getOpenSellOrders();

        if(openOrders == null){
            sender.sendMessage(Util.MARKET_HEADER + "There are no open sell orders at this time.");
            return false;
        }

        sender.sendMessage(Util.MARKET_HEADER + " Open sell orders:");
        for(int i = 0; i< openOrders.size(); i++){
            sender.sendMessage(openOrders.get(i));
        }
        return true;

    }
}
