package org.commandomc.spotifyplugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.bukkit.Bukkit.getServer;


public class SpotifyTools {




    public static CompletableFuture<String> getNowPlayingAsync(String uuid) {
        return CompletableFuture.supplyAsync(() -> {
            String urlString = "https://mcapi.zortos.me/Spotify/Songplaying/" + uuid;
            StringBuilder response = new StringBuilder();

            try {
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();
                } else {
                    // Handle error response
                    return "";
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return response.toString();
        }, CompletableFuture.delayedExecutor(0, TimeUnit.MILLISECONDS));
    }

    public static CompletableFuture<Boolean> isUserAuthenticatedAsync(String uuid) {
        return CompletableFuture.supplyAsync(() -> {
            String urlString = "https://mcapi.zortos.me/Spotify/Checktoken/" + uuid;
            int responseCode = 0;

            try {
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                responseCode = connection.getResponseCode();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return responseCode == HttpURLConnection.HTTP_OK;
        }, CompletableFuture.delayedExecutor(0, TimeUnit.MILLISECONDS));
    }

    // Gets nowplaying and returns the songname
    public static CompletableFuture<String> getSongPlayingAsync(String uuid) {
        return getNowPlayingAsync(uuid)
                .thenApply(response -> {
                    JSONParser parser = new JSONParser();
                    try {
                        Object obj = parser.parse(response);
                        JSONObject json = (JSONObject) obj;
                        JSONObject item = (JSONObject) json.get("item");
                        String songName = (String) item.get("name");
                        return songName;
                    } catch (ParseException e) {
                        e.printStackTrace();
                        return "";
                    }
                });
    }

    public static CompletableFuture<Boolean> isUserInDatabaseAsync(String uuid) {
        return CompletableFuture.supplyAsync(() -> {
            String urlString = "https://mcapi.zortos.me/Spotify/Checkuser/" + uuid;

            try {
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                int responseCode = connection.getResponseCode();
                connection.disconnect();

                return responseCode == HttpURLConnection.HTTP_OK;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        });
    }











}

