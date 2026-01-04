package io.github.tuberoh.locuCore.listeners;
import io.github.tuberoh.locuCore.Menu.GuiMenu;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public class MenuListener implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent event){

        final Inventory clickedInventory = event.getClickedInventory();

        if(clickedInventory==null){

            return;

        }
        if(!(clickedInventory.getHolder() instanceof final GuiMenu menu)){

            return;

        }

        event.setCancelled(true);
        menu.click((Player) event.getWhoClicked(), event.getSlot());


    }




}
