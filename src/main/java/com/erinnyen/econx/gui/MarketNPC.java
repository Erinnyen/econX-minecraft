package com.erinnyen.econx.gui;


import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.WanderingTrader;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class MarketNPC {
    public static final String VENDOR_NAME = "§6§1Market";


    public MarketNPC(Location location){
        WanderingTrader marketNpc = (WanderingTrader) location.getWorld().spawnEntity(location, EntityType.WANDERING_TRADER);

        marketNpc.setCustomName(this.VENDOR_NAME);
        marketNpc.setCustomNameVisible(true);
        //Temporarily adding a potion effect for the traders immobility
        marketNpc.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 500));
    }
}
