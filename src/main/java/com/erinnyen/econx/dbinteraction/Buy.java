package com.erinnyen.econx.dbinteraction;

import org.bukkit.entity.Player;

public class Buy {

    private final DatabaseCredentials dbCreds;
    public final int sellOrderId;
    public final Player buyer;

    public Buy (DatabaseCredentials pDBcreds, int pSellOrderId, Player pBuyer){

        dbCreds = pDBcreds;
        sellOrderId = pSellOrderId;
        buyer = pBuyer;


    }
}
