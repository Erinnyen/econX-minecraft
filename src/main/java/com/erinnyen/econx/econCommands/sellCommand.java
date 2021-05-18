package com.erinnyen.econx.econCommands;


import com.erinnyen.econx.DBInteraction.DBCredentials;
import com.erinnyen.econx.DBInteraction.PlayerDBInteraction;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
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
import org.json.simple.JSONObject;

import java.text.ParseException;


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
            Material type = sellItem.getType(); // Maybe rename this to material
            double instancePrice = askedPrice / amount; // The price of each individual item;

            String sellerName = player.getName();
            int playerId = new PlayerDBInteraction(dbCreds).getID(sellerName); // Temporary



            //serializing the itemStack object sellItem to a Json String so we can save it in the database.
            Gson gson = new Gson();
            String sellItemString = gson.toJson(sellItem.serialize());
            //JSONObject sellItemJSON = new JSONObject(sellItemString);

            System.out.println(sellItemString);


            //sender.sendMessage(header + ChatColor.DARK_RED + " Error: Something went wrong while performing the database entry!");


            /*

            TextComponent confirmMessage = new TextComponent(ChatColor.BOLD + "Click here to confirm placing a sell order of "
                    + ChatColor.BLUE + type + ChatColor.WHITE + " for " + ChatColor.BLUE + askedPrice);

            confirmMessage.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, ""));

            */
            sender.sendMessage(header + ChatColor.BOLD + " You placed a sell-order of " + amount + " "
                    + ChatColor.BLUE + type + ChatColor.WHITE + " for " + ChatColor.GOLD + askedPrice + "C" +
                    ChatColor.GRAY + " (" + instancePrice + "C per individual item).");

            sender.sendMessage(header + ChatColor.BOLD + " Order placed");



            // Add "You can always withdraw uncompleted orders on the market place"


            playerInventory.setItemInMainHand(null);

            sender.sendMessage("This kinda worked");
            //for debugging

            return true;

        }
        return false;
    }
}
