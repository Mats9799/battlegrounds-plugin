package com.matsg.battlegrounds.listener;

import com.matsg.battlegrounds.api.Battlegrounds;
import com.matsg.battlegrounds.api.event.GamePlayerDeathEvent;
import com.matsg.battlegrounds.api.event.GamePlayerDeathEvent.DeathCause;
import com.matsg.battlegrounds.api.event.GamePlayerKillPlayerEvent;
import com.matsg.battlegrounds.api.game.Game;
import com.matsg.battlegrounds.api.game.Spawn;
import com.matsg.battlegrounds.api.game.Team;
import com.matsg.battlegrounds.api.item.*;
import com.matsg.battlegrounds.api.player.GamePlayer;
import com.matsg.battlegrounds.util.EnumMessage;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class GameEventHandler implements Listener {

    private Battlegrounds plugin;
    private Game game;

    public GameEventHandler(Battlegrounds plugin, Game game) {
        this.plugin = plugin;
        this.game = game;

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    private Item getDroppedItem(ItemStack itemStack) {
        for (Item item : game.getItemRegistry().getItems()) {
            if (item instanceof Droppable && ((Droppable) item).isRelated(itemStack)) {
                return item;
            }
        }
        return null;
    }

    private Item getInteractItem(GamePlayer gamePlayer, ItemStack itemStack) {
        Item item = game.getItemRegistry().getWeaponIgnoreMetadata(gamePlayer, itemStack);
        if (item == null && (item = game.getItemRegistry().getItemIgnoreMetadata(itemStack)) == null) {
            return null;
        }
        return item;
    }

    private boolean isPlaying(Player player) {
        Game game = plugin.getGameManager().getGame(player);
        return game != null && game == this.game;
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();

        if (!isPlaying(player)) {
            for (GamePlayer gamePlayer : game.getPlayerManager().getPlayers()) {
                event.getRecipients().remove(gamePlayer.getPlayer());
            }
            return;
        }

        event.setCancelled(true);
        game.getPlayerManager().receivePlayerChat(player, event.getMessage());

        if (plugin.getBattlegroundsConfig().broadcastChat) {
            plugin.getLogger().info("[Game " + game.getId() + "] " + player.getName() + ": " + event.getMessage());
        }
    }

    @EventHandler
    public void onCommandSend(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        List<String> list = plugin.getBattlegroundsConfig().allowedCommands;

        if (!isPlaying(player) || list.contains("*") || list.contains(event.getMessage().split(" ")[0].substring(1, event.getMessage().split(" ")[0].length()))) {
            return;
        }

        event.setCancelled(true);

        EnumMessage.COMMAND_NOT_ALLOWED.send(player);
    }

    @EventHandler
    public void onGamePlayerDeath(GamePlayerDeathEvent event) {
        if (event.getGame() != game) {
            return;
        }
        game.getGameMode().onDeath(event.getGamePlayer(), event.getDeathCause());
    }

    @EventHandler
    public void onGamePlayerKill(GamePlayerKillPlayerEvent event) {
        if (event.getGame() != game) {
            return;
        }
        game.getGameMode().onKill(event.getGamePlayer(), event.getKiller(), event.getWeapon(), event.getHitbox());
    }

    @EventHandler
    public void onItemSwitch(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();

        if (!isPlaying(player) || game == null || !game.getState().isInProgress()) {
            return;
        }

        GamePlayer gamePlayer = game.getPlayerManager().getGamePlayer(player);

        if (gamePlayer == null || gamePlayer.getLoadout() == null) {
            return;
        }

        Weapon weapon = gamePlayer.getLoadout().getWeapon(player.getItemInHand());

        if (weapon == null) {
            return;
        }

        weapon.onSwitch(player);
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        event.setCancelled(isPlaying((Player) event.getEntity()) && !game.getState().isInProgress());
    }

    @EventHandler
    public void onPlayerDamageByPlayer(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player) || !(event.getDamager() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntity();
        boolean differentGame = plugin.getGameManager().getGame((Player) event.getDamager()) != game;

        if (!isPlaying(player) || game == null || differentGame) {
            event.setCancelled(differentGame);
            return;
        }

        GamePlayer gamePlayer = game.getPlayerManager().getGamePlayer(player), damager = game.getPlayerManager().getGamePlayer((Player) event.getDamager());
        Team team = game.getGameMode().getTeam(gamePlayer);

        if (gamePlayer == null || damager == null || damager.getLoadout() == null
                || team == game.getGameMode().getTeam(damager)
                || !(damager.getLoadout().getWeapon(damager.getPlayer().getItemInHand()) instanceof Knife)) {
            event.setCancelled(true);
            return;
        }

        event.setDamage(0.0);

        ((Knife) damager.getLoadout().getWeapon(ItemSlot.KNIFE)).damage(gamePlayer);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();

        if (!isPlaying(player) || game == null) {
            return;
        }

        event.setDeathMessage(null);
        event.setDroppedExp(0);
        event.setKeepInventory(true);
        event.setKeepLevel(true);

        DeathCause deathCause = player.getLastDamageCause() != null ? DeathCause.fromDamageCause(player.getLastDamageCause().getCause()) : null;

        if (deathCause == null) {
            return; // Only notify the game of death events the game should handle
        }

        plugin.getServer().getPluginManager().callEvent(new GamePlayerDeathEvent(game, game.getPlayerManager().getGamePlayer(player), deathCause));
    }

    @EventHandler
    public void onPlayerFoodLevelChange(FoodLevelChangeEvent event) {
        event.setCancelled(isPlaying((Player) event.getEntity()));
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (!isPlaying(player) || game == null || game.getArena() == null || !game.getState().isAllowItems()) {
            return;
        }

        event.setCancelled(true);

        Item item = getInteractItem(game.getPlayerManager().getGamePlayer(player), player.getItemInHand());

        if (item == null || !game.getState().isAllowWeapons() && item instanceof Weapon) {
            return;
        }

        game.getItemRegistry().interact(player, item, event.getAction());
    }

    @EventHandler
    public void onPlayerItemDrop(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        Item item = getInteractItem(game.getPlayerManager().getGamePlayer(player), event.getItemDrop().getItemStack());

        if (item == null || !game.getState().isAllowItems() || !(item instanceof DropListener)) {
            event.setCancelled(true);
            return;
        }

        event.setCancelled(((DropListener) item).onDrop());
    }

    @EventHandler
    public void onPlayerItemPickUp(PlayerPickupItemEvent event) {
        Item item = getDroppedItem(event.getItem().getItemStack());

        if (item == null || !(item instanceof Droppable)) {
            return;
        }

        event.setCancelled(true);

        ((Droppable) item).onPickUp(event.getPlayer(), event.getItem());
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if (!isPlaying(player) || game == null) {
            return;
        }

        game.getPlayerManager().onPlayerMove(player, event.getFrom(), event.getTo());
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();

        if (!isPlaying(player) || game == null || !game.getState().isInProgress()) {
            return;
        }

        GamePlayer gamePlayer = game.getPlayerManager().getGamePlayer(player);
        Spawn spawn = game.getGameMode().getRespawnPoint(gamePlayer);

        game.getPlayerManager().respawnPlayer(gamePlayer, spawn);
        event.setRespawnLocation(spawn.getLocation());
    }
}