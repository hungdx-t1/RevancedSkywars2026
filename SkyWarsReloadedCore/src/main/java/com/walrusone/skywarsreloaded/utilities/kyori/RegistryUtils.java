package com.walrusone.skywarsreloaded.utilities.kyori;

import org.bukkit.GameRule;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Locale;

@SuppressWarnings("unused")
public class RegistryUtils {

    /**
     * Tra cứu động thực thể GameRule từ Registry hệ thống thông qua chuỗi String.
     * Tự động xử lý dọn chuỗi đưa về dạng snake_case chữ thường an toàn.
     */
    @Nullable
    public static GameRule<?> getGameRuleOfString(@NonNull String rawGameRule) {
        NamespacedKey key = NamespacedKey.fromString(rawGameRule.toLowerCase(Locale.ROOT));
        return key != null ? Registry.GAME_RULE.get(key) : null;
    }
}
