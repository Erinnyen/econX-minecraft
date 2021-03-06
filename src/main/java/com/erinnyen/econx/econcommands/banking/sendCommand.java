package com.erinnyen.econx.econcommands.banking;

import com.erinnyen.econx.dbinteraction.DatabaseCredentials;
import com.erinnyen.econx.dbinteraction.PlayerDatabaseUtil;
import com.erinnyen.econx.dbinteraction.Transfer;
import com.erinnyen.econx.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class sendCommand implements CommandExecutor {
    /*
        This is the "send money" command. It will take the player you want to send money to
        (can't be yourself), the amount has to be at least be 1 (so no negatives) and an
        optional comment which will be displayed to the receiver.

     */

    DatabaseCredentials dbCreds;

    public sendCommand(DatabaseCredentials pDBcreds){
        dbCreds = pDBcreds;

    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        /*
            The [Bank] tag will be displayed before each feedback from the system so the player
            knows where it is from.
         */

        if (label.equalsIgnoreCase("send")) { //This will check if the provided command is the "send" we're looking for.
            if (sender instanceof Player) {
                Player player = (Player) sender;
                /*
                    We'll only proceed if the command sender is a player. Only then we can send and receive money.
                 */

                String playerSender = player.getName(); // Defining the sending and receiving player as strings
                String playerReceiver = args[0];


                if(player.getName().equalsIgnoreCase(playerReceiver)){
                    sender.sendMessage(Util.BANK_ERR + "You can't send yourself money!");
                    return false;
                    /*
                        This will return if the sender is equal to the receiver and sends the
                        (command) sender an error-message.
                     */
                }


                if (args.length >= 2) {
                    /*
                        We will only proceed if our argument length is greater or equal
                        to 2, so we make sure that a player- and an amount-argument is given.
                     */
                    PlayerDatabaseUtil dbConn = new PlayerDatabaseUtil(dbCreds); // creating a new Database connection object
                    if(!dbConn.playerExistsInDB(playerReceiver)){
                        /*
                            With this method from the DBInteraction Class we're making sure, that
                            the player we want to send money to is listed in our database. If he's not
                            then we will return and print an error-message.
                         */
                        sender.sendMessage(Util.BANK_ERR + "Player does not exist on this server.");
                        return false;
                    }


                    try {
                        double amount = Double.parseDouble(args[1]);
                        /*
                            We're trying to transform args[1] into a Double.
                            If that doesn't work well catch the exception.
                         */
                        if(amount < 1){
                            sender.sendMessage(Util.BANK_HEADER + Util.TRANSACTION_ERR + " You have to at least send " + ChatColor.GOLD  + "1C");
                            return true;
                        }
                        StringBuilder commentBuilder = new StringBuilder();
                        boolean playerComment;
                        /*
                            The playerComment bool is true if the player specified his or her own argument.
                            This will be set to false if none is provided. In this case the default string, "void
                            transaction" will be set for the comment value in the database.
                         */

                        if(args.length > 2){ // if the player specified a comment for the transaction
                            playerComment = true;
                            for(int i = 2; i < args.length; i++){
                                commentBuilder.append(args[i]).append(" ");
                            }
                            /*
                                This for-loop will merge the space-separated arguments, specified after
                                the /send <player> <amount> into one string. This string will be put into
                                the Database and will be shown to the receiver.
                             */

                        }else{
                            playerComment = false;
                            commentBuilder = new StringBuilder("void transaction"); // This will set the default comment value if none is given.
                        }

                        String comment = commentBuilder.toString(); // Will turn thr StringBuilder object into an actual String


                        Transfer moneyTransfer = new Transfer(dbCreds, playerSender, playerReceiver, amount, comment);
                        String transferFeedback = moneyTransfer.executeTransfer();
                        /*
                            The feedback String returned from the transaction() method from DBInteraction turned into
                            a variable so we can work with it later.
                         */
                        if(transferFeedback != null){
                            sender.sendMessage(transferFeedback);
                            return false;
                            /*
                                If the transaction() method returns anything else than
                                "Transaction completed" an error has occurred. We will print
                                this error and return false.
                             */
                        }
                        sender.sendMessage(Util.BANK_HEADER + ChatColor.WHITE + "You send " + ChatColor.GOLD + amount + "C " +
                                ChatColor.WHITE + "to " +  ChatColor.BOLD + playerReceiver);

                        Player receiverOnline = Bukkit.getPlayerExact(playerReceiver);
                        if(receiverOnline != null){
                            if(playerComment){
                                receiverOnline.sendMessage(Util.BANK_HEADER + ChatColor.WHITE + ChatColor.BOLD + playerSender + ChatColor.RESET + " has send you "
                                        + ChatColor.GOLD + amount + "C " +
                                        ChatColor.WHITE + "with the message:");
                                receiverOnline.sendMessage(ChatColor.BLUE + comment);
                            }else{
                                receiverOnline.sendMessage(Util.BANK_HEADER+ ChatColor.WHITE + playerSender + " has send you "
                                        + ChatColor.GOLD + amount + "C");
                            }

                        }

                        return true;

                    } catch (NumberFormatException e) {
                        /*
                            The catch-block catches the exception if the user didn't provide a number
                            as args[1]. We will print an error-message and return.
                         */
                        e.printStackTrace();
                        sender.sendMessage(Util.BANK_ERR + "Amount musst be a number!");
                        return false;
                    }
                } else {
                    return false;
                }


            }
            sender.sendMessage(Util.NOT_PLAYER);
            return false;
        }
        return false;
    }
}
