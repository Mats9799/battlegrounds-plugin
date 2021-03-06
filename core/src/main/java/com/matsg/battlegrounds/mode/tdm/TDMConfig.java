package com.matsg.battlegrounds.mode.tdm;

import com.matsg.battlegrounds.api.game.Team;
import com.matsg.battlegrounds.game.BattleTeam;
import com.matsg.battlegrounds.storage.BattleCacheYaml;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class TDMConfig extends BattleCacheYaml {

    private boolean scoreboardEnabled;
    private double minimumSpawnDistance;
    private int countdownLength, killsToWin, lives, timeLimit;

    public TDMConfig(String filePath, InputStream resource, Server server) throws IOException {
        super("tdm.yml", filePath, resource, server);
        this.countdownLength = getInt("tdm-countdown-length");
        this.killsToWin = getInt("tdm-kills-to-win");
        this.lives = getInt("tdm-lives");
        this.minimumSpawnDistance = getDouble("tdm-minimum-distance-spawn");
        this.scoreboardEnabled = getBoolean("tdm-scoreboard.enabled");
        this.timeLimit = getInt("tdm-time-limit");
    }

    public int getCountdownLength() {
        return countdownLength;
    }

    public int getKillsToWin() {
        return killsToWin;
    }

    public int getLives() {
        return lives;
    }

    public double getMinimumSpawnDistance() {
        return minimumSpawnDistance;
    }

    public int getTimeLimit() {
        return timeLimit;
    }

    public boolean isScoreboardEnabled() {
        return scoreboardEnabled;
    }

    public Map<String, String> getScoreboardLayout() {
        Map<String, String> map = new HashMap<>();
        for (String string : getConfigurationSection("tdm-scoreboard.layout").getKeys(false)) {
            map.put(string, getString("tdm-scoreboard.layout." + string));
        }
        return map;
    }

    public List<World> getScoreboardWorlds() {
        List<String> list = Arrays.asList(getString("tdm-scoreboard.worlds").split(","));
        List<World> worlds = new ArrayList<>();

        if (list.contains("*")) {
            worlds.addAll(server.getWorlds());
        } else {
            for (String world : list) {
                worlds.add(server.getWorld(world));
            }
        }

        return worlds;
    }

    public List<Team> getTeams() {
        List<Team> list = new ArrayList<>();

        for (String teamId : config.getConfigurationSection("tdm-teams").getKeys(false)) {
            ConfigurationSection section = config.getConfigurationSection("tdm-teams." + teamId);
            String[] array = section.getString("armor-color").split(",");
            Color color = Color.fromRGB(Integer.parseInt(array[0]), Integer.parseInt(array[1]), Integer.parseInt(array[2]));

            list.add(new BattleTeam(Integer.parseInt(teamId), section.getString("name"), color, ChatColor.getByChar(section.getString("chatcolor").charAt(0))));
        }

        return list;
    }
}
