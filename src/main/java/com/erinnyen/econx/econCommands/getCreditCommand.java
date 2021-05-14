package com.erinnyen.econx.econCommands;

import com.erinnyen.econx.DBInteraction.DBCredentials;
import com.erinnyen.econx.DBInteraction.DBInteraction;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class getCreditCommand implements CommandExecutor {

    DBCredentials dbCreds;

    public getCreditCommand(DBCredentials pDBCreds){
        dbCreds = pDBCreds;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        String header = ChatColor.LIGHT_PURPLE + "[Bank] ";

        if (label.equalsIgnoreCase("getCredit")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;

                DBInteraction creditQuery = new DBInteraction(dbCreds);

                String player_1 = player.getName();
                double credit = creditQuery.getCredit(player_1);
                if(credit == -1){
                    sender.sendMessage(header + ChatColor.DARK_RED + "Something went wrong. Could not complete request.");
                    return false;
                }

                player.sendMessage(header + ChatColor.WHITE + "You're credit is currently: "
                        + ChatColor.GOLD + credit + "C");
                return true;
            }
            sender.sendMessage(header + ChatColor.DARK_RED +"You have to be a player to use this command!");
        }
        return false;
    }
}
