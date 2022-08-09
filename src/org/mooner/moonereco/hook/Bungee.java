package org.mooner.moonereco.hook;

import org.bukkit.entity.Player;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

import static org.mooner.moonereco.MoonerEco.plugin;

public class Bungee {
    public static void sendBungeeMessage(String player, String message) {
        try {
            ByteArrayOutputStream b = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(b);

            out.writeUTF("Message");
            out.writeUTF(player);
            out.writeUTF(message);

            if (!plugin.getServer().getOnlinePlayers().isEmpty()) {
                plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
                    Player p = plugin.getServer().getOnlinePlayers().iterator().next();
                    p.sendPluginMessage(plugin, "BungeeCord", b.toByteArray());
                });
            }
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to send BungeeCord message");
        }
    }
}
