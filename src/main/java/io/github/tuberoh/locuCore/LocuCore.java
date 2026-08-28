package io.github.tuberoh.locuCore;

import io.github.tuberoh.locuCore.Commands.LocuCommand;
import io.github.tuberoh.locuCore.Utilities.DataController;
import io.github.tuberoh.locuCore.Utilities.MigrationSystem;
import io.github.tuberoh.locuCore.listeners.MenuListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import java.io.File;


public final class LocuCore extends JavaPlugin {

    DataController dc = new DataController(this);
    @Override
    public void onLoad(){

        //Plugin on load logic
        getLogger().info("LocuCore is loading. Wait");

    }


    @Override
    public void onEnable() {

        if(!dc.connect()){

            getLogger().severe("Database initialization failed. Disabling LocuCore.");
            Bukkit.getPluginManager().disablePlugin(this);
            return;

        }
        final File LocuCorelist = new File(getDataFolder(), "data.yml");

        if(LocuCorelist.exists()){

            MigrationSystem ms = new MigrationSystem(this, LocuCorelist, dc);
            ms.startMigration();

        }

        LocuCommand locuCommand = new LocuCommand(this, dc);
        getCommand("luc").setExecutor(locuCommand);
        getCommand("luc").setTabCompleter(locuCommand);
        getServer().getPluginManager().registerEvents(new MenuListener(), this);
        getLogger().info("LocuCore is enabled. Nothing went wrong");

    }

    @Override
    public void onDisable() {

        dc.disconnect();
        getLogger().info("LocuCore is disabled");

    }
}
