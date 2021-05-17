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

            }catch(NumberFormatException e){
                sender.sendMessage(header + ChatColor.DARK_RED + "Sell price musst be a number!");
                return false;
            }

            PlayerInventory playerInventory = player.getInventory();

            ItemStack sellItem = playerInventory.getItemInMainHand();

            sender.sendMessage("This kinda worked");

            return true;

        }
        return false;
    }
}
