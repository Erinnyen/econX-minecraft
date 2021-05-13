package com.erinnyen.econx.DBInteraction;

import org.bukkit.ChatColor;
import java.sql.*;

public class DBInteraction {

    private String uname;
    private String password;
    private  String url;

    public DBInteraction(DBCredentials pDBcreds){
        uname = pDBcreds.getUsername();
        password = pDBcreds.getPassword();
        url = pDBcreds.getUrl();

    }

    public String transaction(String pPlayer_1, String pPlayer_2, double pAmount, String pComment) {

        String err_header = ChatColor.DARK_RED + "Transaction error: " + ChatColor.GRAY;

        if(pAmount < 1){
            return err_header + " You have to at least send " + ChatColor.GOLD  + "1C";
        }

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        try {
            Connection conn = DriverManager.getConnection(url, uname, password);


            if(!playerExistsInDB(pPlayer_2)){
                return err_header + " Your specified receiver does not exist in out system.";
            }


            PreparedStatement prepQuery1 = conn.prepareStatement("SELECT player_id, credit FROM sql_playerdb.players WHERE name = ?");
            prepQuery1.setString(1, pPlayer_1);

            ResultSet resultSet1 = prepQuery1.executeQuery();

            if(!resultSet1.next()){
                return err_header + "Internal database error: Sender";
            }

            int id_1 = resultSet1.getInt("player_id");
            int cr_1 = resultSet1.getInt("credit");


            PreparedStatement prepQuery2 = conn.prepareStatement("SELECT player_id, credit FROM sql_playerdb.players WHERE name = ?");
            prepQuery2.setString(1, pPlayer_2);

            ResultSet resultSet2 = prepQuery2.executeQuery();

            if(!resultSet2.next()){
                return err_header + "Internal database error: receiver";
            }

            int id_2 = resultSet2.getInt(1);
            int cr_2 = resultSet2.getInt(2);

            if((cr_1 - pAmount) < 0){
                return err_header + "Cannot execute transaction due to insufficient funds.";

            }
            double ncr_1 = cr_1 - pAmount;
            double ncr_2 = cr_2 + pAmount;


            PreparedStatement prepStat1 = conn.prepareStatement("UPDATE sql_playerdb.players SET credit = ? WHERE player_id = ?;");
            prepStat1.setDouble(1, ncr_1);

            prepStat1.setInt(2, id_1);


            PreparedStatement prepStat2 = conn.prepareStatement("UPDATE sql_playerdb.players SET credit = ? WHERE player_id = ?;");
            prepStat2.setDouble(1, ncr_2);
            prepStat2.setInt(2, id_2);

            prepStat1.executeUpdate();
            prepStat2.executeUpdate();
            prepQuery1.close();
            prepQuery2.close();
            prepStat1.close();
            prepStat2.close();

            dbEntryVoidTransaction(id_1, id_2, pAmount, pComment);
            conn.close();

            return "Transaction completed";

        } catch (SQLException e) {
            e.printStackTrace();
            return err_header + "Internal database error: SQLExeption";
        }

    }

    public void dbEntryVoidTransaction(int pSender, int pReceiver, double pAmount, String pComment){

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        try {
            Connection conn = DriverManager.getConnection(url, uname, password);
            PreparedStatement voidTransaction = conn.prepareStatement("INSERT INTO sql_playerdb.transactions (sender, receiver," +
                    " amount, timestamp, transaction_type, comment)" +
                    "VALUES (?,?,?,?,?,?)");

            java.sql.Timestamp timestamp = new Timestamp(System.currentTimeMillis());

            voidTransaction.setInt(1, pSender);
            voidTransaction.setInt(2, pReceiver);
            voidTransaction.setDouble(3, pAmount);
            voidTransaction.setTimestamp(4, timestamp);
            voidTransaction.setInt(5, 1);

            if(pComment != null){
                voidTransaction.setString(6, pComment);
            }else{
                voidTransaction.setString(6, "DEFAULT");
            }
            voidTransaction.executeUpdate();

            voidTransaction.close();
            conn.close();

        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
    }


    public boolean playerExistsInDB(String pPlayer){
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        try {
            Connection conn = DriverManager.getConnection(url, uname, password);
            PreparedStatement playerQuery = conn.prepareStatement("SELECT name FROM sql_playerdb.players");
            ResultSet playernames = playerQuery.executeQuery();

            while(playernames.next()) {
                if (playernames.getString(1).equalsIgnoreCase(pPlayer)) {
                    playernames.close();
                    conn.close();
                    return true;
                }
            }
            playernames.close();
            conn.close();
            return false;

        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return false;
        }
    }


    public void newPLayerEntry(String pUUID, String pName){
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        try {
            Connection conn = DriverManager.getConnection(url, uname, password);
            PreparedStatement newPlayerQuery = conn.prepareStatement("INSERT INTO sql_playerdb.players (uuid, name, credit)" +
                    "VALUES (?, ?, ?)");

            newPlayerQuery.setString(1, pUUID);
            newPlayerQuery.setString(2, pName);
            newPlayerQuery.setDouble(3, 1000);

            newPlayerQuery.executeUpdate();
            newPlayerQuery.close();
            conn.close();


        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }


    public double getCredit(String pName){
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        try {

            Connection conn = DriverManager.getConnection(url, uname, password);
            PreparedStatement creditQuery = conn.prepareStatement("SELECT credit FROM sql_playerdb.players WHERE name = ?");

            creditQuery.setString(1, pName);

            ResultSet creditResult = creditQuery.executeQuery();


            while (creditResult.next()) {
                double credit = creditResult.getDouble(1);
                creditResult.close();
                conn.close();
                return credit;
            }
            creditResult.close();
            conn.close();
            return -1;



        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return -1;
        }
    }

    public int getID(String pPlayer){
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        try {
            Connection conn = DriverManager.getConnection(url, uname, password);
            PreparedStatement playerQuery = conn.prepareStatement("SELECT name, player_id FROM sql_playerdb.players");
            ResultSet playerId = playerQuery.executeQuery();

            while(playerId.next()) {
                if (playerId.getString(1).equals(pPlayer)) {
                    playerId.close();
                    conn.close();
                    return playerId.getInt(2);
                }
            }
            playerId.close();
            conn.close();
            return 0;

        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return 0;
        }
    }
    public void updateLastOnline(String pName){
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        try {
            Connection conn = DriverManager.getConnection(url, uname, password);
            PreparedStatement lastonline = conn.prepareStatement("INSERT INTO sql_playerdb.players (last_online)" +
                    "VALUES (?)");

            lastonline.setString(1, pName);
            lastonline.executeUpdate();
            lastonline.close();
            conn.close();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }


}



