package com.erinnyen.econx.econcommands.market;

import com.erinnyen.econx.dbinteraction.DatabaseCredentials;
import com.erinnyen.econx.gui.MarketGui;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;



public class MarketGuiCommand implements CommandExecutor {

    String header = ChatColor.LIGHT_PURPLE + "[Market]" + ChatColor.RESET;
    private DatabaseCredentials dbCreds;

    public MarketGuiCommand(DatabaseCredentials pDbCreds){
        dbCreds = pDbCreds;

    }
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if(!label.equalsIgnoreCase("creatshop")) return false;

        if(!label.equalsIgnoreCase("gui")){
            return false;
        }
        if(!(sender instanceof Player)){
            sender.sendMessage(header + ChatColor.RED + " You have to be a Player to use this command!");
            return false;
        }
        Player player = (Player) sender;

        MarketGui playerMarketInventoryGui = new MarketGui(dbCreds);
        Inventory playerMarketInv = playerMarketInventoryGui.createInventory(player);
        player.openInventory(playerMarketInv);

        return true;
    }


}
