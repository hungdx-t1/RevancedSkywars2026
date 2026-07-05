package com.walrusone.skywarsreloaded.nms.v1_21_R7;

import com.walrusone.skywarsreloaded.SkyWarsReloaded;
import com.walrusone.skywarsreloaded.game.signs.SWRSign;
import com.walrusone.skywarsreloaded.nms.NMS;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.chat.ComponentSerializer;
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

import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

// v1_21_R7 (1.21.10 và 1.21.11)
@SuppressWarnings("unused")
public class NMSHandler implements NMS {

    @Override
    public void playChestAction(Block block, boolean open) {
        Location location = block.getLocation();
        if (location.getWorld() == null) return;
        if (!(block.getState() instanceof EnderChest enderChest)) return;

        if (open) enderChest.open();
        else enderChest.close();
    }

    @Override
    public PotionEffectType getPotionEffectTypeByName(String... name) {
        for (String n : name) {
            try {
                NamespacedKey key = NamespacedKey.fromString(n.toLowerCase(Locale.ROOT));
                if(key != null) {
                    PotionEffectType type = Registry.EFFECT.get(key);
                    if (type != null) return type;
                }
            } catch (Exception ignored) { }
        }
        return null;
    }

    @Override
    public Enchantment getEnchantmentByName(String... name) {
        for (String n : name) {
            try {
                NamespacedKey key = NamespacedKey.fromString(n.toLowerCase(Locale.ROOT));
                if (key != null) {
                    Enchantment enchantment = Registry.ENCHANTMENT.get(key);
                    if (enchantment != null) return enchantment;
                }
            } catch (Exception ignored) {}
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
        msg = ChatColor.translateAlternateColorCodes('&', msg);
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new ComponentBuilder(msg).create());
    }

    public void setMaxHealth(Player player, int health) {
        AttributeInstance attr = player.getAttribute(Attribute.MAX_HEALTH);
        if (attr != null) {
            attr.setBaseValue(health);
        }
    }

    @SuppressWarnings("deprecation")
    public ChunkGenerator getChunkGenerator() {
        return new ChunkGenerator() {
            @Override
            public @NotNull ChunkData generateChunkData(@NotNull World world, @NotNull Random random, int chunkX, int chunkZ, @NotNull BiomeGrid biome) {
                // Chỉ cần trả về một ChunkData trống hoàn toàn, không chứa block nào để tạo Void World
                return Bukkit.getServer().createChunkData(world);
            }
        };
    }

    public boolean checkMaterial(FallingBlock fb, Material mat) {
        return fb.getBlockData().getMaterial().equals(mat);
    }

