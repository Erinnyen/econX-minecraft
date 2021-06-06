package com.erinnyen.econx.econcommands.admin;

import com.erinnyen.econx.dbinteraction.Buy;
import com.erinnyen.econx.dbinteraction.DatabaseCredentials;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class BuyCommand implements CommandExecutor {

    String header = ChatColor.LIGHT_PURPLE + "[Market]" + ChatColor.RESET;
    DatabaseCredentials dbCreds;

    public BuyCommand(DatabaseCredentials pDBcreds){
        dbCreds = pDBcreds;
    }
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if(!label.equalsIgnoreCase("buy")){
            return false;
        }

        if(!(sender instanceof Player)){
            sender.sendMessage(header + ChatColor.DARK_RED +" You have to be a player to use this command!");
            return false;
        }
        Player buyer = (Player) sender;

        if(!buyer.hasPermission("econx.admin")){
            sender.sendMessage(ChatColor.DARK_RED + "You don't have the permissions to do that.");
            return true;
        }

        if(args.length != 1){
            sender.sendMessage(header + ChatColor.DARK_RED + " Please specify the OrderID from the order you want to buy.");
            return false;
        }


        int sellOrderId;

        try{
            sellOrderId = Integer.parseInt(args[0]);

        }catch(NumberFormatException | IndexOutOfBoundsException e){
            if(e instanceof IndexOutOfBoundsException){
                // Checking which exception we're getting to send the corresponding error message.
                sender.sendMessage(header + ChatColor.DARK_RED + " Please specify the OrderID from the order you want to buy.");
            }else{
                sender.sendMessage(header + ChatColor.DARK_RED + " OrderID must be a number!");
            }
            return false;
        }

        Buy buy = new Buy(dbCreds, sellOrderId, buyer);
        String transactionFeedback = buy.executeBuy();

        if(transactionFeedback != null){
            sender.sendMessage(header + transactionFeedback);
            return false;

        }
        return true;
    }
}
