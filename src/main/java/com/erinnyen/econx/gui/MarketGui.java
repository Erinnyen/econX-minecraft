package com.erinnyen.econx.gui;

import com.erinnyen.econx.dbinteraction.DatabaseCredentials;
import com.erinnyen.econx.dbinteraction.MarketDatabaseUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class MarketGui {

    public Inventory inv;
    private DatabaseCredentials dbCreds;
    ItemStack exitBarrierBlock;
    // Creating an ArrayList, where the index is the slot and the value the order_id.
    public ArrayList<Integer> orderIDsGui;


    public MarketGui(DatabaseCredentials pDBCreds){
        dbCreds = pDBCreds;

        inv = Bukkit.createInventory(null, 54, ChatColor.BLACK + "Items for Sale:");

        exitBarrierBlock = new ItemStack(Material.BARRIER);
        ItemMeta meta = exitBarrierBlock.getItemMeta();
        meta.setDisplayName(ChatColor.RED + "Close Shop");
        List<String> lore = new ArrayList<String>();
        lore.add(ChatColor.GRAY + "Click here to close!");
        meta.setLore(lore);
        exitBarrierBlock.setItemMeta(meta);

        inv.setItem(53, exitBarrierBlock);


    }

    public void addSellOrders(String pPlayerName){

        ArrayList<ItemStack> openSellOrderItemStacks = new MarketDatabaseUtil(dbCreds).getSellOrdersItemStacks(pPlayerName);
        inv.clear();
        // Adding the barrier block in the lower right corner again.
        inv.setItem(53, exitBarrierBlock);
        ArrayList<Integer> list = new ArrayList<Integer>();

        for(Object item : openSellOrderItemStacks.toArray()){
            // I can only do the for loop with an Object so i have to cast ItemStack over it later.
            ItemStack itemStack = (ItemStack) item;
            inv.addItem(itemStack);
            // The order_id is the third entry in the lore.
            String orderIdString = itemStack.getLore().get(2);


            int orderIdInteger = Integer.parseInt(orderIdString);
            //orderIDsGui.add(orderIdInteger);
            list.add(orderIdInteger);
        }
        orderIDsGui = list;
    }
}
