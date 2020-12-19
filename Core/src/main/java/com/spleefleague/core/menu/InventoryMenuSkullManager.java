package com.spleefleague.core.menu;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.spleefleague.core.Core;
import com.spleefleague.core.logger.CoreLogger;
import net.minecraft.server.v1_15_R1.NBTTagCompound;
import net.minecraft.server.v1_15_R1.NBTTagList;
import net.minecraft.server.v1_15_R1.NBTTagString;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitScheduler;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class InventoryMenuSkullManager {

    public static class Texture {

        public String value, signature;

        public Texture(String value, String signature) {
            this.value = value;
            this.signature = signature;
        }

    }

    private static final Map<UUID, Texture> uuidTextureMap;
    private static final Set<UUID> loadingSkulls;

    static {
        uuidTextureMap = new HashMap<>();
        loadingSkulls = new HashSet<>();
    }

    public static Texture getTexture(UUID uuid) {
        try {
            InputStreamReader reader = new InputStreamReader(new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid.toString() + "?unsigned=false").openStream());
            JsonObject textureProperty = new JsonParser().parse(reader).getAsJsonObject().get("properties").getAsJsonArray().get(0).getAsJsonObject();
            return new Texture(textureProperty.get("value").getAsString(), textureProperty.get("signature").getAsString());
        } catch (IOException exception) {
            CoreLogger.logError(exception);
        }
        return null;
    }

    public static ItemStack getDefaultSkull() {
        return new ItemStack(Material.PLAYER_HEAD);
    }

    private static void loadSkull(UUID uuid) {
        loadingSkulls.add(uuid);
        Bukkit.getScheduler().runTaskAsynchronously(Core.getInstance(), () -> {
            Texture texture = getTexture(uuid);
            if (texture != null)
                uuidTextureMap.put(uuid, texture);
            loadingSkulls.remove(uuid);
        });
    }

    private static ItemStack getPlayerSkull(UUID uuid, Texture texture) {
        net.minecraft.server.v1_15_R1.ItemStack nmsStack = CraftItemStack.asNMSCopy(new ItemStack(Material.PLAYER_HEAD));

        if (!nmsStack.hasTag()) {
            nmsStack.setTag(new NBTTagCompound());
        }
        NBTTagCompound compound = nmsStack.getTag();

        NBTTagCompound skullOwner = new NBTTagCompound();
        skullOwner.set("Id", NBTTagString.a(uuid.toString()));

        NBTTagCompound properties = new NBTTagCompound();
        NBTTagList textures = new NBTTagList();
        NBTTagCompound value = new NBTTagCompound();
        value.set("Value", NBTTagString.a(texture.value));
        value.set("Signature", NBTTagString.a(texture.signature));
        textures.add(value);
        properties.set("textures", textures);
        skullOwner.set("Properties", properties);

        compound.set("SkullOwner", skullOwner);
        nmsStack.setTag(compound);

        return CraftItemStack.asBukkitCopy(nmsStack);
    }

    public static ItemStack getPlayerSkullForced(UUID uuid) {
        return getPlayerSkull(uuid, getTexture(uuid));
    }

    public static ItemStack getPlayerSkull(UUID uuid) {
        Texture texture = uuidTextureMap.get(uuid);
        if (texture == null) {
            loadSkull(uuid);
            return new ItemStack(Material.PLAYER_HEAD);
        }

        return getPlayerSkull(uuid, texture);
    }

}
