package com.matsg.battlegrounds.api.player;

import com.matsg.battlegrounds.api.item.*;
import com.matsg.battlegrounds.api.util.Message;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface GamePlayer extends OfflineGamePlayer, Comparable<GamePlayer> {

    int addScore(int score);

    Loadout getLoadout();

    Location getLocation();

    Player getPlayer();

    SavedInventory getSavedInventory();

    int getScore();

    PlayerStatus getStatus();

    void sendMessage(Message message);

    void sendMessage(String message);

    void setLoadout(Loadout loadout);

    void setScore(int score);

    PlayerStatus setStatus(PlayerStatus playerStatus);
}