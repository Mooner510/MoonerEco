package org.mooner.moonereco;

import de.epiceric.shopchest.event.ShopBuySellEvent;
import de.epiceric.shopchest.shop.Shop;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.mooner.moonerbungeeapi.api.BungeeAPI;
import org.mooner.moonereco.API.EcoAPI;
import org.mooner.moonereco.API.LogData;
import org.mooner.moonereco.API.LogType;
import org.mooner.moonereco.API.MoneyData;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.mooner.moonereco.hook.EcoHook;
import org.mooner.moonereco.hook.EcoManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static org.mooner.moonereco.Utils.chat;
import static org.mooner.moonereco.Utils.parseString;

public class MoonerEco extends JavaPlugin implements Listener {

    public static MoonerEco plugin;
    public org.mooner.moonereco.hook.EcoManager EcoManager;
    public EcoHook vaultEcoHook;

    public static final String dataPath = "../db/MoonerEco/";

    @Override
    public void onEnable() {
        plugin=this;
        EcoManager = new EcoManager();
        vaultEcoHook = new EcoHook();
        vaultEcoHook.hook();
        EcoAPI.init = new EcoAPI();

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
            e.getPlayer().sendMessage(prefix + chat("&a오프라인&f인 동안 &e" + offlinePlayer.getName() + "&f님으로부터 &a" + parseString(data.getValue(), 2, true) + "원&f을 받았습니다."));
        }
    }

    @EventHandler
    public void onShop(ShopBuySellEvent e) {
        final String s = e.getShop().getItem().getItemStack().getType() + ":" + e.getNewAmount();
        if(e.getShop().getShopType() == Shop.ShopType.NORMAL) {
            EcoAPI.init.log(e.getPlayer(), e.getShop().getVendor(), LogType.valueOf(e.getType().toString()), s, e.getNewPrice());
        } else {
            EcoAPI.init.log(e.getPlayer(), "Admin Shop", LogType.valueOf(e.getType().toString()), s, e.getNewPrice());
        }
    }

    private static final String prefix = chat("&e[ &f돈 &e] ");

    public OfflinePlayer getOfflinePlayer(String s) {
        Player p = Bukkit.getPlayer(s);
        if(p != null) return p;
        return Arrays.stream(Bukkit.getOfflinePlayers())
                .filter(o -> o.getName() != null && o.getName().toLowerCase().equals(s))
                .findFirst().orElse(null);
    }

    public void me(Player p) {
        p.sendMessage(prefix + chat("&f잔고 : &a" + parseString(EcoAPI.init.getPay(p), 2, true) + "&f원"));
    }

    public void you(CommandSender p, OfflinePlayer o) {
        p.sendMessage(prefix + chat("&a"+o.getName()+"&f님의 잔고 : &a" + parseString(EcoAPI.init.getPay(o), 2, true) + "&f원"));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equals("money")) {
            if(args.length >= 1) {
                switch (args[0].toLowerCase()) {
                    case "보내기", "qhsorl", "send", "pay" -> {
                        if(!(sender instanceof Player p)) {
                            sender.sendMessage(chat("&cIt is Player Command!"));
                            return true;
                        }
                        if(args.length >= 2) {
                            if(args.length >= 3) {
                                if(sender.getName().equals(args[1])) {
                                    p.sendMessage(prefix + chat("&c자기 자신에게 보낼 수 없습니다."));
                                    return true;
                                }
                                OfflinePlayer player = getOfflinePlayer(args[1]);
                                if(player == null) {
                                    p.sendMessage(prefix + chat("&6"+args[1]+"&c님은 오프라인이거나 알 수 없는 플레이어입니다."));
                                    return true;
                                }
                                double d;
                                try {
                                    int index;
                                    if((index = args[2].indexOf(".")) != -1)
                                        args[2] = args[2].substring(0, Math.min(index + 2, args[2].length()));
                                    d = Double.parseDouble(args[2]);
                                } catch (Exception e) {
                                    p.sendMessage(prefix + chat("&c정확한 숫자를 입력해 주세요!"));
                                    return true;
                                }
                                if(d <= 0.1) {
                                    p.sendMessage(prefix + chat("&c최소 &60.1원&c이상 보내야 합니다!"));
                                    return true;
                                }
                                if (!EcoAPI.init.hasPay(p, d)) {
                                    p.sendMessage(prefix + chat("&c가진 돈 보다 더 많은 돈을 보낼 수 없습니다!"));
                                    return true;
                                }
                                EcoAPI.init.removePay(p, d);
                                EcoAPI.init.addPay(player, d);
                                EcoAPI.init.log(p, player, LogType.PAY, d);
                                p.sendMessage(prefix + chat("&e" + player.getName() + "&f님에게 &a" + parseString(d, 2, true) + "원&f을 보냈습니다."));
                                Player offPlayer = player.getPlayer();
                                if(offPlayer != null)
                                    offPlayer.sendMessage(prefix + chat("&e" + p.getName() + "&f님으로부터 &a" + parseString(d, 2, true) + "원&f을 받았습니다."));
                                else
                                    BungeeAPI.sendBungeeMessage(player.getName(), prefix + chat("&e" + p.getName() + "&f님으로부터 &a" + parseString(d, 2, true) + "원&f을 받았습니다."));
                            } else {
                                p.sendMessage(prefix + chat("&c보낼 금액을 입력해주세요."));
                            }
                        } else {
                            p.sendMessage(prefix + chat("&c플레이어를 입력해주세요."));
                        }
                        return true;
                    }
                    case "순위", "top", "tnsdnl" -> {
                        if(!(sender instanceof Player p)) {
                            sender.sendMessage(chat("&cIt is Player Command!"));
                            return true;
                        }
                        p.sendMessage(chat("&7========= &e[ &f돈 순위 &e] &7========="));
                        int top = 1;
                        for (MoneyData data : EcoAPI.init.getTopUser(8)) {
                            OfflinePlayer player = Bukkit.getOfflinePlayer(data.getUUID());
                            p.sendMessage(chat("  &b" + player.getName() + " &f- " + (top == 1 ? "&c" : top == 2 ? "&6" : top == 3 ? "&e" : "&a") + parseString(data.getValue(), 2, true) + "원"));
                            top++;
                        }
                        p.sendMessage(chat("&7========= &e[ &f돈 순위 &e] &7========="));
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
                                    sender.sendMessage(prefix + chat("&6"+args[1]+"&c님은 오프라인이거나 알 수 없는 플레이어입니다."));
                                    return true;
                                }
                                double d;
                                try {
                                    int index;
                                    if((index = args[2].indexOf(".")) != -1)
                                        args[2] = args[2].substring(0, Math.min(index + 2, args[2].length()));
                                    d = Double.parseDouble(args[2]);
                                } catch (Exception e) {
                                    sender.sendMessage(prefix + chat("&c정확한 숫자를 입력해 주세요!"));
                                    return true;
                                }
                                EcoAPI.init.setPay(player, d);
                                if(sender instanceof Player p) {
                                    EcoAPI.init.log(player, p, LogType.ADMIN_SET, d);
                                } else {
                                    EcoAPI.init.log(player, null, LogType.ADMIN_SET, d);
                                }
                                sender.sendMessage(prefix + chat("&e" + player.getName() + "&f님의 잔고를 &a" + parseString(d, 2, true) + "원&f으로 설정했습니다."));
                            } else {
                                sender.sendMessage(prefix + chat("&c설정할 금액을 입력해주세요."));
                            }
                        } else {
                            sender.sendMessage(prefix + chat("&c플레이어를 입력해주세요."));
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
                                    sender.sendMessage(prefix + chat("&6"+args[1]+"&c님은 오프라인이거나 알 수 없는 플레이어입니다."));
                                    return true;
                                }
                                double d;
                                try {
                                    int index;
                                    if((index = args[2].indexOf(".")) != -1)
                                        args[2] = args[2].substring(0, Math.min(index + 2, args[2].length()));
                                    d = Double.parseDouble(args[2]);
                                } catch (Exception e) {
                                    sender.sendMessage(prefix + chat("&c정확한 숫자를 입력해 주세요!"));
                                    return true;
                                }
                                EcoAPI.init.addPay(player, d);
                                if(sender instanceof Player p) {
                                    EcoAPI.init.log(player, p, LogType.ADMIN_ADD, d);
                                } else {
                                    EcoAPI.init.log(player, null, LogType.ADMIN_ADD, d);
                                }
                                sender.sendMessage(prefix + chat("&e" + player.getName() + "&f님의 잔고에 &a" + parseString(d, 2, true) + "원&f을 추가했습니다."));
                            } else {
                                sender.sendMessage(prefix + chat("&c추가할 금액을 입력해주세요."));
                            }
                        } else {
                            sender.sendMessage(prefix + chat("&c플레이어를 입력해주세요."));
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
                                    sender.sendMessage(prefix + chat("&6"+args[1]+"&c님은 오프라인이거나 알 수 없는 플레이어입니다."));
                                    return true;
                                }
                                double d;
                                try {
                                    int index;
                                    if((index = args[2].indexOf(".")) != -1)
                                        args[2] = args[2].substring(0, Math.min(index + 2, args[2].length()));
                                    d = Double.parseDouble(args[2]);
                                } catch (Exception e) {
                                    sender.sendMessage(prefix + chat("&c정확한 숫자를 입력해 주세요!"));
                                    return true;
                                }
                                EcoAPI.init.removePay(player, d);
                                if(sender instanceof Player p) {
                                    EcoAPI.init.log(player, p, LogType.ADMIN_ADD, -d);
                                } else {
                                    EcoAPI.init.log(player, null, LogType.ADMIN_ADD, -d);
                                }
                                sender.sendMessage(prefix + chat("&e" + player.getName() + "&f님의 잔고에서 &a" + parseString(d, 2, true) + "원&f을 제거했습니다."));
                            } else {
                                sender.sendMessage(prefix + chat("&c제거할 금액을 입력해주세요."));
                            }
                        } else {
                            sender.sendMessage(prefix + chat("&c플레이어를 입력해주세요."));
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

    private final List<String> adminList = List.of("보내기", "qhsorl", "send", "pay", "순위", "top", "tnsdnl", "set", "add", "remove");
    private final List<String> normalList = List.of("보내기", "qhsorl", "send", "pay", "순위", "top", "tnsdnl");

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (command.getName().equals("money") && sender instanceof Player p) {
            if(args.length == 1) {
                return new ArrayList<>(p.isOp() ? adminList : normalList);
            } else if(args.length <= 2) {
                return Arrays.stream(Bukkit.getOfflinePlayers())
                        .map(OfflinePlayer::getName)
                        .filter(s -> Objects.nonNull(s) && s.startsWith(args[args.length - 1]))
                        .toList();
            }
        }
        return null;
    }
}
