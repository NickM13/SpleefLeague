/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.world;

import com.google.common.collect.Lists;
import net.minecraft.server.v1_15_R1.SoundEffectType;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.v1_15_R1.block.data.CraftBlockData;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author NickM13
 */
public class FakeBlock {
    
    private static final Map<Material, Sound> breakSoundMap = new HashMap<>();
    private static final Map<Material, Sound> placeSoundMap = new HashMap<>();
    
    private static List<Sound> getSounds(Material material) {
        try {
            BlockData blockData = material.createBlockData();
            net.minecraft.server.v1_15_R1.IBlockData nmsBlockData = ((CraftBlockData) blockData).getState();
            net.minecraft.server.v1_15_R1.SoundEffectType nmsSoundEffectType = nmsBlockData.r();
    
            Field breakSound = SoundEffectType.class.getDeclaredField("z");
            breakSound.setAccessible(true);
            net.minecraft.server.v1_15_R1.SoundEffect nmsBreakSound = (net.minecraft.server.v1_15_R1.SoundEffect) breakSound.get(nmsSoundEffectType);
    
            Field placeSound = SoundEffectType.class.getDeclaredField("B");
            placeSound.setAccessible(true);
            net.minecraft.server.v1_15_R1.SoundEffect nmsPlaceSound = (net.minecraft.server.v1_15_R1.SoundEffect) placeSound.get(nmsSoundEffectType);
        
            Field keyField = net.minecraft.server.v1_15_R1.SoundEffect.class.getDeclaredField("a");
            keyField.setAccessible(true);
    
            net.minecraft.server.v1_15_R1.MinecraftKey nmsBreakString = (net.minecraft.server.v1_15_R1.MinecraftKey) keyField.get(nmsBreakSound);
            net.minecraft.server.v1_15_R1.MinecraftKey nmsPlaceString = (net.minecraft.server.v1_15_R1.MinecraftKey) keyField.get(nmsPlaceSound);
        
            return Lists.newArrayList(Sound.valueOf(nmsBreakString.getKey().replace(".", "_").toUpperCase()),
                    Sound.valueOf(nmsBreakString.getKey().replace(".", "_").toUpperCase()));
        } catch (IllegalArgumentException | NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public static void init() {
        for (Material material : Material.values()) {
            if (material.isBlock()) {
                List<Sound> sounds = getSounds(material);
                if (sounds != null) {
                    breakSoundMap.put(material, sounds.get(0));
                    placeSoundMap.put(material, sounds.get(1));
                }
            }
        }
    }

    private final BlockData blockData;
    
    public FakeBlock(BlockData blockData) {
        this.blockData = blockData;
    }

    public BlockData getBlockData() {
        return blockData;
    }
    
    public Sound getBreakSound() {
        return breakSoundMap.get(blockData.getMaterial());
    }
    
    public Sound getPlaceSound() {
        return placeSoundMap.get(blockData.getMaterial());
    }
    
}
