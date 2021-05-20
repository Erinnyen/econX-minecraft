package com.erinnyen.econx.dbinteraction;

import com.google.gson.Gson;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

import java.sql.*;

public class Sell {

    private final DatabaseCredentials dbCreds;

    public final double totalPrice;
    public final int itemAmount;
    public final String itemType;
    public final double instancePrice;
    public final String sellerName;
    public final int sellerId;
    public final int transactionType;
    public final String sellItemJSON;

    public final ItemStack soldItem;
    public int orderId;

    // The timestamp, of when the order was placed into the database
    public Timestamp openTimestamp;

    public Sell(DatabaseCredentials pDBcreds, double pTotalPrice, String pPlayerName,
                int pTransactionType, ItemStack pSoldItem) {


        // Database credentials:
        dbCreds = pDBcreds;

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

    public void setOrderId(int pOrderId){
        /*
        This method is only used when a Sell object is used outside of the database, (For example in
        the Buy class.) because the orderId is assigned by sql when inserting into the database.
        */
        this.orderId = pOrderId;

    }
    public void setOpenTimestamp(Timestamp pTimestamp){
        /*
        This method is only used when a Sell object is used outside of the database, (For example in
        the Buy class.) because (like the orderId) the timestamp is assigned by sql when inserting into the database.
        */
        this.openTimestamp = pTimestamp;
    }

    public String placeSellOrder(){

        String err_header = ChatColor.DARK_RED + "Error: ";

        String uname = dbCreds.getUsername();
        String password = dbCreds.getPassword();
        String url = dbCreds.getUrl();

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
