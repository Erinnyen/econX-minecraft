package com.erinnyen.econx.econcommands.admin;

import com.erinnyen.econx.gui.MarketNPC;
import com.erinnyen.econx.util.Util;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SetMarketNPC implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {


        if(!(sender instanceof Player)) {
            sender.sendMessage(Util.NOT_PLAYER);
            return true;
        }
        Player player = (Player) sender;
        if(!player.hasPermission("econX.admin")){
            sender.sendMessage(Util.PERM_ERR);
            return true;
        }

        if(args.length != 0) return false;

        new MarketNPC(player.getLocation());
        sender.sendMessage(Util.ECONX_HEADER + " Market NPC creation was successful.");
        return true;
    }
}
