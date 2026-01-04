package io.github.tuberoh.locuCore.Menu;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public abstract class LocuMenu implements GuiMenu{

    private final Inventory inventory;
    private final Map<Integer, Consumer<Player>> actions = new HashMap<>();

    public LocuMenu(Rows rows, String title){

        this.inventory = Bukkit.createInventory(this, rows.getSize(), Component.text(title));

    }


    @Override
    public void click(Player player, int slot) {

        final Consumer<Player> action = this.actions.get(slot);

        if(action!=null){

            action.accept(player);

        }
    }

    @Override
    public void setItem(int slot, ItemStack item) {

        setItem(slot, item, player -> {});

    }

    @Override
    public void setItem(int slot, ItemStack item, Consumer<Player> action) {

        this.actions.put(slot, action);
        getInventory().setItem(slot, item);


    }

    public abstract void onSetItems();

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }

    public enum Rows{

        ONE(1),
        TWO(2),
        THREE(3),
        FOUR(4),
        FIVE(5);

        private final int size;

        Rows(int rows){

            this.size = rows*9;

        }
        public int getSize() {
            return size;
        }

    }
    public ItemStack createItem(Material material, String name, String... lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            if (lore.length > 0) {
                meta.setLore(java.util.Arrays.asList(lore));
            }
            item.setItemMeta(meta);
        }
        return item;
    }
    public String getNameFromUUID(String creatorUUID) {
        if (creatorUUID == null || creatorUUID.isEmpty()) {
            return "Unkwnown";
        }

        try {
            UUID uuid = UUID.fromString(creatorUUID);
            OfflinePlayer creator = Bukkit.getOfflinePlayer(uuid);
            return creator.getName() != null ? creator.getName() : "Unkwnown";
        } catch (IllegalArgumentException e) {
            return "UUID not valid";
        }
    }


}
