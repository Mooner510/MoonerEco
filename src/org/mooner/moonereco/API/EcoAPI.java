package org.mooner.moonereco.API;

import com.comphenix.net.bytebuddy.jar.asm.Type;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.mooner.moonereco.MoonerEco;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.*;

public class EcoAPI {
    public static EcoAPI init;
//    private final HashMap<UUID, Double> lastPay;
    private static final String CONNECTION = "jdbc:sqlite:" + MoonerEco.dataPath + "eco.db";

    public EcoAPI() {
        new File(MoonerEco.dataPath).mkdirs();
        File db = new File(MoonerEco.dataPath, "eco.db");
        if(!db.exists()) {
            try {
                db.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try (
                Connection c = DriverManager.getConnection(CONNECTION);
                PreparedStatement s = c.prepareStatement(
                        "CREATE TABLE IF NOT EXISTS Money (" +
                                "uuid TEXT NOT NULL UNIQUE," +
                                "value REAL NOT NULL," +
                                "PRIMARY KEY(uuid)" +
                                ")")
        ) {
            s.execute();
            MoonerEco.plugin.getLogger().info("성공적으로 Money DB를 생성했습니다.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try (
                Connection c = DriverManager.getConnection(CONNECTION);
                PreparedStatement s = c.prepareStatement(
                        "CREATE TABLE IF NOT EXISTS Log (" +
                                "id INTEGER NOT NULL," +
                                "source TEXT NOT NULL," +
                                "uuid TEXT NOT NULL," +
                                "uuid2 TEXT," +
                                "value REAL NOT NULL," +
                                "data TEXT NOT NULL," +
                                "timestamp INTEGER NOT NULL," +
                                "PRIMARY KEY(id AUTOINCREMENT)" +
                                ")")
        ) {
            s.execute();
            MoonerEco.plugin.getLogger().info("성공적으로 Log DB를 생성했습니다.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean hasPay(OfflinePlayer p, double amount) {
        return getPay(p) >= amount;
    }

    public boolean hasPayAccount(OfflinePlayer p) {
        try(
                Connection c = DriverManager.getConnection(CONNECTION);
                PreparedStatement s = c.prepareStatement("SELECT COUNT(*) FROM Money WHERE uuid=?")
        ) {
            s.setString(1, p.getUniqueId().toString());
            try (
                    ResultSet r = s.executeQuery()
            ) {
                return r.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public double getPay(OfflinePlayer p) {
        try(
                Connection c = DriverManager.getConnection(CONNECTION);
                PreparedStatement s = c.prepareStatement("SELECT value FROM Money WHERE uuid=?")
        ) {
            s.setString(1, p.getUniqueId().toString());
            try (
                    ResultSet r = s.executeQuery()
            ) {
                if (r.next()) {
                    //                    this.lastPay.put(p.getUniqueId(), amount);
                    return r.getDouble(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

//    public double getPay(OfflinePlayer p) {
//        Double d = this.lastPay.get(p.getUniqueId());
//        if(d == null) {
//            double pay = getPay(p);
//            this.lastPay.put(p.getUniqueId(), pay);
//            return pay;
//        }
//        return d;
//    }

    public void setPay(OfflinePlayer p, double amount) {
        Bukkit.getScheduler().runTaskAsynchronously(MoonerEco.plugin, () -> {
            try (
                    Connection c = DriverManager.getConnection(CONNECTION);
                    PreparedStatement s = c.prepareStatement("INSERT INTO Money VALUES(?, ?) ON CONFLICT(uuid) DO UPDATE SET value=?")
            ) {
                s.setString(1, p.getUniqueId().toString());
                s.setDouble(2, amount);
                s.setDouble(3, amount);
                s.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public void addPay(OfflinePlayer p, double amount) {
        setPay(p, getPay(p) + amount);
    }

    public void removePay(OfflinePlayer p, double amount) {
        setPay(p, getPay(p) - amount);
    }

    public List<MoneyData> getTopUser() {
        try (
                Connection c = DriverManager.getConnection(CONNECTION);
                PreparedStatement s = c.prepareStatement("SELECT * FROM Money")
        ) {
            try (ResultSet r = s.executeQuery()) {
                List<MoneyData> list = new ArrayList<>();
                while (r.next()) list.add(new MoneyData(r.getString("uuid"), r.getDouble("value")));
                return list.stream()
                        .sorted(Comparator.comparing(MoneyData::getValue).reversed())
                        .toList();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public List<MoneyData> getTopUser(long length) {
        try (
                Connection c = DriverManager.getConnection(CONNECTION);
                PreparedStatement s = c.prepareStatement("SELECT * FROM Money")
        ) {
            try (ResultSet r = s.executeQuery()) {
                List<MoneyData> list = new ArrayList<>();
                while (r.next()) list.add(new MoneyData(r.getString("uuid"), r.getDouble("value")));
                return list.stream()
                        .sorted(Comparator.comparing(MoneyData::getValue).reversed())
                        .limit(length)
                        .toList();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public void log(OfflinePlayer player, LogType type, double amount) {
        long time = System.currentTimeMillis();
        Bukkit.getScheduler().runTaskAsynchronously(MoonerEco.plugin, () -> {
            try (
                    Connection c = DriverManager.getConnection(CONNECTION);
                    PreparedStatement s = c.prepareStatement("INSERT INTO Log (source, uuid, uuid2, value, timestamp) VALUES(?, ?, null, ?, ?)")
            ) {
                s.setString(1, type.toString());
                s.setString(2, player.getUniqueId().toString());
                s.setDouble(3, amount);
                s.setLong(4, time);
                s.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public void log(OfflinePlayer player, OfflinePlayer to, LogType type, double amount) {
        log(player, to, type, null, amount);
    }

    public void log(OfflinePlayer player, OfflinePlayer to, LogType type, String data, double amount) {
        log(player, to == null ? "Console" : to.getUniqueId().toString(), type, data, amount);
    }

    public void log(OfflinePlayer player, String to, LogType type, String data, double amount) {
        long time = System.currentTimeMillis();
        Bukkit.getScheduler().runTaskAsynchronously(MoonerEco.plugin, () -> {
            try (
                    Connection c = DriverManager.getConnection(CONNECTION);
                    PreparedStatement s = c.prepareStatement("INSERT INTO Log (source, uuid, uuid2, data, value, timestamp) VALUES(?, ?, ?, ?, ?, ?)")
            ) {
                s.setString(1, type.toString());
                s.setString(2, player.getUniqueId().toString());
                s.setString(3, to);
                if(data == null) s.setNull(4, Type.CHAR);
                else s.setString(4, data);
                s.setDouble(5, amount);
                s.setLong(6, time);
                s.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public void log(OfflinePlayer player, String type, String data, double amount) {
        log(player, null, type, data, amount);
    }

    public void log(OfflinePlayer player, OfflinePlayer to, String type, String data, double amount) {
        long time = System.currentTimeMillis();
        Bukkit.getScheduler().runTaskAsynchronously(MoonerEco.plugin, () -> {
            try (
                    Connection c = DriverManager.getConnection(CONNECTION);
                    PreparedStatement s = c.prepareStatement("INSERT INTO Log (source, uuid, uuid2, data, value, timestamp) VALUES(?, ?, ?, ?, ?, ?)")
            ) {
                s.setString(1, type);
                s.setString(2, player.getUniqueId().toString());
                if(to == null) s.setNull(3, Type.CHAR);
                else s.setString(3, to.getUniqueId().toString());
                if(data == null) s.setNull(4, Type.CHAR);
                else s.setString(4, data);
                s.setDouble(5, amount);
                s.setLong(6, time);
                s.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public List<LogData> getLogUnViewed(Player p) {
        UUID uuid = p.getUniqueId();
        try (
                Connection c = DriverManager.getConnection(CONNECTION);
                PreparedStatement s = c.prepareStatement("SELECT * FROM Log WHERE uuid2=? and source=\"PAY\" and timestamp >= " + p.getLastPlayed())
        ) {
            s.setString(1, uuid.toString());
            try (ResultSet r = s.executeQuery()) {
                List<LogData> list = new ArrayList<>();
                while (r.next()) list.add(new LogData(r.getString("uuid"), uuid, LogType.PAY, r.getString("data"), r.getDouble("value"), r.getLong("timestamp")));
                return list;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }
}
