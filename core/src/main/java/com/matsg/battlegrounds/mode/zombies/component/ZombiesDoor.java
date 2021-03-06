package com.matsg.battlegrounds.mode.zombies.component;

import com.matsg.battlegrounds.TranslationKey;
import com.matsg.battlegrounds.InternalsProvider;
import com.matsg.battlegrounds.api.Translator;
import com.matsg.battlegrounds.api.entity.GamePlayer;
import com.matsg.battlegrounds.api.game.*;
import com.matsg.battlegrounds.api.Placeholder;
import com.matsg.battlegrounds.util.BattleSound;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

public class ZombiesDoor implements Door {

    private boolean locked;
    private Game game;
    private int id;
    private InternalsProvider internals;
    private Location maximumPoint, minimumPoint;
    private Material material;
    private Section section;
    private Translator translator;
    private World world;

    public ZombiesDoor(
            int id,
            Game game,
            Section section,
            World world,
            Location maximumPoint,
            Location minimumPoint,
            Material material,
            InternalsProvider internals,
            Translator translator
    ) {
        this.id = id;
        this.game = game;
        this.section = section;
        this.world = world;
        this.maximumPoint = maximumPoint;
        this.minimumPoint = minimumPoint;
        this.material = material;
        this.internals = internals;
        this.translator = translator;
        this.locked = true;
    }

    public int getId() {
        return id;
    }

    public Location getMaximumPoint() {
        return maximumPoint;
    }

    public Location getMinimumPoint() {
        return minimumPoint;
    }

    public Section getSection() {
        return section;
    }

    public World getWorld() {
        return world;
    }

    public boolean isLocked() {
        return locked;
    }

    public boolean contains(Location location) {
        return location != null
                && location.getWorld() == world
                && location.getX() >= minimumPoint.getX() && location.getX() <= maximumPoint.getX()
                && location.getY() >= minimumPoint.getY() && location.getY() <= maximumPoint.getY()
                && location.getZ() >= minimumPoint.getZ() && location.getZ() <= maximumPoint.getZ();
    }

    public double getArea() {
        return getWidth() * getHeight() * getLength();
    }

    public Location getCenter() {
        return maximumPoint.toVector().add(minimumPoint.toVector()).multiply(0.5).toLocation(world);
    }

    public double getHeight() {
        return maximumPoint.getY() - minimumPoint.getY();
    }

    public double getLength() {
        return maximumPoint.getZ() - minimumPoint.getZ();
    }

    public double getWidth() {
        return maximumPoint.getX() - minimumPoint.getX();
    }

    public boolean onInteract(GamePlayer gamePlayer, Block block) {
        // If the door was unlocked, it can not be unlocked again.
        if (!locked) {
            return false;
        }

        // If the player does not have enough points they can not unlock the door.
        if (gamePlayer.getPoints() < section.getPrice()) {
            String actionBar = translator.translate(TranslationKey.ACTIONBAR_UNSUFFICIENT_POINTS.getPath());
            internals.sendActionBar(gamePlayer.getPlayer(), actionBar);
            return true;
        }

        int points = section.getPrice();

        String actionBar = translator.translate(TranslationKey.ACTIONBAR_POINTS_DECREASE.getPath(), new Placeholder("bg_points", points));
        internals.sendActionBar(gamePlayer.getPlayer(), actionBar);

        gamePlayer.setPoints(gamePlayer.getPoints() - points);
        game.updateScoreboard();
        section.setLocked(false);

        BattleSound.DOOR_OPEN.play(game, getCenter());

        return true;
    }

    public boolean onLook(GamePlayer gamePlayer, Block block) {
        // If the door was unlocked, it does not accept look interactions.
        if (!locked) {
            return false;
        }

        String actionBar = translator.translate(TranslationKey.ACTIONBAR_DOOR.getPath(), new Placeholder("bg_price", section.getPrice()));
        internals.sendActionBar(gamePlayer.getPlayer(), actionBar);

        return true;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;

        Material material = locked ? this.material : Material.AIR;

        for (int x = minimumPoint.getBlockX(); x <= maximumPoint.getBlockX(); x++) {
            for (int y = minimumPoint.getBlockY(); y <= maximumPoint.getBlockY(); y++) {
                for (int z = minimumPoint.getBlockZ(); z <= maximumPoint.getBlockZ(); z++) {
                    Block block = world.getBlockAt(x, y, z);
                    block.setType(material);
                }
            }
        }
    }
}
