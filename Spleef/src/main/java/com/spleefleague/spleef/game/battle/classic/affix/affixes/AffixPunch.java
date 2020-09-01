package com.spleefleague.spleef.game.battle.classic.affix.affixes;

import com.spleefleague.core.game.battle.BattlePlayer;
import com.spleefleague.core.util.variable.EntityRaycastResult;
import com.spleefleague.core.util.variable.Point;
import com.spleefleague.spleef.game.battle.classic.ClassicSpleefPlayer;
import com.spleefleague.spleef.game.battle.classic.affix.ClassicSpleefAffixFuture;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

/**
 * @author NickM13
 * @since 5/15/2020
 */
public class AffixPunch extends ClassicSpleefAffixFuture {

    private double punchDelay;
    private double punchPower;

    public AffixPunch() {
        super();
        this.activateTime = 5;
        this.punchDelay = 1;
        this.punchPower = 0.5;
    }

    @Override
    public void onRightClick(ClassicSpleefPlayer csp) {
        if (isRoundActivated()) {
            List<Entity> entities = new ArrayList<>();
            for (BattlePlayer bp : csp.getBattle().getBattlers()) {
                if (!bp.getCorePlayer().equals(csp.getCorePlayer())) {
                    entities.add(bp.getPlayer());
                }
            }
            List<EntityRaycastResult> results = new Point(csp.getPlayer().getEyeLocation()).castEntities(
                    csp.getPlayer().getLocation().getDirection(),
                    new Vector(0.5, 0.5, 0.5),
                    5,
                    entities);
            if (!results.isEmpty()) {
                Entity clickedEntity = results.get(0).getEntity();
                clickedEntity.setVelocity(clickedEntity.getVelocity().setY(0.2).add(csp.getPlayer().getLocation().getDirection().setY(0).normalize().multiply(punchPower)));
            }
        }
    }

    @Override
    protected String getPreActiveMessage(int seconds) {
        return "Punch will activate in " + seconds + " seconds";
    }

}
