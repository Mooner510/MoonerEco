package org.mooner.moonereco.hook;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.ServicePriority;
import org.mooner.moonereco.MoonerEco;

public class EcoHook {
    private Economy provider;

    public void hook() {
        provider = MoonerEco.plugin.EcoManager;
        Bukkit.getServicesManager().register(Economy.class, this.provider, MoonerEco.plugin, ServicePriority.Highest);
        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Vault API Hooked Complete. " + ChatColor.AQUA + MoonerEco.plugin.getName());
    }

    public void unhook() {
        Bukkit.getServicesManager().unregister(Economy.class, this.provider);
        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Vault API Unhooked Complete. " + ChatColor.AQUA + MoonerEco.plugin.getName());
    }
}