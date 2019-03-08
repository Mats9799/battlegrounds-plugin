package com.matsg.battlegrounds.item.factory;

import com.matsg.battlegrounds.Translator;
import com.matsg.battlegrounds.api.config.ItemConfig;
import com.matsg.battlegrounds.api.item.Attachment;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemFactory;
import org.bukkit.inventory.meta.ItemMeta;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({
        Bukkit.class,
        Translator.class
})
public class AttachmentFactoryTest {

    private ConfigurationSection modifiers, section;
    private ItemConfig attachmentConfig;
    private Set<String> modifierSet;
    private String id;

    @Before
    public void setUp() {
        this.modifiers = mock(ConfigurationSection.class);
        this.section = mock(ConfigurationSection.class);
        this.attachmentConfig = mock(ItemConfig.class);

        this.id = "Id";
        this.modifierSet = new HashSet<>();

        PowerMockito.mockStatic(Bukkit.class);
        PowerMockito.mockStatic(Translator.class);

        ItemFactory itemFactory = mock(ItemFactory.class);

        when(Bukkit.getItemFactory()).thenReturn(itemFactory);
        when(itemFactory.getItemMeta(any())).thenReturn(mock(ItemMeta.class));
        when(attachmentConfig.getItemConfigurationSection(id)).thenReturn(section);
        when(modifiers.getKeys(false)).thenReturn(modifierSet);
        when(section.getConfigurationSection("Modifiers")).thenReturn(modifiers);
        when(section.getString("Material")).thenReturn("AIR,1");
    }

    @Test
    public void testMakeAttachmentWithoutModifiers() {
        when(section.getName()).thenReturn(id);
        when(section.getString("GunPart")).thenReturn("MAGAZINE");

        AttachmentFactory factory = new AttachmentFactory(attachmentConfig);
        Attachment attachment = factory.make(id);

        assertNotNull(attachment);
        assertEquals(id, attachment.getId());
    }

    @Test
    public void testMakeAttachmentWithModifiers() {
        String attributeId = "ammo-magazine-size";

        modifierSet.add(attributeId);

        when(modifiers.getString(attributeId)).thenReturn("=1");
        when(section.getName()).thenReturn(id);
        when(section.getString("GunPart")).thenReturn("MAGAZINE");

        AttachmentFactory factory = new AttachmentFactory(attachmentConfig);
        Attachment attachment = factory.make(id);

        assertNotNull(attachment);
        assertEquals(id, attachment.getId());
    }

    @Test(expected = FactoryCreationException.class)
    public void testMakeAttachmantInvalidGunPart() {
        when(section.getString("GunPart")).thenReturn("INVALID");

        AttachmentFactory factory = new AttachmentFactory(attachmentConfig);
        factory.make(id);
    }
}
