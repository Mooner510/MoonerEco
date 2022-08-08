package org.mooner.moonereco.hook;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.mooner.moonereco.Utils;

import java.util.List;
import java.util.UUID;

import static org.mooner.moonereco.API.EcoAPI.init;

public class EcoManager implements Economy {
    @Override
    public boolean isEnabled() {
        return init != null;
    }

    @Override
    public String getName() {
        return "MoonerEco";
    }

    @Override
    public boolean hasBankSupport() {
        return false;
    }

    @Override
    public int fractionalDigits() {
        return -1;
    }

    @Override
    public String format(double v) {
        return Utils.getCommaNumber(v);
    }

    @Override
    public String currencyNamePlural() {
        return "원";
    }

    @Override
    public String currencyNameSingular() {
        return "원";
    }

    @Override
    public boolean hasAccount(String s) {
        return init.hasPayAccount(Bukkit.getOfflinePlayer(UUID.fromString(s)));
    }

    @Override
    public boolean hasAccount(OfflinePlayer offlinePlayer) {
        return init.hasPayAccount(offlinePlayer);
    }

    @Override
    public boolean hasAccount(String s, String s1) {
        return init.hasPayAccount(Bukkit.getOfflinePlayer(UUID.fromString(s)));
    }

    @Override
    public boolean hasAccount(OfflinePlayer offlinePlayer, String s) {
        return init.hasPayAccount(offlinePlayer);
    }

    @Override
    public double getBalance(String s) {
        return init.getLocalPay(Bukkit.getOfflinePlayer(UUID.fromString(s)));
    }

    @Override
    public double getBalance(OfflinePlayer offlinePlayer) {
        return init.getLocalPay(offlinePlayer);
    }

    @Override
    public double getBalance(String s, String s1) {
        return init.getLocalPay(Bukkit.getOfflinePlayer(UUID.fromString(s)));
    }

    @Override
    public double getBalance(OfflinePlayer offlinePlayer, String s) {
        return init.getLocalPay(offlinePlayer);
    }

    @Override
    public boolean has(String s, double v) {
        return init.hasPay(Bukkit.getOfflinePlayer(UUID.fromString(s)), v);
    }

    @Override
    public boolean has(OfflinePlayer offlinePlayer, double v) {
        return init.hasPay(offlinePlayer, v);
    }

    @Override
    public boolean has(String s, String s1, double v) {
        return init.hasPay(Bukkit.getOfflinePlayer(UUID.fromString(s)), v);
    }

    @Override
    public boolean has(OfflinePlayer offlinePlayer, String s, double v) {
        return init.hasPay(Bukkit.getOfflinePlayer(UUID.fromString(s)), v);
    }

    @Override
    public EconomyResponse withdrawPlayer(String s, double v) {
        OfflinePlayer p = Bukkit.getOfflinePlayer(UUID.fromString(s));
        if (init.hasPay(p, v)) {
            init.removePay(p, v);
            return new EconomyResponse(v, init.getLocalPay(p), EconomyResponse.ResponseType.SUCCESS, "");
        }
        return new EconomyResponse(0, init.getLocalPay(p), EconomyResponse.ResponseType.FAILURE, "충분한 돈을 가지고 있지 않습니다.");
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer p, double v) {
        if (init.hasPay(p, v)) {
            init.removePay(p, v);
            return new EconomyResponse(v, init.getLocalPay(p), EconomyResponse.ResponseType.SUCCESS, "");
        }
        return new EconomyResponse(0, init.getLocalPay(p), EconomyResponse.ResponseType.FAILURE, "충분한 돈을 가지고 있지 않습니다.");
    }

    @Override
    public EconomyResponse withdrawPlayer(String s, String s1, double v) {
        OfflinePlayer p = Bukkit.getOfflinePlayer(UUID.fromString(s));
        if (init.hasPay(p, v)) {
            init.removePay(p, v);
            return new EconomyResponse(v, init.getLocalPay(p), EconomyResponse.ResponseType.SUCCESS, "");
        }
        return new EconomyResponse(0, init.getLocalPay(p), EconomyResponse.ResponseType.FAILURE, "충분한 돈을 가지고 있지 않습니다.");
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer p, String s, double v) {
        if (init.hasPay(p, v)) {
            init.removePay(p, v);
            return new EconomyResponse(v, init.getLocalPay(p), EconomyResponse.ResponseType.SUCCESS, "");
        }
        return new EconomyResponse(0, init.getLocalPay(p), EconomyResponse.ResponseType.FAILURE, "충분한 돈을 가지고 있지 않습니다.");
    }

    @Override
    public EconomyResponse depositPlayer(String s, double v) {
        OfflinePlayer p = Bukkit.getOfflinePlayer(UUID.fromString(s));
        init.addPay(p, v);
        return new EconomyResponse(v, init.getLocalPay(p), EconomyResponse.ResponseType.SUCCESS, "");
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer p, double v) {
        init.addPay(p, v);
        return new EconomyResponse(v, init.getLocalPay(p), EconomyResponse.ResponseType.SUCCESS, "");
    }

    @Override
    public EconomyResponse depositPlayer(String s, String s1, double v) {
        OfflinePlayer p = Bukkit.getOfflinePlayer(UUID.fromString(s));
        init.addPay(p, v);
        return new EconomyResponse(v, init.getLocalPay(p), EconomyResponse.ResponseType.SUCCESS, "");
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer p, String s, double v) {
        init.addPay(p, v);
        return new EconomyResponse(v, init.getLocalPay(p), EconomyResponse.ResponseType.SUCCESS, "");
    }

    @Override
    public EconomyResponse createBank(String s, String s1) {
        return null;
    }

    @Override
    public EconomyResponse createBank(String s, OfflinePlayer offlinePlayer) {
        return null;
    }

    @Override
    public EconomyResponse deleteBank(String s) {
        return null;
    }

    @Override
    public EconomyResponse bankBalance(String s) {
        return null;
    }

    @Override
    public EconomyResponse bankHas(String s, double v) {
        return null;
    }

    @Override
    public EconomyResponse bankWithdraw(String s, double v) {
        return null;
    }

    @Override
    public EconomyResponse bankDeposit(String s, double v) {
        return null;
    }

    @Override
    public EconomyResponse isBankOwner(String s, String s1) {
        return null;
    }

    @Override
    public EconomyResponse isBankOwner(String s, OfflinePlayer offlinePlayer) {
        return null;
    }

    @Override
    public EconomyResponse isBankMember(String s, String s1) {
        return null;
    }

    @Override
    public EconomyResponse isBankMember(String s, OfflinePlayer offlinePlayer) {
        return null;
    }

    @Override
    public List<String> getBanks() {
        return null;
    }

    @Override
    public boolean createPlayerAccount(String s) {
        return true;
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer offlinePlayer) {
        return true;
    }

    @Override
    public boolean createPlayerAccount(String s, String s1) {
        return true;
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer offlinePlayer, String s) {
        return true;
    }
}
