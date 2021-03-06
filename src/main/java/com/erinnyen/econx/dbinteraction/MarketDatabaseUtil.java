package com.erinnyen.econx.dbinteraction;

import com.erinnyen.econx.util.Util;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MarketDatabaseUtil {


    private final String uname;
    private final String password;
    private  final String url;
    private final DatabaseCredentials dbCreds;

    public MarketDatabaseUtil(DatabaseCredentials pDBcreds){

        uname = pDBcreds.getUsername();
        password = pDBcreds.getPassword();
        url = pDBcreds.getUrl();
        dbCreds = pDBcreds;

    }


    public ArrayList<String> getOpenSellOrders(){

        ArrayList<String> orderList = new ArrayList<>();

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }

        try {
            Connection conn = DriverManager.getConnection(url, uname, password);

            PreparedStatement openOrdersQuery = conn.prepareStatement("SELECT amount, type, instance_price, price, order_id FROM sql_econx.open_sell_orders");
            ResultSet openOrdersResultSet = openOrdersQuery.executeQuery();

            if(!openOrdersResultSet.next()){
                openOrdersQuery.close();
                conn.close();
                return null;

            }

            openOrdersResultSet.next();
            while(!openOrdersResultSet.isAfterLast()){
                int amount = openOrdersResultSet.getInt(1);
                String type = openOrdersResultSet.getString(2);
                double instance_price = openOrdersResultSet.getDouble(3);
                double total_price = openOrdersResultSet.getDouble(4);
                int order_id = openOrdersResultSet.getInt(5);

                String msg = ChatColor.DARK_PURPLE + "(" + Integer.toString(order_id) + ")" +
                        ChatColor.WHITE + " " + Integer.toString(amount) + " " + ChatColor.BLUE + type +
                        ChatColor.WHITE + " for " + ChatColor.GOLD + total_price + "C " + ChatColor.GRAY +
                        "(" + instance_price + "C per item)";

                orderList.add(msg);
                openOrdersResultSet.next();
            }
            openOrdersResultSet.close();
            conn.close();
            return orderList;

        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
            return null;
        }
    }
    public ArrayList<ItemStack> getSellOrdersItemStacks(String playerName){

        ArrayList<ItemStack> itemsForSale = new ArrayList<>();


        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }

        try {
            Connection conn = DriverManager.getConnection(url, uname, password);
            PreparedStatement openOrdersQuery = conn.prepareStatement("SELECT JSONString, order_id, instance_price, price, type FROM sql_econx.open_sell_orders WHERE seller_name != ?LIMIT 46;");
            // Limit 46 are 4 x 9 rows of items..
            openOrdersQuery.setString(1, playerName);
            ResultSet openOrdersResultSet = openOrdersQuery.executeQuery();


            if(!openOrdersResultSet.next()){
                openOrdersQuery.close();
                conn.close();
                return null;

            }

            while(openOrdersResultSet.next()) {
                int orderId = openOrdersResultSet.getInt(2);
                double instancePrice = openOrdersResultSet.getDouble(3);
                double totalPrice = openOrdersResultSet.getDouble(4);
                String type = openOrdersResultSet.getString(5);

                // deserializing the json String
                String json = openOrdersResultSet.getString(1);
                Gson gson = new Gson();
                Map<String, Object> map = gson.fromJson(json, new TypeToken<Map<String, Object>>(){}.getType());
                ItemStack soldItemObject = ItemStack.deserialize(map);

                ItemMeta itemMeta = soldItemObject.getItemMeta();
                itemMeta.setDisplayName(type + ChatColor.GRAY + " for " + ChatColor.GOLD + Double.toString(totalPrice) + "C");

                List<String> lore = new ArrayList<String>();
                lore.add(ChatColor.GRAY + "(" + instancePrice + "C per item)");
                lore.add(ChatColor.BOLD + "" + ChatColor.BLUE + "Click to buy!");
                // Adding the orderId to the lore so we can identify it later.
                lore.add(Integer.toString(orderId));
                itemMeta.setLore(lore);
                soldItemObject.setItemMeta(itemMeta);

                itemsForSale.add(soldItemObject);
            }
            return itemsForSale;

        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
            return null;
        }
    }

    public boolean testForSufficientFunds(int orderId, String playerName){
        //If any error or exception occurs, this method will return false.
        double price;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        try {
            Connection conn = DriverManager.getConnection(url, uname, password);
            PreparedStatement orderQuery = conn.prepareStatement("SELECT price FROM sql_econx.open_sell_orders WHERE order_id = ?;");
            orderQuery.setInt(1, orderId);

            ResultSet orderQueryResult = orderQuery.executeQuery();
            if(orderQueryResult.next()){
                price = orderQueryResult.getDouble(1);
            }else{
                return false;
            }

            PlayerDatabaseUtil playerQueries = new PlayerDatabaseUtil(dbCreds);


            if(!playerQueries.playerExistsInDB(playerName)){
                return false;
            }
            if(playerQueries.getCredit(playerName) == -1){
                return false;
            }

            if((playerQueries.getCredit(playerName) - price) < 0 ){
                return false;
            }

        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
            return false;
        }
        return true;
    }

    public ArrayList<ItemStack> getOwnSellOrders(String playerName){
        // basically like getSellOrdersItemStacks but reversed.
        ArrayList<ItemStack> itemsForSale = new ArrayList<>();

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }

        try {
            Connection conn = DriverManager.getConnection(url, uname, password);
            PreparedStatement openOrdersQuery = conn.prepareStatement("SELECT JSONString, order_id, instance_price, price, type FROM sql_econx.open_sell_orders WHERE seller_name = ? LIMIT 46;");
            // Limit 46 are 4 x 9 rows of items.
            openOrdersQuery.setString(1, playerName);
            ResultSet openOrdersResultSet = openOrdersQuery.executeQuery();


            if(!openOrdersResultSet.next()){
                openOrdersQuery.close();
                conn.close();
                return null;

            }

            while(openOrdersResultSet.next()) {
                int orderId = openOrdersResultSet.getInt(2);
                double instancePrice = openOrdersResultSet.getDouble(3);
                double totalPrice = openOrdersResultSet.getDouble(4);
                String type = openOrdersResultSet.getString(5);

                // deserializing the json String
                String json = openOrdersResultSet.getString(1);
                Gson gson = new Gson();
                Map<String, Object> map = gson.fromJson(json, new TypeToken<Map<String, Object>>(){}.getType());
                ItemStack soldItemObject = ItemStack.deserialize(map);

                ItemMeta itemMeta = soldItemObject.getItemMeta();
                itemMeta.setDisplayName(type + ChatColor.GRAY + " for " + ChatColor.GOLD + Double.toString(totalPrice) + "C");

                List<String> lore = new ArrayList<String>();
                lore.add(ChatColor.GRAY + "(" + instancePrice + "C per item)");
                lore.add(ChatColor.BOLD + "" + ChatColor.BLUE + "Click to withdraw from Market!");
                // Adding the orderId to the lore so we can identify it later.
                lore.add(Integer.toString(orderId));
                itemMeta.setLore(lore);
                soldItemObject.setItemMeta(itemMeta);

                itemsForSale.add(soldItemObject);
            }
            return itemsForSale;

        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
            return null;
        }
    }

    public String withdrawSellOrder(Player seller, int orderId){




        if(seller.getInventory().firstEmpty() == -1){
            return Util.MARKET_ERR + "Your inventory is full.";
        }

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return Util.DATABASE_ERR + "Mysql Driver not found";
        }

        try{

            Connection conn = DriverManager.getConnection(dbCreds.getUrl(), dbCreds.getUsername(), dbCreds.getPassword());
            PreparedStatement getItemQuery = conn.prepareStatement("SELECT JSONString FROM sql_econx.open_sell_orders WHERE order_id = ? AND seller_name = ?;");
            getItemQuery.setInt(1, orderId);
            getItemQuery.setString(2, seller.getName());

            ResultSet getItemResultSet = getItemQuery.executeQuery();
            if(getItemResultSet.next()) {

                String itemJSON = getItemResultSet.getString(1);
                Gson gson = new Gson();

                Map<String, Object> map = gson.fromJson(itemJSON, new TypeToken<Map<String, Object>>() {}.getType());
                ItemStack itemStack = ItemStack.deserialize(map);

                // Please Add check if the inventory is full
                Bukkit.getPlayerExact(seller.getName()).getInventory().addItem(itemStack);

            } else {
                return Util.DATABASE_ERR + "Something went wrong";
            }

            PreparedStatement deleteOrder = conn.prepareStatement("DELETE FROM sql_econx.open_sell_orders WHERE order_id = ?;");
            deleteOrder.setInt(1, orderId);
            deleteOrder.execute();

            return null;
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
            return Util.DATABASE_ERR + "SQLExeption";
        }

    }

}








