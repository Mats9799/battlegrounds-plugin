package com.matsg.battlegrounds.storage.sql;

import com.matsg.battlegrounds.api.entity.OfflineGamePlayer;
import com.matsg.battlegrounds.api.game.Game;
import com.matsg.battlegrounds.api.storage.StatisticContext;
import com.matsg.battlegrounds.api.storage.StoredPlayer;
import org.bukkit.OfflinePlayer;

import java.sql.*;
import java.util.*;

public class SQLPlayerRecord implements StoredPlayer {

    private Connection connection;
    private int deaths, exp, headshots, kills;
    private String name;
    private UUID uuid;

    public SQLPlayerRecord(Connection connection, UUID uuid) {
        this.connection = connection;
        this.uuid = uuid;

        try {
            fetchInfo();
            fetchStatistics();
        } catch (SQLException e) {
            throw new SQLPlayerStorageException("Could not fetch data of database record for player uuid " + uuid.toString());
        }
    }

    public int getDeaths() {
        return deaths;
    }

    public void setDeaths(int deaths) {
        this.deaths = deaths;
    }

    public int getExp() {
        return exp;
    }

    public void setExp(int exp) {
        this.exp = exp;
    }

    public int getHeadshots() {
        return headshots;
    }

    public void setHeadshots(int headshots) {
        this.headshots = headshots;
    }

    public int getKills() {
        return kills;
    }

    public void setKills(int kills) {
        this.kills = kills;
    }

    public String getName() {
        return name;
    }

    public UUID getUUID() {
        return uuid;
    }

    public boolean isOnline() {
        return false;
    }

