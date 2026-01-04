package io.github.tuberoh.locuCore.Menus;
import io.github.tuberoh.locuCore.LocuCore;
import io.github.tuberoh.locuCore.Menu.LocuMenu;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class LocationsMenu extends LocuMenu {

    private final LocuCore plugin;
    private final int page;
    private final int items_page = 36;
    private final File locuCorelist;
    private final FileConfiguration locuCoreconfig;

    public LocationsMenu(LocuCore plugin, int page, File locuCorelist) {
        super(Rows.FIVE, "Locations - Pag. " + (page+1));
        this.plugin = plugin;
        this.page = page;
        this.locuCorelist = locuCorelist;
        locuCoreconfig = YamlConfiguration.loadConfiguration(locuCorelist);

    }
    @Override
    public void onSetItems() {

        ConfigurationSection locations = locuCoreconfig.getConfigurationSection("Location");

        if (locations == null || locations.getKeys(false).isEmpty()) {
            ItemStack noLocations = createItem(Material.RED_STAINED_GLASS_PANE, "§c§lNo Locations saved", "§7Use /luc set");
            setItem(22, noLocations, player -> {});

            setItem(40, createItem(Material.OAK_DOOR, "§eHome"), player -> {new MMenu(plugin, locuCorelist).open(player);});
            return;
        }

        List<String> allLocations = new ArrayList<>(locations.getKeys(false));
        int startIndex = page * items_page;
        int endIndex = Math.min(startIndex + items_page, allLocations.size());
        int slot = 0;

        for (int i = startIndex; i < endIndex; i++) {
            final String locName = allLocations.get(i);

            String path = "Location." + locName;
            String world = locuCoreconfig.getString(path + ".World");
            String creatorUUID = locuCoreconfig.getString(path + ".UUID_creator");

            ItemStack item = createItem(
                    Material.COMPASS,
                    "§6§l" + locName,
                    "§bWorld: §f" + world,
                    "§eOwner: §f" + new MMenu(plugin, locuCorelist).getNameFromUUID(creatorUUID),
                    "",
                    "§a§nClick to manage"
            );

            setItem(slot, item, player -> {
                new LocationDetailed(plugin, locName, locuCorelist).open(player);
            });

            slot++;
        }

        if (page > 0) {
            ItemStack arrowLeft = createItem(Material.ARROW, "§e§l Back");
            setItem(39, arrowLeft, player -> {new LocationsMenu(plugin, page-1, locuCorelist).open(player);});
        }

        ItemStack backButton = createItem(Material.OAK_DOOR, "§eMain page");
        setItem(40, backButton, player -> {
            new MMenu(plugin, locuCorelist).open(player);
        });


        if (endIndex < allLocations.size()) {
            ItemStack arrowRight = createItem(Material.ARROW, "§e§lNext Page");
            setItem(41, arrowRight, player -> {
                new LocationsMenu(plugin, page + 1, locuCorelist).open(player);
            });
        }

    }

}

