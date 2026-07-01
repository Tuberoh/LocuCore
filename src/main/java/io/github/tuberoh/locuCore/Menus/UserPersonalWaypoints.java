package io.github.tuberoh.locuCore.Menus;

import io.github.tuberoh.locuCore.LocuCore;
import io.github.tuberoh.locuCore.Menu.LocuMenu;
import io.github.tuberoh.locuCore.Utilities.DataController;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class UserPersonalWaypoints extends LocuMenu {

    private final LocuCore plugin;
    private final DataController dc;

    public UserPersonalWaypoints(LocuCore plugin, DataController dc) {

        super(Rows.FIVE, "Menu");
        this.plugin = plugin;
        this.dc = dc;

    }

    @Override
    public void onSetItems() {

        ItemStack private_waypoints = createItem(Material.RED_SHULKER_BOX, "§cPrivate Waypoints");
        ItemStack public_waypoints = createItem(Material.LIME_SHULKER_BOX, "§aPublic Waypoints");
        ItemStack back_button = createItem(Material.ARROW, "§e§l Back");
        ItemStack home_button = createItem(Material.OAK_DOOR, "§eHome");

        ItemStack orange_pane = createItem(Material.ORANGE_STAINED_GLASS_PANE, " ");
        ItemStack gray_pane = createItem(Material.GRAY_STAINED_GLASS_PANE, " ");

        for(int i=0; i<9; i++){

            setItem(i, gray_pane);

        }

        for(int i=9; i<36; i++){

            setItem(i, orange_pane);

        }
        for(int i=36; i<45; i++){

            setItem(i, gray_pane);

        }
        setItem(21, private_waypoints, player -> {

            new WpMenu(plugin, 0, dc, "private_owned").open(player);

        });
        setItem(23, public_waypoints, player -> {

            new WpMenu(plugin, 0, dc, "public_owned").open(player);

        });
        setItem(40,  home_button, player -> {

            new MMenu(plugin, dc).open(player);

        });
        setItem(39, back_button, player -> {

           new WaypointsTypeSelector(plugin, dc).open(player);

        });

    }

}
