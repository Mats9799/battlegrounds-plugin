package com.matsg.battlegrounds.api.game;

import com.matsg.battlegrounds.api.player.GamePlayer;
import org.bukkit.ChatColor;
import org.bukkit.Color;

import java.util.Collection;

public interface Team {

    ChatColor getChatColor();

    Color getColor();

    int getId();

    String getName();

    Collection<GamePlayer> getPlayers();

    int getScore();
}