package com.matsg.battlegrounds.listener;

import com.matsg.battlegrounds.api.Battlegrounds;
import com.matsg.battlegrounds.api.event.GamePlayerDeathEvent;
import com.matsg.battlegrounds.api.event.GamePlayerDeathEvent.DeathCause;
import com.matsg.battlegrounds.api.event.GamePlayerKillPlayerEvent;
import com.matsg.battlegrounds.api.game.EventHandler;
import com.matsg.battlegrounds.api.game.Game;
import com.matsg.battlegrounds.api.item.Item;
import com.matsg.battlegrounds.api.item.Weapon;
import com.matsg.battlegrounds.api.player.GamePlayer;
import com.matsg.battlegrounds.api.util.Placeholder;
import com.matsg.battlegrounds.gui.View;
import com.matsg.battlegrounds.util.EnumMessage;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class BattleEventHandler implements EventHandler {

    private Battlegrounds plugin;

    public BattleEventHandler(Battlegrounds plugin) {
        this.plugin = plugin;
        plugin.getEventManager().registerEventHandler(this);
    }

    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        Game game = plugin.getGameManager().getGame(player);

        if (game == null) {
            for (GamePlayer gamePlayer : plugin.getGameManager().getAllPlayers()) {
                event.getRecipients().remove(gamePlayer.getPlayer());
            }
            return;
        }

        event.setCancelled(true);

        game.broadcastMessage(EnumMessage.PLAYER_MESSAGE.getMessage(
                new Placeholder("bg_message", event.getMessage()),
                new Placeholder("player_name", player.getName())));

        if (plugin.getBattlegroundsConfig().broadcastChat) {
            plugin.getLogger().info("[Game " + game.getId() + "] " + player.getName() + ": " + event.getMessage());
        }
    }

    public void onCommandSend(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        List<String> list = plugin.getBattlegroundsConfig().allowedCommands;

        if (plugin.getGameManager().getGame(player) == null || player.isOp() || list.contains("*")
                || list.contains(event.getMessage().split(" ")[0].substring(1, event.getMessage().split(" ")[0].length()))) {
            return;
        }

        event.setCancelled(true);

        EnumMessage.COMMAND_NOT_ALLOWED.send(player);
    }

    public void onGamePlayerDeath(GamePlayerDeathEvent event) {
        event.getGame().broadcastMessage(event.getDeathCause().getDeathMessage());
    }

    public void onGamePlayerKill(GamePlayerKillPlayerEvent event) {
        event.getGame().getGameMode().onKill(event.getGamePlayer(), event.getKiller(), event.getWeapon());
    }

    public void onPlayerDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntity();
        Game game = plugin.getGameManager().getGame(player);

        event.setCancelled(game != null && !game.getState().isInProgress());
    }

    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Game game = plugin.getGameManager().getGame(player);

        if (game == null || game.getArena() == null || !game.getState().isAllowItems()) {
            return;
        }

        GamePlayer gamePlayer = game.getPlayerManager().getGamePlayer(player);
        Item item = game.getItemRegistry().getItemIgnoreMetadata(player.getInventory().getItemInMainHand());

        if (item == null || item instanceof Weapon && ((Weapon) item).getGamePlayer() != gamePlayer) {
            return;
        }

        event.setCancelled(true);
        game.getItemRegistry().interact(item, event.getAction());
    }

    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (plugin.getPlayerStorage().contains(player.getUniqueId())) {
            return;
        }
        plugin.getPlayerStorage().registerPlayer(player.getUniqueId(), player.getName());
    }

    public void onViewItemClick(InventoryClickEvent event) {
        ItemStack item = event.getCurrentItem();
        Player player = (Player) event.getWhoClicked();

        if (event.getInventory().getHolder() instanceof View) {
            event.setCancelled(true);

            ((View) event.getInventory().getHolder()).onClick(player, item, event.getClick());
        }
    }

    public void onViewItemClose(InventoryCloseEvent event) {
        if (event.getInventory().getHolder() instanceof View && !((View) event.getInventory().getHolder()).onClose()) {
            plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                public void run() {
                    event.getPlayer().openInventory(event.getInventory());
                }
            }, 1);
        }
    }
}