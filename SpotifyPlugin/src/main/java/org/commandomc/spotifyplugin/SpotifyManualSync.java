package org.commandomc.spotifyplugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Team;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class SpotifyManualSync implements CommandExecutor {
    SpotifyPlugin plugin;

    public SpotifyManualSync(SpotifyPlugin plugin) {
        this.plugin = plugin;
    }



    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
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




                            Team team = player.getScoreboard().getTeam(uuid.toString());
                            if (team == null) {
                                team = player.getScoreboard().registerNewTeam(uuid.toString());
                                team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
                            }

                            String currentPrefix = team.getPrefix();
                            String newPrefix = ChatColor.GREEN + "[" + songName + "] ";

                            if (!newPrefix.equals(currentPrefix)) {
                                team.setPrefix(newPrefix);
                            }

                            team.addEntry(player.getName());

                            Bukkit.broadcastMessage(ChatColor.YELLOW + "Synced: " + player.getName());

                        } else {
                            Team team = player.getScoreboard().getEntryTeam(player.getName());
                            if (team != null && team.getName().equals(uuid.toString())) {
                                team.removeEntry(player.getName());
                                team.unregister();
                            }


                        }
                    });
                }
            }
        }.runTaskAsynchronously(plugin);

        return true;
    }
}
