package com.spleefleague.spleef.game.battle.power.training;

import com.comphenix.protocol.wrappers.BlockPosition;
import com.spleefleague.core.game.battle.Battle;
import com.spleefleague.core.game.battle.BattlePlayer;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.world.FakeBlock;
import com.spleefleague.core.world.FakeUtils;
import com.spleefleague.core.world.build.BuildStructure;
import com.spleefleague.core.world.build.BuildStructures;
import com.spleefleague.spleef.Spleef;
import com.spleefleague.spleef.game.battle.SpleefBattlePlayer;
import com.spleefleague.spleef.game.battle.power.PowerSpleefPlayer;
import com.spleefleague.spleef.game.battle.power.ability.Abilities;
import com.spleefleague.spleef.game.battle.power.ability.Ability;
import com.spleefleague.spleef.game.battle.power.ability.abilities.AbilityMobility;
import com.spleefleague.spleef.game.battle.power.ability.abilities.AbilityOffensive;
import com.spleefleague.spleef.game.battle.power.ability.abilities.AbilityUtility;
import com.spleefleague.spleef.game.battle.power.ability.abilities.mobility.MobilityEnderRift;
import com.spleefleague.spleef.player.SpleefPlayer;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

/**
 * @author NickM
 * @since 4/15/2020
 */
public class PowerTrainingPlayer extends PowerSpleefPlayer {

    public PowerTrainingPlayer(CorePlayer cp, Battle<?> battle) {
        super(cp, battle);
    }

}
