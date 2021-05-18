package com.erinnyen.econx.econCommands.market;

import com.erinnyen.econx.DBInteraction.DBCredentials;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class BuyCommand implements CommandExecutor {


    DBCredentials dbCreds;

    public BuyCommand(DBCredentials pDBcreds){
        dbCreds = pDBcreds;
    }
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {



        return false;
    }
}
