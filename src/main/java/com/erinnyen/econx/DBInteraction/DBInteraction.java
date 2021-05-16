package com.erinnyen.econx.DBInteraction;

import org.bukkit.ChatColor;
import java.sql.*;
import java.time.DateTimeException;
import java.util.ArrayList;

public class DBInteraction {

    private final String uname;
    private final String password;
    private  final String url;

    public DBInteraction(DBCredentials pDBcreds){
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
            PreparedStatement creditQuery = conn.prepareStatement("SELECT 1 FROM sql_playerdb.players");
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
        // Please rewrite this method to use sql to check if the player exists.
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
            PreparedStatement playerQuery = conn.prepareStatement("SELECT name, player_id FROM sql_playerdb.players");
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
            PreparedStatement lastonline = conn.prepareStatement("UPDATE sql_playerdb.players SET last_online = ? WHERE player_id = ?;");

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
                    "(SELECT * FROM sql_playerdb.transactions ORDER BY transaction_id DESC LIMIT ?) AS derived_table WHERE " +
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
                    msg = ChatColor.GRAY + "[" + shortedTimestamp + "] " + ChatColor.GREEN + "+" + amount
                            + ChatColor.WHITE + " from " + ChatColor.BOLD + getName(sender_id);

                }else {
                    msg = ChatColor.GRAY + "[" + shortedTimestamp + "] " + ChatColor.RED + "-" + amount
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
            PreparedStatement creditQuery = conn.prepareStatement("SELECT name FROM sql_playerdb.players WHERE player_id = ?");

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

        // Please find a way to not use depreciated methods.

        String day = Integer.toString(date.getDay());
        String month = Integer.toString(date.getMonth());
        String hours = Integer.toString(time.getHours());
        String minutes = Integer.toString(time.getMinutes());

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



