package io.github.tuberoh.locuCore.Menus;

import io.github.tuberoh.locuCore.LocuCore;
import io.github.tuberoh.locuCore.Menu.LocuMenu;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import java.io.File;

public class DeleteMenuConf extends LocuMenu {

    private final String locationName;
    private final LocuCore plugin;
    private final File locuCorelist;
    private final FileConfiguration locuCoreconfig;


    public DeleteMenuConf(LocuCore plugin, String locationName, File locuCorelist){

        super(Rows.THREE, "§c" + "Are you sure?");
        this.plugin = plugin;
        this.locationName = locationName;
        this.locuCorelist = locuCorelist;
        locuCoreconfig = YamlConfiguration.loadConfiguration(locuCorelist);

    }

    @Override
    public void onSetItems() {

        ItemStack gray = createItem(Material.GRAY_STAINED_GLASS_PANE, " ");

        for(int i=0; i<27; i++){

            setItem(i, gray);

        }

        ItemStack deny = createItem(Material.RED_CONCRETE, "§cDeny");
        setItem(14, deny, player -> {

            new LocationDetailed(plugin, locationName, locuCorelist).open(player);

        });

        ItemStack confirm = createItem(Material.GREEN_CONCRETE, "§aConfirm");
        setItem(12, confirm, player -> {
            try {
                locuCoreconfig.set("Location." + locationName, null);
                locuCoreconfig.save(locuCorelist);
                player.sendMessage("§8[§6LocuCore§8] §aLocation removed: " + locationName);
                new LocationsMenu(plugin, 0, locuCorelist).open(player);
            }
            catch (Exception e) {
                player.sendMessage("§8[§6LocuCore§8] §cError on removing the location!");
                plugin.getLogger().severe("Error: " + e.getMessage());
            }
        });

    }
}
