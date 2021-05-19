package com.erinnyen.econx.dbinteaction;

import com.google.gson.Gson;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

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
    public final String sellerName;
    public final int sellerId;
    public final int transactionType;
    public final String sellItemJSON;

    public final ItemStack soldItem;

    public Sell(DatabaseCredentials pDBcreds, double pTotalPrice, String pPlayerName,
                int pTransactionType, ItemStack pSoldItem) {


        // Database credentials:
        dbCreds = pDBcreds;
        uname = pDBcreds.getUsername();
        password = pDBcreds.getPassword();
        url = pDBcreds.getUrl();


        totalPrice = pTotalPrice;
        soldItem = pSoldItem;
        itemAmount = soldItem.getAmount();

        //Using the "Object" to get the material type as a string. (toString() method of class Object)
        Object materialType = soldItem.getType();
        itemType = materialType.toString();

        sellerName = pPlayerName;
        sellerId = new PlayerDatabaseUtil(dbCreds).getID(sellerName);
        // add a "check sell order for type" feature please.
        transactionType = pTransactionType;

        // serializing the itemStack object sellItem to a Json String so we can save it in the database.
        Gson gson = new Gson();
        sellItemJSON = gson.toJson(soldItem.serialize());
        instancePrice = totalPrice / itemAmount;


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
            sellOrderEntry.setString(5, sellerName);
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
