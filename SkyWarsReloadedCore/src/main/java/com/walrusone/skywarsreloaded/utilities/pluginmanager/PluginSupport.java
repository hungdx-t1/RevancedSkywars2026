package com.walrusone.skywarsreloaded.utilities.pluginmanager;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("unused")
public final class PluginSupport {
    private static final Logger logger = LoggerFactory.getLogger(PluginSupport.class);

    private static boolean hasVault;
    private static boolean hasDecentHolograms;
    private static boolean hasAdvancedSlimePaper;
    private static boolean hasPlaceholderAPI;
    private static boolean setup = false;

    public static void initialize() {
        if (setup) return;

        PluginManager pluginManager = Bukkit.getPluginManager();

        // 1. Kiểm tra Vault Economy
        hasVault = pluginManager.isPluginEnabled("Vault");
        if (hasVault) {
            logger.info("Tìm thấy plugin liên kết hệ thống kinh tế: Vault.");
        } else {
            logger.warn("Không tìm thấy Vault. Các tính năng phần thưởng tiền tệ sẽ bị bỏ qua.");
        }

        // 2. Kiểm tra DecentHolograms đời mới (Gộp từ nhánh tính năng của bạn)
        hasDecentHolograms = pluginManager.isPluginEnabled("DecentHolograms");
        if (hasDecentHolograms) {
            logger.info("Tìm thấy hệ thống Hologram đời mới: DecentHolograms.");
        } else {
            logger.warn("Không tìm thấy DecentHolograms. Bảng xếp hạng Hologram sẽ bị tắt.");
        }

        // 3. Kiểm tra Advanced Slime Paper (Chuẩn v4 đóng gói mới cho bản 26.1.2)
        hasAdvancedSlimePaper = tryToGetAdvancedSlimePaper(pluginManager);
        if (hasAdvancedSlimePaper) {
            logger.info("Tìm thấy hệ thống nén map nâng cao: AdvancedSlimePaper (ASPaperPlugin).");
        } else {
            logger.warn("Không tìm thấy ASPaperPlugin. Hệ thống sẽ tự động dùng bộ nạp thế giới dạng File truyền thống.");
        }

        hasPlaceholderAPI = pluginManager.isPluginEnabled("PlaceholderAPI");
        if (hasPlaceholderAPI) {
            logger.info("Thấy PlaceholderAPI. Đang kết nối...");
        } else {
            logger.warn("Không tìm thấy plugin PlaceholderAPI. Đang bỏ qua...");
        }

        setup = true;
    }

    private static boolean tryToGetAdvancedSlimePaper(PluginManager pluginManager) {
        try {
            Class.forName("com.infernalsuite.asp.api.AdvancedSlimePaperAPI"); // v4.0
            return pluginManager.isPluginEnabled("ASPaperPlugin");
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public static boolean isHasDecentHolograms() {
        return hasDecentHolograms;
    }

    public static boolean isHasVault() {
        return hasVault;
    }

    public static boolean isHasSlimeWorldPlugin() {
        return hasAdvancedSlimePaper;
    }

    public static boolean isHasPlaceholderAPI() {
        return hasPlaceholderAPI;
    }

    public static boolean isSetup() {
        return setup;
    }
}
