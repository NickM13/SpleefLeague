package com.spleefleague.zone.gear.wayfinder;

import com.google.common.util.concurrent.AtomicDouble;
import com.spleefleague.core.menu.InventoryMenuUtils;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.util.MathUtils;
import com.spleefleague.core.world.global.GlobalWorld;
import com.spleefleague.zone.CoreZones;
import com.spleefleague.zone.fragments.FragmentContainer;
import com.spleefleague.zone.gear.Gear;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author NickM13
 * @since 3/3/2021
 */
public class GearWayfinder extends Gear {

    private ItemStack lampEmpty, lampFill0, lampFill1, lampFill2;

    public GearWayfinder() {
        super(GearType.WAYFINDER);
    }

    public GearWayfinder(String identifier, String name) {
        super(GearType.WAYFINDER, identifier, name);
    }

    private void spawnParticles(Player player, Vector pos, int count) {
        player.spawnParticle(Particle.FIREWORKS_SPARK,
                pos.getX(), pos.getY(), pos.getZ(),
                count, 0, 0, 0, 0.02);
    }

    private static final int FIRST_TICKS = 60;
    private static final double FIRST_ROTATION = Math.PI * 12;
    private static final double HEIGHT = 2.5;

    private static final int SECOND_TICKS = 20;
    private static final double SECOND_ROTATION = Math.PI * 36;

    private void animation(CorePlayer corePlayer, Vector target) {
        GlobalWorld globalWorld = corePlayer.getGlobalWorld();

        Player player = corePlayer.getPlayer();

        AtomicDouble i = new AtomicDouble(0);
        globalWorld.addRepeatingTask(() -> {
            Vector sparkCenter = corePlayer.getPlayer().getEyeLocation().toVector();
            sparkCenter.setY(sparkCenter.getY() + i.get() * HEIGHT / FIRST_TICKS);
            Vector vector = MathUtils.getPointOnCircleHorizontal(
                    sparkCenter,
                    Math.pow(1. - (i.getAndAdd(1) / FIRST_TICKS), 2) * 2,
                    i.get() / FIRST_TICKS * FIRST_ROTATION);
            spawnParticles(player, vector, (int) ((i.get() / FIRST_TICKS) * 5));
        }, FIRST_TICKS, 1, () -> {
            Vector start = corePlayer.getPlayer().getEyeLocation().toVector();
            start.setY(start.getY() + i.get() * HEIGHT / FIRST_TICKS);
            Vector direction = target.clone().subtract(start).normalize();
            i.set(0);
            globalWorld.addRepeatingTask(() -> {
                start.add(direction);
                Vector vector = MathUtils.getPointOnCircle(
                        start,
                        direction,
                        1. - (i.getAndAdd(1) / SECOND_TICKS),
                        i.get() / SECOND_TICKS * SECOND_ROTATION);
                spawnParticles(player, vector, 5);
            }, SECOND_TICKS, 1);
        });
    }

    @Override
    protected void createGearItems() {
        lampEmpty = applyPersistents(InventoryMenuUtils.createCustomItem(Material.LEVER, 1));
        lampFill0 = applyPersistents(InventoryMenuUtils.createCustomItem(Material.TORCH, 11));
        lampFill1 = applyPersistents(InventoryMenuUtils.createCustomItem(Material.TORCH, 12));
        lampFill2 = applyPersistents(InventoryMenuUtils.createCustomItem(Material.TORCH, 13));
    }

    @Override
    protected boolean onActivate(CorePlayer corePlayer) {
        String skin = corePlayer.getCollectibles().getInfo(this).getSelectedSkin();
        if (skin.isEmpty()) return false;
        FragmentContainer fragment = CoreZones.getInstance().getFragmentManager().getContainer(skin);
        if (fragment != null) {
            Vector vector = fragment.getClosest(corePlayer);
            if (vector != null) {
                animation(corePlayer, vector.add(new Vector(0, 2, 0)));
                return true;
            }
        }
        return false;
    }

    @Override
    public ItemStack getGearItem(CorePlayer corePlayer) {
        return lampFill2;
    }

}
