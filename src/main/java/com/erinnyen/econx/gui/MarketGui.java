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
    public final ItemStack exitBarrierBlock;
    public final ItemStack confirmPurchaseBlock;
    public final ItemStack cancelPurchaseBlock;
    public final ItemStack youTooPoorBlock;
    public final ItemStack yourOwnOrdersBlock;

    int sellId;


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
        cancelLore.add(ChatColor.GRAY + "Click here to cancel!");
        cancelMeta.setLore(cancelLore);
        cancelPurchaseBlock.setItemMeta(cancelMeta);


        youTooPoorBlock = new ItemStack(Material.BARRIER);
        ItemMeta youTooPoorMeta = youTooPoorBlock.getItemMeta();
        youTooPoorMeta.setDisplayName(ChatColor.RED + "You can't afford this item!");
        youTooPoorBlock.setItemMeta(youTooPoorMeta);

        yourOwnOrdersBlock = new ItemStack(Material.EMERALD);
        ItemMeta yourOwnOrdersMeta = yourOwnOrdersBlock.getItemMeta();
        yourOwnOrdersMeta.setDisplayName(ChatColor.BLUE + "Your sell orders.");
        List<String> yourOwnOrdersLore = new ArrayList<>();
        yourOwnOrdersLore.add(ChatColor.GRAY + "Click here to view the items your selling!");
        yourOwnOrdersMeta.setLore(yourOwnOrdersLore);
        yourOwnOrdersBlock.setItemMeta(yourOwnOrdersMeta);


        // Maybe add a refresh market block.

    }

    public Inventory createInventory(Player owner){

        ArrayList<ItemStack> openSellOrderItemStacks = new MarketDatabaseUtil(dbCreds).getSellOrdersItemStacks(owner.getName());
        Inventory inventory = Bukkit.createInventory(owner, 54, ChatColor.BLACK + "Items for Sale:");
        // Adding the barrier block in the lower right corner again
        inventory.setItem(53, exitBarrierBlock);
        inventory.setItem(45, yourOwnOrdersBlock);

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


    public Inventory createConfirmationInventory(Player owner, ItemStack item, int sellOrderId){


        sellId = sellOrderId;
        //Adding the sell orderId to the
        ItemMeta confirmMeta = confirmPurchaseBlock.getItemMeta();
        List<String> confirmLore = confirmPurchaseBlock.getItemMeta().getLore();
        confirmLore.add(Integer.toString(sellOrderId));
        confirmMeta.setLore(confirmLore);
        confirmPurchaseBlock.setItemMeta(confirmMeta);


        //Maybe add Price in title later.
        Inventory confirmInventory = Bukkit.createInventory(owner, 36, ChatColor.BLACK + "Please confirm your purchase!");
        confirmInventory.setItem(30, confirmPurchaseBlock);
        confirmInventory.setItem(32, cancelPurchaseBlock);
        //placeholder item


        // Just keeping the instance Price for the itemLore in the confirmation Inventory Gui.
        ItemMeta sellItemMeta = item.getItemMeta();
        List<String> sellItemLore = new ArrayList<String>();
        String instancePrice = item.getItemMeta().getLore().get(0);
        sellItemLore.add(instancePrice);
        sellItemMeta.setLore(sellItemLore);
        item.setItemMeta(sellItemMeta);

        confirmInventory.setItem(13, item);

        return confirmInventory;

    }
    public Inventory createOwnOrdersInventory(Player owner){
        Inventory inv = Bukkit.createInventory(owner, 54, ChatColor.BLACK + "Items you are selling:");
        inv.setItem(53, exitBarrierBlock);
        ArrayList<ItemStack> openSellOrderItemStacks = new MarketDatabaseUtil(dbCreds).getOwnSellOrders(owner.getName());

        for(Object item : openSellOrderItemStacks.toArray()){
            // I can only do the for loop with an Object so i have to cast ItemStack over it later.
            ItemStack itemStack = (ItemStack) item;
            inv.addItem(itemStack);
        }

        return inv;
    }

    public void updateOwnOrderInventory(Inventory inventory, Player owner){
        inventory.clear();
        ArrayList<ItemStack> openSellOrderItemStacks = new MarketDatabaseUtil(dbCreds).getOwnSellOrders(owner.getName());


        inventory.setItem(53, exitBarrierBlock);

        for(Object item : openSellOrderItemStacks.toArray()){
            // I can only do the for loop with an Object so i have to cast ItemStack over it later.
            ItemStack itemStack = (ItemStack) item;
            inventory.addItem(itemStack);
        }
    }

    public void cantAffordItem(Inventory inventory, int index){
        inventory.setItem(index, youTooPoorBlock);
    }
}
