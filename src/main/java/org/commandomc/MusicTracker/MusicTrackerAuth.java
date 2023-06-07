package org.commandomc.MusicTracker;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;


import java.util.UUID;

public class MusicTrackerAuth implements CommandExecutor {
    MusicTracker plugin;


    public MusicTrackerAuth(MusicTracker plugin) {
        this.plugin = plugin;
    }




    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Player player = (Player) sender;
        UUID uuid = player.getUniqueId();
        if (args.length == 1) {
           switch (args[0].toLowerCase()){
               case "spotify":
                   SpotifyTools.isUserAuthenticatedAsync(uuid.toString())
                           .thenAccept(isAuthenticated -> {
                               if (isAuthenticated) {
                                   sender.sendMessage(ChatColor.GREEN + "Spotify Account Linked");
                               } else {
                                   sender.sendMessage("To show the song currently playing, you need to authenticate:");
                                   TextComponent message1 = new TextComponent(ChatColor.BOLD.toString() + ChatColor.UNDERLINE + "Click Here");
                                   message1.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://mcapi.zortos.me/Spotify/Oauth/" + uuid));
                                   sender.spigot().sendMessage(message1);
                                   sender.sendMessage("After you authenticated use /Auth");
                               }
                           });
                   return true;
               case "soundcloud":
                   sender.sendMessage("We are working on SoundCloud implementation");
                   return true;
               case "apple-music":
                   sender.sendMessage("We are working on Apple Music implementation");
                   return true;

           }
        }



        return true;
    }


}
