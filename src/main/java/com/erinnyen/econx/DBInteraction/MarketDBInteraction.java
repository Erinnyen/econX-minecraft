package com.erinnyen.econx.DBInteraction;

import org.bukkit.ChatColor;
import org.json.simple.JSONObject;

import java.sql.*;
import java.util.ArrayList;

public class MarketDBInteraction {


    private final String uname;
    private final String password;
    private  final String url;

    public MarketDBInteraction(DBCredentials pDBcreds){


        uname = pDBcreds.getUsername();
        password = pDBcreds.getPassword();
        url = pDBcreds.getUrl();

    }
    public String createSellOrder(double pPrice, int pAmount, String pType, double pInstancePrice, String pPlayerName,
                                   int pSeller_id, int pTransactionType, String pSellItemJSON){

        String err_header = ChatColor.DARK_RED + "Error: ";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        try {
            Connection conn = DriverManager.getConnection(url, uname, password);

            PreparedStatement sellOrderEntry = conn.prepareStatement("INSERT INTO sql_econx.open_sell_orders" +
                    "(price, amount, type, instance_price, seller_name, seller_id, transaction_type, JSONString)" +
                    "VALUES (?,?,?,?,?,?,?,?)");

            sellOrderEntry.setDouble(1, pPrice);
            sellOrderEntry.setInt(2, pAmount);
            sellOrderEntry.setString(3, pType);
            sellOrderEntry.setDouble(4, pInstancePrice);
            sellOrderEntry.setString(5, pPlayerName);
            sellOrderEntry.setInt(6, pSeller_id);
            sellOrderEntry.setInt(7, pTransactionType);
            sellOrderEntry.setString(8, pSellItemJSON);

            sellOrderEntry.executeUpdate();

            sellOrderEntry.close();
            conn.close();


        }catch(SQLException e){
            e.printStackTrace();
            return err_header + "Something went wrong with the database entry!";
        }

        return "Transaction completed!";
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








