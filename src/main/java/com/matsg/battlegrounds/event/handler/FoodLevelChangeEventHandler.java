package com.matsg.battlegrounds.event.handler;

import com.matsg.battlegrounds.api.Battlegrounds;
import com.matsg.battlegrounds.api.event.handler.EventHandler;
import com.matsg.battlegrounds.api.game.Game;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.FoodLevelChangeEvent;

public class FoodLevelChangeEventHandler implements EventHandler<FoodLevelChangeEvent> {

    private Battlegrounds plugin;

    public FoodLevelChangeEventHandler(Battlegrounds plugin) {
        this.plugin = plugin;
    }

    public void handle(FoodLevelChangeEvent event) {
        Player player = (Player) event.getEntity();
        Game game = plugin.getGameManager().getGame(player);

        if (game == null) {
            return;
        }

        event.setCancelled(true);
    }
}