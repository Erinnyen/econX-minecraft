package com.erinnyen.econx.Listeners;


import com.erinnyen.econx.DBInteraction.DBCredentials;
import com.erinnyen.econx.DBInteraction.PlayerDBInteraction;
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
    String header = ChatColor.GRAY + "[Server]" + ChatColor.RESET;

    DBCredentials dbCreds;

    public ConnectionListeners(DBCredentials pDBcreds){
        dbCreds = pDBcreds;

    }


    @EventHandler
    public void onJoin(PlayerJoinEvent event){


        Player p = event.getPlayer();
        PlayerDBInteraction dbConn = new PlayerDBInteraction(dbCreds);
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(p.getName());


        if(!dbConn.playerExistsInDB(p.getName())){
            // maybe remove the second condition later- just for debugging purposes.
            event.setJoinMessage(header + "Welcome " + ChatColor.BLUE + p.getName() + "!");
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
        event.setQuitMessage(header + "Bye, " + ChatColor.BLUE + player.getName() + "!");

        PlayerDBInteraction dbConn = new PlayerDBInteraction(dbCreds);
        dbConn.updateLastOnline(player_name);

    }
}
