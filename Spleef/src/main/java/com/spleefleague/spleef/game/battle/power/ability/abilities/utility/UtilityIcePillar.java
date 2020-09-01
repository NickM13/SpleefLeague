package com.spleefleague.spleef.game.battle.power.ability.abilities.utility;

import com.comphenix.protocol.wrappers.BlockPosition;
import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.game.battle.BattlePlayer;
import com.spleefleague.core.world.FakeBlock;
import com.spleefleague.core.world.FakeUtils;
import com.spleefleague.core.world.build.BuildStructure;
import com.spleefleague.core.world.build.BuildStructures;
import com.spleefleague.core.world.game.GameUtils;
import com.spleefleague.core.world.game.GameWorld;
import com.spleefleague.spleef.Spleef;
import com.spleefleague.spleef.game.battle.power.PowerSpleefPlayer;
import com.spleefleague.spleef.game.battle.power.ability.abilities.AbilityUtility;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.Map;

/**
 * @author NickM13
 * @since 5/19/2020
 */
public class UtilityIcePillar extends AbilityUtility {

    private static final int RADIUS = 5;
    private static final double RISE = 1.5;
    private static final double STAY = 1.5;
    private static final double DURATION = 5;
    private static final int SPACING = 4;
    private static final int SLOW = 1;

    public UtilityIcePillar() {
        super(2, 20);
    }

    @Override
    public String getDisplayName() {
        return "Ice Pillar";
    }

    @Override
    public String getDescription() {
        return Chat.DESCRIPTION + "Raise a pillar of ice beneath the caster, quickly lifting them upward.";
    }

    private void tick(PowerSpleefPlayer psp, Vector pos, double remainder) {
        if (remainder <= 0) return;
        for (BattlePlayer bp : psp.getBattle().getBattlers()) {
            if (!bp.getCorePlayer().equals(psp.getCorePlayer()) &&
                    bp.getPlayer().getLocation().toVector().distance(pos) < RADIUS) {
                bp.getPlayer().addPotionEffect(PotionEffectType.SLOW.createEffect(SPACING + 10, SLOW - 1));
            }
        }
        GameUtils.spawnRingParticles(psp.getBattle().getGameWorld(), pos, Type.UTILITY.getDustMedium(), RADIUS, 10);
        psp.getBattle().getGameWorld().runTask(Bukkit.getScheduler().runTaskLater(Spleef.getInstance(), () -> {
            tick(psp, pos, remainder - (SPACING / 20D));
        }, SPACING));
    }

    /**
     * Called every 0.1 seconds (2 ticks)
     *
     * @param psp
     */
    @Override
    public void update(PowerSpleefPlayer psp) {
        super.update(psp);
    }

    /**
     * This is called when a player uses an ability that isn't on cooldown.
     *
     * @param psp Casting Player
     */
    @Override
    public boolean onUse(PowerSpleefPlayer psp) {
        BuildStructure structure = BuildStructures.get("power:icepillar");
        GameWorld gameWorld = psp.getBattle().getGameWorld();
        if (FakeUtils.isOnGround(psp.getCorePlayer())) {
            BlockPosition blockPos = new BlockPosition(
                    (int) Math.round(psp.getPlayer().getLocation().getX()),
                    psp.getPlayer().getLocation().getBlockY(),
                    (int) Math.round(psp.getPlayer().getLocation().getZ()));
            for (Map.Entry<BlockPosition, FakeBlock> entry : structure.getFakeBlocks().entrySet()) {
                gameWorld.setBlockDelayed(entry.getKey().add(blockPos), entry.getValue().getBlockData(), entry.getKey().getY() * 2 + 8);
                gameWorld.addBlockDelayed(entry.getKey().add(blockPos), Material.AIR.createBlockData(), ((5 - entry.getKey().getY()) * 2) + (int) (DURATION * 20));
            }
            gameWorld.playSound(psp.getPlayer().getLocation(), Sound.ENTITY_TURTLE_DEATH_BABY, 1.f, 0.7f);
            psp.getPlayer().setVelocity(psp.getPlayer().getVelocity().setY(1.15));
            return true;
        }
        return false;
    }

    /**
     * Called at the start of a round
     *
     * @param psp
     */
    @Override
    public void reset(PowerSpleefPlayer psp) {

    }

}
