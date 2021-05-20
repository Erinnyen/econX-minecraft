package com.erinnyen.econx.dbinteraction;

import org.bukkit.ChatColor;

import java.sql.*;

public class Transfer {

    private DatabaseCredentials dbCredentials;
    private final String uname;
    private final String password;
    private  final String url;

    public final String Player_1;
    public final String Player_2;
    public final double amount;
    public final String comment;

    public Transfer(DatabaseCredentials pDBcreds, String pPlayer_1, String pPlayer_2, double pAmount, String pComment){

        dbCredentials = pDBcreds;
        // I am still using the DataBaseCredential object, to pass it to the playerExistInDB() method later in this class.

        // Database credentials:
        uname = pDBcreds.getUsername();
        password = pDBcreds.getPassword();
        url = pDBcreds.getUrl();



        Player_1 = pPlayer_1;
        Player_2 = pPlayer_2;
        amount = pAmount;
        comment = pComment;

    }
    public String executeTransfer() {

        String err_header = ChatColor.DARK_RED + "Transaction error: " + ChatColor.GRAY;

        if(amount < 1){
            return err_header + " You have to at least send " + ChatColor.GOLD  + "1C";
        }

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        try {
            Connection conn = DriverManager.getConnection(url, uname, password);


            if(!new PlayerDatabaseUtil(dbCredentials).playerExistsInDB(Player_2)){
                return err_header + " Your specified receiver does not exist in out system.";
            }


            PreparedStatement prepQuery1 = conn.prepareStatement("SELECT player_id, credit FROM sql_econx.players WHERE name = ?");
            prepQuery1.setString(1, Player_1);

            ResultSet resultSet1 = prepQuery1.executeQuery();

            if(!resultSet1.next()){
                return err_header + "Internal database error: Sender";
            }

            int id_1 = resultSet1.getInt("player_id");
            int cr_1 = resultSet1.getInt("credit");


            PreparedStatement prepQuery2 = conn.prepareStatement("SELECT player_id, credit FROM sql_econx.players WHERE name = ?");
            prepQuery2.setString(1, Player_2);

            ResultSet resultSet2 = prepQuery2.executeQuery();

            if(!resultSet2.next()){
                return err_header + "Internal database error: receiver";
            }

            int id_2 = resultSet2.getInt(1);
            int cr_2 = resultSet2.getInt(2);

            if((cr_1 - amount) < 0){
                return err_header + "Cannot execute transaction due to insufficient funds.";

            }
            double ncr_1 = cr_1 - amount;
            double ncr_2 = cr_2 + amount;


            PreparedStatement prepStat1 = conn.prepareStatement("UPDATE sql_econx.players SET credit = ? WHERE player_id = ?;");
            prepStat1.setDouble(1, ncr_1);

            prepStat1.setInt(2, id_1);


            PreparedStatement prepStat2 = conn.prepareStatement("UPDATE sql_econx.players SET credit = ? WHERE player_id = ?;");
            prepStat2.setDouble(1, ncr_2);
            prepStat2.setInt(2, id_2);

            prepStat1.executeUpdate();
            prepStat2.executeUpdate();
            prepQuery1.close();
            prepQuery2.close();
            prepStat1.close();
            prepStat2.close();

            transferDatabaseEntry(id_1, id_2, amount, comment);
            conn.close();

            return "Transaction completed";

        } catch (SQLException e) {
            e.printStackTrace();
            return err_header + "Internal database error: SQLExeption";
        }

    }
    private void transferDatabaseEntry(int pSender, int pReceiver, double pAmount, String pComment){

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        try {
            Connection conn = DriverManager.getConnection(url, uname, password);
            PreparedStatement voidTransaction = conn.prepareStatement("INSERT INTO sql_econx.transactions (sender, receiver," +
                    " amount, timestamp, transaction_type, comment)" +
                    "VALUES (?,?,?,?,?,?)");

            java.sql.Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            //change the default fot timestamp in sql table to current timestamp.

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
}
