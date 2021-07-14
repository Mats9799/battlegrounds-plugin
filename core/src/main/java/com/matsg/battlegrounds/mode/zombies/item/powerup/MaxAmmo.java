package com.matsg.battlegrounds.mode.zombies.item.powerup;

import com.matsg.battlegrounds.api.entity.GamePlayer;
import com.matsg.battlegrounds.api.game.Game;
import com.matsg.battlegrounds.api.item.Equipment;
import com.matsg.battlegrounds.api.item.Firearm;
import com.matsg.battlegrounds.api.item.Loadout;
import com.matsg.battlegrounds.api.item.MeleeWeapon;
import com.matsg.battlegrounds.mode.zombies.item.PowerUpEffect;
import com.matsg.battlegrounds.util.XMaterial;
import org.bukkit.Material;

import java.util.function.Consumer;

public class MaxAmmo implements PowerUpEffect {

    private Game game;
    private int duration;
    private Material material;
    private String name;

    public MaxAmmo(Game game, String name) {
        this.game = game;
        this.name = name;
        this.duration = 0;
        this.material = XMaterial.GUNPOWDER.parseMaterial();
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public Material getMaterial() {
        return material;
    }

    public String getName() {
        return name;
    }

    public void activate(Consumer<PowerUpEffect> callback) {
        for (GamePlayer gamePlayer : game.getPlayerManager().getActivePlayers()) {
            Loadout loadout = gamePlayer.getLoadout();

            for (Firearm firearm : loadout.getFirearms()) {
               if (firearm != null) {
                   firearm.setAmmo(firearm.getMaxAmmo());
                   firearm.update();
               }
            }

            Equipment equipment = loadout.getEquipment();
            equipment.setAmount(equipment.getMaxAmount());
            equipment.update();

            MeleeWeapon meleeWeapon = loadout.getMeleeWeapon();
            meleeWeapon.setAmount(meleeWeapon.getMaxAmount());
            meleeWeapon.update();
        }
        callback.accept(this);
    }

    public boolean isApplicableForActivation() {
        return true;
    }

    public double modifyDamage(double damage) {
        return damage;
    }

    public int modifyPoints(int points) {
        return points;
    }
}
