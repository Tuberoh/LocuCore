package io.github.tuberoh.locuCore.Menus;
import io.github.tuberoh.locuCore.LocuCore;
import io.github.tuberoh.locuCore.Menu.LocuMenu;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;

public class LocationDetailed extends LocuMenu{

    private final String locationName;
    private final LocuCore plugin;
    private final File locuCorelist;
    private final FileConfiguration locuCoreconfig;

    public LocationDetailed(LocuCore plugin, String locationName, File locuCorelist) {
        super(Rows.FOUR, "§0§n" + locationName);
        this.plugin = plugin;
        this.locationName = locationName;
        this.locuCorelist = locuCorelist;
        locuCoreconfig = YamlConfiguration.loadConfiguration(locuCorelist);

    }

    @Override
    public void onSetItems() {

        ItemStack orange_pane = createItem(Material.ORANGE_STAINED_GLASS_PANE, " ");
        ItemStack gray_pane = createItem(Material.GRAY_STAINED_GLASS_PANE, " ");

        for(int i=0; i<27; i++){

            setItem(i, orange_pane);

        }
        for(int i=28; i<36; i++){

            setItem(i, gray_pane);

        }

        String path = "Location." + locationName;

        if (!locuCoreconfig.contains(path)) {
            ItemStack error = createItem(
                    Material.BARRIER,
                    "§c§lError!",
                    "§7This location doesn't exists anymore!"
            );
            setItem(13, error, player -> {});
            return;
        }
        String worldName = locuCoreconfig.getString(path + ".World");
        double x = locuCoreconfig.getDouble(path + ".Coordinate.x");
        double y = locuCoreconfig.getDouble(path + ".Coordinate.y");
        double z = locuCoreconfig.getDouble(path + ".Coordinate.z");
        String creatorUUID = locuCoreconfig.getString(path + ".UUID_creator");
        String coord = "§fx: " + Math.floor(x) +" y: " + Math.floor(y) + " z: " + Math.floor(z);

        ItemStack location_info = createItem(Material.COMPASS,"§6" + locationName, "§9Coordinates: " + coord, "§eOwner: §f" + new MMenu(plugin, locuCorelist).getNameFromUUID(creatorUUID), "§bWorld: §f" + worldName);
        setItem(4, location_info);

        World world = Bukkit.getWorld(worldName);
        
        ItemStack tpItem = createItem(Material.ENDER_PEARL, "§aTeleport");

        setItem(14, tpItem, player -> {
            if (world == null) {
                player.sendMessage("§8[§6LocuCore§8] §cError: the world doesn't exists anymore!");
                return;
            }

            Location location = new Location(world, x, y, z);
            player.teleport(location);
            player.sendMessage("§8[§6LocuCore§8] §aTeleported to: §e" + locationName);
            player.closeInventory();
            player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);
            player.spawnParticle(Particle.END_ROD, player.getLocation(), 80, 1, 1, 1, 0.1);
        });


        ItemStack removeItem = createItem(Material.TNT, "§cRemove", "§7Delete this location");

        setItem(12, removeItem, player -> {
            if (!canRemove(player, creatorUUID)) {
                player.sendMessage("§8[§6LocuCore§8] §c§lYou don't have the right permission");
            }
            else{

                new DeleteMenuConf(plugin, locationName, locuCorelist).open(player);

            }
        });

        ItemStack backButton = createItem(Material.ARROW, "§eBack");

        setItem(30, backButton, player -> {
            new LocationsMenu(plugin, 0, locuCorelist).open(player);
        });

        ItemStack mainMenu = createItem(Material.OAK_DOOR, "§eMain Menu");

        setItem(31, mainMenu, player -> {

            new MMenu(plugin, locuCorelist).open(player);

        });

        ItemStack close = createItem(Material.REDSTONE, "§cClose");

        setItem(27, close, player -> {

            player.closeInventory();

        });

        ItemStack object = createItem(Material.LANTERN, " ");
        setItem(13, object);

    }

    private boolean canRemove(Player player, String creatorUUID) {

        if (creatorUUID != null && player.getUniqueId().toString().equals(creatorUUID)) {
            return true;
        }

        return player.hasPermission("locucore.rank.admin");

    }
}
