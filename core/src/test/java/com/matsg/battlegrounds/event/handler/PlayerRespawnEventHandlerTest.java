package com.matsg.battlegrounds.event.handler;

import com.matsg.battlegrounds.api.Battlegrounds;
import com.matsg.battlegrounds.api.GameManager;
import com.matsg.battlegrounds.api.game.Game;
import com.matsg.battlegrounds.api.game.PlayerManager;
import com.matsg.battlegrounds.api.game.GameMode;
import com.matsg.battlegrounds.api.entity.GamePlayer;
import com.matsg.battlegrounds.entity.BattleGamePlayer;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class PlayerRespawnEventHandlerTest {

    private Battlegrounds plugin;
    private Game game;
    private GameManager gameManager;
    private Player player;
    private PlayerManager playerManager;
    private PlayerRespawnEvent event;

    @Before
    public void setUp() {
        this.plugin = mock(Battlegrounds.class);
        this.game = mock(Game.class);
        this.gameManager = mock(GameManager.class);
        this.player = mock(Player.class);
        this.playerManager = mock(PlayerManager.class);

        this.event = new PlayerRespawnEvent(player, new Location(mock(World.class), 0, 0, 0), false);

        when(game.getPlayerManager()).thenReturn(playerManager);
        when(gameManager.getGame(player)).thenReturn(game);
        when(plugin.getGameManager()).thenReturn(gameManager);
    }

    @Test
    public void respawnNotPlaying() {
        when(gameManager.getGame(player)).thenReturn(null);

        PlayerRespawnEventHandler eventHandler = new PlayerRespawnEventHandler(plugin);
        eventHandler.handle(event);

        verify(playerManager, times(0)).getGamePlayer(player);
    }

    @Test
    public void respawnLocationFromGameMode() {
        GameMode gameMode = mock(GameMode.class);

        GamePlayer gamePlayer = new BattleGamePlayer(player, null);
        Location location = new Location(mock(World.class), 0, 0, 0);

        when(game.getGameMode()).thenReturn(gameMode);
        when(gameMode.getRespawnLocation(gamePlayer)).thenReturn(location);
        when(playerManager.getGamePlayer(player)).thenReturn(gamePlayer);

        PlayerRespawnEventHandler eventHandler = new PlayerRespawnEventHandler(plugin);
        eventHandler.handle(event);

        verify(gameMode, times(1)).respawnPlayer(gamePlayer);

        assertEquals(location, event.getRespawnLocation());
    }
}
