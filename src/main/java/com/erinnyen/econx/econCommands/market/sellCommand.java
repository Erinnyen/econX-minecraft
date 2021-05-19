package com.erinnyen.econx.econCommands.market;


import com.erinnyen.econx.DBInteraction.DBCredentials;
import com.erinnyen.econx.DBInteraction.MarketDBInteraction;
import com.erinnyen.econx.DBInteraction.PlayerDatabaseUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;
import com.google.gson.Gson;


public class sellCommand implements CommandExecutor {

    DBCredentials dbCreds;

    public sellCommand(DBCredentials pDBcreds){
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


            int amount = sellItem.getAmount();
            double instancePrice = askedPrice / amount; // The price of each individual item.
            String sellerName = player.getName();
            int playerId = new PlayerDatabaseUtil(dbCreds).getID(sellerName);
            int transactionType = 1; // transaction type for commodity - please add check feature later

            Object materialType = sellItem.getType();
            String sellItemType = materialType.toString();



            //serializing the itemStack object sellItem to a Json String so we can save it in the database.

            Gson gson = new Gson();
            String sellItemJSONString = gson.toJson(sellItem.serialize());

            MarketDBInteraction sellOrderDBEntry = new MarketDBInteraction(dbCreds);

            String sellOrderReturnMessage = sellOrderDBEntry.createSellOrder(askedPrice, amount, sellItemType, instancePrice,
                    sellerName, playerId, transactionType, sellItemJSONString);

            if(!sellOrderReturnMessage.equals("Transaction completed!")){
                sender.sendMessage(header + ChatColor.DARK_RED + sellOrderReturnMessage);
                return false;
            }

            sender.sendMessage(header + ChatColor.BOLD + " You placed a sell-order of " + amount + " "
                    + ChatColor.BLUE + sellItemType + ChatColor.WHITE + " for " + ChatColor.GOLD + askedPrice + "C" +
                    ChatColor.GRAY + " (" + instancePrice + "C per individual item).");

            sender.sendMessage(header + ChatColor.BOLD + " Order placed");

            // Add "You can always withdraw uncompleted orders on the market place"

            playerInventory.setItemInMainHand(null);

            return true;

        }
        return false;
    }
}
