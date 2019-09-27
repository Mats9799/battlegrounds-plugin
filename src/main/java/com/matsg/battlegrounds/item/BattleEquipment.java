package com.matsg.battlegrounds.item;

import com.matsg.battlegrounds.TaskRunner;
import com.matsg.battlegrounds.api.Version;
import com.matsg.battlegrounds.api.item.Equipment;
import com.matsg.battlegrounds.api.entity.GamePlayer;
import com.matsg.battlegrounds.api.item.ItemMetadata;
import com.matsg.battlegrounds.api.item.Transaction;
import com.matsg.battlegrounds.api.util.Sound;
import com.matsg.battlegrounds.item.mechanism.IgnitionSystem;
import com.matsg.battlegrounds.util.BattleSound;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public abstract class BattleEquipment extends BattleWeapon implements Equipment {

    protected boolean beingThrown;
    protected double longRange, midRange, shortRange, velocity;
    protected EquipmentType equipmentType;
    protected IgnitionSystem ignitionSystem;
    protected int amount, cooldown, ignitionTime, maxAmount;
    protected List<Item> droppedItems;
    protected Sound[] ignitionSound;

    public BattleEquipment(
            ItemMetadata metadata,
            ItemStack itemStack,
            TaskRunner taskRunner,
            Version version,
            EquipmentType equipmentType,
            IgnitionSystem ignitionSystem,
            Sound[] ignitionSound,
            int amount,
            int cooldown,
            int ignitionTime,
            double longRange,
            double midRange,
            double shortRange,
            double velocity
    ) {
        super(metadata, itemStack, taskRunner, version);
        this.equipmentType = equipmentType;
        this.ignitionSystem = ignitionSystem;
        this.ignitionSound = ignitionSound;
        this.amount = amount;
        this.cooldown = cooldown;
        this.ignitionTime = ignitionTime;
        this.longRange = longRange;
        this.midRange = midRange;
        this.shortRange = shortRange;
        this.velocity = velocity;
        this.beingThrown = false;
        this.droppedItems = new ArrayList<>();
        this.maxAmount = amount;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public List<Item> getDroppedItems() {
        return droppedItems;
    }

    public Item getFirstDroppedItem() {
        return droppedItems.get(0);
    }

    public Sound[] getIgnitionSound() {
        return ignitionSound;
    }

    public int getIgnitionTime() {
        return ignitionTime;
    }

    public double getLongRange() {
        return longRange;
    }

    public void setLongRange(double longRange) {
        this.longRange = longRange;
    }

    public int getMaxAmount() {
        return maxAmount;
    }

    public double getMidRange() {
        return midRange;
    }

    public void setMidRange(double midRange) {
        this.midRange = midRange;
    }

    public double getShortRange() {
        return shortRange;
    }

    public void setShortRange(double shortRange) {
        this.shortRange = shortRange;
    }

    public EquipmentType getType() {
        return equipmentType;
    }

    public double getVelocity() {
        return velocity;
    }

    public void setVelocity(double velocity) {
        this.velocity = velocity;
    }

    public boolean isBeingThrown() {
        return beingThrown;
    }

    public void cooldown(int cooldownDuration) {
        taskRunner.runTaskLater(() -> beingThrown = false, cooldownDuration);
    }

    public Equipment clone() {
        return (Equipment) super.clone();
    }

    public void deployEquipment() {
        deployEquipment(velocity);
    }

    public void deployEquipment(double velocity) {
        Item item = gamePlayer.getPlayer().getWorld().dropItem(gamePlayer.getPlayer().getEyeLocation(), new ItemStackBuilder(itemStack.clone()).setAmount(1).build());
        item.setPickupDelay(1000);
        item.setVelocity(gamePlayer.getPlayer().getEyeLocation().getDirection().multiply(velocity));

        amount--;
        beingThrown = true;
        droppedItems.add(item);
        ignitionSystem.igniteItem(item);

        cooldown(cooldown);
        update();

        BattleSound.EXPLOSIVE_THROW.play(game, item.getLocation());
    }

    public void handleTransaction(Transaction transaction) {
        this.gamePlayer = transaction.getGamePlayer();
    }

    public boolean isRelated(ItemStack itemStack) {
        for (Item item : droppedItems) {
            if (item.getItemStack() == itemStack) {
                return true;
            }
        }
        return false;
    }

    public boolean onDrop(GamePlayer gamePlayer, Item item) {
        return true;
    }

    public void onLeftClick() {
        if (amount <= 0 || beingThrown || game == null || gamePlayer == null) {
            return;
        }
        deployEquipment(velocity);
    }

    public boolean onPickUp(GamePlayer gamePlayer, Item item) {
        return true;
    }

    public void onRightClick() {
        if (amount <= 0 || beingThrown || game == null || gamePlayer == null) {
            return;
        }
        deployEquipment(0.0);
    }

    public void onSwap() { }

    public void onSwitch() { }

    public void remove() {
        super.remove();
        for (Item item : droppedItems) {
            item.remove();
        }
        droppedItems.clear();
    }

    public void resetState() {
        amount = maxAmount;
    }

    public boolean update() {
        itemStack = new ItemStackBuilder(itemStack)
                .addItemFlags(ItemFlag.values())
                .setAmount(amount)
                .setUnbreakable(true)
                .build();
        if (gamePlayer != null) {
            gamePlayer.getPlayer().getInventory().setItem(itemSlot.getSlot(), itemStack);
        }
        return gamePlayer != null;
    }
}
