package com.matsg.battlegrounds.api.item;

import com.matsg.battlegrounds.api.entity.GamePlayer;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;

public interface Droppable {

    Collection<Item> getDroppedItems();

    boolean isDroppable();

    void setDroppable(boolean droppable);

    boolean isRelated(ItemStack itemStack);

    boolean onDrop(GamePlayer gamePlayer, Item item);

    boolean onPickUp(GamePlayer gamePlayer, Item item);
}
