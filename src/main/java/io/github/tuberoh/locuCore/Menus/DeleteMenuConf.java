package io.github.tuberoh.locuCore.Menus;

import io.github.tuberoh.locuCore.LocuCore;
import io.github.tuberoh.locuCore.Menu.LocuMenu;
import io.github.tuberoh.locuCore.Objects.Waypoints;
import io.github.tuberoh.locuCore.Utilities.DataController;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class DeleteMenuConf extends LocuMenu {

    private final String locationName;
    private final LocuCore plugin;
    private final DataController dc;
    private final Waypoints wp;


    public DeleteMenuConf(LocuCore plugin, String locationName, DataController dc, Waypoints wp) {

        super(Rows.THREE, "§c" + "Are you sure?");
        this.plugin = plugin;
        this.locationName = locationName;
        this.dc = dc;
        this.wp = wp;

    }

    @Override
    public void onSetItems() {

        ItemStack gray = createItem(Material.GRAY_STAINED_GLASS_PANE, " ");

        for(int i=0; i<27; i++){

            setItem(i, gray);

        }

        ItemStack deny = createItem(Material.RED_CONCRETE, "§cDeny");
        setItem(14, deny, player -> {

            new WaypointDetailed(plugin, locationName, dc, wp).open(player);

        });

        ItemStack confirm = createItem(Material.GREEN_CONCRETE, "§aConfirm");
        setItem(12, confirm, player -> {

            dc.deleteWaypoint(wp.getOwner_uuid(), locationName);
            String status = wp.getStatus() ? "public" : "private";
            new WpMenu(plugin, 0, dc, status).open(player);
            player.sendMessage("§8[§6LocuCore§8] §c" + wp.getName() + "has been deleted successfully");

        });

    }
}
