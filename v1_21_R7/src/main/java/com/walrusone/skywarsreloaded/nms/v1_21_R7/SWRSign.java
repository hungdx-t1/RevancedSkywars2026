package com.walrusone.skywarsreloaded.nms.v1_21_R7;

import com.walrusone.skywarsreloaded.SkyWarsReloaded;
import com.walrusone.skywarsreloaded.enums.MatchState;
import com.walrusone.skywarsreloaded.game.GameMap;
import com.walrusone.skywarsreloaded.utilities.Messaging;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.block.sign.Side;
import org.bukkit.block.sign.SignSide;

@Deprecated
public class SWRSign implements com.walrusone.skywarsreloaded.game.signs.SWRSign {
    private final String gameName;
    private final Location location;

    public SWRSign(String name, Location location) {
        this.gameName = name;
        this.location = location;
    }

    @Override
    public void setMaterial(GameMap gMap, Block attachedBlock) {
        if (attachedBlock == null) return;
        attachedBlock.getWorld().loadChunk(attachedBlock.getChunk());

        MatchState state = gMap.getMatchState();
        if (state.equals(MatchState.WAITINGSTART) || state.equals(MatchState.WAITINGLOBBY)) {
            updateBlock(attachedBlock, "blockwaiting");
        } else if (gMap.getMatchState().equals(MatchState.PLAYING)) {
            updateBlock(attachedBlock, "blockplaying");
        } else if (gMap.getMatchState().equals(MatchState.ENDING)) {
            updateBlock(attachedBlock, "blockending");
        } else {
            updateBlock(attachedBlock, "blockoffline");
        }
    }

    @Override
    public void updateBlock(Block block, String item) {
        if (block == null) return;
        block.setType(SkyWarsReloaded.getIM().getItem(item).getType());
    }

    @Override
    public Block getAttachedBlock(Block b) {
        BlockData blockData = b.getState().getBlockData();
        BlockFace face = BlockFace.DOWN;

        // Nhận diện cả WallSign truyền thống và HangingSign đời mới
        if (blockData instanceof WallSign wallSign) {
            face = wallSign.getFacing().getOppositeFace();
        }
//        else if (blockData instanceof HangingSign hangingSign) {
//            face = hangingSign.getFacing().getOppositeFace();
//        }

        return b.getRelative(face);
    }

    @Override
    public void update() {
        GameMap gMap = SkyWarsReloaded.get().getGameMapManager().getMap(gameName);
        Location loc = location.clone();

        Block block = loc.getBlock();
        Material type = block.getType();

        if (Tag.SIGNS.isTagged(type)) {
            Block attachedBlock;
            if(!(block.getState() instanceof Sign sign)) return;

            String typeName = type.name();
            if (typeName.contains("HANGING")) {
                attachedBlock = loc.clone().add(0, 1, 0).getBlock();
            } else if (typeName.contains("WALL")) {
                attachedBlock = getAttachedBlock(block);
            } else {
                attachedBlock = loc.clone().add(0, -1, 0).getBlock();
            }

            setMaterial(gMap, attachedBlock);

            if(!sign.getChunk().isLoaded()) {
                sign.getChunk().load();
            }

            if (gMap != null) {
                formatSign(gMap, sign);
            }

            // Đổi thành false để chặn tính toán vật lý lặp lại, cứu hiệu năng server
            sign.update(false, false);
        }
    }

    @Override
    public Location getLocation() {
        return location;
    }

    @Override
    public String getName() {
        return gameName;
    }

    private void formatSign(GameMap gMap, Sign sign) {
        String state;
        MatchState matchState = gMap.getMatchState();
        if (matchState.equals(MatchState.WAITINGSTART) || matchState.equals(MatchState.WAITINGLOBBY)) {
            state = new Messaging.MessageFormatter().format("signs.joinable");
        } else if (matchState.equals(MatchState.PLAYING)) {
            state = new Messaging.MessageFormatter().format("signs.playing");
        } else if (matchState.equals(MatchState.ENDING)) {
            state = new Messaging.MessageFormatter().format("signs.ending");
        } else {
            state = new Messaging.MessageFormatter().format("signs.offline");
        }

        String team = gMap.getTeamSize() > 1 ? "team" : "";

        int playerCount = (matchState == MatchState.WAITINGLOBBY)
                ? gMap.getWaitingPlayers().size()
                : gMap.getPlayerCount();

        // Lấy mặt trước (Front Side) của biển báo theo chuẩn 1.21.11
        SignSide frontSide = sign.getSide(Side.FRONT);

        for (int i = 0; i < 4; i++) {
            String formattedLine = new Messaging.MessageFormatter()
                    .setVariable("matchstate", state)
                    .setVariable("mapname", gMap.getDisplayName().toUpperCase())
                    .setVariable("playercount", String.valueOf(playerCount))
                    .setVariable("maxplayers", String.valueOf(gMap.getMaxPlayers()))
                    .setVariable("teamsize", String.valueOf(gMap.getTeamSize()))
                    .format("signs.line" + (i + 1) + team);

            frontSide.setLine(i, formattedLine);
        }
    }
}