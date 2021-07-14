package com.matsg.battlegrounds.mode.zombies.component;

import com.matsg.battlegrounds.TranslationKey;
import com.matsg.battlegrounds.InternalsProvider;
import com.matsg.battlegrounds.api.Translator;
import com.matsg.battlegrounds.api.entity.GamePlayer;
import com.matsg.battlegrounds.api.game.*;
import com.matsg.battlegrounds.gui.ViewFactory;
import com.matsg.battlegrounds.gui.view.ItemTransactionView;
import com.matsg.battlegrounds.gui.view.View;
import com.matsg.battlegrounds.mode.zombies.PerkManager;
import com.matsg.battlegrounds.mode.zombies.item.Perk;
import com.matsg.battlegrounds.api.item.Transaction;
import com.matsg.battlegrounds.api.Placeholder;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class ZombiesPerkMachine implements PerkMachine {

    private static final int MAX_NUMBER_PERKS = 3;

    private boolean locked;
    private Game game;
    private int id;
    private int maxBuys;
    private int price;
    private InternalsProvider internals;
    private Map<GamePlayer, Integer> purchases;
    private Perk perk;
    private PerkManager perkManager;
    private Sign sign;
    private String[] signLayout;
    private Translator translator;
    private ViewFactory viewFactory;

    public ZombiesPerkMachine(
            int id,
            Game game,
            Sign sign,
            Perk perk,
            PerkManager perkManager,
            int maxBuys,
            int price,
            InternalsProvider internals,
            Translator translator,
            ViewFactory viewFactory
    ) {
        this.id = id;
        this.game = game;
        this.sign = sign;
        this.perk = perk;
        this.perkManager = perkManager;
        this.maxBuys = maxBuys;
        this.price = price;
        this.internals = internals;
        this.translator = translator;
        this.viewFactory = viewFactory;
        this.locked = true;
        this.purchases = new HashMap<>();
    }

    public int getId() {
        return id;
    }

    public int getMaxBuys() {
        return maxBuys;
    }

    public void setMaxBuys(int maxBuys) {
        this.maxBuys = maxBuys;
    }

    public Perk getPerk() {
        return perk;
    }

    public void setPerk(Perk perk) {
        this.perk = perk;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public Sign getSign() {
        return sign;
    }

    public void setSign(Sign sign) {
        this.sign = sign;
    }

    public String[] getSignLayout() {
        return signLayout;
    }

    public void setSignLayout(String[] signLayout) {
        this.signLayout = signLayout;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public boolean onInteract(GamePlayer gamePlayer, Block block) {
        // In case the perk machine is locked or the player has too many perks or the player already has bought the perk, reject the interaction
        if (locked || perkManager.getPerkCount(gamePlayer) >= MAX_NUMBER_PERKS || perkManager.hasPerkEffect(gamePlayer, perk.getEffect().getType())) {
            return false;
        }

        Player player = gamePlayer.getPlayer();

        // Reject the interaction when the player does not have enough points
        if (gamePlayer.getPoints() < price) {
            String actionBar = translator.translate(TranslationKey.ACTIONBAR_UNSUFFICIENT_POINTS.getPath());
            internals.sendActionBar(player, actionBar);
            return true;
        }

        int purchaseCount = purchases.getOrDefault(gamePlayer, 0);

        // Reject the interaction when the player has bought the perk too many times
        if (purchaseCount > maxBuys) {
            String actionBar = translator.translate(TranslationKey.ACTIONBAR_PERKMACHINE_SOLD_OUT.getPath());
            internals.sendActionBar(player, actionBar);
            return true;
        }

        int slot = perkManager.getPerkCount(gamePlayer) + 4;

        Consumer<Transaction> onTransactionComplete = transaction -> {
            // Add the perk purchase to the purchase count
            purchases.put(gamePlayer, purchaseCount + 1);
            // Subtract perk price from player points
            gamePlayer.setPoints(gamePlayer.getPoints() - transaction.getPoints());
            // Add the perk item to the registry
            game.getItemRegistry().addItem(perk);
            // Update player score on the scoreboard
            game.updateScoreboard();
            // Assign the player to the perk effect instance
            perk.getEffect().setGamePlayer(gamePlayer);
            // Register perk purchase to the perk manager
            perkManager.addPerk(gamePlayer, perk);
            // Send the player an action bar displaying the deducted amount of points
            String actionBar = translator.translate(TranslationKey.ACTIONBAR_POINTS_DECREASE.getPath(), new Placeholder("bg_points", transaction.getPoints()));
            internals.sendActionBar(gamePlayer.getPlayer(), actionBar);
        };

        // A new perk is given out, so make a clone of the original in the perk machine
        Perk perk = this.perk.clone();

        View view = viewFactory.make(ItemTransactionView.class, instance -> {
            instance.setGame(game);
            instance.setGamePlayer(gamePlayer);
            instance.setItem(perk);
            instance.setItemStack(perk.getItemStack());
            instance.setOnTransactionComplete(onTransactionComplete);
            instance.setPoints(price);
            instance.setSlot(slot);
        });
        view.openInventory(player);

        return true;
    }

    public boolean updateSign() {
        if (signLayout == null || signLayout.length < 4) {
            return false;
        }

        for (int i = 0; i <= 3; i++) {
            sign.setLine(i, translator.createSimpleMessage(signLayout[i],
                    new Placeholder("bg_perk", perk.getEffect().getName()),
                    new Placeholder("bg_price", price)
            ));
        }

        return sign.update();
    }
}
