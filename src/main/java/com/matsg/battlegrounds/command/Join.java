package com.matsg.battlegrounds.command;

import com.matsg.battlegrounds.TranslationKey;
import com.matsg.battlegrounds.api.GameManager;
import com.matsg.battlegrounds.api.Translator;
import com.matsg.battlegrounds.api.game.Game;
import com.matsg.battlegrounds.api.Placeholder;
import com.matsg.battlegrounds.api.storage.BattlegroundsConfig;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Join extends Command {

    private BattlegroundsConfig config;
    private GameManager gameManager;

    public Join(Translator translator, GameManager gameManager, BattlegroundsConfig config) {
        super(translator);
        this.gameManager = gameManager;
        this.config = config;

        setAliases("j");
        setDescription(createMessage(TranslationKey.DESCRIPTION_JOIN));
        setName("join");
        setPlayerOnly(true);
        setUsage("bg join [id]");
    }

    public void execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;

        if (gameManager.getGame(player) != null) {
            player.sendMessage(createMessage(TranslationKey.ALREADY_PLAYING));
            return;
        }

        if (args.length == 1) {
            player.sendMessage(createMessage(TranslationKey.SPECIFY_GAME_ID));
            return;
        }

        int id;

        try {
            id = Integer.parseInt(args[1]);
        } catch (Exception e) {
            player.sendMessage(createMessage(TranslationKey.INVALID_ARGUMENT_TYPE, new Placeholder("bg_arg", args[1])));
            return;
        }

        if (!gameManager.exists(id)) {
            player.sendMessage(createMessage(TranslationKey.GAME_NOT_EXISTS, new Placeholder("bg_game", id)));
            return;
        }

        Game game = gameManager.getGame(id);

        if (!config.joinableGameStates.contains(game.getState().toString())) {
            player.sendMessage(createMessage(TranslationKey.IN_PROGRESS));
            return;
        }

        if (game.getPlayerManager().getPlayers().size() >= game.getConfiguration().getMaxPlayers()) {
            player.sendMessage(createMessage(TranslationKey.SPOTS_FULL));
            return;
        }

        game.getPlayerManager().addPlayer(player);
    }
}
