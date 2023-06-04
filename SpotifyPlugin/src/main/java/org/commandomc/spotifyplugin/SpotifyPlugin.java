package org.commandomc.spotifyplugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

public final class SpotifyPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }


    @EventHandler
    public void onJoin(PlayerJoinEvent joinEvent) {

    }
    @EventHandler
    public void onChat(AsyncPlayerChatEvent e){
        Player player = e.getPlayer();
        Team team = player.getScoreboard().getEntryTeam(player.getName());
        if (team != null && team.getName().equals("spotify")) {
            String prefix = team.getPrefix();
            String message = e.getMessage();
            String chatMessage = prefix + ChatColor.WHITE + player.getName() + ": " + message;
            e.setFormat(chatMessage);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        UUID uuid = e.getPlayer().getUniqueId();
        Team team = e.getPlayer().getScoreboard().getEntryTeam(e.getPlayer().getName());
        if (team != null && team.getName().equals(uuid.toString())) {
            team.removeEntry(e.getPlayer().getName());
            team.unregister();
        }
    }

}
