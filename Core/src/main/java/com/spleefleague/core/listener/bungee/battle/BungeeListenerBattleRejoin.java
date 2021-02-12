package com.spleefleague.core.listener.bungee.battle;

import com.spleefleague.core.Core;
import com.spleefleague.core.game.BattleMode;
import com.spleefleague.core.game.battle.Battle;
import com.spleefleague.core.listener.bungee.BungeeListener;
import com.spleefleague.coreapi.utils.packet.bungee.battle.PacketBungeeBattleRejoin;
import org.bukkit.entity.Player;

import java.util.UUID;

public class BungeeListenerBattleRejoin extends BungeeListener<PacketBungeeBattleRejoin> {

    @Override
    protected void receive(Player sender, PacketBungeeBattleRejoin packet) {
        BattleMode battleMode = BattleMode.get(packet.mode);
        UUID battleId = packet.battleId;
        Core.getInstance().getPlayers().addPlayerJoinAction(packet.sender, cp -> {
            Battle<?> battle = battleMode.getOngoingBattles().get(battleId);
            if (battle.isOngoing()) {
                battle.rejoinBattler(cp);
            } else {
                Core.getInstance().returnToHub(cp);
            }
        }, true);
    }

}
