package com.erinnyen.econx.dbinteaction;

import org.bukkit.ChatColor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Sell {

    private DatabaseCredentials dbCreds;
    private final String uname;
    private final String password;
    private  final String url;

    public final double totalPrice;
    public final int itemAmount;
    public final String itemType;
    public final double instancePrice;
    public final String playerName;
    public final int sellerId;
    public final int transactionType;
    public final String sellItemJSON;

    public Sell(DatabaseCredentials pDBcreds, double pTotalPrice, int pItemAmount, String pItemType, double pInstancePrice, String pPlayerName,
                int pSellerId, int pTransactionType, String pSellItemJSON) {


        // Database credentials:
        dbCreds = pDBcreds;
        uname = pDBcreds.getUsername();
        password = pDBcreds.getPassword();
        url = pDBcreds.getUrl();


        totalPrice = pTotalPrice;
        itemAmount = pItemAmount;
        itemType = pItemType;
        instancePrice = pInstancePrice;
        playerName = pPlayerName;
        sellerId = pSellerId;
        transactionType = pTransactionType;
        sellItemJSON = pSellItemJSON;


    }

    public String placeSellOrder(){

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

            sellOrderEntry.setDouble(1, totalPrice);
            sellOrderEntry.setInt(2, itemAmount);
            sellOrderEntry.setString(3, itemType);
            sellOrderEntry.setDouble(4, instancePrice);
            sellOrderEntry.setString(5, playerName);
            sellOrderEntry.setInt(6, sellerId);
            sellOrderEntry.setInt(7, transactionType);
            sellOrderEntry.setString(8, sellItemJSON);

            sellOrderEntry.executeUpdate();

            sellOrderEntry.close();
            conn.close();


        }catch(SQLException e){
            e.printStackTrace();
            return err_header + "Something went wrong with the database entry!";
        }

        return "Transaction completed!";
    }
}