    public StoredPlayer addStatisticAttributes(StatisticContext context) {
        Game game = context.getGame();
        OfflineGamePlayer player = context.getPlayer();

        this.deaths += player.getDeaths();
        this.exp += player.getExp();
        this.headshots += player.getHeadshots();
        this.kills += player.getKills();

        try {
            PreparedStatement ps = connection.prepareStatement("INSERT INTO `battlegrounds_statistic`(`player_uuid`, `game_id`, `gamemode`, `kills`, `deaths`, `headshots`) VALUES(?, ?, ?, ?, ?, ?)");
            ps.setString(1, player.getUUID().toString());
            ps.setInt(2, game.getId());
            ps.setString(3, game.getGameMode().getShortName());
            ps.setInt(4, player.getKills());
            ps.setInt(5, player.getDeaths());
            ps.setInt(6, player.getHeadshots());
            ps.execute();
            ps.close();

            ps = connection.prepareStatement("UPDATE `battlegrounds_player` SET exp = exp + ? WHERE player_uuid = ?");
            ps.setInt(1, player.getExp());
            ps.setString(2, player.getUUID().toString());
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            throw new SQLPlayerStorageException("Could not save statistic attributes of player uuid " + uuid.toString());
        }
        return this;
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

    public void createDefaultAttributes(OfflinePlayer player) {
        this.uuid = player.getUniqueId();
        this.name = player.getName();

        try {
            PreparedStatement ps = connection.prepareStatement("INSERT INTO `battlegrounds_player` VALUES(?, ?, ?);");
            ps.setString(1, uuid.toString());
            ps.setString(2, name);
            ps.setInt(3, 0);
            ps.execute();
            ps.close();
        } catch (SQLException e) {
            throw new SQLPlayerStorageException("Could not insert new database record for player uuid " + uuid.toString());
        }
    }

    public Map<String, String> getLoadoutSetup(int loadoutNr) {
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * " +
                    "FROM `battlegrounds_loadout` " +
                    "WHERE player_uuid = ? AND loadout_nr = ?"
            );
            ps.setString(1, uuid.toString());
            ps.setInt(2, loadoutNr);

            ResultSet rs = ps.executeQuery();

            if (!rs.next()) {
                rs.close();
                ps.close();
                return null;
            }

            Map<String, String> loadoutSetup = new HashMap<>();
            loadoutSetup.put("loadout_nr", rs.getString("loadout_nr"));
            loadoutSetup.put("loadout_name", rs.getString("loadout_name"));
            loadoutSetup.put("name", rs.getString("loadout_name"));
            loadoutSetup.put("primary", rs.getString("primary_firearm"));
            loadoutSetup.put("primary_attachments", rs.getString("primary_attachments"));
            loadoutSetup.put("secondary", rs.getString("secondary_firearm"));
            loadoutSetup.put("secondary_attachments", rs.getString("secondary_attachments"));
            loadoutSetup.put("equipment", rs.getString("equipment"));
            loadoutSetup.put("melee_weapon", rs.getString("melee_weapon"));

            rs.close();
            ps.close();

            return loadoutSetup;
        } catch (SQLException e) {
            throw new SQLPlayerStorageException("Could not retrieve loadout setup of player uuid " + uuid.toString());
        }
    }

    public Collection<Map<String, String>> getLoadoutSetups() {
        List<Map<String, String>> loadoutSetups = new ArrayList<>();
        for (int i = 1; i <= 5; i ++) {
            loadoutSetups.add(getLoadoutSetup(i));
        }
        return loadoutSetups;
    }

    public int getStatisticAttribute(StatisticContext context) {
        if (context.getStatisticName() == null) {
            throw new SQLPlayerStorageException("Could not retrieve null statistic attribute from player uuid " + uuid.toString());
        }

        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * " +
                    "FROM `battlegrounds_statistic` " +
                    "WHERE game_id = ? AND gamemode = ? " +
                    "GROUP BY player_uuid " +
                    "HAVING player_uuid = ?"
            );
            ps.setString(3, uuid.toString());

            if (context != null && context.getGame() != null) {
                ps.setInt(1, context.getGame().getId());
                ps.setString(2, context.getGame().getGameMode().getShortName());
            } else {
                ps.setNull(1, Types.INTEGER);
                ps.setNull(2, Types.VARCHAR);
            }

            int result = 0;
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                result = rs.getInt(context.getStatisticName());
            }

            rs.close();
            ps.close();

            return result;
        } catch (SQLException e) {
            throw new SQLPlayerStorageException("Could not retrieve statistic attribute " + context.getStatisticName() + " from player uuid " + uuid.toString());
        }
    }

    public void saveLoadout(int loadoutNr, Map<String, String> loadoutSetup) {
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * " +
                    "FROM `battlegrounds_loadout` " +
                    "WHERE player_uuid = ? AND loadout_nr = ?;"
            );
            ps.setString(1, uuid.toString());
            ps.setInt(2, loadoutNr);

            ResultSet rs = ps.executeQuery();
            boolean exists = rs.next();

            rs.close();
            ps.close();

            if (!exists) {
                ps = connection.prepareStatement("INSERT INTO `battlegrounds_loadout` (player_uuid, loadout_nr, loadout_name, primary_firearm, primary_attachments, secondary_firearm, secondary_attachments, equipment, melee_weapon)" +
                                "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)"
                );
                ps.setString(1, uuid.toString());
                ps.setInt(2, loadoutNr);
                ps.setString(3, loadoutSetup.get("loadout_name"));
                ps.setString(4, loadoutSetup.get("primary"));
                ps.setString(5, loadoutSetup.get("primary_attachments"));
                ps.setString(6, loadoutSetup.get("secondary"));
                ps.setString(7, loadoutSetup.get("secondary_attachments"));
                ps.setString(8, loadoutSetup.get("equipment"));
                ps.setString(9, loadoutSetup.get("melee_weapon"));
                ps.execute();
            } else {
                ps = connection.prepareStatement("UPDATE `battlegrounds_loadout` " +
                                "SET loadout_name = ?, primary_firearm = ?, primary_attachments = ?, secondary_firearm = ?, secondary_attachments = ?, equipment = ?, melee_weapon = ? " +
                                "WHERE player_uuid = ? AND loadout_nr = ?"
                );
                ps.setString(1, loadoutSetup.get("loadout_name"));
                ps.setString(2, loadoutSetup.get("primary"));
                ps.setString(3, loadoutSetup.get("primary_attachments"));
                ps.setString(4, loadoutSetup.get("secondary"));
                ps.setString(5, loadoutSetup.get("secondary_attachments"));
                ps.setString(6, loadoutSetup.get("equipment"));
                ps.setString(7, loadoutSetup.get("melee_weapon"));
                ps.setString(8, uuid.toString());
                ps.setInt(9, loadoutNr);
                ps.executeUpdate();
            }

            ps.close();
        } catch (SQLException e) {
            throw new SQLPlayerStorageException("Could not save loadout from player uuid " + uuid.toString());
        }
    }

    public void updateName(String name) {
        this.name = name;

        try {
            PreparedStatement ps = connection.prepareStatement("UPDATE `battlegrounds_player`" +
                    "SET `player_name` = ?" +
                    "WHERE `player_uuid` = ?"
            );
            ps.setString(1, name);
            ps.setString(2, uuid.toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new SQLPlayerStorageException("Could not update data record of player uuid " + uuid.toString());
        }
    }

    private void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void fetchInfo() throws SQLException {
        PreparedStatement ps = connection.prepareStatement("SELECT *" +
                "FROM `battlegrounds_player`" +
                "WHERE player_uuid = ?"
        );
        ps.setString(1, uuid.toString());

        ResultSet rs = ps.executeQuery();

        // Check if the player has an exisiting player record.
        if (rs.next()) {
            this.exp = rs.getInt("exp");
            this.name = rs.getString("player_name");
        }

        rs.close();
        ps.close();
    }

    private void fetchStatistics() throws SQLException {
        PreparedStatement ps = connection.prepareStatement("SELECT `battlegrounds_player`.player_uuid, `battlegrounds_player`.exp AS exp, SUM(`battlegrounds_statistic`.kills) AS kills, SUM(`battlegrounds_statistic`.deaths) AS deaths, SUM(`battlegrounds_statistic`.headshots) AS headshots " +
                "FROM `battlegrounds_player` " +
                "JOIN `battlegrounds_statistic` " +
                "ON `battlegrounds_player`.player_uuid = `battlegrounds_statistic`.player_uuid " +
                "WHERE `battlegrounds_player`.player_uuid = ? " +
                "GROUP BY `battlegrounds_player`.player_uuid"
        );
        ps.setString(1, uuid.toString());

        ResultSet rs = ps.executeQuery();

        // Check if the player has existing statistic records.
        if (rs.next()) {
            this.deaths = rs.getInt("deaths");
            this.headshots = rs.getInt("headshots");
            this.kills = rs.getInt("kills");
        }

        rs.close();
        ps.close();
    }
}
