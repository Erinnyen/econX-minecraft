package com.erinnyen.econx.dbinteraction;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.sql.*;
import java.util.Map;

public class Buy {

    private final DatabaseCredentials dbCreds;
    public final int sellOrderId;
    public final Player buyer;


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
    public Timestamp openTimestamp;

    public Buy (DatabaseCredentials pDBcreds, int pSellOrderId, Player pBuyer){

        dbCreds = pDBcreds;
        sellOrderId = pSellOrderId;
        buyer = pBuyer;

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




        return "all good";
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
                return theOrderYouWantToBuy;
            }
            return null;
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
            return null;
        }
    }
}

