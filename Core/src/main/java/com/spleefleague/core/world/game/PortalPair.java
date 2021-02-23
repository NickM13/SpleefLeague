package com.spleefleague.core.world.game;

import com.comphenix.protocol.wrappers.BlockPosition;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PortalPair {

    public static class Portal {

        private final Location tpLoc;
        private final Vector visualLoc;
        private final BlockPosition blockPos;
        private final BlockPosition relativePos;
        private final BlockFace blockFace;

        public Portal(World world, BlockPosition blockPos, BlockFace blockFace) {
            this.blockPos = blockPos;
            this.blockFace = blockFace;
            this.relativePos = new BlockPosition(
                    blockPos.getX() + blockFace.getModX(),
                    blockPos.getY() + blockFace.getModY(),
                    blockPos.getZ() + blockFace.getModZ());
            tpLoc = blockPos.toLocation(world).add(0.5, 0.5, 0.5).add(blockFace.getDirection());
            visualLoc = blockPos.toVector().add(new Vector(0.5, 0.5, 0.5)).add(blockFace.getDirection().multiply(0.55));
        }

        public Location getTpLoc() {
            return tpLoc;
        }

        public Vector getVisualPos() {
            return visualLoc;
        }

        public BlockPosition getBlockPos() {
            return blockPos;
        }

        public BlockPosition getRelativePos() {
            return relativePos;
        }

        public BlockFace getBlockFace() {
            return blockFace;
        }

    }

    private static final long TELEPORT_DELAY = 2000;

    private boolean last = false;
    private Portal portal1, portal2;

    private static class LastTeleport {

        long time;
        boolean first;

        public LastTeleport(long time, boolean first) {
            this.time = time;
            this.first = first;
        }

    }

    public Map<UUID, LastTeleport> lastTeleports = new HashMap<>();

    public int colorIndex;

    public PortalPair(int colorIndex) {
        this.colorIndex = colorIndex;
    }

    public boolean canTeleport1(UUID uuid) {
        return (!lastTeleports.containsKey(uuid) || lastTeleports.get(uuid).time < System.currentTimeMillis() || !lastTeleports.get(uuid).first);
    }

    public boolean canTeleport2(UUID uuid) {
        return (!lastTeleports.containsKey(uuid) || lastTeleports.get(uuid).time < System.currentTimeMillis() || lastTeleports.get(uuid).first);
    }

    public Vector getNewVelocityTo1(Entity entity) {
        Vector origVel = entity.getVelocity();

        if (portal1.getBlockFace().equals(portal2.getBlockFace())) {
            switch (portal1.getBlockFace()) {
                case UP:
                case DOWN:
                    origVel.multiply(new Vector(1, -1, 1));
                    break;
                case NORTH:
                case SOUTH:
                    origVel.multiply(new Vector(1, 1, -1));
                    break;
                case EAST:
                case WEST:
                    origVel.multiply(new Vector(-1, 1, 1));
                    break;
            }
        } else if (!portal1.getBlockFace().getOppositeFace().equals(portal2.getBlockFace())) {
            Vector axis = portal1.getBlockFace().getDirection().crossProduct(portal2.getBlockFace().getDirection());
            origVel.rotateAroundAxis(axis, Math.toRadians(90));
        }
        return origVel;
    }

    public Vector getNewVelocityTo2(Entity entity) {
        Vector origVel = entity.getVelocity();

        if (portal1.getBlockFace().equals(portal2.getBlockFace())) {
            switch (portal1.getBlockFace()) {
                case UP:
                case DOWN:
                    origVel.multiply(new Vector(1, -1, 1));
                    break;
                case NORTH:
                case SOUTH:
                    origVel.multiply(new Vector(1, 1, -1));
                    break;
                case EAST:
                case WEST:
                    origVel.multiply(new Vector(-1, 1, 1));
                    break;
            }
        } else if (!portal1.getBlockFace().getOppositeFace().equals(portal2.getBlockFace())) {
            Vector axis = portal2.getBlockFace().getDirection().crossProduct(portal1.getBlockFace().getDirection());
            origVel.rotateAroundAxis(axis, Math.toRadians(90));
        }
        return origVel;
    }

    public void teleportTo1(Entity entity) {
        Vector origDir = entity.getLocation().getDirection();
        Vector origVel = entity.getVelocity();
        Location toLoc = portal1.getTpLoc();

        if (portal1.getBlockFace().equals(portal2.getBlockFace())) {
            switch (portal1.getBlockFace()) {
                case UP:
                case DOWN:
                    origVel.multiply(new Vector(1, -1, 1));
                    break;
                case NORTH:
                case SOUTH:
                    origVel.multiply(new Vector(1, 1, -1));
                    break;
                case EAST:
                case WEST:
                    origVel.multiply(new Vector(-1, 1, 1));
                    break;
            }
        } else if (!portal1.getBlockFace().getOppositeFace().equals(portal2.getBlockFace())) {
            Vector axis = portal1.getBlockFace().getDirection().crossProduct(portal2.getBlockFace().getDirection());
            origDir.rotateAroundAxis(axis, Math.toRadians(90));
            origVel.rotateAroundAxis(axis, Math.toRadians(90));
        }

        toLoc.setDirection(origDir);
        entity.teleport(toLoc);
        entity.setVelocity(origVel);
        lastTeleports.put(entity.getUniqueId(), new LastTeleport(System.currentTimeMillis() + TELEPORT_DELAY, true));
    }

    public void teleportTo2(Entity entity) {
        Vector origDir = entity.getLocation().getDirection();
        Vector origVel = entity.getVelocity();
        Location toLoc = portal2.getTpLoc();

        if (portal1.getBlockFace().equals(portal2.getBlockFace())) {
            switch (portal1.getBlockFace()) {
                case UP:
                case DOWN:
                    origVel.multiply(new Vector(1, -1, 1));
                    break;
                case NORTH:
                case SOUTH:
                    origVel.multiply(new Vector(1, 1, -1));
                    break;
                case EAST:
                case WEST:
                    origVel.multiply(new Vector(-1, 1, 1));
                    break;
            }
        } else if (!portal1.getBlockFace().getOppositeFace().equals(portal2.getBlockFace())) {
            Vector axis = portal2.getBlockFace().getDirection().crossProduct(portal1.getBlockFace().getDirection());
            origDir.rotateAroundAxis(axis, Math.toRadians(90));
            origVel.rotateAroundAxis(axis, Math.toRadians(90));
        }

        toLoc.setDirection(origDir);
        entity.teleport(toLoc);
        entity.setVelocity(origVel);
        lastTeleports.put(entity.getUniqueId(), new LastTeleport(System.currentTimeMillis() + TELEPORT_DELAY, false));
    }

    public Portal getPortal1() {
        return portal1;
    }

    public Portal getPortal2() {
        return portal2;
    }

    public void pushPortal(World world, BlockPosition pos, BlockFace blockFace) {
        Portal portal = new Portal(world, pos, blockFace);
        if ((portal1 != null && portal1.getRelativePos().equals(portal.getRelativePos())) ||
                (portal2 != null && portal2.getRelativePos().equals(portal.getRelativePos()))) return;
        if (last) portal1 = portal;
        else portal2 = portal;
        last = !last;
    }

    public void popPortal1() {
        portal1 = null;
        last = true;
    }

    public void popPortal2() {
        portal2 = null;
        last = false;
    }

    public boolean isLinked() {
        return portal1 != null && portal2 != null;
    }

}
