package com.erinnyen.econx.econcommands.banking;

import com.erinnyen.econx.dbinteraction.DatabaseCredentials;
import com.erinnyen.econx.dbinteraction.PlayerDatabaseUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class getCreditCommand implements CommandExecutor {

    DatabaseCredentials dbCreds;

    public getCreditCommand(DatabaseCredentials pDBCreds){
        dbCreds = pDBCreds;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        String header = ChatColor.LIGHT_PURPLE + "[Bank] ";

        if (label.equalsIgnoreCase("getCredit") || label.equalsIgnoreCase("credit")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;

                PlayerDatabaseUtil creditQuery = new PlayerDatabaseUtil(dbCreds);

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
