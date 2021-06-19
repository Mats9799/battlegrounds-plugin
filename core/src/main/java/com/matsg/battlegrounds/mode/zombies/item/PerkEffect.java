package com.matsg.battlegrounds.mode.zombies.item;

import com.matsg.battlegrounds.api.entity.GamePlayer;
import com.matsg.battlegrounds.api.entity.PlayerEffect;
import com.matsg.battlegrounds.item.ItemStackBuilder;
import com.matsg.battlegrounds.util.ReflectionUtils;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;

import java.lang.reflect.Method;

public abstract class PerkEffect implements PlayerEffect {

    protected GamePlayer gamePlayer;
    protected ItemStack itemStack;
    protected String name;
    private PerkEffectType type;

    public PerkEffect(PerkEffectType type, String name, Color color) {
        this.type = type;
        this.name = name;
        this.itemStack = new ItemStackBuilder(Material.POTION)
                .addItemFlags(ItemFlag.values())
                .setDisplayName(ChatColor.WHITE + name)
                .build();

        // In version 1.11 and up, set the potion's color.
        if (ReflectionUtils.getEnumVersion().getValue() >= 11) {
            PotionMeta meta = (PotionMeta) itemStack.getItemMeta();
            try {
                Method method = meta.getClass().getMethod("setColor");
                method.invoke(meta, color);
            } catch (Exception e) { }
            itemStack.setItemMeta(meta);
        }
    }

    public GamePlayer getGamePlayer() {
        return gamePlayer;
    }

    public void setGamePlayer(GamePlayer gamePlayer) {
        this.gamePlayer = gamePlayer;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public String getName() {
        return name;
    }

    public PerkEffectType getType() {
        return type;
    }

    public void refresh() { }

    public void remove() {
        gamePlayer.getPlayer().getInventory().remove(itemStack);
        removePerk();
    }

    public abstract void removePerk();
}
