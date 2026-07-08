package com.walrusone.skywarsreloaded.utilities.kyori;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;

@SuppressWarnings("unused")
public class RegistryUtils {
    private static final Logger logger = LoggerFactory.getLogger(RegistryUtils.class);
    private static final RegistryAccess registryAccess;

    static {
        registryAccess = RegistryAccess.registryAccess();
    }

    @Nullable
    public static GameRule<?> getGameRuleOfString(@NonNull String rawGameRule) {
        NamespacedKey key = NamespacedKey.fromString(rawGameRule.toLowerCase(Locale.ROOT));
        return key != null ? Registry.GAME_RULE.get(key) : null;
    }

    @Nullable
    public static Sound getSoundOfString(@NonNull String rawSound) {
        NamespacedKey key = NamespacedKey.fromString(rawSound.toLowerCase(Locale.ROOT));
        return key != null ? Registry.SOUND_EVENT.get(key) : null;
    }

    @Nullable
    public static Material getMaterialOfString(@NonNull String rawMaterial) {
        return Material.matchMaterial(rawMaterial);
    }

    @Nullable
    public static PotionEffectType getPotionEffectTypeOfString(@NonNull String rawEffect) {
        NamespacedKey key = NamespacedKey.fromString(rawEffect.toLowerCase(Locale.ROOT));
        return key != null ? Registry.EFFECT.get(key) : null;
    }

    @Nullable
    public static Enchantment getEnchantmentOfString(@NonNull String rawEnchant) {
        NamespacedKey key = NamespacedKey.fromString(rawEnchant.toLowerCase(Locale.ROOT));
        return key != null ? registryAccess.getRegistry(RegistryKey.ENCHANTMENT).get(key) : null;
    }

    /**
     *  Mẹo: Sử dụng chính xác material list từ <a href="https://jd.papermc.io/paper/26.1.2/org/bukkit/Material.html">đây</a>
     */
    @NonNull
    public static ItemStack createSingletonItem(@NonNull String rawMaterial) {
        Material material = getMaterialOfString(rawMaterial);
        if (material == null) return new ItemStack(Material.AIR, 1);
        return new ItemStack(material, 1);
    }

    /**
     * @deprecated Xem {@link RegistryUtils#createSingletonItem(String)}
     */
    @NonNull
    @Deprecated
    public static ItemStack getFallbackLegacyMaterial(@NonNull String item) {
        Material mat = getMaterialOfString(item);
        if (mat != null) return new ItemStack(mat, 1);

        return switch (item.toUpperCase(Locale.ROOT)) {
            case "SKULL_ITEM" -> new ItemStack(Material.SKELETON_SKULL, 1);
            case "ENDER_PORTAL_FRAME" -> new ItemStack(Material.END_PORTAL_FRAME, 1);
            case "WORKBENCH" -> new ItemStack(Material.CRAFTING_TABLE, 1);
            case "IRON_FENCE" -> new ItemStack(Material.IRON_BARS, 1);
            case "REDSTONE_COMPARATOR" -> new ItemStack(Material.COMPARATOR, 1);
            case "SIGN_POST" -> new ItemStack(Material.BIRCH_SIGN, 1);
            case "STONE_PLATE" -> new ItemStack(Material.STONE_PRESSURE_PLATE, 1);
            case "IRON_PLATE" -> new ItemStack(Material.HEAVY_WEIGHTED_PRESSURE_PLATE, 1);
            case "GOLD_PLATE" -> new ItemStack(Material.LIGHT_WEIGHTED_PRESSURE_PLATE, 1);
            case "MOB_SPAWNER" -> new ItemStack(Material.SPAWNER, 1);
            case "SNOW_BALL" -> new ItemStack(Material.SNOWBALL, 1);
            default -> new ItemStack(Material.AIR, 1);
        };
    }
}
