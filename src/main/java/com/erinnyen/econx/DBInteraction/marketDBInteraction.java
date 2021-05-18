package com.erinnyen.econx.DBInteraction;

import org.bukkit.ChatColor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class marketDBInteraction {


    private final String uname;
    private final String password;
    private  final String url;

    public marketDBInteraction(DBCredentials pDBcreds){


        uname = pDBcreds.getUsername();
        password = pDBcreds.getPassword();
        url = pDBcreds.getUrl();

    }
    public String createSellOrder(double pAmount, String type, double pInstancePrice, String pPlayerName,
                                   int pSeller_id, int transaction_type, String pSellItemJSON){

        String err_header = ChatColor.DARK_RED + "Error: ";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        try {
            Connection conn = DriverManager.getConnection(url, uname, password);

            PreparedStatement sellOrderEntry = conn.prepareStatement("INSERT INTO sql_econx.open_sell_orders" +
                    "(amount, type, instance_price, seller_name, seller_id, ");
        }catch(SQLException e){
            e.printStackTrace();
            return err_header + "Something went wrong!";
        }



        return "hello world";
    }

}
