package com.erinnyen.econx.econcommands.market;

import com.erinnyen.econx.dbinteraction.DatabaseCredentials;
import com.erinnyen.econx.dbinteraction.MarketDatabaseUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class MarketGUI implements CommandExecutor {

    String header = ChatColor.LIGHT_PURPLE + "[Market]" + ChatColor.RESET;
    DatabaseCredentials dbCreds;
    Inventory inv;

    public MarketGUI(DatabaseCredentials pDbCreds){
        dbCreds = pDbCreds;

    }
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {



        if(!label.equalsIgnoreCase("gui")){
            return false;
        }
        if(!(sender instanceof Player)){
            sender.sendMessage(header + ChatColor.RED + "You have to be a Player to use this command!");
            return false;
        }

        Player player = (Player) sender;
        inv = Bukkit.createInventory(null, 27, ChatColor.LIGHT_PURPLE + "Items for Sale:");

        MarketDatabaseUtil sellOrders = new MarketDatabaseUtil(dbCreds);
        ArrayList<ItemStack> itemsForSale = sellOrders.getSellOrdersItemStacks(player.getName());

        try {
            for (int i = 0; i < itemsForSale.toArray().length; i++) {
                ItemStack item = itemsForSale.get(i);
                inv.addItem(item);
            }
        } catch (NullPointerException e) {
            sender.sendMessage(header + ChatColor.RED + " There are no viewable offers.");
            return false;
        }


        return true;
    }
}
