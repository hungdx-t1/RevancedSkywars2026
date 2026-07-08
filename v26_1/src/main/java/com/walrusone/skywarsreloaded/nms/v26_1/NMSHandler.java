package com.walrusone.skywarsreloaded.nms.v26_1;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.walrusone.skywarsreloaded.SkyWarsReloaded;
import com.walrusone.skywarsreloaded.game.signs.SWRSign;
import com.walrusone.skywarsreloaded.nms.NMS;
import com.walrusone.skywarsreloaded.utilities.kyori.RegistryUtils;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ResolvableProfile;
import io.papermc.paper.datacomponent.item.TooltipDisplay;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.kyori.adventure.title.Title;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.block.Block;
import org.bukkit.block.EnderChest;
import org.bukkit.block.Skull;
import org.bukkit.block.data.BlockData;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.WorldInfo;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

// v26_1 (26.1 hoặc 26.1.2)
@SuppressWarnings("unused")
public class NMSHandler implements NMS {
    @Override
    public void playChestAction(Block block, boolean open) {
        Location location = block.getLocation();
        if (location.getWorld() == null) return;
        if (!(block.getState() instanceof EnderChest enderChest)) return;
        if (open) enderChest.open(); else enderChest.close();
    }

    @Override
    public PotionEffectType getPotionEffectTypeByName(String... name) {
        for (String n : name) {
            PotionEffectType type = RegistryUtils.getPotionEffectTypeOfString(n);
            if (type != null) return type;
        }
        return null;
    }

    @Override
    public Enchantment getEnchantmentByName(String... name) {
        for (String n : name) {
            Enchantment enchantment = RegistryUtils.getEnchantmentOfString(n);
            if (enchantment != null) return enchantment;
        }
        return null;
    }

    public void spawnDragon(World world, Location loc) {
        EnderDragon dragon = (EnderDragon) world.spawnEntity(loc, EntityType.ENDER_DRAGON);
        dragon.setPhase(EnderDragon.Phase.CHARGE_PLAYER);
        Location locClone = loc.clone();
        locClone.setYaw(ThreadLocalRandom.current().nextFloat() * 360.0F);
        locClone.setPitch(0.0F);
        dragon.teleport(locClone);
    }

    public void setEntityTarget(Entity bukkitEntity, Player player) {
        if (bukkitEntity instanceof Creature creature) {
            creature.setTarget(player);
        }
    }

    public void sendActionBar(Player player, String msg) {
        Component actionBarComponent = LegacyComponentSerializer.legacyAmpersand().deserialize(msg);
        player.sendActionBar(actionBarComponent);
    }

    public void setMaxHealth(Player player, int health) {
        AttributeInstance attr = player.getAttribute(Attribute.MAX_HEALTH);
        if (attr != null) {
            attr.setBaseValue(health);
        }
    }

    public ChunkGenerator getChunkGenerator() {
        return new ChunkGenerator() {
            // Ghi đè hàm generateNoise() hiện đại và để trống hoàn toàn để tạo ra bản đồ trống rỗng (Void) siêu tốc.
            @Override
            public void generateNoise(@NotNull WorldInfo worldInfo, @NotNull Random random, int chunkX, int chunkZ, @NotNull ChunkData chunkData) {
                // Để trống hoàn toàn: Không đặt khối block nào vào thế giới, Paper sẽ tự hiểu đây là Void Map.
            }
        };
    }

    public boolean checkMaterial(FallingBlock fb, Material mat) {
        return fb.getBlockData().getMaterial().equals(mat);
    }

    @Override
    public SWRSign createSWRSign(String name, Location location) {
        return new com.walrusone.skywarsreloaded.nms.v26_1.SWRSign(name, location);
    }

