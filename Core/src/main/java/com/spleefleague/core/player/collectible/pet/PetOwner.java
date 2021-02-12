package com.spleefleague.core.player.collectible.pet;

import com.spleefleague.core.player.CorePlayer;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * @author NickM13
 * @since 4/22/2020
 */
public class PetOwner {

    CorePlayer corePlayer;
    EntityPet entityPet;
    Item fetchItem;

    public PetOwner(CorePlayer corePlayer) {
        this.corePlayer = corePlayer;
        this.fetchItem = null;
    }

    public Player getPlayer() {
        return corePlayer.getPlayer();
    }

    public CorePlayer getCorePlayer() {
        return corePlayer;
    }

    public void setEntityPet(EntityPet entityPet) {
        this.entityPet = entityPet;
    }

    public EntityPet getEntityPet() {
        return entityPet;
    }

    public void throwFetch() {
        if (fetchItem == null) {
            fetchItem = corePlayer.getPlayer().getWorld().dropItem(corePlayer.getLocation(), new ItemStack(Material.STICKY_PISTON));
            fetchItem.setVelocity(corePlayer.getPlayer().getLocation().getDirection().multiply(10));
        }
    }

}
