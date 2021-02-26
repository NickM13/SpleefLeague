/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.world;

import com.google.common.collect.Lists;
import net.minecraft.server.v1_15_R1.IBlockData;
import net.minecraft.server.v1_15_R1.MinecraftKey;
import net.minecraft.server.v1_15_R1.SoundEffect;
import net.minecraft.server.v1_15_R1.SoundEffectType;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.v1_15_R1.block.data.CraftBlockData;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author NickM13
 */
public class FakeBlock {

    private static final Map<Material, Sound> breakSoundMap = new HashMap<>();
    private static final Map<Material, Sound> stepSoundMap = new HashMap<>();
    private static final Map<Material, Sound> placeSoundMap = new HashMap<>();
    private static final Map<Material, Sound> hitSoundMap = new HashMap<>();
    private static final Map<Material, Sound> fallSoundMap = new HashMap<>();

    private static Field fieldBreakSound;
    private static Field fieldStepSound;
    private static Field fieldPlaceSound;
    private static Field fieldHitSound;
    private static Field fieldFallSound;

    private static Field fieldMinecraftKey;

    private static List<Sound> getSounds(Material material) {
        List<Sound> list = new ArrayList<>();
        try {
            BlockData blockData = material.createBlockData();
            IBlockData nmsBlockData = ((CraftBlockData) blockData).getState();
            SoundEffectType nmsSoundEffectType = nmsBlockData.r();

            SoundEffect breakSoundEffect =  (SoundEffect) fieldBreakSound.get(nmsSoundEffectType);
            SoundEffect stepSoundEffect =   (SoundEffect) fieldStepSound.get(nmsSoundEffectType);
            SoundEffect placeSoundEffect =  (SoundEffect) fieldPlaceSound.get(nmsSoundEffectType);
            SoundEffect hitSoundEffect =    (SoundEffect) fieldHitSound.get(nmsSoundEffectType);
            SoundEffect fallSoundEffect =   (SoundEffect) fieldFallSound.get(nmsSoundEffectType);

            list.add(Sound.valueOf(((MinecraftKey) fieldMinecraftKey.get(breakSoundEffect)).getKey().replace('.', '_').toUpperCase()));
            list.add(Sound.valueOf(((MinecraftKey) fieldMinecraftKey.get(stepSoundEffect)).getKey().replace('.', '_').toUpperCase()));
            list.add(Sound.valueOf(((MinecraftKey) fieldMinecraftKey.get(placeSoundEffect)).getKey().replace('.', '_').toUpperCase()));
            list.add(Sound.valueOf(((MinecraftKey) fieldMinecraftKey.get(hitSoundEffect)).getKey().replace('.', '_').toUpperCase()));
            list.add(Sound.valueOf(((MinecraftKey) fieldMinecraftKey.get(fallSoundEffect)).getKey().replace('.', '_').toUpperCase()));
        } catch (IllegalArgumentException | IllegalAccessException ignored) {

        }
        return list;
    }

    public static void init() {
        try {
            fieldBreakSound = SoundEffectType.class.getDeclaredField("z");
            fieldBreakSound.setAccessible(true);

            fieldStepSound = SoundEffectType.class.getDeclaredField("A");
            fieldStepSound.setAccessible(true);

            fieldPlaceSound = SoundEffectType.class.getDeclaredField("B");
            fieldPlaceSound.setAccessible(true);

            fieldHitSound = SoundEffectType.class.getDeclaredField("C");
            fieldHitSound.setAccessible(true);

            fieldFallSound = SoundEffectType.class.getDeclaredField("D");
            fieldFallSound.setAccessible(true);

            fieldMinecraftKey = SoundEffect.class.getDeclaredField("a");
            fieldMinecraftKey.setAccessible(true);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

        for (Material material : Material.values()) {
            if (material.isBlock()) {
                List<Sound> sounds = getSounds(material);
                if (!sounds.isEmpty()) {
                    breakSoundMap.put(material, sounds.get(0));
                    stepSoundMap.put(material, sounds.get(1));
                    placeSoundMap.put(material, sounds.get(2));
                    hitSoundMap.put(material, sounds.get(2));
                    fallSoundMap.put(material, sounds.get(2));
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

    public Sound getStepSound() {
        return stepSoundMap.get(blockData.getMaterial());
    }

    public Sound getBreakSound() {
        return breakSoundMap.get(blockData.getMaterial());
    }

    public Sound getPlaceSound() {
        return placeSoundMap.get(blockData.getMaterial());
    }

    public Sound getHitSound() {
        return hitSoundMap.get(blockData.getMaterial());
    }

    public Sound getFallSound() {
        return fallSoundMap.get(blockData.getMaterial());
    }

}
