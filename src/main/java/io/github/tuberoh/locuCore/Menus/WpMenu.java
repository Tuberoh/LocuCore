package io.github.tuberoh.locuCore.Menus;

import io.github.tuberoh.locuCore.LocuCore;
import io.github.tuberoh.locuCore.Menu.LocuMenu;
import io.github.tuberoh.locuCore.Objects.Waypoints;
import io.github.tuberoh.locuCore.Utilities.DataController;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import java.util.ArrayList;
import java.util.List;

public class WpMenu extends LocuMenu{

    private final LocuCore plugin;
    private final int page;
    private final String type;
    private final DataController dc;
    private static final int ITEMS_PER_PAGE = 36;
    private static final int SLOT_EMPTY    = 22;
    private static final int SLOT_PREV     = 39;
    private static final int SLOT_HOME     = 40;
    private static final int SLOT_NEXT     = 41;

    public WpMenu(LocuCore plugin, int page, DataController dc, String type) {

        super(LocuMenu.Rows.FIVE, "Waypoints - Pag. " + (page+1));
        this.plugin = plugin;
        this.page = page;
        this.dc = dc;
        this.type = type;

    }
    @Override
    public void onSetItems() {

        List<Waypoints> waypoints = new ArrayList<>();
        String owner_uuid = viewer.getUniqueId().toString();
        ItemStack gray_pane = createItem(Material.GRAY_STAINED_GLASS_PANE, " ");

         if(type.equals("private_owned") && dc.getPrivateWaypointsNames(owner_uuid).isEmpty()) {

             ItemStack noLocations = createItem(Material.RED_STAINED_GLASS_PANE, "§c§lNo private Waypoints saved", "§7Use /luc set");
             setItem(SLOT_EMPTY, noLocations, player -> {});

             setItem(SLOT_HOME, createItem(Material.OAK_DOOR, "§eHome"), player -> {new MMenu(plugin, dc).open(player);});
             return;


         }
         if(type.equals("public_owned") && dc.getPublicWaypoints(owner_uuid).isEmpty()){

             ItemStack noLocations = createItem(Material.RED_STAINED_GLASS_PANE, "§c§lNo public personal waypoints saved", "§7Use /luc set");
             setItem(SLOT_EMPTY, noLocations, player -> {});

             setItem(SLOT_HOME, createItem(Material.OAK_DOOR, "§eHome"), player -> {new MMenu(plugin, dc).open(player);});
             return;



         }


         if(type.equals("public") && dc.getAllPublicWaypointsNames().isEmpty()) {

             ItemStack noLocations = createItem(Material.RED_STAINED_GLASS_PANE, "§c§lNo public Waypoints saved", "§7Use /luc set");
             setItem(SLOT_EMPTY, noLocations, player -> {});

             setItem(SLOT_HOME, createItem(Material.OAK_DOOR, "§eHome"), player -> {new MMenu(plugin, dc).open(player);});
             return;


         }
         if(type.equals("private_owned")){

             waypoints = new ArrayList<>(dc.getPrivateWaypoints(owner_uuid));

         }
         else if(type.equals("public_owned")){

             waypoints = new ArrayList<>(dc.getPublicWaypoints(owner_uuid));

         }
         else if(type.equals("public")){

             waypoints = new ArrayList<>(dc.getAllPublicWaypoints());

         }

        for(int i=36; i<45; i++){

            setItem(i, gray_pane);

        }


        int startIndex = page * ITEMS_PER_PAGE;
        int endIndex = Math.min(startIndex + ITEMS_PER_PAGE, waypoints.size());
        int slot = 0;

        for (int i = startIndex; i < endIndex; i++){

            final Waypoints wp= waypoints.get(i);

            String locName = wp.getName();
            String world = wp.getWorld();
            String creatorUUID = wp.getOwner_uuid();

            ItemStack item = createItem(
                    Material.COMPASS,
                    "§6§l" + locName,
                    "§bWorld: §f" + world,
                    "§eOwner: §f" + new MMenu(plugin, dc).getNameFromUUID(creatorUUID),
                    "",
                    "§a§nClick to manage"
            );

            setItem(slot, item, player -> {
                new WaypointDetailed(plugin, locName, dc, wp).open(player);
            });

            slot++;
        }

        if (page > 0) {
            ItemStack arrowLeft = createItem(Material.ARROW, "§e§l Back");
            setItem(SLOT_PREV, arrowLeft, player -> {new WpMenu(plugin, page-1, dc, type).open(player);});
        }

        ItemStack backButton = createItem(Material.OAK_DOOR, "§eHome");
        setItem(SLOT_HOME, backButton, player -> {
            new MMenu(plugin, dc).open(player);
        });


        if (endIndex < waypoints.size()) {
            ItemStack arrowRight = createItem(Material.ARROW, "§e§lNext Page");
            setItem(SLOT_NEXT, arrowRight, player -> {
                new WpMenu(plugin, page + 1, dc, type).open(player);
            });
        }

    }

}
