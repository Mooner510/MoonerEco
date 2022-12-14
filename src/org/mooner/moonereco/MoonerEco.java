package org.mooner.moonereco;

import de.epiceric.shopchest.event.ShopBuySellEvent;
import de.epiceric.shopchest.shop.Shop;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.mooner.moonerbungeeapi.api.BungeeAPI;
import org.mooner.moonereco.API.EcoAPI;
import org.mooner.moonereco.API.LogData;
import org.mooner.moonereco.API.LogType;
import org.mooner.moonereco.API.MoneyData;
import org.mooner.moonereco.hook.EcoHook;
import org.mooner.moonereco.hook.EcoManager;

import java.util.*;

import static org.mooner.moonereco.Utils.chat;
import static org.mooner.moonereco.Utils.parseString;

public class MoonerEco extends JavaPlugin implements Listener {

    public static MoonerEco plugin;
    public org.mooner.moonereco.hook.EcoManager EcoManager;
    public EcoHook vaultEcoHook;

    public static final String dataPath = "../db/MoonerEco/";

    @Override
    public void onLoad() {
        plugin=this;
        if(vaultEcoHook == null) {
            EcoManager = new EcoManager();
            vaultEcoHook = new EcoHook();
            vaultEcoHook.hook();
            EcoAPI.init = new EcoAPI();
        }
    }

    @Override
    public void onEnable() {
        if(vaultEcoHook == null) {
            EcoManager = new EcoManager();
            vaultEcoHook = new EcoHook();
            vaultEcoHook.hook();
            EcoAPI.init = new EcoAPI();
        }

        Bukkit.getConsoleSender().sendMessage(chat("&bPlugin Enabled! &7- &6MoonerEco"));
        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        Bukkit.getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        vaultEcoHook.unhook();
        Bukkit.getConsoleSender().sendMessage(chat("&bPlugin Disabled! &7- &6MoonerEco"));
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        for (LogData data : EcoAPI.init.getLogUnViewed(e.getPlayer())) {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(data.getTo());
            e.getPlayer().sendMessage(prefix + chat("&a????????????&f??? ?????? &e" + offlinePlayer.getName() + "&f??????????????? &a" + parseString(data.getValue(), 2, true) + "???&f??? ???????????????."));
        }
    }

    @EventHandler
    public void onShop(ShopBuySellEvent e) {
        final Map.Entry<Material, Integer> data = toSaveData(e.getShop().getItem().getItemStack().getType());
        if(e.getShop().getShopType() == Shop.ShopType.NORMAL) {
            EcoAPI.init.log(e.getPlayer(), e.getShop().getVendor(), LogType.valueOf(e.getType().toString()), data.getKey().toString(), e.getNewAmount() * data.getValue(), e.getNewPrice());
        } else {
            EcoAPI.init.log(e.getPlayer(), "Admin Shop", LogType.valueOf(e.getType().toString()), data.getKey().toString(), e.getNewAmount() * data.getValue(), e.getNewPrice());
        }
    }

    public Map.Entry<Material, Integer> toSaveData(Material m) {
        return switch (m) {
            case COAL_BLOCK -> new AbstractMap.SimpleImmutableEntry<>(Material.COAL, 9);
            case LAPIS_BLOCK -> new AbstractMap.SimpleImmutableEntry<>(Material.LAPIS_LAZULI, 9);
            case COPPER_BLOCK -> new AbstractMap.SimpleImmutableEntry<>(Material.COPPER_INGOT, 9);
            case IRON_BLOCK -> new AbstractMap.SimpleImmutableEntry<>(Material.IRON_INGOT, 9);
            case GOLD_BLOCK -> new AbstractMap.SimpleImmutableEntry<>(Material.GOLD_INGOT, 9);
            case QUARTZ_BLOCK -> new AbstractMap.SimpleImmutableEntry<>(Material.QUARTZ, 4);
            case AMETHYST_BLOCK -> new AbstractMap.SimpleImmutableEntry<>(Material.AMETHYST_SHARD, 4);
            case GLOWSTONE -> new AbstractMap.SimpleImmutableEntry<>(Material.GLOWSTONE_DUST, 4);
            case REDSTONE_BLOCK -> new AbstractMap.SimpleImmutableEntry<>(Material.REDSTONE, 9);
            case NETHERITE_BLOCK -> new AbstractMap.SimpleImmutableEntry<>(Material.NETHERITE_INGOT, 9);
            case EMERALD_BLOCK -> new AbstractMap.SimpleImmutableEntry<>(Material.EMERALD, 9);
            case DIAMOND_BLOCK -> new AbstractMap.SimpleImmutableEntry<>(Material.DIAMOND, 9);
            case DRIED_KELP_BLOCK -> new AbstractMap.SimpleImmutableEntry<>(Material.DRIED_KELP, 9);
            default -> new AbstractMap.SimpleImmutableEntry<>(m, 1);
        };
    }

