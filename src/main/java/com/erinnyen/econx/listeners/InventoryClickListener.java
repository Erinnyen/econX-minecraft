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

        //Ignoring all the click events, that aren't within our shop
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
        if(event.getCurrentItem().getItemMeta().getLore() == null){
            return;
        }

        /*
            Only if the criteria above aren't met, then we'll set the cancelled to true.
            This means you can still move around items in your own inventory although you have the shop open.
        */

        event.setCancelled(true);
        Player player = (Player) event.getWhoClicked();

        if(event.getSlot() == 53){
            player.closeInventory();
            return;
        }

        /*
            Using the try/catch here to catch the exceptions produced if the item doesn't
            have the specific lore we specified in MarketGui.
         */
        try {
            int sellOrderId = Integer.parseInt(event.getCurrentItem().getItemMeta().getLore().get(2));
            Buy buy = new Buy(dbCreds, sellOrderId, player);
            String buyFeedback = buy.executeBuy();
            if (buyFeedback != null) {
                player.sendMessage(buyFeedback);
                player.sendMessage(header + ChatColor.DARK_RED + " Something went wrong with executing the buy order.");
                return;
            }
            marketGui.addSellOrders(player.getName());
            player.sendMessage(header + ChatColor.BOLD + " Transactions completed.");


        } catch (IndexOutOfBoundsException | NumberFormatException e) {
            return;
        }
        return;
    }
}
