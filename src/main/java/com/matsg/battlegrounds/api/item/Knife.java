package com.matsg.battlegrounds.api.item;

import com.matsg.battlegrounds.api.player.GamePlayer;

public interface Knife extends Weapon {

    Knife clone();

    double damage(GamePlayer gamePlayer);

    int getAmount();

    double getDamage();

    int getMaxAmount();

    boolean isThrowable();

    void setAmount(int amount);

    void setDamage(double damage);

    void shoot();
}