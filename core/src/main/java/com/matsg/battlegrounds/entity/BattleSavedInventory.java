package com.matsg.battlegrounds.entity;

import com.matsg.battlegrounds.api.entity.SavedInventory;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class BattleSavedInventory implements SavedInventory {

    private final float exp;
    private final int level;
    private final Inventory inventory;
    private final ItemStack[] armor, items;

    public BattleSavedInventory(Player player, Inventory inventory) {
        this.inventory = inventory;
        this.armor = player.getInventory().getArmorContents();
        this.exp = player.getExp();
        this.items = player.getInventory().getContents();
        this.level = player.getLevel();

        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
    }

    public Inventory getInventory() {
        return inventory;
    }

    public void restore(Player player) {
        player.getInventory().setArmorContents(armor);
        player.getInventory().setContents(items);
        player.setExp(exp);
        player.setLevel(level);
    }
}
