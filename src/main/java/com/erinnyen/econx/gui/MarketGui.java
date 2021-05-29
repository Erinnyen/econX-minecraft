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
    ItemStack confirmPurchaseBlock;
    ItemStack cancelPurchaseBlock;


    public MarketGui(DatabaseCredentials pDBCreds){
        dbCreds = pDBCreds;

        exitBarrierBlock = new ItemStack(Material.BARRIER);
        ItemMeta meta = exitBarrierBlock.getItemMeta();
        meta.setDisplayName(ChatColor.RED + "Close Shop");
        List<String> lore = new ArrayList<String>();
        lore.add(ChatColor.GRAY + "Click here to close!");
        meta.setLore(lore);
        exitBarrierBlock.setItemMeta(meta);


        confirmPurchaseBlock = new ItemStack(Material.GREEN_CONCRETE);
        ItemMeta confirmMeta = confirmPurchaseBlock.getItemMeta();
        confirmMeta.setDisplayName(ChatColor.GREEN + "Confirm Purchase!");
        List<String> confirmLore = new ArrayList<String>();
        confirmLore.add(ChatColor.GRAY + "Click here to confirm!");
        confirmMeta.setLore(confirmLore);
        confirmPurchaseBlock.setItemMeta(confirmMeta);

        cancelPurchaseBlock = new ItemStack(Material.RED_CONCRETE);
        ItemMeta cancelMeta = cancelPurchaseBlock.getItemMeta();
        cancelMeta.setDisplayName(ChatColor.RED + "Cancel Purchase!");
        List<String> cancelLore = new ArrayList<String>();
        cancelLore.add(ChatColor.GRAY + "Click here to confirm!");
        cancelMeta.setLore(confirmLore);
        cancelPurchaseBlock.setItemMeta(cancelMeta);


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


    public Inventory createConfirmationInventory(Player owner, ItemStack item){

        //Maybe add Price in title later.
        Inventory confirmInventory = Bukkit.createInventory(owner, 36, ChatColor.BLACK + "Please confirm your purchase!");
        confirmInventory.setItem(30, confirmPurchaseBlock);
        confirmInventory.setItem(32, cancelPurchaseBlock);
        //placeholder item

        // Removing the "click to buy!" from the Item Lore
        ItemMeta sellItemMeta = item.getItemMeta();
        List<String> sellItemLore = sellItemMeta.getLore();
        sellItemLore.set(2, "");
        sellItemMeta.setLore(sellItemLore);
        item.setItemMeta(sellItemMeta);

        confirmInventory.setItem(13, item);




        return confirmInventory;

    }
}
