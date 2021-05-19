package com.erinnyen.econx.dbinteaction;

import org.bukkit.ChatColor;

import java.sql.*;
import java.util.ArrayList;

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


}








