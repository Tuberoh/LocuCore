package io.github.tuberoh.locuCore.Menus;

import io.github.tuberoh.locuCore.LocuCore;
import io.github.tuberoh.locuCore.Menu.LocuMenu;
import io.github.tuberoh.locuCore.Utilities.DataController;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class WaypointsTypeSelector extends LocuMenu{

    private final LocuCore plugin;
    private final DataController dc;

    public WaypointsTypeSelector(LocuCore plugin, DataController dc) {
        super(Rows.FIVE, "Select Waypoints Type" );
        this.plugin = plugin;
        this.dc = dc;

    }

    @Override
    public void onSetItems() {

        ItemStack orange_pane = createItem(Material.ORANGE_STAINED_GLASS_PANE, " ");
        ItemStack gray_pane = createItem(Material.GRAY_STAINED_GLASS_PANE, " ");

        for(int i=0; i<9; i++){

            setItem(i, gray_pane);

        }
        for(int i=0; i<36; i++){

            setItem(i, orange_pane);

        }
        for(int i=36; i<45; i++){

            setItem(i, gray_pane);

        }

        ItemStack back_button= createItem(Material.OAK_DOOR, "§eHome");

        setItem(40, back_button, player -> {

            new MMenu(plugin, dc).open(player);

        });

        ItemStack public_waypoints= createItem(Material.COMPASS, "§cPublic Waypoints");

        setItem(23, public_waypoints, player -> {

           new WpMenu(plugin, 0 , dc, "public").open(player);

        });

        ItemStack private_waypoints= createItem(Material.RECOVERY_COMPASS, "§ePersonal Waypoints");

        setItem(21, private_waypoints, player -> {

            new UserPersonalWaypoints(plugin, dc).open(player);

        });

    }
}
