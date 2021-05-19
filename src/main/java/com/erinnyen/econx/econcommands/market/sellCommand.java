package com.erinnyen.econx.econcommands.market;


import com.erinnyen.econx.dbinteaction.DatabaseCredentials;
import com.erinnyen.econx.dbinteaction.Sell;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;


public class sellCommand implements CommandExecutor {

    DatabaseCredentials dbCreds;

    public sellCommand(DatabaseCredentials pDBcreds){
        dbCreds = pDBcreds;
    }


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        String header = ChatColor.LIGHT_PURPLE + "[Market]" + ChatColor.RESET;
        double askedPrice;
        //make Market command package

        if(label.equalsIgnoreCase("sell")){

            if(!(sender instanceof Player)){
                sender.sendMessage(header + ChatColor.DARK_RED +"You have to be a player to use this command!");
                return false;
            }

            if(args.length != 1){
                sender.sendMessage("Usage:");
                return false;
            }

            try{
                askedPrice = Double.parseDouble(args[0]);

            }catch(NumberFormatException | IndexOutOfBoundsException e){
                if(e instanceof IndexOutOfBoundsException){
                    // Checking which exception we're getting to send the corresponding error message.
                    sender.sendMessage(header + ChatColor.DARK_RED + " Please specify a sell-price");
                }else{
                    sender.sendMessage(header + ChatColor.DARK_RED + " Sell-price must be a number!");
                }
                return false;
            }

            Player player = (Player) sender;

            PlayerInventory playerInventory = player.getInventory();
            ItemStack sellItem = playerInventory.getItemInMainHand();

            if(sellItem.getType().equals(Material.AIR)){
                sender.sendMessage(header + ChatColor.DARK_RED + " Please hold the item you want to sell in your hand!");
                return false;

            }

            String sellerName = player.getName();
            int transactionType = 1; // transaction type for commodity - please add check feature later

            Sell sellOrder = new Sell(dbCreds, askedPrice, sellerName, transactionType, sellItem);
            String sellOrderReturnString = sellOrder.placeSellOrder();

            if(!sellOrderReturnString.equals("Transaction completed!")){
                sender.sendMessage(header + ChatColor.DARK_RED + sellOrderReturnString);
                return false;
            }

            sender.sendMessage(header + ChatColor.BOLD + " You placed a sell-order of " + sellOrder.itemAmount + " "
                    + ChatColor.BLUE + sellOrder.itemType + ChatColor.WHITE + " for " + ChatColor.GOLD + askedPrice + "C" +
                    ChatColor.GRAY + " (" + sellOrder.instancePrice + "C per individual item).");

            sender.sendMessage(header + ChatColor.BOLD + " Order placed");

            // Add "You can always withdraw uncompleted orders on the market place"

            playerInventory.setItemInMainHand(null);

            return true;

        }
        return false;
    }
}
