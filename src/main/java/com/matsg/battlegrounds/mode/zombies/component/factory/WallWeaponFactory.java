package com.matsg.battlegrounds.mode.zombies.component.factory;

import com.matsg.battlegrounds.api.Translator;
import com.matsg.battlegrounds.api.game.Game;
import com.matsg.battlegrounds.api.item.Weapon;
import com.matsg.battlegrounds.mode.zombies.component.WallWeapon;
import com.matsg.battlegrounds.mode.zombies.component.ZombiesWallWeapon;
import org.bukkit.Rotation;
import org.bukkit.entity.ItemFrame;

public class WallWeaponFactory {

    private Game game;
    private Translator translator;

    public WallWeaponFactory(Game game, Translator translator) {
        this.game = game;
        this.translator = translator;
    }

    /**
     * Creates a wall weapon component based on the given input.
     *
     * @param id the component id
     * @param itemFrame the item frame of the wall weapon
     * @param weapon the weapon the wall weapon sells
     * @param price the wall weapon price
     * @return a wall weapon implementation
     */
    public WallWeapon make(
            int id,
            ItemFrame itemFrame,
            Weapon weapon,
            int price
    ) {
        itemFrame.setItem(weapon.getItemStack());
        itemFrame.setRotation(Rotation.COUNTER_CLOCKWISE_45);

        return new ZombiesWallWeapon(id, game, itemFrame, weapon, translator, price);
    }
}
