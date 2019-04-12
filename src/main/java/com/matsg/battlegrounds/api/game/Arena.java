package com.matsg.battlegrounds.api.game;

import com.matsg.battlegrounds.api.entity.GamePlayer;

import java.util.Collection;

public interface Arena extends Extent {

    /**
     * Gets the name of the arena.
     *
     * @return The arena name
     */
    String getName();

    /**
     * Gets the collection of sections in the arena.
     *
     * @return The section collection.
     */
    Collection<Section> getSections();

    /**
     * Gets the collection of spawns.
     *
     * @return The spawn collection
     */
    Collection<Spawn> getSpawns();

    /**
     * Gets whether the arena is active or not.
     *
     * @return True if the arena is active, otherwise false
     */
    boolean isActive();

    /**
     * Sets the active state of the arena
     *
     * @param active Whether the arena should be active or not
     */
    void setActive(boolean active);

    /**
     * Searches for a component from the arena with a certain id. Includes section components.
     *
     * @param id The component id.
     * @return The component with the corresponding id or null if there are no matching components.
     */
    ArenaComponent getComponent(int id);

    /**
     * Gets a random entry of the spawn collection.
     *
     * @return A random spawn entry
     */
    Spawn getRandomSpawn();

    /**
     * Gets a random entry of the spawn collection with a minimum distance to the nearest foe.
     *
     * @param distance The minimum distance between the location and the nearest foe
     * @return A random spawn entry
     */
    Spawn getRandomSpawn(double distance);

    /**
     * Gets a random entry of the spawn collection owned by a specific team.
     *
     * @param teamId The id of the team to find a random spawn of
     * @return A random spawn entry
     */
    Spawn getRandomSpawn(int teamId);

    /**
     * Gets a random entry of the spawn collection owned by a specific team with a minimum distance to the nearest foe.
     *
     * @param teamId The id of the team to find a random spawn of
     * @param distance The minimum distance between the location and the nearest foe
     * @return A random spawn entry
     */
    Spawn getRandomSpawn(int teamId, double distance);

    /**
     * Gets a section from the name with a certain name.
     *
     * @param name The section name.
     * @return The section with the corresponding name or null if it does not exist.
     */
    Section getSection(String name);

    /**
     * Gets the spawn which currently being used by a specific player.
     *
     * @param gamePlayer The player to find the spawn of
     * @return The spawn of the player if it is currently occupying one, otherwise null
     */
    Spawn getSpawn(GamePlayer gamePlayer);

    /**
     * Gets a spawn with a certain index number.
     *
     * @param index The spawn index
     * @return The spawn with the index number if one exists, otherwise null
     */
    Spawn getSpawn(int index);

    /**
     * Gets the base spawn of a team.
     *
     * @param teamId The id of the team to get the base spawn of
     * @return The team's base spawn
     */
    Spawn getTeamBase(int teamId);

    /**
     * Gets whether the arena has borders set up.
     *
     * @return True if the arena has borders, otherwise false
     */
    boolean hasBorders();
}
