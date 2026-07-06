package com.walrusone.skywarsreloaded.commands.admin;

import com.walrusone.skywarsreloaded.SkyWarsReloaded;
import com.walrusone.skywarsreloaded.api.event.SkyWarsReloadEvent;
import com.walrusone.skywarsreloaded.api.event.SkyWarsReloadPreLoadEvent;
import com.walrusone.skywarsreloaded.utilities.Messaging;
import com.walrusone.skywarsreloaded.utilities.SWRServer;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ReloadCmd extends com.walrusone.skywarsreloaded.commands.BaseCmd {
    public ReloadCmd(SkyWarsReloaded plugin, String t) {
        super(plugin);
        type = t;
        forcePlayer = false;
        cmdName = "reload";
        alias = new String[]{"r"};
        argLength = 1;
    }

    public boolean run(CommandSender sender, Player player, String[] args) {
        SkyWarsReloaded.get().onDisable();
        Bukkit.getPluginManager().callEvent(new SkyWarsReloadPreLoadEvent());
        SkyWarsReloaded.get().load();

        if (SkyWarsReloaded.getCfg().bungeeMode() && SkyWarsReloaded.getCfg().isLobbyServer()) {
            SkyWarsReloaded.get().prepareServers();

            SWRServer.updateServerSigns();
        }

        sender.sendMessage(new Messaging.MessageFormatter().format("command.reload"));
        Bukkit.getPluginManager().callEvent(new SkyWarsReloadEvent());
        return true;
    }
}
