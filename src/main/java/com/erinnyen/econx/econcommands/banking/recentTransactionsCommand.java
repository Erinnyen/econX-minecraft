package com.erinnyen.econx.econcommands.banking;

import com.erinnyen.econx.dbinteraction.DatabaseCredentials;
import com.erinnyen.econx.dbinteraction.PlayerDatabaseUtil;
import com.erinnyen.econx.util.Util;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class recentTransactionsCommand implements CommandExecutor {

    DatabaseCredentials dbCreds;

    public recentTransactionsCommand(DatabaseCredentials pDBCreds){
        dbCreds = pDBCreds;

    }
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if(label.equalsIgnoreCase("recent") || label.equalsIgnoreCase("recentTransaction") || label.equalsIgnoreCase("getrecent")){
            if(sender instanceof Player){
                Player player = (Player) sender;


                PlayerDatabaseUtil dbQuery = new PlayerDatabaseUtil(dbCreds);
                String playerName = player.getName();
                ArrayList<String> transactions = dbQuery.getRecentTransactions(playerName, 9);
                //limits the output to pLength

                if(transactions == null){
                    sender.sendMessage(Util.BANK_HEADER + ChatColor.YELLOW + "You don't have any recent transactions to view.");
                    return true;
                }
                sender.sendMessage(Util.BANK_HEADER + "Recent transactions:");
                for(int i = 0; i< transactions.size(); i++){
                    sender.sendMessage(transactions.get(i));
                }
                return true;
            }
            sender.sendMessage(Util.NOT_PLAYER);
        }
        return false;
    }
}
