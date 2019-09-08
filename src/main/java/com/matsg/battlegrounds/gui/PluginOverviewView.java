package com.matsg.battlegrounds.gui;

import com.matsg.battlegrounds.TranslationKey;
import com.matsg.battlegrounds.api.Battlegrounds;
import com.matsg.battlegrounds.api.Placeholder;
import com.matsg.battlegrounds.api.Translator;
import com.matsg.battlegrounds.api.game.Game;
import com.matsg.battlegrounds.item.ItemStackBuilder;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;

public class PluginOverviewView implements View {

    private static final int INVENTORY_SIZE = 45;
    private static final long REFRESH_TASK_DELAY = 0;
    private static final long REFRESH_TASK_PERIOD = 200;
    private static final String EMPTY_STRING = "";

    private Battlegrounds plugin;
    private BukkitTask task;
    private Inventory inventory;
    private Map<ItemStack, Game> games;
    private Translator translator;

    public PluginOverviewView(Battlegrounds plugin, Translator translator) {
        this.plugin = plugin;
        this.translator = translator;
        this.games = new HashMap<>();
        this.inventory = createInventory();

        task = new BukkitRunnable() {
            public void run() {
                // Remove all icons from previous game states
                inventory.clear();

                for (Game game : plugin.getGameManager().getGames()) {
                    ItemStack itemStack = new ItemStackBuilder(game.getState().toItemStack())
                            .setDisplayName(
                                    translator.translate(TranslationKey.VIEW_PLUGIN_OVERVIEW_GAME,
                                            new Placeholder("bg_game", game.getId())
                                    )
                            )
                            .setLore(
                                    translator.translate(TranslationKey.VIEW_PLUGIN_OVERVIEW_GAME_ARENA,
                                            new Placeholder("bg_arena", game.getArena().getName())
                                    ),
                                    translator.translate(TranslationKey.VIEW_PLUGIN_OVERVIEW_GAME_GAMEMODE,
                                            new Placeholder("bg_gamemode", game.getGameMode().getName())
                                    ),
                                    translator.translate(TranslationKey.VIEW_PLUGIN_OVERVIEW_GAME_PLAYERS,
                                            new Placeholder("bg_maxplayers", game.getConfiguration().getMaxPlayers()),
                                            new Placeholder("bg_players", game.getPlayerManager().getPlayers().size())
                                    ),
                                    translator.translate(TranslationKey.VIEW_PLUGIN_OVERVIEW_GAME_STATE,
                                            new Placeholder("bg_state", game.getState())
                                    ),
                                    EMPTY_STRING,
                                    translator.translate(TranslationKey.VIEW_PLUGIN_OVERVIEW_GAME_OVERVIEW)
                            )
                            .build();

                    inventory.addItem(itemStack);
                    games.put(itemStack, game);
                }
            }
        }.runTaskTimer(plugin, REFRESH_TASK_DELAY, REFRESH_TASK_PERIOD);
    }

    public Inventory getInventory() {
        return inventory;
    }

    public void onClick(Player player, ItemStack itemStack, ClickType clickType) {
        if (itemStack == null) {
            return;
        }

        Game game = games.get(itemStack);

        if (game == null) {
            return;
        }

        View view = new GameOverviewView(plugin, game, translator, this);

        player.openInventory(view.getInventory());
    }

    public boolean onClose() {
        task.cancel();
        return true;
    }

    private Inventory createInventory() {
        String title = translator.translate(TranslationKey.VIEW_PLUGIN_OVERVIEW_TITLE);

        return plugin.getServer().createInventory(this, INVENTORY_SIZE, title);
    }
}
