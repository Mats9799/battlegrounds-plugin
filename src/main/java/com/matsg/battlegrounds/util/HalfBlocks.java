package com.matsg.battlegrounds.util;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HalfBlocks {

    private static final Map<Byte, BlockFace> directions = getDirectionMap();
    private static final List<Slab> slabs = getSlabs();
    private static final List<Stairs> stairs = getStairs();

    private static Map<Byte, BlockFace> getDirectionMap() {
        Map<Byte, BlockFace> map = new HashMap<>();
        map.put((byte) 0, BlockFace.EAST);
        map.put((byte) 1, BlockFace.WEST);
        map.put((byte) 2, BlockFace.SOUTH);
        map.put((byte) 3, BlockFace.NORTH);
        return map;
    }

    private static List<Slab> getSlabs() {
        List<Slab> list = new ArrayList<>();
        Material[] slabTypes = new Material[] { Material.STEP, Material.STONE_SLAB2, Material.WOOD_STEP };
        for (Material material : slabTypes) {
            for (byte i = 0; i <= 17; i ++) {
                list.add(new Slab(material, i, i <= 8));
            }
        }
        return list;
    }

    private static List<Stairs> getStairs() {
        List<Stairs> list = new ArrayList<>();
        Material[] stairsTypes = new Material[] { Material.ACACIA_STAIRS, Material.BIRCH_WOOD_STAIRS, Material.BRICK_STAIRS,
                Material.COBBLESTONE_STAIRS, Material.DARK_OAK_STAIRS, Material.JUNGLE_WOOD_STAIRS, Material.NETHER_BRICK_STAIRS,
                Material.QUARTZ_STAIRS, Material.RED_SANDSTONE_STAIRS, Material.SANDSTONE_STAIRS, Material.SMOOTH_STAIRS,
                Material.SPRUCE_WOOD_STAIRS, Material.WOOD_STAIRS };

        for (Material material : stairsTypes) {
            for (byte i = 0; i <= 7; i ++) {
                list.add(new Stairs(material, i, directions.get((byte) (i % 4)), i >= 4));
            }
        }
        return list;
    }

    private static HalfBlock getHalfblock(Block block) {
        for (HalfBlock halfBlock : getHalfBlockList()) {
            if (halfBlock.getMaterial() == block.getType() && halfBlock.getData() == block.getData()) {
                return halfBlock;
            }
        }
        return null;
    }

    public static List<HalfBlock> getHalfBlockList() {
        List<HalfBlock> list = new ArrayList<>();
        list.addAll(slabs);
        list.addAll(stairs);
        return list;
    }

    public static boolean isAir(Location location) {
        Block block = location.getBlock();
        if (!isHalfBlock(block)) {
            return block.getType() == Material.AIR;
        }
        return getHalfblock(block).isAir(location);
    }

    public static boolean isHalfBlock(Block block) {
        return getHalfblock(block) != null;
    }

    public static boolean isSlab(Material material) {
        for (Slab slab : slabs) {
            if (slab.material == material) {
                return true;
            }
        }
        return false;
    }

    public static boolean isStairs(Material material) {
        for (Stairs stairs : stairs) {
            if (stairs.material == material) {
                return true;
            }
        }
        return false;
    }

    private interface HalfBlock {

        byte getData();

        Material getMaterial();

        boolean isAir(Location location);
    }

    private static class Slab implements HalfBlock {

        private boolean bottom;
        private byte data;
        private Material material;

        private Slab(Material material, byte data, boolean bottom) {
            this.bottom = bottom;
            this.data = data;
            this.material = material;
        }

        public byte getData() {
            return data;
        }

        public Material getMaterial() {
            return material;
        }

        public boolean isAir(Location location) {
            return location.getY() - location.getBlockY() > 0.5 == bottom;
        }
    }

    private static class Stairs implements HalfBlock {

        private boolean upsideDown;
        private BlockFace blockFace;
        private byte data;
        private Material material;

        private Stairs(Material material, byte data, BlockFace blockFace, boolean upsideDown) {
            this.blockFace = blockFace;
            this.data = data;
            this.material = material;
            this.upsideDown = upsideDown;
        }

        public byte getData() {
            return data;
        }

        public Material getMaterial() {
            return material;
        }

        public boolean isAir(Location location) {
            double x = location.getX() - location.getBlockX(), y = location.getY() - location.getBlockY(), z = location.getZ() - location.getBlockZ();
            if (y > 0.5 == upsideDown) {
                return false;
            }
            return (blockFace.getModX() > 0 && x < 0.5) || (blockFace.getModX() < 0 && x > 0.5) || (blockFace.getModZ() > 0 && z < 0.5) || (blockFace.getModZ() < 0 && z > 0.5);
        }
    }
}