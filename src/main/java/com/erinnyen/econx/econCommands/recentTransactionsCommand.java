package com.erinnyen.econx.econCommands;

import com.erinnyen.econx.DBInteraction.DBCredentials;
import com.erinnyen.econx.DBInteraction.DBInteraction;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class recentTransactionsCommand implements CommandExecutor {

    DBCredentials dbCreds;

    public recentTransactionsCommand(DBCredentials pDBCreds){
        dbCreds = pDBCreds;

    }
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        String header = ChatColor.LIGHT_PURPLE + "[Bank] " + ChatColor.WHITE;

        if(label.equalsIgnoreCase("recent") || label.equalsIgnoreCase("recentTransaction") || label.equalsIgnoreCase("getrecent")){
            if(sender instanceof Player){
                Player player = (Player) sender;

                DBInteraction dbQuery = new DBInteraction(dbCreds);
                String playerName = player.getName();
                ArrayList<String> transactions = dbQuery.getRecentTransactions(playerName);

                if(transactions == null){
                    sender.sendMessage(header + "You don't have any recent transactions to view.");
                    return false;
                }

                for(int i = 0; i< transactions.size(); i++){
                    sender.sendMessage(header + transactions.get(i));
                }
                return true;
            }
            sender.sendMessage(header + ChatColor.DARK_RED +"You have to be a player to use this command!");
        }
        return false;
    }
}
