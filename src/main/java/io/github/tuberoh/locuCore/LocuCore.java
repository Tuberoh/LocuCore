package io.github.tuberoh.locuCore;

import io.github.tuberoh.locuCore.Commands.LocuCommand;
import io.github.tuberoh.locuCore.listeners.MenuListener;
import io.github.tuberoh.locuCore.listeners.PlayerListener;
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

        getLogger().info("[LocuCore] The plugin is enabled. Nothing went wrong");
        final File LocuCorelist = new File(getDataFolder(), "data.yml");
        if (!LocuCorelist.exists()) {
            saveResource("data.yml", false);
        }


        LocuCommand locuCommand = new LocuCommand(this, LocuCorelist);
        try{

            getCommand("luc").setExecutor(locuCommand);
            getCommand("luc").setTabCompleter(locuCommand);

        }
        catch(NullPointerException e){

            getLogger().warning(e.getMessage());

        }

        getServer().getPluginManager().registerEvents(new MenuListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);

    }

    @Override
    public void onDisable() {

        getLogger().info("[LocuCore] The plugin is disabled");

    }
}
