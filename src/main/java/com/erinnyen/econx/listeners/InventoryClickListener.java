package com.erinnyen.econx.listeners;

import com.erinnyen.econx.dbinteraction.Buy;
import com.erinnyen.econx.dbinteraction.DatabaseCredentials;
import com.erinnyen.econx.dbinteraction.MarketDatabaseUtil;
import com.erinnyen.econx.gui.MarketGui;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;



public class InventoryClickListener implements Listener {

    String header = ChatColor.LIGHT_PURPLE + "[Market]" + ChatColor.RESET;
    DatabaseCredentials dbCreds;

    public InventoryClickListener(DatabaseCredentials pDBcreds){
        dbCreds = pDBcreds;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event){
        /*
            Ignoring all the click events, that aren't within our shop
            I have to do a try catch here, because there are other inventories, that are smaller than 53
            so we will produce a NullPointerException.
         */

        boolean isTheInvWereLookingFor = false;
        try {
            // The Market inventory has the distinct exitBarrierBlock in the lower right corner.
            if(event.getInventory().getItem(53).getType() == Material.BARRIER) {
                isTheInvWereLookingFor = true;
            }
        } catch (IndexOutOfBoundsException | NullPointerException e) {
            //Doing nothing
        }
        try {
            // The confirmInventory has the distinct confirm concrete Block in the lower left.
            if(event.getInventory().getItem(30).getType() == Material.GREEN_CONCRETE){
                isTheInvWereLookingFor = true;
            }
        } catch (IndexOutOfBoundsException | NullPointerException e) {
            //Doing nothing
        }
        if(!isTheInvWereLookingFor){
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
        // I have to do this if-clause, bc the youTooPoorBlock doesn't have an ItemLore.
        if(event.getCurrentItem().getType() != Material.BARRIER) {
            if (event.getCurrentItem().getItemMeta().getLore() == null) {
                return;
            }
        }
        event.setCancelled(true);

        Player player = (Player) event.getWhoClicked();

        if(event.getInventory().getSize() == 54){
            if(event.getSlot() == 53){
                player.closeInventory();
                return;
            }
            try {
                int sellOrderId = Integer.parseInt(event.getCurrentItem().getItemMeta().getLore().get(2));
                MarketGui gui = new MarketGui(dbCreds);
                //testing if the player has enough money to buy the item
                if(!new MarketDatabaseUtil(dbCreds).testForSufficientFunds(sellOrderId, event.getWhoClicked().getName())){
                    gui.cantAffordItem(event.getInventory(), event.getSlot());
                    event.getWhoClicked().sendMessage(header + ChatColor.RED + " You don't have enough money to buy this.");
                    return;
                }

                player.closeInventory();
                player.openInventory(gui.createConfirmationInventory(player, event.getCurrentItem(), sellOrderId));
                return;
            } catch (NumberFormatException e){
                e.printStackTrace();
                return;
            }


        }
        if(event.getInventory().getSize() == 36){
            if(event.getSlot() == 32){
                player.closeInventory();
                return;
            }
            if(event.getSlot() == 30){
                try {
                    int sellOrderId = Integer.parseInt(event.getInventory().getItem(30).getItemMeta().getLore().get(1));
                    Buy buy = new Buy(dbCreds, sellOrderId, player);
                    String buyFeedback = buy.executeBuy();
                    if (buyFeedback != null) {
                        player.sendMessage(buyFeedback);
                        player.sendMessage(header + ChatColor.DARK_RED + " Something went wrong with executing the buy order.");
                        return;
                    }

                    player.sendMessage(header + ChatColor.BOLD + " Transactions completed.");
                    player.closeInventory();
                    return;

                } catch (IndexOutOfBoundsException | NumberFormatException e) {
                    return;
                }
            }
        }


        return;
    }

}
