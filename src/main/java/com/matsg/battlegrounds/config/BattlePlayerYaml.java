package com.matsg.battlegrounds.config;

import com.matsg.battlegrounds.api.Battlegrounds;
import com.matsg.battlegrounds.api.config.PlayerYaml;
import com.matsg.battlegrounds.api.config.StoredPlayer;
import com.matsg.battlegrounds.api.item.Loadout;
import com.matsg.battlegrounds.item.BattleLoadout;
import org.bukkit.configuration.ConfigurationSection;

import java.io.IOException;
import java.util.*;

public class BattlePlayerYaml extends AbstractYaml implements PlayerYaml {

    private UUID uuid;

    public BattlePlayerYaml(Battlegrounds plugin, UUID uuid) throws IOException {
        super(plugin, plugin.getDataFolder().getPath() + "/players", uuid.toString() + ".yml", false);
        this.uuid = uuid;
    }

    private Loadout getLoadout(int classNumber) {
        ConfigurationSection section = getConfigurationSection("Class." + classNumber);
        return new BattleLoadout(
                section.getString("Name"),
                plugin.getFireArmConfig().get(section.getString("Primary")),
                plugin.getFireArmConfig().get(section.getString("Secondary")),
                plugin.getEquipmentConfig().get(section.getString("Equipment")),
                plugin.getKnifeConfig().get(section.getString("Knife")));
    }

    public List<Loadout> getLoadouts() {
        List<Loadout> list = new ArrayList<>();
        for (int i = 1; i <= 5; i ++) {
            list.add(getLoadout(i));
        }
        return list;
    }

    public StoredPlayer getStoredPlayer() {
        return new LocalStoredPlayer(uuid, this);
    }

    public void saveLoadout(int classNumber, Loadout loadout) {
        set("Class." + classNumber + ".Name", loadout.getName());
        set("Class." + classNumber + ".Primary", loadout.getPrimary().getName());
        set("Class." + classNumber + ".Secondary", loadout.getSecondary().getName());
        set("Class." + classNumber + ".Equipment", loadout.getEquipment().getName());
        set("Class." + classNumber + ".Knife", loadout.getKnife().getName());
        save();
    }

    private class LocalStoredPlayer implements StoredPlayer {

        private int deaths, exp, headshots, kills;
        private PlayerYaml playerYaml;
        private String name;
        private UUID uuid;

        private LocalStoredPlayer(UUID uuid, PlayerYaml playerYaml) {
            this.uuid = uuid;
            this.playerYaml = playerYaml;
            this.deaths = getInt("Global.Deaths");
            this.exp = getInt("Global.Exp");
            this.headshots = getInt("Global.Headshots");
            this.kills = getInt("Global.Kills");
            this.name = getString("Global.Name");
        }

        public int getDeaths() {
            return deaths;
        }

        public int getExp() {
            return exp;
        }

        public int getHeadshots() {
            return headshots;
        }

        public int getKills() {
            return kills;
        }

        public String getName() {
            return name;
        }

        public PlayerYaml getPlayerYaml() {
            return playerYaml;
        }

        public UUID getUUID() {
            return uuid;
        }

        public void setDeaths(int deaths) {
            this.deaths = deaths;
        }

        public void setExp(int exp) {
            this.exp = exp;
        }

        public void setHeadshots(int headshots) {
            this.headshots = headshots;
        }

        public void setKills(int kills) {
            this.kills = kills;
        }

        public int getAttribute(String attribute) {
            for (String string : mapValues().keySet()) {
                if (string.equalsIgnoreCase(attribute)) {
                    return mapValues().get(string);
                }
            }
            return 0;
        }

        public int compareTo(StoredPlayer o) {
            if (exp != o.getExp()) {
                return o.getExp() - exp;
            }
            if (kills != o.getKills()) {
                return o.getKills() - kills;
            }
            return name.compareTo(o.getName());
        }

        public boolean isOnline() {
            return false;
        }

        private Map<String, Integer> mapValues() {
            Map<String, Integer> map = new HashMap<>();
            map.put("deaths", deaths);
            map.put("exp", exp);
            map.put("headshots", headshots);
            map.put("kills", kills);
            return map;
        }
    }
}