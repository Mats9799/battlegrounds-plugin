package com.matsg.battlegrounds.api.game;

import com.matsg.battlegrounds.api.item.Loadout;
import com.matsg.battlegrounds.api.entity.GamePlayer;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.function.Predicate;

public interface PlayerManager {

    /**
     * Gets all players.
     *
     * @return all players in the game
     */
    Collection<GamePlayer> getPlayers();

    /**
     * Adds a player to a game.
     *
     * @param player the player to add to the game
     * @return the created GamePlayer instance of the player
     */
    GamePlayer addPlayer(Player player);

    /**
     * Sends a message to all players in the game.
     *
     * @param message the message to send
     */
    void broadcastMessage(String message);

    /**
     * Changes the loadout of a player.
     *
     * @param gamePlayer the player to change the loadout of
     * @param loadout the new loadout for the player
     */
    void changeLoadout(GamePlayer gamePlayer, Loadout loadout);

    /**
     * Resets a player to their default state without removing them from the game.
     *
     * @param gamePlayer the player to clear
     */
    void clearPlayer(GamePlayer gamePlayer);

    /**
     * Gets the GamePlayer instance of a player
     *
     * @param player the player to get the GamePlayer instance of
     * @return the GamePlayer instance or null if the player is not in the game
     */
    GamePlayer getGamePlayer(Player player);

    /**
     * Gets the currently living players.
     *
     * @return an array containing all active players
     */
    GamePlayer[] getLivingPlayers();

    /**
     * Gets the currently living players of a team.
     *
     * @param team the team to get the players from
     * @return an array containing all active players in this team
     */
    GamePlayer[] getLivingPlayers(Team team);

    /**
     * Gets nearby enemy players of a player.
     *
     * @param team the team to find enemy players of
     * @param location the location to look for enemy players
     * @param range the maximum distance between enemy and the player
     * @return an array of nearby enemy players
     */
    GamePlayer[] getNearbyEnemyPlayers(Team team, Location location, double range);

    /**
     * Gets nearby players from a location.
     *
     * @param location the location to get nearby players of
     * @param range the maximum distance between player and the location
     * @return an array of nearby players
     */
    GamePlayer[] getNearbyPlayers(Location location, double range);

    /**
     * Gets the nearest player from a location.
     *
     * @param location the location to get the nearest player of
     * @return the nearest player
     */
    GamePlayer getNearestPlayer(Location location);

    /**
     * Gets the nearest player from a location with a certain constraint.
     *
     * @param location the location to get the nearest player of
     * @param constraint the player constraint
     * @return the nearest player
     */
    GamePlayer getNearestPlayer(Location location, Predicate<GamePlayer> constraint);

    /**
     * Gets the nearest player from a location within a certain range.
     *
     * @param location the location to get the nearest player of
     * @param range the maximum distance between player and the location
     * @return the nearest player or null if there are no players in the range
     */
    GamePlayer getNearestPlayer(Location location, double range);

    /**
     * Gets the nearest player from a location, within a certain range and with a certain constraint.
     *
     * @param location the location to get the nearest player of
     * @param range the maximum distance between player and the location
     * @param constraint the player constraint
     * @return the nearest player
     */
    GamePlayer getNearestPlayer(Location location, double range, Predicate<GamePlayer> constraint);

    /**
     * Gets the nearest player from a certain from a location.
     *
     * @param location the location to get the nearest player of
     * @param team the team the player has to be in
     * @return the nearest player of this team or null if there are no players in this team
     */
    GamePlayer getNearestPlayer(Location location, Team team);

    /**
     * Gets the nearest player from a certain from a location within a certain range.
     *
     * @param location the location to get the nearest player of
     * @param team the team the player has to be in
     * @param range the maximum distance between player and the location
     * @return the nearest player of this team or null if there are no players in this team or there are no players from this team in the range
     */
    GamePlayer getNearestPlayer(Location location, Team team, double range);

    /**
     * Removes a player from the game.
     *
     * @param gamePlayer the player to remove
     * @return whether the player was removed from the game
     */
    boolean removePlayer(GamePlayer gamePlayer);

    /**
     * Sets a player visible to other players.
     *
     * @param gamePlayer the player
     * @param visible whether the player should be visible or not
     */
    void setVisible(GamePlayer gamePlayer, boolean visible);

    /**
     * Sets a player visible to other players in a certain team.
     *
     * @param gamePlayer the player
     * @param team the team to perform the action on
     * @param visible whether the player should be visible or not
     */
    void setVisible(GamePlayer gamePlayer, Team team, boolean visible);

    /**
     * Updates a player's exp bar according to their game exp.
     *
     * @param gamePlayer the player to update the exp bar of
     */
    void updateExpBar(GamePlayer gamePlayer);
}