    private static final String prefix = chat("&e[ &f??? &e] ");

    public OfflinePlayer getOfflinePlayer(String s) {
        Player p = Bukkit.getPlayer(s);
        if(p != null && p.isOnline()) return p;
        String finder = s.toLowerCase();
        return Arrays.stream(Bukkit.getOfflinePlayers())
                .filter(o -> (o.getName() != null && o.getName().toLowerCase().equals(finder)) || o.getUniqueId().toString().equals(finder))
                .findFirst().orElse(null);
    }

    public void me(Player p) {
        p.sendMessage(prefix + chat("&f?????? : &a" + parseString(EcoAPI.init.getPay(p), 2, true) + "&f???"));
    }

    public void you(CommandSender p, OfflinePlayer o) {
        p.sendMessage(prefix + chat("&a"+o.getName()+"&f?????? ?????? : &a" + parseString(EcoAPI.init.getPay(o), 2, true) + "&f???"));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equals("money")) {
            if(args.length >= 1) {
                switch (args[0].toLowerCase()) {
                    case "?????????", "qhsorl", "send", "pay" -> {
                        if(!(sender instanceof Player p)) {
                            sender.sendMessage(chat("&cIt is Player Command!"));
                            return true;
                        }
                        if(args.length >= 2) {
                            if(args.length >= 3) {
                                if(sender.getName().equals(args[1])) {
                                    p.sendMessage(prefix + chat("&c?????? ???????????? ?????? ??? ????????????."));
                                    return true;
                                }
                                OfflinePlayer player = getOfflinePlayer(args[1]);
                                if(player == null) {
                                    p.sendMessage(prefix + chat("&6"+args[1]+"&c?????? ????????????????????? ??? ??? ?????? ?????????????????????."));
                                    return true;
                                }
                                double d;
                                try {
                                    int index;
                                    if((index = args[2].indexOf(".")) != -1)
                                        args[2] = args[2].substring(0, Math.min(index + 2, args[2].length()));
                                    d = Double.parseDouble(args[2]);
                                } catch (Exception e) {
                                    p.sendMessage(prefix + chat("&c????????? ????????? ????????? ?????????!"));
                                    return true;
                                }
                                if(d <= 0.1) {
                                    p.sendMessage(prefix + chat("&c?????? &60.1???&c?????? ????????? ?????????!"));
                                    return true;
                                }
                                if (!EcoAPI.init.hasPay(p, d)) {
                                    p.sendMessage(prefix + chat("&c?????? ??? ?????? ??? ?????? ?????? ?????? ??? ????????????!"));
                                    return true;
                                }
                                EcoAPI.init.removePay(p, d);
                                EcoAPI.init.addPay(player, d);
                                EcoAPI.init.log(p, player, LogType.PAY, d);
                                p.sendMessage(prefix + chat("&e" + player.getName() + "&f????????? &a" + parseString(d, 2, true) + "???&f??? ???????????????."));
                                Player offPlayer = player.getPlayer();
                                if(offPlayer != null)
                                    offPlayer.sendMessage(prefix + chat("&e" + p.getName() + "&f??????????????? &a" + parseString(d, 2, true) + "???&f??? ???????????????."));
                                else
                                    BungeeAPI.sendBungeeMessage(player.getName(), prefix + chat("&e" + p.getName() + "&f??????????????? &a" + parseString(d, 2, true) + "???&f??? ???????????????."));
                            } else {
                                p.sendMessage(prefix + chat("&c?????? ????????? ??????????????????."));
                            }
                        } else {
                            p.sendMessage(prefix + chat("&c??????????????? ??????????????????."));
                        }
                        return true;
                    }
                    case "??????", "top", "tnsdnl" -> {
                        sender.sendMessage(chat("&7========= &e[ &f??? ?????? &e] &7========="));
                        final List<MoneyData> topUser = EcoAPI.init.getTopUser();
                        int top = 1;
                        if(sender instanceof Player p) {
                            int myTop = 0;
                            for (MoneyData data : topUser) {
                                OfflinePlayer player = Bukkit.getOfflinePlayer(data.getUUID());
                                final String s = top == 1 ? "&c" : top == 2 ? "&6" : top == 3 ? "&e" : top <= 5 ? "&a" : "&7";
                                if (p.getName().equals(player.getName())) {
                                    if (top <= 8)
                                        sender.sendMessage(chat("  &d" + player.getName() + " &f- " + s + parseString(data.getValue(), 2, true) + "???"));
                                    myTop = top;
                                } else {
                                    if (top <= 8)
                                        sender.sendMessage(chat("  &b" + player.getName() + " &f- " + s + parseString(data.getValue(), 2, true) + "???"));
                                }
                                if (top++ >= 8 && myTop != 0) break;
                            }
                            if (myTop > 8)
                                sender.sendMessage(chat(" &f?????? ??????: &7" + (top == 1 ? "&c" : top == 2 ? "&6" : top == 3 ? "&e" : top <= 5 ? "&a" : "&7") + top + "???"));
                        } else {
                            for (MoneyData data : topUser) {
                                OfflinePlayer player = Bukkit.getOfflinePlayer(data.getUUID());
                                sender.sendMessage(chat("  &b" + player.getName() + " &f- " + (top == 1 ? "&c" : top == 2 ? "&6" : top == 3 ? "&e" : top <= 5 ? "&a" : "&7") + parseString(data.getValue(), 2, true) + "???"));
                                top++;
                            }
                        }
                        sender.sendMessage(chat("&7========= &e[ &f??? ?????? &e] &7========="));
                        return true;
                    }
                    case "set" -> {
                        if(sender instanceof Player p && !p.isOp()) {
                            me(p);
                            return true;
                        }
                        if(args.length >= 2) {
                            if(args.length >= 3) {
                                OfflinePlayer player = getOfflinePlayer(args[1]);
                                if(player == null) {
                                    sender.sendMessage(prefix + chat("&6"+args[1]+"&c?????? ????????????????????? ??? ??? ?????? ?????????????????????."));
                                    return true;
                                }
                                double d;
                                try {
                                    int index;
                                    if((index = args[2].indexOf(".")) != -1)
                                        args[2] = args[2].substring(0, Math.min(index + 2, args[2].length()));
                                    d = Double.parseDouble(args[2]);
                                } catch (Exception e) {
                                    sender.sendMessage(prefix + chat("&c????????? ????????? ????????? ?????????!"));
                                    return true;
                                }
                                final double pay = EcoAPI.init.getPay(player);
                                EcoAPI.init.setPay(player, d);
                                if(sender instanceof Player p) {
                                    EcoAPI.init.log(player, p, LogType.ADMIN_ADD, d - pay);
                                } else {
                                    EcoAPI.init.log(player, null, LogType.ADMIN_ADD, d - pay);
                                }
                                sender.sendMessage(prefix + chat("&e" + player.getName() + "&f?????? ????????? &a" + parseString(d, 2, true) + "???&f?????? ??????????????????."));
                            } else {
                                sender.sendMessage(prefix + chat("&c????????? ????????? ??????????????????."));
                            }
                        } else {
                            sender.sendMessage(prefix + chat("&c??????????????? ??????????????????."));
                        }
                        return true;
                    }
                    case "add" -> {
                        if(sender instanceof Player p && !p.isOp()) {
                            me(p);
                            return true;
                        }
                        if(args.length >= 2) {
                            if(args.length >= 3) {
                                OfflinePlayer player = getOfflinePlayer(args[1]);
                                if(player == null) {
                                    sender.sendMessage(prefix + chat("&6"+args[1]+"&c?????? ????????????????????? ??? ??? ?????? ?????????????????????."));
                                    return true;
                                }
                                double d;
                                try {
                                    int index;
                                    if((index = args[2].indexOf(".")) != -1)
                                        args[2] = args[2].substring(0, Math.min(index + 2, args[2].length()));
                                    d = Double.parseDouble(args[2]);
                                } catch (Exception e) {
                                    sender.sendMessage(prefix + chat("&c????????? ????????? ????????? ?????????!"));
                                    return true;
                                }
                                EcoAPI.init.addPay(player, d);
                                if(sender instanceof Player p) {
                                    EcoAPI.init.log(player, p, LogType.ADMIN_ADD, d);
                                } else {
                                    EcoAPI.init.log(player, null, LogType.ADMIN_ADD, d);
                                }
                                sender.sendMessage(prefix + chat("&e" + player.getName() + "&f?????? ????????? &a" + parseString(d, 2, true) + "???&f??? ??????????????????."));
                            } else {
                                sender.sendMessage(prefix + chat("&c????????? ????????? ??????????????????."));
                            }
                        } else {
                            sender.sendMessage(prefix + chat("&c??????????????? ??????????????????."));
                        }
                        return true;
                    }
                    case "remove" -> {
                        if(sender instanceof Player p && !p.isOp()) {
                            me(p);
                            return true;
                        }
                        if(args.length >= 2) {
                            if(args.length >= 3) {
                                OfflinePlayer player = getOfflinePlayer(args[1]);
                                if(player == null) {
                                    sender.sendMessage(prefix + chat("&6"+args[1]+"&c?????? ????????????????????? ??? ??? ?????? ?????????????????????."));
                                    return true;
                                }
                                double d;
                                try {
                                    int index;
                                    if((index = args[2].indexOf(".")) != -1)
                                        args[2] = args[2].substring(0, Math.min(index + 2, args[2].length()));
                                    d = Double.parseDouble(args[2]);
                                } catch (Exception e) {
                                    sender.sendMessage(prefix + chat("&c????????? ????????? ????????? ?????????!"));
                                    return true;
                                }
                                EcoAPI.init.removePay(player, d);
                                if(sender instanceof Player p) {
                                    EcoAPI.init.log(player, p, LogType.ADMIN_ADD, -d);
                                } else {
                                    EcoAPI.init.log(player, null, LogType.ADMIN_ADD, -d);
                                }
                                sender.sendMessage(prefix + chat("&e" + player.getName() + "&f?????? ???????????? &a" + parseString(d, 2, true) + "???&f??? ??????????????????."));
                            } else {
                                sender.sendMessage(prefix + chat("&c????????? ????????? ??????????????????."));
                            }
                        } else {
                            sender.sendMessage(prefix + chat("&c??????????????? ??????????????????."));
                        }
                        return true;
                    }
                    default -> {
                        OfflinePlayer player = getOfflinePlayer(args[0]);
                        if(player != null) {
                            you(sender, player);
                            return true;
                        }
                    }
                }
            }
            if(sender instanceof Player p) me(p);
            return true;
        }
        return false;
    }

    private final List<String> adminList = List.of("?????????", "qhsorl", "send", "pay", "??????", "top", "tnsdnl", "set", "add", "remove");
    private final List<String> normalList = List.of("?????????", "qhsorl", "send", "pay", "??????", "top", "tnsdnl");

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (command.getName().equals("money") && sender instanceof Player p) {
            if(args.length == 1) {
                final ArrayList<String> list = new ArrayList<>(p.isOp() ? adminList : normalList);
                list.addAll(Arrays.stream(Bukkit.getOfflinePlayers())
                        .map(OfflinePlayer::getName)
                        .filter(s -> Objects.nonNull(s) && s.toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
                        .toList());
                return list;
            } else if(args.length <= 2) {
                return Arrays.stream(Bukkit.getOfflinePlayers())
                        .map(OfflinePlayer::getName)
                        .filter(s -> Objects.nonNull(s) && s.toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
                        .toList();
            }
        }
        return null;
    }
}
