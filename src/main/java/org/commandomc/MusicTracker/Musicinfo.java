package org.commandomc.MusicTracker;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.UUID;



public class Musicinfo implements CommandExecutor {

    MusicTracker plugin;
    public Musicinfo(MusicTracker plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1 || Bukkit.getPlayer(args[0]) != null) {
            Player player = Bukkit.getPlayer(args[0]);
            UUID uuid = player.getUniqueId();
            String username = player.getName();

            new BukkitRunnable() {
                @Override
                public void run() {
                    String response = SpotifyTools.getNowPlayingAsync(uuid.toString());
                    try {
                        JSONParser parser = new JSONParser();
                        Object obj = parser.parse(response);

                        JSONObject json = (JSONObject) obj;
                        JSONObject item = (JSONObject) json.get("item");
                        JSONArray artistsArray = (JSONArray) item.get("artists");

                        StringBuilder artistsBuilder = new StringBuilder();
                        for (Object artistObj : artistsArray) {
                            JSONObject artist = (JSONObject) artistObj;
                            String artistName = (String) artist.get("name");
                            artistsBuilder.append(artistName).append(", ");
                        }
                        String artists = artistsBuilder.toString().trim();
                        if (artists.endsWith(",")) {
                            artists = artists.substring(0, artists.length() - 1);
                        }

                        String songName = (String) item.get("name");

                        String musicUri = item.get("uri").toString();
                        String musicUrl = "https://open.spotify.com/track/" + musicUri.split(":")[2];

                        String finalArtists = artists;
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                sender.sendMessage(ChatColor.GREEN + username + " Is Playing " + songName + " By " + finalArtists + " On Spotify");
                                TextComponent message1 = new TextComponent(ChatColor.BOLD.toString() + ChatColor.UNDERLINE + "Listen to the song");
                                message1.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, musicUrl));
                                sender.spigot().sendMessage(message1);
                            }
                        }.runTask(plugin);

                    } catch (ParseException e) {
                        e.printStackTrace();
                        sender.sendMessage(ChatColor.RED + "User is not listening to anything");
                    }
                }
            }.runTaskAsynchronously(plugin);

            return true;
        } else {
            sender.sendMessage("Player not found.");
            return true;
        }

    }
}
