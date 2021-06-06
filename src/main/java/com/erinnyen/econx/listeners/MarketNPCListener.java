package com.erinnyen.econx.listeners;

import com.erinnyen.econx.gui.MarketNPC;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class MarketNPCListener implements Listener {

    String header = ChatColor.AQUA + "[EconX]" + ChatColor.RESET;

    @EventHandler
    public void rightClickInteraction(PlayerInteractEntityEvent event){
        if(!(event.getRightClicked() instanceof Villager)) return;

        Villager trader = (Villager) event.getRightClicked();

        if(trader.getCustomName().equals(MarketNPC.VENDOR_NAME)){
            event.setCancelled(true);
            Player player = (Player) event.getPlayer();
            player.sendMessage("It worked");


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
                player.sendMessage(header + " You deleted the shop!");
            }
        }

        return;
    }
}
