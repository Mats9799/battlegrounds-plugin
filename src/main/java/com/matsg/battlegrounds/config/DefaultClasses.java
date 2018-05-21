package com.matsg.battlegrounds.config;

import com.matsg.battlegrounds.api.Battlegrounds;
import com.matsg.battlegrounds.api.item.Loadout;
import com.matsg.battlegrounds.item.BattleLoadout;
import org.bukkit.configuration.ConfigurationSection;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DefaultClasses extends AbstractYaml {

    public DefaultClasses(Battlegrounds plugin) throws IOException {
        super(plugin, "default-classes.yml", true);
    }

    public List<Loadout> getList() {
        List<Loadout> list = new ArrayList<>();
        for (String loadoutClass : getKeys(false)) {
            list.add(parseLoadout(getConfigurationSection(loadoutClass)));
        }
        return list;
    }

    private Loadout parseLoadout(ConfigurationSection section) {
        return new BattleLoadout(
                section.getString("Name"),
                plugin.getFireArmConfig().get(section.getString("Primary")),
                plugin.getFireArmConfig().get(section.getString("Secondary")),
                plugin.getEquipmentConfig().get(section.getString("Equipment")),
                plugin.getKnifeConfig().get(section.getString("Knife"))
        );
    }
}