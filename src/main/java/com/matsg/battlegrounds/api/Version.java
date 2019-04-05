package com.matsg.battlegrounds.api;

import com.matsg.battlegrounds.api.entity.Hellhound;
import com.matsg.battlegrounds.api.entity.Zombie;
import com.matsg.battlegrounds.api.game.Game;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface Version {

    Hellhound createHellhound(Game game);

    Zombie createZombie(Game game);

    void sendActionBar(Player player, String message);

    void sendJSONMessage(Player player, String message, String command, String hoverMessage);

    void sendTitle(Player player, String title, String subTitle, int fadeIn, int time, int fadeOut);

    void spawnColoredParticle(Location location, String effect, float red, float green, float blue);

    void spawnParticle(Location location, String effect, int amount, float offsetX, float offsetY, float offsetZ, int speed);
}
