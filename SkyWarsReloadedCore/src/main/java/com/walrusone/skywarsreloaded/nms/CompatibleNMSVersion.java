package com.walrusone.skywarsreloaded.nms;

import com.walrusone.skywarsreloaded.SkyWarsReloaded;
import org.bukkit.Bukkit;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum CompatibleNMSVersion {
    v1_21_R7("v1_21_R7",List.of("1.21.10", "1.21.11")),
    v26_1("v26_1", List.of("26.1", "26.1.2")),
    v26_2("v26_2", List.of("26.2")),
    ;

    private final String nmsImplVersion;
    private final List<String> bukkitVersion;

    CompatibleNMSVersion(String nmsImplVersion, List<String> bukkitVersion) {
        this.nmsImplVersion = nmsImplVersion;
        this.bukkitVersion = bukkitVersion;
    }

    public String getNmsImplVersion() {
        return nmsImplVersion;
    }

    public List<String> getBukkitVersions() {
        return bukkitVersion;
    }

    public static NMS loadNMS(SkyWarsReloaded plugin) {
        Logger logger = plugin.getLogger();

        // Lấy chuỗi Bukkit Version (Ví dụ: "1.21.11-R0.1-SNAPSHOT" hoặc "26.1.2-R0.1-SNAPSHOT")
        String rawBukkitVersion = Bukkit.getBukkitVersion();
        String mcVersion = rawBukkitVersion;

        // cắt bỏ phần đuôi thừa
        Pattern pattern = Pattern.compile("^([0-9.]+)");
        Matcher matcher = pattern.matcher(rawBukkitVersion);
        if (matcher.find()) {
            mcVersion = matcher.group(1);
        }

        CompatibleNMSVersion selectedNMSVersion = null;
        for (CompatibleNMSVersion key : CompatibleNMSVersion.values()) {
            if (key.getBukkitVersions().contains(mcVersion)) {
                selectedNMSVersion = key;
                break;
            }
        }

        logger.info("Trying to load NMS support for server version '" + mcVersion + "'...");
        logger.warning("Đang sử dụng phiên bản " + Bukkit.getVersion() + ".");
        logger.warning("Plugin này hiện đang là bản thử nghiệm. Mọi bug hoặc lỗi xảy ra vui lòng tạo ticket để được xử lý.");

        if (selectedNMSVersion == null) {
            logger.severe("Bạn đang dùng phiên bản server không được support. Plugin này chỉ support từ 1.21.11 đến 26.2");
            return null;
        }

        logger.info("Detected NMS Version " + selectedNMSVersion.toString().toLowerCase());
        try {
            String nmsImplVersionStr = selectedNMSVersion.getNmsImplVersion();
            final Class<?> clazz = Class.forName("com.walrusone.skywarsreloaded.nms." + nmsImplVersionStr + ".NMSHandler");
            if (NMS.class.isAssignableFrom(clazz)) {
                logger.info("Loaded support for NMS server version " + mcVersion + " using handler: " + nmsImplVersionStr + ".");
                return (NMS) clazz.getConstructor().newInstance();
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, "Failed to load NMS support for server version " + mcVersion + ". Please report this issue.", e);
        }

        logger.severe("Bạn đang dùng phiên bản server không được support. Plugin này chỉ support 1.21.11 đến 26.2.");
        return null;
    }
}
