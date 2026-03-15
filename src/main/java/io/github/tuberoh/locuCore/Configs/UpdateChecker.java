package io.github.tuberoh.locuCore.Configs;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import io.github.tuberoh.locuCore.LocuCore;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import java.net.HttpURLConnection;
import java.net.URL;


public class UpdateChecker {

    private final LocuCore plugin;

    public UpdateChecker(LocuCore plugin) {
            this.plugin = plugin;
    }

    public void check(Player player) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                HttpURLConnection conn = (HttpURLConnection)
                        new URL("https://api.modrinth.com/v2/project/locucore/version")
                                .openConnection();
                conn.setRequestProperty("User-Agent", "LocuCore");

                JsonArray versions = JsonParser.parseString(new String(conn.getInputStream().readAllBytes())).getAsJsonArray();
                String latest = versions.get(0).getAsJsonObject().get("version_number").getAsString();

                if (!latest.equalsIgnoreCase(plugin.getPluginMeta().getVersion())) {
                    Bukkit.getScheduler().runTask(plugin, () ->
                            player.sendMessage(Component.text()
                                    .append(Component.text("------LocuCore------\n", NamedTextColor.GOLD))
                                    .append(Component.empty())
                                    .append(Component.text("\nUpdate Available!\n", NamedTextColor.YELLOW))
                                    .append(Component.text("Current Version: ", NamedTextColor.GRAY))
                                    .append(Component.text(plugin.getPluginMeta().getVersion() + "\n", NamedTextColor.GREEN))
                                    .append(Component.text("New Version: ", NamedTextColor.GRAY))
                                    .append(Component.text(latest + "\n", NamedTextColor.GREEN))
                                    .append(Component.empty())
                                    .append(Component.text("Download it now!\n", NamedTextColor.GOLD)
                                            .clickEvent(ClickEvent.openUrl("https://modrinth.com/plugin/locucore"))
                                            .decorate(TextDecoration.UNDERLINED).decorate(TextDecoration.ITALIC))
                                    .append(Component.empty())
                                    .append(Component.text("\n---------------------", NamedTextColor.GOLD))
                                    .build())
                    );
                }

            } catch (Exception ignored) {}
        });
    }
}