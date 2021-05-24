package com.erinnyen.econx.dbinteraction;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.sql.*;
import java.util.Map;

public class Buy {

    private final DatabaseCredentials dbCreds;
    public final int sellOrderId;
    public final Player buyer;
    public final String buyerName;
    public final int buyerId;


    // This is the Sell Object, that is going to be bought.
    public final Sell sellOrderToBuy;

    public final double totalPrice;
    public final int itemAmount;
    public final String itemType;
    public final double instancePrice;
    public final String sellerName;
    public final int sellerId;
    public final int transactionType;
    public final String sellItemJSON;

    public final ItemStack soldItem;

    // The timestamp, of when the order was placed into the database
    public final Timestamp openTimestamp;


    public Buy (DatabaseCredentials pDBcreds, int pSellOrderId, Player pBuyer){

        dbCreds = pDBcreds;
        sellOrderId = pSellOrderId;
        buyer = pBuyer;

        buyerName = buyer.getName();
        buyerId = new PlayerDatabaseUtil(dbCreds).getID(buyerName);

        sellOrderToBuy = this.getSellOrder();
        totalPrice = sellOrderToBuy.totalPrice;
        itemAmount = sellOrderToBuy.itemAmount;
        itemType = sellOrderToBuy.itemType;
        instancePrice = sellOrderToBuy.instancePrice;
        sellerName = sellOrderToBuy.sellerName;
        sellerId = sellOrderToBuy.sellerId;
        transactionType = sellOrderToBuy.transactionType;
        sellItemJSON = sellOrderToBuy.sellItemJSON;

        // the ItemStack object of the item that is sold.
        soldItem = sellOrderToBuy.soldItem;

        openTimestamp = sellOrderToBuy.openTimestamp;


    }
    public String executeBuy(){

        String err_header = ChatColor.DARK_RED + " Transaction error: " + ChatColor.GRAY;


        if (!sellerName.equals(buyerName)){ // Just for testing, please remove ! for production.
            return err_header + "You can't buy your own sell orders.";
        }

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return err_header + "Mysql Driver not found";
        }

        try{

            String comment = " Sold" + Integer.toString(itemAmount) + " of " + itemType;


            //Just for testing

            //Transfer tranferFunds = new Transfer(dbCreds, buyerName, sellerName, totalPrice, comment);
            //String transferFeedback = tranferFunds.executeTransfer();

            //if(transferFeedback != null){
            //    return transferFeedback;
            //}

            Connection conn = DriverManager.getConnection(dbCreds.getUrl(), dbCreds.getUsername(), dbCreds.getPassword());
            PreparedStatement closedSaleUpdate = conn.prepareStatement("INSERT INTO sql_econx.closed_sales (sell_order_id, price, amount, type, instance_price, seller_name, seller_id, buyer_name," +
                    "buyer_id, open_timestamp, transaction_type, JSONString) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)");

            closedSaleUpdate.setInt(1,sellOrderId);
            closedSaleUpdate.setDouble(2, totalPrice);
            closedSaleUpdate.setInt(3, itemAmount);
            closedSaleUpdate.setString(4, itemType);
            closedSaleUpdate.setDouble(5, instancePrice);
            closedSaleUpdate.setString(6, sellerName);
            closedSaleUpdate.setInt(7, sellerId);
            closedSaleUpdate.setString(8, buyerName);
            closedSaleUpdate.setInt(9, buyerId);
            closedSaleUpdate.setTimestamp(10, openTimestamp);
            closedSaleUpdate.setInt(11, transactionType);
            closedSaleUpdate.setString(12, sellItemJSON);

            closedSaleUpdate.executeUpdate();
            closedSaleUpdate.close();

            // Deleting the open order
            PreparedStatement deleteOpenOrder = conn.prepareStatement("DELETE FROM sql_econx.open_sell_orders WHERE order_id = (?)");
            deleteOpenOrder.setInt(1, sellOrderId);
            // execute() returns bool so maybe add check if it returns true.

            if(deleteOpenOrder.execute()){
                deleteOpenOrder.close();
                conn.close();
                return err_header + "Internal database error: Deletion of open_sell_order";
            }
            deleteOpenOrder.close();
            conn.close();

            // Adding the bought item to the buyers inventory.
            // Please Add check if the inventory is full
            Bukkit.getPlayerExact(buyerName).getInventory().addItem(soldItem);

            return null;

        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
            return err_header + "Internal database error: SQLExeption";
        }

    }

    private Sell getSellOrder(){

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        try{

            Connection conn = DriverManager.getConnection(dbCreds.getUrl(), dbCreds.getUsername(), dbCreds.getPassword());
            PreparedStatement sellOrderQuery = conn.prepareStatement("SELECT price, seller_name, transaction_type, JSONString, order_id, timestamp  FROM sql_econx.open_sell_orders WHERE order_id = ?");
            sellOrderQuery.setInt(1, sellOrderId);

            ResultSet sellOrdersResult = sellOrderQuery.executeQuery();

            if(sellOrdersResult.next()){

                String json = sellOrdersResult.getString(4);
                Gson gson = new Gson();

                Map<String, Object> map = gson.fromJson(json, new TypeToken<Map<String, Object>>(){}.getType());
                ItemStack soldItemObject = ItemStack.deserialize(map);

                Sell theOrderYouWantToBuy = new Sell(dbCreds, sellOrdersResult.getDouble(1), sellOrdersResult.getString(2),
                        sellOrdersResult.getInt(3), soldItemObject);
                theOrderYouWantToBuy.setOrderId(sellOrdersResult.getInt(5));
                theOrderYouWantToBuy.setOpenTimestamp(sellOrdersResult.getTimestamp(6));


                conn.close();
                sellOrdersResult.close();
                return theOrderYouWantToBuy;
            }
            return null;
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
            return null;
        }
    }
}

