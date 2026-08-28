package io.github.tuberoh.locuCore.listeners;
import io.github.tuberoh.locuCore.Menu.GuiMenu;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

public class MenuListener implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent event){

        if(!(event.getView().getTopInventory().getHolder() instanceof final GuiMenu menu)){

            return;

        }
        event.setCancelled(true);

        int rawSlot = event.getRawSlot();
        int topSize = event.getView().getTopInventory().getSize();

        if(rawSlot >= 0 && rawSlot < topSize){
            menu.click((Player) event.getWhoClicked(), event.getSlot());
        }

    }
    @EventHandler
    public void onDrag(InventoryDragEvent event){

        if(!(event.getView().getTopInventory().getHolder() instanceof GuiMenu)){

            return;

        }

        int topSize = event.getView().getTopInventory().getSize();

        for(int slot : event.getRawSlots()){
            if(slot < topSize){
                event.setCancelled(true);
                break;
            }
        }

    }

}
