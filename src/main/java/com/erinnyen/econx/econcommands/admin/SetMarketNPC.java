package com.erinnyen.econx.econcommands.admin;

import com.erinnyen.econx.gui.MarketNPC;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SetMarketNPC implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        String header = ChatColor.AQUA + "[EconX]" + ChatColor.RESET;
        if(!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.DARK_RED + " You have to be a player to use this command.");
            return true;
        }
        Player player = (Player) sender;
        if(!player.hasPermission("econX.admin")){
            sender.sendMessage(ChatColor.DARK_RED + " You don't have the permissions to use this command.");
            return true;
        }

        if(args.length != 0) return false;

        new MarketNPC(player.getLocation());
        sender.sendMessage(header + "Market NPC creation was successful.");
        return true;
    }
}
