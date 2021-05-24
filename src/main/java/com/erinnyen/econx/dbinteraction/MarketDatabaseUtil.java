package com.erinnyen.econx.dbinteraction;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.bukkit.ChatColor;
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

    public MarketDatabaseUtil(DatabaseCredentials pDBcreds){

        uname = pDBcreds.getUsername();
        password = pDBcreds.getPassword();
        url = pDBcreds.getUrl();

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

            PreparedStatement openOrdersQuery = conn.prepareStatement("SELECT JSONString, order_id, instance_price, price, type FROM sql_econx.open_sell_orders;");
            // Add where seller_name != ? later please.
            //openOrdersQuery.setString(1, playerName);
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
                lore.add(Integer.toString(orderId));
                lore.add(ChatColor.BOLD + "" + ChatColor.BLUE + "Click to buy");
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


}








