package com.erinnyen.econx.Listeners;


import com.erinnyen.econx.DBInteraction.DBInteraction;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;


import java.util.UUID;

public class ConnectionListeners implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event){

        Player p = event.getPlayer();
        DBInteraction dbConn = new DBInteraction();
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(p.getName());


        if(!offlinePlayer.hasPlayedBefore()){
            event.setJoinMessage(ChatColor.BLUE + "Hello " + p.getName());
            UUID uuid = p.getUniqueId();
            String name = p.getName();
            String str_uuid = uuid.toString();
            dbConn.newPLayerEntry(str_uuid, name);

        }
    }
    @EventHandler
    public void onLeft(PlayerQuitEvent event){
        Player player = event.getPlayer();
        String player_name = player.getName();
        event.setQuitMessage(ChatColor.BLUE + "Bye, " + player.getName());

        DBInteraction dbConn = new DBInteraction();
        dbConn.updateLastOnline(player_name);

    }
}
