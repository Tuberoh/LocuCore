package io.github.tuberoh.locuCore.listeners;
import io.github.tuberoh.locuCore.Configs.UpdateChecker;
import io.github.tuberoh.locuCore.LocuCore;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerListener implements Listener {

    private final LocuCore plugin;

    public PlayerListener(LocuCore plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission("locucore.rank.admin")) {
            Bukkit.getScheduler().runTaskLater(plugin, () -> new UpdateChecker(plugin).check(player), 20L);
        }
    }
}
