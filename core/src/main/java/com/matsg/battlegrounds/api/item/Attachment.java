package com.matsg.battlegrounds.api.item;

import com.matsg.battlegrounds.api.util.AttributeModifier;

public interface Attachment extends Item {

    GunPart getGunPart();

    boolean isToggleable();

    Attachment clone();

    AttributeModifier getModifier(String attribute);
}
