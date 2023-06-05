package org.commandomc.MusicTracker;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class SpotifyManualSync implements CommandExecutor {
    public HashMap<String, Long> cooldowns = new HashMap<>();

    SpotifyPlugin plugin;

    public SpotifyManualSync(SpotifyPlugin plugin) {
        this.plugin = plugin;
    }



    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (cooldowns.containsKey(sender.getName())) {
            long cooldownExpiration = cooldowns.get(sender.getName());
            long currentTime = System.currentTimeMillis();
            long secondsLeft = (cooldownExpiration - currentTime) / 1000;

            if (secondsLeft > 0) {
                // Cool down
                return true;
            }
        }
        // Girlfir
        // No cooldown found or cooldown has expired, save new cooldown
        int cooldownTime = 5;
        long cooldownExpiration = System.currentTimeMillis() + (cooldownTime * 1000);
        cooldowns.put(sender.getName(), cooldownExpiration);
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    UUID uuid = player.getUniqueId();
                    CompletableFuture<Boolean> isAuthenticatedFuture = SpotifyTools.isUserAuthenticatedAsync(uuid.toString());
                    CompletableFuture<String> nowPlayingFuture = SpotifyTools.getSongPlayingAsync(uuid.toString());

                    CompletableFuture<Void> combinedFuture = CompletableFuture.allOf(isAuthenticatedFuture, nowPlayingFuture);
                    combinedFuture.thenAccept(ignored -> {
                        boolean isAuthenticated = isAuthenticatedFuture.join();
                        String nowPlaying = nowPlayingFuture.join();

                        if (isAuthenticated && !nowPlaying.isEmpty()) {
                            String songName = "♪ " + nowPlaying;

                            String currentPrefix = player.getDisplayName().split(" ")[0]; // Get the current prefix from the display name

                            String newPrefix = ChatColor.GREEN + "[" + songName + "]  " + ChatColor.WHITE;

                            if (!newPrefix.equals(currentPrefix)) {
                                String newName = newPrefix + player.getName();
                                player.setDisplayName(newName); // Set the new display name with the chat prefix
                                player.setPlayerListName(newName); // Update the player's name in the player list as well
                            }
                            sender.sendMessage(ChatColor.GREEN + "Synced User: " + player.getName());

                            Bukkit.getLogger().info(ChatColor.YELLOW + "Synced User: " + player.getName());
                        } else {
                            String currentPrefix = player.getDisplayName().split(" ")[0]; // Get the current prefix from the display name
                            if (!currentPrefix.isEmpty()) {
                                String newName = player.getName();
                                player.setDisplayName(newName); // Remove the chat prefix from the display name
                                player.setPlayerListName(newName); // Update the player's name in the player list as well
                            }
                            sender.sendMessage(ChatColor.RED+"User : " +  player.getName() + "Auth Expired");
                        }
                    });
                }
            }
        }.runTaskAsynchronously(plugin);

        return true;
    }
}
