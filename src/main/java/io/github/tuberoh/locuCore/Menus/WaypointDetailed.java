package io.github.tuberoh.locuCore.Menus;
import io.github.tuberoh.locuCore.LocuCore;
import io.github.tuberoh.locuCore.Menu.LocuMenu;
import io.github.tuberoh.locuCore.Objects.Waypoints;
import io.github.tuberoh.locuCore.Utilities.DataController;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class WaypointDetailed extends LocuMenu{

    private final String locationName;
    private final LocuCore plugin;
    private final DataController dc;
    private final Waypoints wp;
    private final Set<UUID> onCooldown = new HashSet<>();

    public WaypointDetailed(LocuCore plugin, String locationName, DataController dc, Waypoints wp) {
        super(Rows.FOUR, "§0§n" + locationName);
        this.plugin = plugin;
        this.locationName = locationName;
        this.dc = dc;
        this.wp = wp;

    }

    @Override
    public void onSetItems(){

        ItemStack orange_pane = createItem(Material.ORANGE_STAINED_GLASS_PANE, " ");
        ItemStack gray_pane = createItem(Material.GRAY_STAINED_GLASS_PANE, " ");

        for(int i=0; i<27; i++){

            setItem(i, orange_pane);

        }
        for(int i=28; i<36; i++){

            setItem(i, gray_pane);

        }

        String worldName = wp.getWorld();
        double x = wp.getX();
        double y = wp.getY();
        double z = wp.getZ();
        double yaw = wp.getYaw();
        double pitch = wp.getPitch();
        String creatorUUID = wp.getOwner_uuid();
        UUID id = UUID.fromString(creatorUUID);
        OfflinePlayer p = Bukkit.getOfflinePlayer(id);
        String username = p.getName();

        String coord = "§fx: " + Math.floor(x) +" y: " + Math.floor(y) + " z: " + Math.floor(z);
        String status = wp.getStatus() ? "public" : "private";

        ItemStack location_info = createItem(Material.COMPASS,"§6" + locationName, "§9Coordinates: " + coord, "§eOwner: §f" + username , "§bWorld: §f" + worldName, "§dStatus: §f" + status);
        setItem(4, location_info);

        World world = Bukkit.getWorld(worldName);
        
        ItemStack tpItem = createItem(Material.ENDER_PEARL, "§aTeleport");

        setItem(14, tpItem, player -> {
            if (world == null) {
                player.sendMessage("§8[§6LocuCore§8] §cError: the world doesn't exists anymore!");
                return;
            }

            Location location = new Location(world, x, y, z, (float) yaw, (float) pitch);
            player.teleport(location);
            player.sendMessage("§8[§6LocuCore§8] §aTeleported to: §e" + locationName);
            player.closeInventory();
            player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);
            player.spawnParticle(Particle.END_ROD, player.getLocation(), 80, 1, 1, 1, 0.1);
        });


        ItemStack removeItem = createItem(Material.TNT, "§cRemove", "§7Delete this location");

        setItem(12, removeItem, player -> {

            if (!canRemove(player, creatorUUID)) {
                player.sendMessage("§8[§6LocuCore§8] §cYou don't have the right permission");
            }
            else{

                new DeleteMenuConf(plugin, locationName, dc, wp).open(player);

            }

        });

        ItemStack mainMenu = createItem(Material.OAK_DOOR, "§eMain Menu");

        setItem(31, mainMenu, player -> {

            new MMenu(plugin, dc).open(player);

        });

        ItemStack close = createItem(Material.REDSTONE, "§cClose");

        setItem(27, close, player -> {

            player.closeInventory();

        });

        ItemStack object = createItem(Material.LANTERN, " ");
        setItem(13, object);
        ItemStack blocked = createItem(Material.BARRIER, "§cBlocked");

        if(viewer.getUniqueId().toString().equals(wp.getOwner_uuid()) || viewer.hasPermission("locucore.rank.admin")){

            renderVisibilityToggle(wp);

        }
        else{

            setItem(22, blocked, player -> {

                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
                player.sendMessage("§8[§6LocuCore§8] §cYou don't have the right permission");

            });

        }


    }

    private boolean canRemove(Player player, String creatorUUID) {

        if (creatorUUID != null && player.getUniqueId().toString().equals(creatorUUID)) {
            return true;
        }

        return player.hasPermission("locucore.rank.admin");

    }
    private void renderVisibilityToggle(Waypoints wp) {
        boolean isPublic = wp.getStatus();

        ItemStack green_dye = createItem(Material.LIME_DYE, "§aPublic");
        ItemStack red_dye = createItem(Material.RED_DYE, "§cPrivate");

        ItemStack icon = isPublic ? green_dye : red_dye;

        setItem(22, icon, player ->{
            boolean newStatus = !isPublic;
            UUID uuid = player.getUniqueId();
            long COOLDOWN_TICKS = 60L;
            if(onCooldown.contains(uuid)){

                player.sendMessage("§8[§6LocuCore§8] §cPlease, don't spam");
                return;

            }
            onCooldown.add(uuid);
            Bukkit.getScheduler().runTaskLater(plugin, () -> onCooldown.remove(uuid), COOLDOWN_TICKS);

            dc.editVisibility(wp.getOwner_uuid(), wp.getName(), newStatus);
            wp.setStatus(newStatus);
            String status = newStatus ? "§2public" : "§4private";
            player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
            player.sendMessage("§8[§6LocuCore§8] §a" + wp.getName() + " is now " + status);


            renderVisibilityToggle(wp);
        });
    }
}

