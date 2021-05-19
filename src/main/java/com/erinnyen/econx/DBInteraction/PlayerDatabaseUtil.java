package com.erinnyen.econx.DBInteraction;

import org.bukkit.ChatColor;
import java.sql.*;
import java.util.ArrayList;

public class PlayerDatabaseUtil {

    private final String uname;
    private final String password;
    private  final String url;

    public PlayerDatabaseUtil(DatabaseCredentials pDBcreds){
        uname = pDBcreds.getUsername();
        password = pDBcreds.getPassword();
        url = pDBcreds.getUrl();

    }

    public boolean testConnection(){
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        try {

            Connection conn = DriverManager.getConnection(url, uname, password);
            PreparedStatement creditQuery = conn.prepareStatement("SELECT 1 FROM sql_econx.transaction_type");
            ResultSet testResultSet = creditQuery.executeQuery();
            if(testResultSet.next()){
                return true;
            }
            testResultSet.close();
            conn.close();

        } catch (SQLException | NullPointerException e) {
            // handle SQL error here!
            e.printStackTrace();
            System.out.println("Error: Something went wrong with the Database connection!");
        }
        return false;

    }





    public boolean playerExistsInDB(String pPlayer){
        // Please rewrite this method to use sql to check if the player exists.
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        try {
            Connection conn = DriverManager.getConnection(url, uname, password);
            PreparedStatement playerQuery = conn.prepareStatement("SELECT name FROM sql_econx.players");
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
            PreparedStatement newPlayerQuery = conn.prepareStatement("INSERT INTO sql_econx.players (uuid, name, credit)" +
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
            PreparedStatement creditQuery = conn.prepareStatement("SELECT credit FROM sql_econx.players WHERE name = ?");

            creditQuery.setString(1, pName);

            ResultSet creditResult = creditQuery.executeQuery();


            if(creditResult.next()){
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
        // this method is pure laziness, for the long-term please use "JOIN" in the sql statements
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        try {
            Connection conn = DriverManager.getConnection(url, uname, password);
            PreparedStatement playerQuery = conn.prepareStatement("SELECT name, player_id FROM sql_econx.players");
            ResultSet playerId = playerQuery.executeQuery();

            while(playerId.next()) {
                if (playerId.getString(1).equalsIgnoreCase(pPlayer)) {
                    int id = playerId.getInt(2);
                    playerId.close();
                    conn.close();
                    return id;
                }
            }
            playerId.close();
            conn.close();
            return 0;

        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
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

            if(getID(pName) == 0){
                System.out.println("Error: The player you wanted to update does not exist.");
                return;
            }
            PreparedStatement lastonline = conn.prepareStatement("UPDATE sql_econx.players SET last_online = ? WHERE player_id = ?;");

            lastonline.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
            lastonline.setInt(2, getID(pName));
            lastonline.executeUpdate();
            lastonline.close();
            conn.close();

        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }

    public ArrayList<String> getRecentTransactions(String pPlayer, int pLength){

        ArrayList<String> transactionList = new ArrayList<>();

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        try {
            Connection conn = DriverManager.getConnection(url, uname, password);

            int pId = getID(pPlayer);


            PreparedStatement transactionQuery = conn.prepareStatement("SELECT sender, receiver, amount, timestamp FROM" +
                    "(SELECT * FROM sql_econx.transactions ORDER BY transaction_id DESC LIMIT ?) AS derived_table WHERE " +
                    "receiver = ? OR sender = ? ORDER BY transaction_id ASC LIMIT ?;");
            transactionQuery.setInt(1, pLength);
            transactionQuery.setInt(2, pId);
            transactionQuery.setInt(3, pId);
            transactionQuery.setInt(4, pLength);
            ResultSet recent_transactions = transactionQuery.executeQuery();

            if(!recent_transactions.next()){
                recent_transactions.close();
                conn.close();
                return null;

            }

            recent_transactions.next();


            while(!recent_transactions.isAfterLast()){
                // isAfterLast() will return true if the cursor is after the last row

                int sender_id = recent_transactions.getInt(1);
                int receiver_id = recent_transactions.getInt(2);
                double amount = recent_transactions.getDouble(3);

                String shortedTimestamp = shortTimestamp(recent_transactions.getTimestamp(4));

                String msg;
                if (pId == receiver_id){
                    msg = ChatColor.GRAY + "[" + shortedTimestamp + "] " + ChatColor.GREEN + "+" + amount + "C"
                            + ChatColor.WHITE + " from " + ChatColor.BOLD + getName(sender_id);

                }else {
                    msg = ChatColor.GRAY + "[" + shortedTimestamp + "] " + ChatColor.RED + "-" + amount + "C"
                            + ChatColor.WHITE + " to " + ChatColor.BOLD + getName(receiver_id);

                }
                transactionList.add(msg);
                recent_transactions.next();

            }

            recent_transactions.close();
            conn.close();
            return transactionList;

        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return null;
        }

    }

    public String getName(int pID){
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        try {

            Connection conn = DriverManager.getConnection(url, uname, password);
            PreparedStatement creditQuery = conn.prepareStatement("SELECT name FROM sql_econx.players WHERE player_id = ?");

            creditQuery.setInt(1, pID);

            ResultSet nameResult = creditQuery.executeQuery();


            if(nameResult.next()){
                String playername = nameResult.getString(1);
                nameResult.close();
                conn.close();
                return playername;
            }
            nameResult.close();
            conn.close();
            return null;



        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return null;
    }
    public String shortTimestamp(Timestamp pTimestamp){

        Date date = new Date(pTimestamp.getTime());
        Time time = new Time(pTimestamp.getTime());

        System.out.println(date);
        System.out.println(time);

        // Please find a way to not use depreciated methods.
        // Please use the calender because it doesn't work

        String day = Integer.toString(date.getDay());
        String month = Integer.toString(date.getMonth());
        String hours = Integer.toString(time.getHours());
        String minutes = Integer.toString(time.getMinutes());

        System.out.println(day);

        //Making these to string because i want to have them in the dd and mm format and not d and m.

        if(date.getDay() < 10){
            day = "0" + day;
        }
        if(date.getMonth() < 10){
            month = "0" + month;
        }
        if(time.getHours() < 10){
            hours = "0" + hours;
        }
        if(time.getMinutes() < 10){
            minutes = "0" + minutes;

        }
        // I hate those if-statements please find a better way to do that right here.

        String date_dd_mm_hh_mm = day + "." + month + " " + hours + ":" + minutes;

        /*
            This is my own format to display the recent transactions - the regular timestamp is too long.
            I know it is highly inefficient, but i currently don't know any better way.
            I also don't like how I am using depreciated methods, but again i don't know any way around it.
            Please rewrite this method. It is so ugly.


         */
       return date_dd_mm_hh_mm;
    }

}