    @Override
    public void applyTotemEffect(Player player) {
        PlayerInventory pInv = player.getInventory();
        ItemStack mainHand = pInv.getItemInMainHand();
        ItemStack offHand = pInv.getItemInOffHand();

        // Consume item
        if (mainHand.getType().equals(Material.TOTEM_OF_UNDYING)) {
            pInv.setItemInMainHand(new ItemStack(Material.AIR));
        } else if (offHand.getType().equals(Material.TOTEM_OF_UNDYING)) {
            pInv.setItemInOffHand(new ItemStack(Material.AIR));
        }

        // Apply potion effects (version specific)
        player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 20 * 40, 0, false, true));

        // TOTEM_RESSURECT deprecated, use PROTECTED_FROM_DEATH instead
        player.sendEntityEffect(EntityEffect.PROTECTED_FROM_DEATH, player); // On screen effect
        // Particles
        new BukkitRunnable() {
            byte count = 0;

            @Override
            public void run() {
                if (count > 30) {
                    this.cancel();
                    return;
                } else {
                    count++;
                }
                player.getWorld().spawnParticle(Particle.TOTEM_OF_UNDYING, player.getLocation(), 10, 0.1, 0.1, 0.1, 0.5);
            }
        }.runTaskTimer(SkyWarsReloaded.get(), 0, 1);
    }

    @SuppressWarnings("DeprecatedIsStillUsed")
    @Deprecated
    private String getColorFromByte(byte color) {
        return switch (color) {
            case 1 -> "ORANGE";
            case 2 -> "MAGENTA";
            case 3 -> "LIGHT_BLUE";
            case 4 -> "YELLOW";
            case 5 -> "LIME";
            case 6 -> "PINK";
            case 7 -> "GRAY";
            case 8 -> "LIGHT_GRAY";
            case 9 -> "CYAN";
            case 10 -> "PURPLE";
            case 11 -> "BLUE";
            case 12 -> "BROWN";
            case 13 -> "GREEN";
            case 14 -> "RED";
            case 16 -> "BLACK";
            default -> "WHITE"; // also, case 0 will support this
        };
    }

    @SuppressWarnings("UnstableApiUsage") // api này còn mới, mới add hồi 1.21.9 nên dùng SuppressWarnings để xóa warning
    public void updateSkull(Skull skull, UUID uuid) {
        PlayerProfile profile = Bukkit.createProfile(uuid);
        ResolvableProfile resolvableProfile = ResolvableProfile.resolvableProfile(profile);

        if (skull.getType().equals(Material.SKELETON_SKULL)) {
            Block block = skull.getBlock();
            block.setType(Material.PLAYER_HEAD);
            Skull s = (Skull) block.getState();
            s.setProfile(resolvableProfile);
        } else {
            skull.setProfile(resolvableProfile);
            skull.update(true, false);
        }
    }

    public Entity spawnFallingBlock(Location loc, Material mat, boolean damage) {
        if (loc.getWorld() == null) return null;
        BlockData blockData = Bukkit.createBlockData(mat);
        return loc.getWorld().spawn(loc, FallingBlock.class, fallingBlock -> {
            fallingBlock.setBlockData(blockData);
            fallingBlock.setDropItem(false);
            fallingBlock.setHurtEntities(damage);
        });
    }

    public void updateSkull(SkullMeta skullMeta, Player player) {
        skullMeta.setOwningPlayer(player);
    }

    @SuppressWarnings({"unchecked"})
    public void setGameRule(World world, String ruleName, String value) {
        // Handle bools
        Boolean valueBool = null;
        if (value.equalsIgnoreCase("true")) valueBool = true;
        else if (value.equalsIgnoreCase("false")) valueBool = false;

        // Handle ints
        Integer valueInt = null;
        if (valueBool == null) {
            try {
                valueInt = Integer.parseInt(value);
            } catch (Exception ignored) { }
        }

        // Apply
        try {
            GameRule<?> rawRule = RegistryUtils.getGameRuleOfString(ruleName);
            if (rawRule == null) throw new Exception("Invalid GameRule: " + ruleName);

            if (valueBool == null) {
                if(valueInt == null) throw new Exception("Invalid GameRule or value provided: " + ruleName + " -> " + value);
                GameRule<Integer> gameRule = (GameRule<Integer>) rawRule;
                world.setGameRule(gameRule, valueInt);
            } else {
                GameRule<Boolean> gameRule = (GameRule<Boolean>) rawRule;
                world.setGameRule(gameRule, valueBool);
            }
        } catch (Exception ex) {
            getLogger().error("Error setting GameRule: {} with value: {}", ruleName, value, ex);
        }
    }

    public boolean headCheck(Block h1) {
        return (h1.getType() == Material.valueOf("PLAYER_WALL_HEAD"))
                || (h1.getType() == Material.valueOf("PLAYER_HEAD"))
                || (h1.getType() == Material.valueOf("SKELETON_SKULL"));
    }

    public ItemStack getBlankPlayerHead() {
        return new ItemStack(Material.PLAYER_HEAD, 1);
    }

    public ItemStack getColorItem(String mat, byte color) {
        String col = getColorFromByte(color);
        if (mat.equalsIgnoreCase("wool"))
            return new ItemStack(Material.valueOf(col + "_WOOL"), 1);
        if (mat.equalsIgnoreCase("stained_glass"))
            return new ItemStack(Material.valueOf(col + "_STAINED_GLASS"), 1);
        if (mat.equalsIgnoreCase("banner")) {
            return new ItemStack(Material.valueOf(col + "_BANNER"), 1);
        }
        return new ItemStack(Material.valueOf(col + "_STAINED_GLASS"), 1);
    }

    public void setBlockWithColor(World world, int x, int y, int z, Material mat, byte cByte) {
        world.getBlockAt(x, y, z).setType(mat);
    }

    @Override
    public Objective getNewObjective(Scoreboard scoreboard, String rawCriteria, String DisplayName) {
        Criteria criteria = Criteria.create(rawCriteria);
        Component titleComponent = LegacyComponentSerializer.legacyAmpersand().deserialize(DisplayName);
        return scoreboard.registerNewObjective(DisplayName, criteria, titleComponent);
    }

    @Override
    public boolean isHoldingTotem(Player player) {
        return player.getInventory().getItemInMainHand().getType().equals(Material.TOTEM_OF_UNDYING) ||
                player.getInventory().getItemInOffHand().getType().equals(Material.TOTEM_OF_UNDYING);
    }

    public Block getHitBlock(ProjectileHitEvent event) {
        return event.getHitBlock();
    }

    public void sendTitle(Player player, int fadein, int stay, int fadeout, String title, String subtitle) {
        String displayName = PlainTextComponentSerializer.plainText().serialize(player.displayName());

        Component mainTitle = title != null
                ? LegacyComponentSerializer.legacyAmpersand().deserialize(title.replaceAll("%player%", displayName))
                : Component.empty();

        Component subTitle = subtitle != null
                ? LegacyComponentSerializer.legacyAmpersand().deserialize(subtitle.replaceAll("%player%", displayName))
                : Component.empty();

        Title.Times times = Title.Times.times(
                Duration.ofMillis(fadein * 50L),
                Duration.ofMillis(stay * 50L),
                Duration.ofMillis(fadeout * 50L)
        );

        player.showTitle(Title.title(mainTitle, subTitle, times));
    }

    public void playGameSound(Location loc, String paramEnumName, String paramCategory, float paramVolume, float paramPitch, boolean paramIsCustom) {
        if (loc.getWorld() == null) return;
        SoundCategory soundCateg = paramCategory == null ? SoundCategory.MASTER : SoundCategory.valueOf(paramCategory);
        if (paramIsCustom) {
            loc.getWorld().playSound(loc, paramEnumName, soundCateg, paramVolume, paramPitch);
        } else {
            Sound sound = RegistryUtils.getSoundOfString(paramEnumName);
            if (sound != null) {
                loc.getWorld().playSound(loc, sound, soundCateg, paramVolume, paramPitch);
            }
        }
    }

    public void sendParticles(World world, String type, float x, float y, float z, float offsetX, float offsetY, float offsetZ, float data, int amount) {
        world.spawnParticle(Particle.valueOf(type), x, y, z, amount, offsetX, offsetY, offsetZ, data);
    }

    public ItemStack getMainHandItem(Player player) {
        return player.getInventory().getItemInMainHand();
    }

    public ItemStack getOffHandItem(Player player) {
        return player.getInventory().getItemInOffHand();
    }

    public boolean isValueParticle(String string) {
        try {
            Particle.valueOf(string);
        } catch (IllegalArgumentException e) {
            return false;
        }
        return true;
    }

    @Override
    public void sendJSON(Player sender, String json) {
        Component jsonComponent = GsonComponentSerializer.gson().deserialize(json);
        sender.sendMessage(jsonComponent);
    }

    public boolean removeFromScoreboardCollection(Scoreboard scoreboard) {
        return false;
    }

    public void respawnPlayer(Player player) {
        player.spigot().respawn();
    }

    public FireworkEffect getFireworkEffect(Color one, Color two, Color three, Color four, Color five, FireworkEffect.Type type) {
        return FireworkEffect.builder().flicker(false).withColor(one, two, three, four).withFade(five).with(type).trail(true).build();
    }

    public String getItemName(ItemStack item) {
        if (item != null) {
            ItemMeta meta = item.getItemMeta();
            if (meta != null && meta.hasDisplayName() && meta.displayName() != null) {
                Component displayName = meta.displayName();
                if(displayName != null) {
                    return LegacyComponentSerializer.legacyAmpersand().serialize(displayName);
                }
            }
            return item.getType().name();
        }
        return "";
    }

    public ItemStack getItemStack(Material material, List<String> lore, String message) {
        ItemStack addItem = new ItemStack(material, 1);
        ItemMeta addItemMeta = addItem.getItemMeta();
        if (addItemMeta != null) {
            addItemMeta.displayName(LegacyComponentSerializer.legacyAmpersand().deserialize(message));
            if (lore != null) {
                List<Component> componentLore = lore.stream()
                        .map(line -> (Component) LegacyComponentSerializer.legacyAmpersand().deserialize(line))
                        .toList();
                addItemMeta.lore(componentLore);
            }
            addItemMeta.addItemFlags(ItemFlag.values());
            addItem.setItemMeta(addItemMeta);
        }
        return addItem;
    }

    @SuppressWarnings("UnstableApiUsage")
    public ItemStack getItemStack(ItemStack item, List<String> lore, String message) {
        ItemStack addItem = item.clone();
        ItemMeta addItemMeta = addItem.getItemMeta();
        if (addItemMeta != null) {
            addItemMeta.displayName(LegacyComponentSerializer.legacyAmpersand().deserialize(message));
            if (lore != null) {
                List<Component> componentLore = lore.stream()
                        .map(line -> (Component) LegacyComponentSerializer.legacyAmpersand().deserialize(line))
                        .toList();
                addItemMeta.lore(componentLore);
            }
            addItemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            addItem.setData(DataComponentTypes.TOOLTIP_DISPLAY, TooltipDisplay.tooltipDisplay().hideTooltip(true).build());
            addItem.setItemMeta(addItemMeta);
        }
        return addItem;
    }

    public void deleteCache() { /* empty */ }
}
