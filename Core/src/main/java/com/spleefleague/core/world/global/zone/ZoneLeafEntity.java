package com.spleefleague.core.world.global.zone;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.spleefleague.core.Core;
import com.spleefleague.core.player.CorePlayer;
import net.minecraft.server.v1_15_R1.EntityItem;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

import java.util.Iterator;
import java.util.List;

public class ZoneLeafEntity extends net.minecraft.server.v1_15_R1.EntityItem {

    String zoneName;
    String leafName;

    public ZoneLeafEntity(net.minecraft.server.v1_15_R1.World world, ZoneLeaf leaf, String zoneName) {
        super(net.minecraft.server.v1_15_R1.EntityTypes.ITEM, world);
        this.zoneName = zoneName;
        this.leafName = leaf.getName();
        setPosition(leaf.getPos().getX(), leaf.getPos().getY(), leaf.getPos().getZ());
        setPickupDelay(0);
        net.minecraft.server.v1_15_R1.ItemStack nmsItemStack = (CraftItemStack.asNMSCopy(new ItemStack(Material.HONEYCOMB)));
        nmsItemStack.setCount(1);
        setItemStack(nmsItemStack);
        setNoGravity(true);

        double radius = 1;
        List<EntityItem> list = this.world.a(EntityItem.class, this.getBoundingBox().grow(radius, radius, radius), (entityitemx) -> {
            return entityitemx != this;
        });
        Iterator iterator = list.iterator();

        while(iterator.hasNext()) {
            EntityItem entityitem = (EntityItem) iterator.next();
            entityitem.die();
        }
    }

    @Override
    public void tick() {
        if (this.getItemStack().isEmpty()) {
            this.die();
        }
    }

    @Override
    public void inactiveTick() {

    }

    @Override
    public void pickup(net.minecraft.server.v1_15_R1.EntityHuman entityhuman) {
        CorePlayer cp = Core.getInstance().getPlayers().get(entityhuman.getUniqueID());
        if (!cp.canBuild() && cp.getCollectibles().addLeaf(zoneName + ":" + leafName)) {
            if (cp.getCollectibles().getLeafCount(zoneName) >= GlobalZone.getZone(zoneName).getLeaves().size()) {
                cp.getPlayer().playSound(cp.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, 1, 1.122462f);
                cp.getPlayer().playSound(cp.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, 1, 1.334840f);
                cp.getPlayer().playSound(cp.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, 1, 1.781797f);
            } else {
                cp.getPlayer().playSound(cp.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, 1, 1.189207f);
            }
            PacketContainer destroyEntityPacket = new PacketContainer(PacketType.Play.Server.ENTITY_DESTROY);
            int[] ids = { getBukkitEntity().getEntityId() };
            destroyEntityPacket.getIntegerArrays().write(0, ids);
            Core.sendPacket(cp, destroyEntityPacket);
        }
    }

    public String getFullName() {
        return zoneName + ":" + leafName;
    }

}
