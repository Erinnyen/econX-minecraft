package com.erinnyen.econx.econcommands.market;

import com.erinnyen.econx.dbinteaction.DatabaseCredentials;
import com.erinnyen.econx.dbinteaction.MarketDBInteraction;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class ViewSellOrdersCommand implements CommandExecutor {

    String header = ChatColor.LIGHT_PURPLE + "[Market]" + ChatColor.RESET;
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
        if(args.length != 0){
            sender.sendMessage("Usage:");
            return false;
        }

        ArrayList<String> openOrders = new MarketDBInteraction(dbCreds).getOpenSellOrders();

        if(openOrders == null){
            sender.sendMessage(header + "There are no open sell orders at this time.");
            return false;
        }

        sender.sendMessage(header + " Open sell orders:");
        for(int i = 0; i< openOrders.size(); i++){
            sender.sendMessage(openOrders.get(i));
        }
        return true;

    }
}
