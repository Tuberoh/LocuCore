package io.github.tuberoh.locuCore;

import io.github.tuberoh.locuCore.Commands.LocuCommand;
import io.github.tuberoh.locuCore.listeners.MenuListener;
import org.bukkit.plugin.java.JavaPlugin;
import java.io.File;


public final class LocuCore extends JavaPlugin {

    @Override
    public void onLoad(){

        //Plugin on load logic
        getLogger().info("LocuCore is loading. Wait");

    }


    @Override
    public void onEnable() {

        getLogger().info("LocuCore is enabled. Nothing went wrong");

        saveResource("data.yml", false);

        final File LocuCorelist = new File(getDataFolder(), "data.yml");

        LocuCommand locuCommand = new LocuCommand(this, LocuCorelist);
        getCommand("luc").setExecutor(locuCommand);
        getCommand("luc").setTabCompleter(locuCommand);
        getServer().getPluginManager().registerEvents(new MenuListener(), this);

    }

    @Override
    public void onDisable() {

        getLogger().info("LocuCore is disabled");

    }
}
