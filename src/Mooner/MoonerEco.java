package Mooner;

import Mooner.API.EcoAPI;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import static Mooner.Utils.chat;

public class MoonerEco extends JavaPlugin implements Listener {

    public static MoonerEco plugin;
    public EcoManager EcoManager;
    public EcoHook vaultEcoHook;

    public static final String dataPath = "plugins/MoonerEco/";

    @Override
    public void onEnable() {
        plugin=this;
        EcoManager = new EcoManager();
        vaultEcoHook = new EcoHook();
        vaultEcoHook.hook();
        EcoAPI.init = new EcoAPI();

        Bukkit.getConsoleSender().sendMessage(chat("&bPlugin Enabled! &7- &6MoonerEco"));
        Bukkit.getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        vaultEcoHook.unhook();
        Bukkit.getConsoleSender().sendMessage(chat("&bPlugin Disabled! &7- &6MoonerEco"));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equals("money")) {
            return true;
        }
        return false;
    }
}
