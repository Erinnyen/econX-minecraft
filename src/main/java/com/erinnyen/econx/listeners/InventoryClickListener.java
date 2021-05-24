package com.erinnyen.econx.listeners;

import com.erinnyen.econx.dbinteraction.DatabaseCredentials;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class InventoryClickListener implements Listener {


    DatabaseCredentials dbCreds;
    Inventory shopInventory;

    public InventoryClickListener(DatabaseCredentials pDBcreds, Inventory pShopInventory){
        dbCreds = pDBcreds;
        shopInventory = pShopInventory;

    }

    @EventHandler
    public void onClick(InventoryClickEvent event){
        if(!event.getInventory().equals(shopInventory)){
            return;
        }
        if(event.getCurrentItem() == null){
            return;
        }
        if(event.getCurrentItem().getItemMeta() == null){
            return;
        }
        if (event.getCurrentItem().getItemMeta().getDisplayName() == null){
            return;
        }
        event.setCancelled(true);
        Player player = (Player) event.getWhoClicked();


        if(event.getSlot() == 0){
            player.getInventory().addItem(new ItemStack(Material.COAL));
        }

        if(event.getSlot() == 53){
            player.closeInventory();
        }
    }
}
