package com.erinnyen.econx.util;

import org.bukkit.ChatColor;

public class Util {

    //headers
    public final static String MARKET_HEADER = ChatColor.LIGHT_PURPLE + "[Market] " + ChatColor.RESET;
    public final static String BANK_HEADER =  ChatColor.LIGHT_PURPLE + "[Bank] " + ChatColor.RESET;
    public final static String ECONX_HEADER = ChatColor.AQUA + "[EconX] " + ChatColor.RESET;
    public final static String SERVER_HEADER = ChatColor.GREEN + "[Server]" + ChatColor.RESET;

    //error headers
    public final static String MARKET_ERR = ChatColor.LIGHT_PURPLE + "[Market]" + ChatColor.DARK_RED + " Error: ";
    public final static String BANK_ERR = ChatColor.LIGHT_PURPLE + "[Bank] " + ChatColor.DARK_RED + " Error: ";
    public final static String ECONX_ERR = ChatColor.AQUA + "[EconX]"  + ChatColor.DARK_RED + " Error: ";
    public final static String TRANSACTION_ERR = ChatColor.DARK_RED + "Transaction error: " + ChatColor.GRAY;
    public final static String DATABASE_ERR =  ChatColor.DARK_RED + "Internal Database error: ";

    //info
    public final static String NOT_PLAYER = ChatColor.DARK_RED + "You have to be a player to use this command!";
    public final static String PERM_ERR = ChatColor.DARK_RED + "You don't have the permissions to do that.";


}
