package io.github.tuberoh.locuCore.Menus;

import io.github.tuberoh.locuCore.LocuCore;
import io.github.tuberoh.locuCore.Menu.LocuMenu;
import io.github.tuberoh.locuCore.Objects.Waypoints;
import io.github.tuberoh.locuCore.Utilities.DataController;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class WpMenu extends LocuMenu{

    private final LocuCore plugin;
    private final int page;
    private final String type;
    private final DataController dc;
    private final List<Waypoints> wplist;
    private final boolean isSearch;
    private static final int ITEMS_PER_PAGE = 36;
    private static final int SLOT_EMPTY = 22;
    private static final int SLOT_CLOSE = 36;
    private static final int SLOT_PREV = 39;
    private static final int SLOT_HOME = 40;
    private static final int SLOT_NEXT = 41;
    private static final int SLOT_SEARCH = 44;

    public WpMenu(LocuCore plugin, int page, DataController dc, String type) {

        super(LocuMenu.Rows.FIVE, "Waypoints - Pag. " + (page+1));
        this.plugin = plugin;
        this.page = page;
        this.dc = dc;
        this.type = type;
        wplist = new ArrayList<>();
        isSearch = false;

    }
    public WpMenu(LocuCore plugin, int page, DataController dc, String type, List <Waypoints> wplist){


        super(LocuMenu.Rows.FIVE, "Waypoints - Pag. " + (page+1));
        this.plugin = plugin;
        this.page = page;
        this.dc = dc;
        this.wplist = new ArrayList<>(wplist);
        this.type = type;
        isSearch = true;

    }
    @Override
    public void onSetItems() {

        List<Waypoints> waypoints = new ArrayList<>();
        String owner_uuid = viewer.getUniqueId().toString();
        ItemStack closeButton = createItem(Material.REDSTONE, "§cClose");
        ItemStack backButton = createItem(Material.OAK_DOOR, "§eHome");
        ItemStack orange_pane = createItem(Material.ORANGE_STAINED_GLASS_PANE, " ");

        for(int i=36; i<45; i++){

            setItem(i, orange_pane);

        }
        if(isSearch){

            if(wplist.isEmpty()){

                ItemStack noLocations = createItem(Material.RED_STAINED_GLASS_PANE, "§c§lNo waypoints found", "§7Please search again!");
                setItem(SLOT_EMPTY, noLocations, player -> {});

                setItem(SLOT_HOME, backButton, player -> {new MMenu(plugin, dc).open(player);});
                setItem(SLOT_CLOSE, closeButton, player -> {

                    player.closeInventory();

                });
                return;

            }
            else{

                waypoints = new ArrayList<>(wplist);

            }


        }
         else if(type.equals("private_owned") && dc.getPrivateWaypointsNames(owner_uuid).isEmpty()) {

             ItemStack noLocations = createItem(Material.RED_STAINED_GLASS_PANE, "§c§lNo private Waypoints saved", "§7Use /luc set");
             setItem(SLOT_EMPTY, noLocations, player -> {});
             setItem(SLOT_HOME, backButton, player -> {new MMenu(plugin, dc).open(player);});
             setItem(SLOT_CLOSE, closeButton, player -> {

                player.closeInventory();

             });
             return;

         }
         else if(type.equals("public_owned") && dc.getPublicWaypoints(owner_uuid).isEmpty()){

             ItemStack noLocations = createItem(Material.RED_STAINED_GLASS_PANE, "§c§lNo public personal waypoints saved", "§7Use /luc set");
             setItem(SLOT_EMPTY, noLocations, player -> {});
             setItem(SLOT_HOME, backButton, player -> {new MMenu(plugin, dc).open(player);});
             setItem(SLOT_CLOSE, closeButton, player -> {

                player.closeInventory();

             });
             return;

         }
         else if(type.equals("public") && dc.getAllPublicWaypointsNames().isEmpty()) {

             ItemStack noLocations = createItem(Material.RED_STAINED_GLASS_PANE, "§c§lNo public Waypoints saved", "§7Use /luc set");
             setItem(SLOT_EMPTY, noLocations, player -> {});
             setItem(SLOT_HOME, backButton, player -> {new MMenu(plugin, dc).open(player);});
             setItem(SLOT_CLOSE, closeButton, player -> {

                 player.closeInventory();

             });
             return;

         }
         else{

            if(type.equals("private_owned")){

                waypoints = new ArrayList<>(dc.getPrivateWaypoints(owner_uuid));

            }
            else if(type.equals("public_owned")){

                waypoints = new ArrayList<>(dc.getPublicWaypoints(owner_uuid));

            }
            else if(type.equals("public")){

                waypoints = new ArrayList<>(dc.getAllPublicWaypoints());

            }

        }

        int startIndex = page * ITEMS_PER_PAGE;
        int endIndex = Math.min(startIndex + ITEMS_PER_PAGE, waypoints.size());
        int waypoint_slot = 0;

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

            setItem(waypoint_slot, item, player -> {
                new WaypointDetailed(plugin, locName, dc, wp).open(player);
            });

            waypoint_slot++;
        }

        setItem(SLOT_CLOSE, closeButton, player -> {

            player.closeInventory();

        });

        if (page > 0) {
            ItemStack arrowLeft = createItem(Material.ARROW, "§e§l Back");
            setItem(SLOT_PREV, arrowLeft, player -> {new WpMenu(plugin, page-1, dc, type).open(player);});
        }


        setItem(SLOT_HOME, backButton, player -> {
            new MMenu(plugin, dc).open(player);
        });


        if (endIndex < waypoints.size()) {
            ItemStack arrowRight = createItem(Material.ARROW, "§e§lNext Page");
            setItem(SLOT_NEXT, arrowRight, player -> {
                new WpMenu(plugin, page + 1, dc, type).open(player);
            });
        }

        ItemStack searchButton = createItem(Material.ANVIL, "§eSearch");
        ItemStack confirmBtn = createItem(Material.GREEN_STAINED_GLASS_PANE, "§aConfirm");
        ItemMeta meta = confirmBtn.getItemMeta();
        meta.setLore(Arrays.asList("§aConfirm"));
        confirmBtn.setItemMeta(meta);
        setItem(SLOT_SEARCH, searchButton, player -> {

            Bukkit.getScheduler().runTask(plugin, () -> {

                new AnvilGUI.Builder()
                        .onClickAsync((slot, stateSnapshot) -> CompletableFuture.supplyAsync(() -> {
                            if(slot != AnvilGUI.Slot.OUTPUT) {
                                return Collections.emptyList();
                            }
                            String response = stateSnapshot.getText();

                            if(response.isEmpty()){

                                return Arrays.asList(AnvilGUI.ResponseAction.replaceInputText("Insert a valid name!"));


                            }
                            if(response.length() > 16){

                                return Arrays.asList(AnvilGUI.ResponseAction.replaceInputText("Name is too long!"));

                            }
                            if(!response.matches("[a-zA-Z0-9_]+")){

                                return Arrays.asList(AnvilGUI.ResponseAction.replaceInputText("Name can only contains letters, numbers, underscores"));

                            }
                            List<Waypoints> results;
                            if(type.equals("private_owned")){

                                results = dc.searchWaypoints(response, owner_uuid, false);

                            }
                            else if(type.equals("public_owned")){

                                results = dc.searchWaypoints(response, owner_uuid, true);

                            }
                            else if(type.equals("public")){

                                results = dc.searchWaypoints(response);

                            }
                            else{

                                results = Collections.emptyList();
                            }
                            player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);

                            return Arrays.asList(AnvilGUI.ResponseAction.close(),
                                    AnvilGUI.ResponseAction.run(() ->
                                    new WpMenu(plugin, 0, dc, type, results).open(player)
                            ));

                        }))
                        .itemOutput(confirmBtn)
                        .text("Insert name")
                        .title("Search waypoint")
                        .plugin(plugin)
                        .open(player);

            });

        });

    }

}
