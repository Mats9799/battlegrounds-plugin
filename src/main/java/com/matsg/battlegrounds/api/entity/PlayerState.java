package com.matsg.battlegrounds.api.entity;

import com.matsg.battlegrounds.api.game.Game;
import org.bukkit.GameMode;

public enum PlayerState {

    ACTIVE(0, true, 'f', GameMode.SURVIVAL, true, true),
    DOWNED(1, true, 'f', GameMode.SURVIVAL, false, false),
    SPECTATING(2, false, '7', GameMode.CREATIVE, false, true);

    private boolean alive, interact, move;
    private char hex;
    private GameMode gameMode;
    private int id;

    PlayerState(int id, boolean alive, char hex, GameMode gameMode, boolean interact, boolean move) {
        this.id = id;
        this.alive = alive;
        this.gameMode = gameMode;
        this.hex = hex;
        this.interact = interact;
        this.move = move;
    }

    public static PlayerState valueOf(int id) {
        for (PlayerState playerState : values()) {
            if (playerState.id == id) {
                return playerState;
            }
        }
        return null;
    }

    public boolean canInteract() {
        return interact;
    }

    public boolean canMove() {
        return move;
    }

    public char getHex() {
        return hex;
    }

    public boolean isAlive() {
        return alive;
    }

    public void apply(Game game, GamePlayer gamePlayer) {
        game.getPlayerManager().setVisible(gamePlayer, alive);
        gamePlayer.getPlayer().setGameMode(gameMode);
        gamePlayer.getPlayer().setWalkSpeed(move ? (float) 0.2 : (float) 0.0);
    }
}