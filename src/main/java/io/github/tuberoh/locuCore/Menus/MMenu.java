package io.github.tuberoh.locuCore.Menus;
import io.github.tuberoh.locuCore.Menu.LocuMenu;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import io.github.tuberoh.locuCore.LocuCore;
import java.io.File;

public class MMenu extends LocuMenu {

    private final LocuCore plugin;
    private final File locuCorelist;

    public MMenu(LocuCore plugin, File locuCorelist){

        super(Rows.THREE, "Menu");
        this.plugin = plugin;
        this.locuCorelist = locuCorelist;

    }

    @Override
    public void onSetItems() {

        String version = plugin.getPluginMeta().getVersion();
        ItemStack locu = createItem(Material.AMETHYST_SHARD, "§6LocuCore");
        ItemStack info = createItem(Material.PAPER, "§bVersion: §a" + version);
        ItemStack stoneclose = createItem(Material.REDSTONE, "§cClose");
        ItemStack creator = createItem(Material.COPPER_LANTERN, "§2Created by: ItsTuberino_");
        ItemStack locationslist = createItem(Material.COMPASS, "§cLocations");

        ItemStack orange_pane = createItem(Material.ORANGE_STAINED_GLASS_PANE, " ");
        for(int i=0; i<27; i++){

            setItem(i, orange_pane);

        }
        setItem(4, locu);
        setItem(13, locationslist, player -> {new LocationsMenu(plugin, 0, locuCorelist).open(player);});
        setItem(18, stoneclose, player -> {

            player.closeInventory();

        });
        setItem(22, info);
        setItem(26, creator);
    }


}

