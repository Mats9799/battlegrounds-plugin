package com.matsg.battlegrounds.api.event;

import com.matsg.battlegrounds.api.game.Game;
import com.matsg.battlegrounds.api.entity.GamePlayer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public class GamePlayerDeathEvent extends Event {

    private static final HandlerList handlerList = new HandlerList();
    private DeathCause deathCause;
    private Game game;
    private GamePlayer gamePlayer;

    public GamePlayerDeathEvent(Game game, GamePlayer gamePlayer, DeathCause deathCause) {
        this.game = game;
        this.gamePlayer = gamePlayer;
        this.deathCause = deathCause;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    /**
     * Gets the cause of the player's death.
     *
     * @return the death cause
     */
    public DeathCause getDeathCause() {
        return deathCause;
    }

    /**
     * Gets the game in which the death took place.
     *
     * @return the game
     */
    public Game getGame() {
        return game;
    }

    /**
     * Gets the player who died.
     *
     * @return the player
     */
    public GamePlayer getGamePlayer() {
        return gamePlayer;
    }

    public HandlerList getHandlers() {
        return handlerList;
    }

    public enum DeathCause {

        BURNING(1, "game-death-burning", DamageCause.FIRE, DamageCause.FIRE_TICK, DamageCause.LAVA),
        DROWNING(2, "game-death-drowning", DamageCause.DROWNING),
        FALLING(3, "game-death-falling", DamageCause.FALL),
        PLAYER_KILL(4, "game-death-player-kill"),
        SUICIDE(5, "game-death-suicide", DamageCause.SUICIDE),
        MOB_KILL(6, "game-death-mob-kill", DamageCause.ENTITY_ATTACK);

        private DamageCause[] damageCause;
        private int id;
        private String messagePath;

        DeathCause(int id, String messagePath, EntityDamageEvent.DamageCause... damageCause) {
            this.id = id;
            this.damageCause = damageCause;
            this.messagePath = messagePath;
        }

        public static DeathCause fromDamageCause(DamageCause damageCause) {
            for (DeathCause deathCause : values()) {
                for (DamageCause other : deathCause.damageCause) {
                    if (other == damageCause) {
                        return deathCause;
                    }
                }
            }
            return null;
        }

        public String getMessagePath() {
            return messagePath;
        }

        public int getId() {
            return id;
        }
    }
}
