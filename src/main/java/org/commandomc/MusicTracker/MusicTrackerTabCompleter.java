package org.commandomc.MusicTracker;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MusicTrackerTabCompleter implements TabCompleter {

    private List<String> getStreamingServices(){
        List<String> array = Arrays.asList("spotify", "soundcloud", "apple-music");
        return array;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (command.getName().equalsIgnoreCase("auth")) {
            return getStreamingServices();
        }




        return null;
    }

}
