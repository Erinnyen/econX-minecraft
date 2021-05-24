package com.erinnyen.econx.listeners;

import com.erinnyen.econx.dbinteraction.Buy;
import com.erinnyen.econx.dbinteraction.DatabaseCredentials;
import com.erinnyen.econx.gui.MarketGui;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class InventoryClickListener implements Listener {

    String header = ChatColor.LIGHT_PURPLE + "[Market]" + ChatColor.RESET;
    DatabaseCredentials dbCreds;
    MarketGui marketGui;

    public InventoryClickListener(DatabaseCredentials pDBcreds, MarketGui pMarketGui){
        dbCreds = pDBcreds;
        marketGui = pMarketGui;

    }

    @EventHandler
    public void onClick(InventoryClickEvent event){
        if(!event.getInventory().equals(marketGui.inv)){
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

        if(event.getSlot() == 53){
            player.closeInventory();
            return;
        }
        if(marketGui.orderIDsGui.get(event.getSlot()) != null){ // Error prevention.
            Buy buy = new Buy(dbCreds, marketGui.orderIDsGui.get(event.getSlot()), player);
            String buyFeedback = buy.executeBuy();
            if(buyFeedback != null){
                player.sendMessage(buyFeedback);
                return;
            }
            marketGui.addSellOrders(player.getName());
            player.sendMessage(header + ChatColor.BOLD + " Transactions completed.");

        }else{
            player.sendMessage(header + ChatColor.DARK_RED + " Something went wrong with executing the buy order.");
        }

        return;
    }
}
