package com.walrusone.skywarsreloaded.utilities;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class JSONMessage {
    /**
     * Tạo tin nhắn click để dịch chuyển đến vị trí biển báo đấu trường
     */
    public static void generateArenaMenuMessage(World world, int i, Block block, Player player) {
        Location loc = block.getLocation();
        int x = loc.getBlockX();
        int y = loc.getBlockY();
        int z = loc.getBlockZ();

        Component message = Component.text("Sign " + i + ": " + world.getName() + " - " + x + ", " + y + ", " + z)
                .color(NamedTextColor.GOLD)
                .hoverEvent(HoverEvent.showText(Component.text("Click to teleport", NamedTextColor.GOLD)))
                .clickEvent(ClickEvent.runCommand("/teleport " + x + " " + y + " " + z));
        player.sendMessage(message);
    }

    /**
     * Tạo tin nhắn mời vào Party, click để đồng ý gia nhập
     */
    public static void generateInviteMessage(Player invited) {
        String rawText = new Messaging.MessageFormatter().format("party.clicktoaccept");

        Component inviteText = LegacyComponentSerializer.legacyAmpersand().deserialize(rawText);
        Component message = inviteText
                .color(NamedTextColor.AQUA)
                .hoverEvent(HoverEvent.showText(inviteText.color(NamedTextColor.GOLD)))
                .clickEvent(ClickEvent.runCommand("/swp a"));
        invited.sendMessage(message);
    }
}