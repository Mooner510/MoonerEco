package Mooner.API;

import org.bukkit.OfflinePlayer;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static Mooner.MoonerEco.dataPath;
import static Mooner.MoonerEco.plugin;

public class EcoAPI {
    public static EcoAPI init;
    private final HashMap<UUID, Double> lastPay;
    private static final String CONNECTION = "jdbc:sqlite:" + dataPath + "eco.db";

    public EcoAPI() {
        lastPay = new HashMap<>();

        new File(dataPath).mkdirs();
        File db = new File(dataPath, "eco.db");
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
            plugin.getLogger().info("성공적으로 Money DB를 생성했습니다.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try (
                Connection c = DriverManager.getConnection(CONNECTION);
                PreparedStatement s = c.prepareStatement(
                        "CREATE TABLE IF NOT EXISTS PayLog (" +
                                "id INTEGER NOT NULL" +
                                "from TEXT NOT NULL," +
                                "to TEXT NOT NULL," +
                                "value REAL NOT NULL," +
                                "PRIMARY KEY(id AUTOINCREMENT)" +
                                ")")
        ) {
            s.execute();
            plugin.getLogger().info("성공적으로 PayLog DB를 생성했습니다.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try (
                Connection c = DriverManager.getConnection(CONNECTION);
                PreparedStatement s = c.prepareStatement(
                        "CREATE TABLE IF NOT EXISTS MoneyLog (" +
                                "id INTEGER NOT NULL" +
                                "uuid TEXT NOT NULL," +
                                "source TEXT," +
                                "value REAL NOT NULL," +
                                "PRIMARY KEY(id AUTOINCREMENT)" +
                                ")")
        ) {
            s.execute();
            plugin.getLogger().info("성공적으로 MoneyLog DB를 생성했습니다.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean hasPay(OfflinePlayer p, double amount) {
        return this.lastPay.getOrDefault(p.getUniqueId(), 0d) >= amount;
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

    private double getPay(OfflinePlayer p) {
        try(
                Connection c = DriverManager.getConnection(CONNECTION);
                PreparedStatement s = c.prepareStatement("SELECT value FROM Money WHERE uuid=?")
        ) {
            s.setString(1, p.getUniqueId().toString());
            try (
                    ResultSet r = s.executeQuery()
            ) {
                if (r.next()) {
                    final double amount = r.getDouble(1);
                    this.lastPay.put(p.getUniqueId(), amount);
                    return amount;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public double getLocalPay(OfflinePlayer p) {
        return this.lastPay.getOrDefault(p.getUniqueId(), 0d);
    }

    public void setPay(OfflinePlayer p, double amount) {
        try (
                Connection c = DriverManager.getConnection(CONNECTION);
                PreparedStatement s = c.prepareStatement("INSERT INTO Money VALUES(?, ?) ON CONFLICT(uuid) DO UPDATE SET value=?");
        ) {
            s.setString(1, p.getUniqueId().toString());
            s.setDouble(2, amount);
            s.setDouble(3, amount);
            s.executeUpdate();
            this.lastPay.put(p.getUniqueId(), amount);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addPay(OfflinePlayer p, double amount) {
        setPay(p, getLocalPay(p) + amount);
    }

    public boolean removePay(OfflinePlayer p, double amount) {
        if (!hasPay(p, amount)) return false;
        setPay(p, getLocalPay(p) - amount);
        return true;
    }

    public void logPay(OfflinePlayer from, OfflinePlayer to, double amount) {
        try (
                Connection c = DriverManager.getConnection(CONNECTION);
                PreparedStatement s = c.prepareStatement("INSERT INTO MoneyLog (from, to, value) VALUES(?, ?, ?, ?)")
        ) {
            s.setString(1, from.getUniqueId().toString());
            s.setString(2, to.getUniqueId().toString());
            s.setDouble(3, amount);
            s.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<PayLogData> getPayLogFrom(UUID uuid) {
        try (
                Connection c = DriverManager.getConnection(CONNECTION);
                PreparedStatement s = c.prepareStatement("SELECT * FROM PayLog WHERE from=?")
        ) {
            s.setString(1, uuid.toString());
            try (ResultSet r = s.executeQuery()) {
                List<PayLogData> list = new ArrayList<>();
                while (r.next()) list.add(new PayLogData(uuid, r.getString("to"), r.getDouble("value")));
                return list;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public List<PayLogData> getPayLogTo(UUID uuid) {
        try (
                Connection c = DriverManager.getConnection(CONNECTION);
                PreparedStatement s = c.prepareStatement("SELECT * FROM PayLog WHERE to=?")
        ) {
            s.setString(1, uuid.toString());
            try (ResultSet r = s.executeQuery()) {
                List<PayLogData> list = new ArrayList<>();
                while (r.next()) list.add(new PayLogData(r.getString("from"), uuid, r.getDouble("value")));
                return list;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public List<PayLogData> getPayLogAll(UUID uuid) {
        try (
                Connection c = DriverManager.getConnection(CONNECTION);
                PreparedStatement s = c.prepareStatement("SELECT * FROM PayLog WHERE from=? or to=?")
        ) {
            s.setString(1, uuid.toString());
            s.setString(2, uuid.toString());
            try (ResultSet r = s.executeQuery()) {
                List<PayLogData> list = new ArrayList<>();
                while (r.next()) list.add(new PayLogData(uuid, uuid, r.getDouble("value")));
                return list;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public void logMoney(OfflinePlayer p, String source, double amount) {
        try (
                Connection c = DriverManager.getConnection(CONNECTION);
                PreparedStatement s = c.prepareStatement("INSERT INTO MoneyLog (uuid, source, value) VALUES(?, ?, ?)")
        ) {
            s.setString(1, p.getUniqueId().toString());
            s.setString(2, source);
            s.setDouble(3, amount);
            s.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<MoneyLogData> getMoneyLogs(UUID uuid) {
        try (
                Connection c = DriverManager.getConnection(CONNECTION);
                PreparedStatement s = c.prepareStatement("SELECT * FROM MoneyLog WHERE from=?")
        ) {
            s.setString(1, uuid.toString());
            try (ResultSet r = s.executeQuery()) {
                List<MoneyLogData> list = new ArrayList<>();
                while (r.next()) list.add(new MoneyLogData(uuid, r.getString("to"), r.getDouble("value")));
                return list;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }
}
