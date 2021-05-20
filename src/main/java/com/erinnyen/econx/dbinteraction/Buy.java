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

    public Buy (DatabaseCredentials pDBcreds, int pSellOrderId, Player pBuyer){

        dbCreds = pDBcreds;
        sellOrderId = pSellOrderId;
        buyer = pBuyer;


    }
    public void executeBuy(){}

    private void getSellOrder(){

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return;
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
            }

        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }
}

