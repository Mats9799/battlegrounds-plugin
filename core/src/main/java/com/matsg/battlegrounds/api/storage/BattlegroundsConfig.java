package com.matsg.battlegrounds.api.storage;

import com.matsg.battlegrounds.api.Battlegrounds;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BattlegroundsConfig extends AbstractYaml {

    public boolean arenaProtection = getBoolean("game-arena-protection");
    public boolean broadcastChat = getBoolean("game-broadcast-chat");
    public ConfigurationSection lobbyScoreboard = getConfigurationSection("game-scoreboard.lobby");
    public int firearmDamageModifer = getInt("game-firearm-damage-modifier");
    public int loadoutCreationLevel = getInt("loadout-creation-level");
    public int mobHealthBarLength = getInt("game-mob-health-bar.length");
    public double firearmAccuracy = getDouble("game-firearm-accuracy");
    public List<String> allowedCommands = getStringList("game-allowed-commands");
    public List<String> joinableGameStates = getStringList("game-joinable-states");
    public List<String> pierceableMaterials = getStringList("game-pierceable-materials");
    public String mobHealthBarEndSymbol = getString("game-mob-health-bar.end-symbol");
    public String mobHealthBarStartSymbol = getString("game-mob-health-bar.start-symbol");
    public String mobHealthBarSymbol = getString("game-mob-health-bar.health-symbol");

    public BattlegroundsConfig(String filePath, InputStream resource) throws IOException {
        super("config.yml", filePath, resource, false);
    }

    public boolean getDisplayBloodEffect(String entityType) {
        return getBoolean("game-display-blood-effect." + entityType);
    }

    public String[] getGameSignLayout() {
        List<String> list = new ArrayList<>();
        for (String string : getConfigurationSection("game-sign-layout").getKeys(false)) {
            list.add(getString("game-sign-layout." + string));
        }
        return list.toArray(new String[list.size()]);
    }

    public String getGameSignState(String gameState) {
        return ChatColor.translateAlternateColorCodes('&', getString("game-sign-state." + gameState));
    }

    public Map<String, String> getLobbyScoreboardLayout() {
        Map<String, String> map = new HashMap<>();
        for (String string : getConfigurationSection("game-scoreboard.lobby.layout").getKeys(false)) {
            map.put(string, getString("game-scoreboard.lobby.layout." + string));
        }
        return map;
    }
}
