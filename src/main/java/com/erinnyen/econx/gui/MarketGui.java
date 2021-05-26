package com.erinnyen.econx.gui;

import com.erinnyen.econx.dbinteraction.DatabaseCredentials;
import com.erinnyen.econx.dbinteraction.MarketDatabaseUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class MarketGui {
    private DatabaseCredentials dbCreds;
    ItemStack exitBarrierBlock;



    public MarketGui(DatabaseCredentials pDBCreds){
        dbCreds = pDBCreds;



        exitBarrierBlock = new ItemStack(Material.BARRIER);
        ItemMeta meta = exitBarrierBlock.getItemMeta();
        meta.setDisplayName(ChatColor.RED + "Close Shop");
        List<String> lore = new ArrayList<String>();
        lore.add(ChatColor.GRAY + "Click here to close!");
        meta.setLore(lore);
        exitBarrierBlock.setItemMeta(meta);


    }

    public Inventory createInventory(Player owner){

        ArrayList<ItemStack> openSellOrderItemStacks = new MarketDatabaseUtil(dbCreds).getSellOrdersItemStacks(owner.getName());
        Inventory inventory = Bukkit.createInventory(owner, 54, ChatColor.BLACK + "Items for Sale:");
        // Adding the barrier block in the lower right corner again
        inventory.setItem(53, exitBarrierBlock);

        for(Object item : openSellOrderItemStacks.toArray()){
            // I can only do the for loop with an Object so i have to cast ItemStack over it later.
            ItemStack itemStack = (ItemStack) item;
            inventory.addItem(itemStack);
        }

        return inventory;
    }

    public void updateInv(Inventory inventory, Player owner){
        inventory.clear();
        ArrayList<ItemStack> openSellOrderItemStacks = new MarketDatabaseUtil(dbCreds).getSellOrdersItemStacks(owner.getName());


        inventory.setItem(53, exitBarrierBlock);

        for(Object item : openSellOrderItemStacks.toArray()){
            // I can only do the for loop with an Object so i have to cast ItemStack over it later.
            ItemStack itemStack = (ItemStack) item;
            inventory.addItem(itemStack);
        }
    }
}
