package com.erinnyen.econx.listeners;

import com.erinnyen.econx.dbinteraction.DatabaseCredentials;
import com.erinnyen.econx.gui.MarketGui;
import com.erinnyen.econx.gui.MarketNPC;
import com.erinnyen.econx.util.Util;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.Inventory;

public class MarketNPCListener implements Listener {

    private final DatabaseCredentials DBCREDS;

    public MarketNPCListener(DatabaseCredentials databaseCredentials){
        DBCREDS = databaseCredentials;
    }

    @EventHandler
    public void rightClickInteraction(PlayerInteractEntityEvent event){
        if(!(event.getRightClicked() instanceof Villager)) return;

        Villager trader = (Villager) event.getRightClicked();

        if(trader.getCustomName().equals(MarketNPC.VENDOR_NAME)){
            event.setCancelled(true);
            Player player = (Player) event.getPlayer();
            MarketGui playerMarketInventoryGui = new MarketGui(DBCREDS);
            Inventory playerMarketInv = playerMarketInventoryGui.createInventory(player);
            player.openInventory(playerMarketInv);


        }

    }

    @EventHandler
    public void shopDamage(EntityDamageByEntityEvent event){
        if(!(event.getEntity() instanceof Villager)) return;

        Villager shop = (Villager) event.getEntity();

        if(!shop.getCustomName().equals(MarketNPC.VENDOR_NAME)) return;

        event.setCancelled(true);
        if(!(event.getDamager() instanceof Player)) return;
        Player player = (Player) event.getDamager();

        if(player.hasPermission("econX.admin")){
            if(player.getInventory().getItemInMainHand().getType().equals(Material.WATER_BUCKET)){
                shop.setHealth(0);
                player.sendMessage(Util.ECONX_HEADER + "You deleted the shop!");
            }
        }

        return;
    }
}
