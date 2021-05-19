package com.erinnyen.econx.econCommands.banking;

import com.erinnyen.econx.DBInteraction.DBCredentials;
import com.erinnyen.econx.DBInteraction.PlayerDatabaseUtil;
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


                PlayerDatabaseUtil dbQuery = new PlayerDatabaseUtil(dbCreds);
                String playerName = player.getName();
                ArrayList<String> transactions = dbQuery.getRecentTransactions(playerName, 9);
                //limits the output to pLength

                if(transactions == null){
                    sender.sendMessage(header + "You don't have any recent transactions to view.");
                    return true;
                }
                sender.sendMessage(header + "recent transactions:");
                for(int i = 0; i< transactions.size(); i++){
                    sender.sendMessage(transactions.get(i));
                }
                return true;
            }
            sender.sendMessage(header + ChatColor.DARK_RED +"You have to be a player to use this command!");
        }
        return false;
    }
}
