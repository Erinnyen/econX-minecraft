package com.erinnyen.econx.econCommands;

import com.erinnyen.econx.DBInteraction.DBCredentials;
import com.erinnyen.econx.DBInteraction.DBInteraction;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class recentTransactionsCommand implements CommandExecutor {

    DBCredentials dbCreds;

    public recentTransactionsCommand(DBCredentials pDBCreds){
        dbCreds = pDBCreds;

    }
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        String header = ChatColor.LIGHT_PURPLE + "[Bank] ";

        if(label.equalsIgnoreCase("recentTransactions")){
            if(sender instanceof Player){
                Player player = (Player) sender;

                DBInteraction dbQuery = new DBInteraction(dbCreds);

                String playerName = player.getName();

                dbQuery.getRecentTransactions(playerName);


            }
        }
        return false;
    }
}
