package com.matsg.battlegrounds.item.factory;

import com.matsg.battlegrounds.FactoryCreationException;
import com.matsg.battlegrounds.api.item.GunPart;
import com.matsg.battlegrounds.api.item.ItemMetadata;
import com.matsg.battlegrounds.api.storage.ItemConfig;
import com.matsg.battlegrounds.api.item.Attachment;
import com.matsg.battlegrounds.api.util.AttributeModifier;
import com.matsg.battlegrounds.api.item.ItemFactory;
import com.matsg.battlegrounds.item.BattleAttachment;
import com.matsg.battlegrounds.item.BattleGunPart;
import com.matsg.battlegrounds.item.ItemStackBuilder;
import com.matsg.battlegrounds.item.modifier.*;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class AttachmentFactory implements ItemFactory<Attachment> {

    private FireModeFactory fireModeFactory;
    private ItemConfig attachmentConfig;
    private ReloadSystemFactory reloadSystemFactory;

    public AttachmentFactory(ItemConfig attachmentConfig, FireModeFactory fireModeFactory, ReloadSystemFactory reloadSystemFactory) {
        this.attachmentConfig = attachmentConfig;
        this.fireModeFactory = fireModeFactory;
        this.reloadSystemFactory = reloadSystemFactory;
    }

    public Attachment make(String id) {
        ConfigurationSection section = attachmentConfig.getItemConfigurationSection(id);
        String[] material = section.getString("Material").split(",");

        ItemMetadata metadata = new ItemMetadata(id, section.getString("DisplayName"), section.getString("Description"));
        ItemStack itemStack = new ItemStackBuilder(Material.valueOf(material[0]))
                .setDurability(Short.valueOf(material[1]))
                .build();

        try {
            GunPart gunPart = BattleGunPart.valueOf(section.getString("GunPart"));

            Attachment attachment = new BattleAttachment(
                    metadata,
                    itemStack,
                    gunPart,
                    getModifiers(section),
                    section.getBoolean("Toggleable")
            );
            attachment.update();
            return attachment;
        } catch (Exception e) {
            throw new FactoryCreationException(e.getMessage(), e);
        }
    }

    private AttributeModifier getModifier(String regex, String type) {
        if (type.equals("boolean")) {
            return new BooleanAttributeModifier(regex);
        }
        if (type.equals("firemode")) {
            return new FireModeAttributeModifier(regex, fireModeFactory);
        }
        if (type.equals("float")) {
            return new FloatAttributeModifier(regex);
        }
        if (type.equals("integer")) {
            return new IntegerAttributeModifier(regex);
        }
        if (type.equals("reloadtype")) {
            return new ReloadSystemAttributeModifier(regex, reloadSystemFactory);
        }
        return null;
    }

    private Map<String, AttributeModifier> getModifiers(ConfigurationSection section) {
        ConfigurationSection modifiersSection = section.getConfigurationSection("Modifiers");
        Map<String, AttributeModifier> map = new HashMap<>();

        for (String attributeId : modifiersSection.getKeys(false)) {
            String regex = modifiersSection.getString(attributeId + ".regex");
            String type = modifiersSection.getString(attributeId + ".type");
            AttributeModifier modifier = getModifier(regex, type);

            map.put(attributeId, modifier);
        }

        return map;
    }
}
