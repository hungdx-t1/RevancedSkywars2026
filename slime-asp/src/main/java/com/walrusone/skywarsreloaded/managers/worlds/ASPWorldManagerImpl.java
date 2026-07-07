package com.walrusone.skywarsreloaded.managers.worlds;

import com.infernalsuite.asp.api.AdvancedSlimePaperAPI;
import com.infernalsuite.asp.api.loaders.SlimeLoader;
import com.infernalsuite.asp.api.world.SlimeWorld;
import com.infernalsuite.asp.api.world.properties.SlimeProperties;
import com.infernalsuite.asp.api.world.properties.SlimePropertyMap;
import com.infernalsuite.asp.loaders.file.FileLoader;
import com.walrusone.skywarsreloaded.SkyWarsReloaded;

import com.walrusone.skywarsreloaded.api.enums.worldmanager.WorldManagerType;
import org.bukkit.*;
import org.bukkit.entity.SpawnCategory;
import org.bukkit.event.world.WorldLoadEvent;

import java.io.File;
import java.util.logging.Level;

@SuppressWarnings({"unused"})
public class ASPWorldManagerImpl implements ASPWorldManager {
    private final AdvancedSlimePaperAPI slime;
    private final SlimeLoader loader;

    public ASPWorldManagerImpl() {
        this.slime = AdvancedSlimePaperAPI.instance();
        this.loader = new FileLoader(new File("slime_worlds"));
    }

    @SuppressWarnings("UnstableApiUsage")
    @Override
    public World createEmptyWorld(String name, World.Environment environment) {
        SlimePropertyMap propertyMap = new SlimePropertyMap();
        propertyMap.setValue(SlimeProperties.SPAWN_X, 0);
        propertyMap.setValue(SlimeProperties.SPAWN_Y, 64);
        propertyMap.setValue(SlimeProperties.SPAWN_Z, 0);
        propertyMap.setValue(SlimeProperties.ENVIRONMENT, environment.name().toLowerCase());
        propertyMap.setValue(SlimeProperties.DIFFICULTY, "normal");

        try {
            SlimeWorld slimeWorld = slime.createEmptyWorld(name, false, propertyMap, loader);
            slime.loadWorld(slimeWorld, true);

            World bukkitWorld = Bukkit.getWorld(name);
            if (bukkitWorld == null) return null;

            Bukkit.getPluginManager().callEvent(new WorldLoadEvent(bukkitWorld));

            Location location = new Location(bukkitWorld, 0, 61, 0);
            location.getBlock().setType(Material.BEDROCK);

            this.setWorldSettings(bukkitWorld);
            return bukkitWorld;
        } catch (Exception e) {
            SkyWarsReloaded.get().getLogger().log(Level.SEVERE, "Không thể tạo thế giới trống qua ASP cho: " + name, e);
        }
        return null;
    }

    @Override
    public boolean loadWorld(String worldName, World.Environment environment, boolean readOnly) {
        // Kiểm tra xem thế giới đã nằm trong bộ nhớ RAM chưa
        World world = Bukkit.getWorld(worldName);
        if (world != null) {
            this.setWorldSettings(world);
            SkyWarsReloaded.get().getServer().unloadWorld(world, false);
        }

        try {
            // Đọc trực tiếp map từ bộ nạp lưu trữ của ASP
            if (!loader.worldExists(worldName)) { // không còn là completablefuture nữa, dùng thẳng bool
                SkyWarsReloaded.get().getLogger().severe("Bản đồ SkyWars \"" + worldName + "\" không tồn tại trong bộ lưu trữ ASP!");
                return false;
            }

            // Tạo map property mặc định để nạp
            SlimePropertyMap propertyMap = new SlimePropertyMap();
            propertyMap.setValue(SlimeProperties.ENVIRONMENT, environment.name().toLowerCase());

            SlimeWorld slimeWorld = slime.readWorld(loader, worldName, readOnly, propertyMap);
            slime.loadWorld(slimeWorld, true);

            world = Bukkit.getWorld(worldName);
            if (world != null) {
                this.setWorldSettings(world);
                return true;
            }
        } catch (Exception e) {
            SkyWarsReloaded.get().getLogger().log(Level.SEVERE, "Lỗi nghiêm trọng khi nạp thế giới ASP: " + worldName, e);
        }
        return false;
    }

    @Override
    public void unloadWorld(String worldName, boolean shouldSave) {
        World world = Bukkit.getWorld(worldName);
        if (world == null) return;

        try {
            // ASP v4 tự động xử lý lưu trữ an toàn thông qua Bukkit.unloadWorld
            // Nếu thế giới được đánh dấu cứu dữ liệu, ASP sẽ tự bắt event và ghi đĩa bất đồng bộ
            Bukkit.unloadWorld(world, shouldSave);
        } catch (Exception e) {
            SkyWarsReloaded.get().getLogger().log(Level.SEVERE, "Lỗi khi unload thế giới ASP: " + worldName, e);
        }
    }

    @Override
    public WorldManagerType getType() {
        return WorldManagerType.ASWM;
    }

    @Override
    public void copyWorld(File source, File target) { }

    @Override
    public void deleteWorld(String name, boolean removeFile) {
        unloadWorld(name, false);
        if (removeFile) {
            try {
                loader.deleteWorld(name);
            } catch (Exception e) {
                SkyWarsReloaded.get().getLogger().log(Level.SEVERE, "Không thể xóa file thế giới ASP của đấu trường: " + name, e);
            }
        }
    }

    @Override
    public void deleteWorld(File file) { }

    private void setWorldSettings(World world) {
        world.setSpawnFlags(true, true);
        world.setStorm(false);
        world.setThundering(false);
        world.setWeatherDuration(Integer.MAX_VALUE);
        world.setGameRule(GameRules.RESPAWN_RADIUS, 0);
        world.setGameRule(GameRules.PVP, true);
        world.setGameRule(GameRules.FIRE_SPREAD_RADIUS_AROUND_PLAYER, 0);
        world.setTicksPerSpawns(SpawnCategory.ANIMAL, 1);
        world.setTicksPerSpawns(SpawnCategory.MONSTER, 1);
        world.setAutoSave(false);

        SkyWarsReloaded.getNMS().setGameRule(world, "spawn_mobs", "false"); // old: doMobSpawning
        SkyWarsReloaded.getNMS().setGameRule(world, "mob_griefing", "false"); // old: mobGriefing
        SkyWarsReloaded.getNMS().setGameRule(world, "fire_spread_radius_around_player", "false"); // old: doFireTick
        SkyWarsReloaded.getNMS().setGameRule(world, "show_death_messages", "false"); // old: showDeathMessages
        SkyWarsReloaded.getNMS().setGameRule(world, "show_advancement_messages", "false"); // old: announceAdvancements
        SkyWarsReloaded.getNMS().setGameRule(world, "advance_time", "false"); // old: doDaylightCycle
    }
}
