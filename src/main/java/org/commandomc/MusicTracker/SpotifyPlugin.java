package org.commandomc.MusicTracker;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import org.bukkit.scoreboard.*;

import java.util.UUID;

public final class SpotifyPlugin extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        this.getCommand("auth").setExecutor(new SpotifyAuth(this));
        this.getCommand("spotifySync").setExecutor(new SpotifyManualSync(this));

        getServer().getPluginManager().registerEvents(this, this);
        int delay = 0;
        int period = 20 * 60; // 20 ticks per second * 60 seconds = 1 minute

        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
                // Execute the spotifySync command here
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "spotifySync");
            }
        }, delay, period);

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
