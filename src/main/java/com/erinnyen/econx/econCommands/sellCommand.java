package com.erinnyen.econx.econCommands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;

public class sellCommand implements CommandExecutor {


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        String header = ChatColor.LIGHT_PURPLE + "[Market]" + ChatColor.RESET;
        //make Market command package

        if(label.equalsIgnoreCase("sell")){

            if(!(sender instanceof Player)){
                sender.sendMessage(header + ChatColor.DARK_RED +"You have to be a player to use this command!");
                return false;
            }

            Player player = (Player) sender;



            try{
                double asked_price = Double.parseDouble(args[0]);

            }catch(NumberFormatException | IndexOutOfBoundsException e){
                if(e instanceof IndexOutOfBoundsException){
                    // Checking which exception we're getting to send the corresponding error message.
                    sender.sendMessage(header + ChatColor.DARK_RED + " Please specify a sell-price");
                }else{
                    sender.sendMessage(header + ChatColor.DARK_RED + " Sell-price must be a number!");
                }
                return false;
            }

            PlayerInventory playerInventory = player.getInventory();


            try{
                ItemStack sellItem = playerInventory.getItemInMainHand();
            }catch(NullPointerException e){
                sender.sendMessage(header + ChatColor.DARK_RED + "Please hold the item you want to sell in your hand!");
                return false;

            }


            sender.sendMessage("This kinda worked");

            return true;

        }
        return false;
    }
}