    @Override
    public SWRSign createSWRSign(String name, Location location) {
        return new com.walrusone.skywarsreloaded.nms.v1_21_R7.SWRSign(name, location);
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
        // On screen effect
        player.playEffect(EntityEffect.TOTEM_RESURRECT);
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

    public void updateSkull(Skull skull, UUID uuid) {
        if (skull.getType().equals(Material.SKELETON_SKULL)) {
            Block block = skull.getBlock();
            block.setType(Material.PLAYER_HEAD);
            Skull s = (Skull) block.getState();
            s.setOwningPlayer(Bukkit.getOfflinePlayer(uuid));
        } else {
            skull.setOwningPlayer(Bukkit.getOfflinePlayer(uuid));
        }
    }

    public Entity spawnFallingBlock(Location loc, Material mat, boolean damage) {
        if (loc.getWorld() == null) return null;
        BlockData blockData = Bukkit.createBlockData(mat);
        FallingBlock block = loc.getWorld().spawnFallingBlock(loc, blockData); // MaterialData deprecated, dùng org.bukkit.block.BlockData instead.
        block.setDropItem(false);
        block.setHurtEntities(damage);
        return block;
    }

    public void updateSkull(SkullMeta skullMeta, Player player) {
        skullMeta.setOwningPlayer(player);
    }

    @SuppressWarnings({"unchecked", "CallToPrintStackTrace"})
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
            GameRule<?> rawRule = GameRule.getByName(ruleName);
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
            ex.printStackTrace();
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

    /**
     * @deprecated Sử dụng chính xác material list từ <a href="https://jd.papermc.io/paper/1.21.1/org/bukkit/Material.html">đây</a>
     */
    @Deprecated
    public ItemStack getMaterial(String item) {
        if (item.equalsIgnoreCase("SKULL_ITEM"))
            return new ItemStack(Material.valueOf("SKELETON_SKULL"), 1);
        if (item.equalsIgnoreCase("ENDER_PORTAL_FRAME"))
            return new ItemStack(Material.valueOf("END_PORTAL_FRAME"), 1);
        if (item.equalsIgnoreCase("WORKBENCH"))
            return new ItemStack(Material.valueOf("CRAFTING_TABLE"), 1);
        if (item.equalsIgnoreCase("IRON_FENCE"))
            return new ItemStack(Material.valueOf("IRON_BARS"), 1);
        if (item.equalsIgnoreCase("REDSTONE_COMPARATOR"))
            return new ItemStack(Material.valueOf("COMPARATOR"));
        if (item.equalsIgnoreCase("SIGN_POST"))
            return new ItemStack(Material.valueOf("BIRCH_SIGN"));
        if (item.equalsIgnoreCase("STONE_PLATE"))
            return new ItemStack(Material.valueOf("STONE_PRESSURE_PLATE"));
        if (item.equalsIgnoreCase("IRON_PLATE"))
            return new ItemStack(Material.valueOf("HEAVY_WEIGHTED_PRESSURE_PLATE"));
        if (item.equalsIgnoreCase("GOLD_PLATE"))
            return new ItemStack(Material.valueOf("LIGHT_WEIGHTED_PRESSURE_PLATE"));
        if (item.equalsIgnoreCase("MOB_SPAWNER"))
            return new ItemStack(Material.valueOf("SPAWNER"));
        if (item.equalsIgnoreCase("SNOW_BALL")) {
            return new ItemStack(Material.valueOf("SNOWBALL"));
        }
        return new ItemStack(Material.AIR, 1);
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
        // mới: tạo 1 criteria để hợp với api mới
        Criteria criteria = Criteria.create(rawCriteria);
        return scoreboard.registerNewObjective(DisplayName, criteria, DisplayName);
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
        if (subtitle != null) {
            subtitle = subtitle.replaceAll("%player%", player.getDisplayName());
            subtitle = ChatColor.translateAlternateColorCodes('&', subtitle);
        }
        if (title != null) {
            title = title.replaceAll("%player%", player.getDisplayName());
            title = ChatColor.translateAlternateColorCodes('&', title);
        }
        player.sendTitle(title, subtitle, fadein, stay, fadeout);
    }

    public void playGameSound(Location loc, String paramEnumName, String paramCategory, float paramVolume, float paramPitch, boolean paramIsCustom) {
        if (loc.getWorld() == null) return;
        SoundCategory soundCateg = paramCategory == null ? SoundCategory.MASTER : SoundCategory.valueOf(paramCategory);
        if (paramIsCustom) {
            loc.getWorld().playSound(loc, paramEnumName, soundCateg, paramVolume, paramPitch);
        } else {
            loc.getWorld().playSound(loc, Sound.valueOf(paramEnumName), soundCateg, paramVolume, paramPitch);
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
        sender.spigot().sendMessage(ChatMessageType.CHAT, ComponentSerializer.parse(ChatColor.translateAlternateColorCodes('&', json)));
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
            if (meta != null && meta.hasDisplayName()) {
                return meta.getDisplayName();
            }
            return item.getType().name();
        }
        return "";
    }

    public ItemStack getItemStack(Material material, List<String> lore, String message) {
        ItemStack addItem = new ItemStack(material, 1);
        ItemMeta addItemMeta = addItem.getItemMeta();
        if (addItemMeta != null) {
            addItemMeta.setDisplayName(message);
            addItemMeta.setLore(lore);
            addItemMeta.addItemFlags(ItemFlag.values());
            addItem.setItemMeta(addItemMeta);
        }
        return addItem;
    }

    public ItemStack getItemStack(ItemStack item, List<String> lore, String message) {
        ItemStack addItem = item.clone();
        ItemMeta addItemMeta = addItem.getItemMeta();
        if (addItemMeta != null) {
            addItemMeta.setDisplayName(message);
            addItemMeta.setLore(lore);
            addItemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.valueOf("HIDE_ADDITIONAL_TOOLTIP"));
            addItem.setItemMeta(addItemMeta);
        }
        return addItem;
    }

    public void deleteCache() { /* empty */ }
}
