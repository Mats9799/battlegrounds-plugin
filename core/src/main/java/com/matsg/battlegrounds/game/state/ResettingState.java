package com.matsg.battlegrounds.game.state;

import com.matsg.battlegrounds.api.game.GameAction;
import com.matsg.battlegrounds.api.game.GameState;
import com.matsg.battlegrounds.util.XMaterial;
import org.bukkit.inventory.ItemStack;

public class ResettingState implements GameState {

    public boolean isAllowed(GameAction action) {
        return action == GameAction.MOVEMENT;
    }

    public boolean isInProgress() {
        return true;
    }

    public GameState next() {
        return new WaitingState();
    }

    public GameState previous() {
        return new InGameState();
    }

    public ItemStack toItemStack() {
        return new ItemStack(XMaterial.TERRACOTTA.parseMaterial(), 1, (short) 3);
    }

    public String toString() {
        return "Resetting";
    }
}
