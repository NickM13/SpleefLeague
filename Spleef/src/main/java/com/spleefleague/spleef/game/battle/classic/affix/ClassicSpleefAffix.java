package com.spleefleague.spleef.game.battle.classic.affix;

import com.spleefleague.core.menu.InventoryMenuAPI;
import com.spleefleague.core.menu.InventoryMenuItem;
import com.spleefleague.coreapi.database.annotation.DBField;
import com.spleefleague.coreapi.database.variable.DBEntity;
import com.spleefleague.spleef.game.battle.classic.ClassicSpleefBattle;
import com.spleefleague.spleef.game.battle.classic.ClassicSpleefPlayer;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * @author NickM13
 * @since 5/1/2020
 */
public abstract class ClassicSpleefAffix {

    private final ClassicSpleefAffixes.AffixType type;

    protected ClassicSpleefBattle battle;

    public ClassicSpleefAffix(ClassicSpleefAffixes.AffixType type) {
        this.type = type;
    }

    public void setBattle(ClassicSpleefBattle battle) {
        this.battle = battle;
    }

    public ClassicSpleefAffixes.AffixType getType() {
        return type;
    }

    /**
     * Called at the start of a battle
     */
    public void startBattle(ClassicSpleefBattle battle) {

    }

    /**
     * Called at the start of a round
     */
    public void startRound() {

    }

    public void onBlockBreak(ClassicSpleefPlayer csp) {

    }

    public void onRightClick(ClassicSpleefPlayer csp) {

    }

    /**
     * Called every 2 ticks (1/10 of a second)
     */
    public void update() {

    }

}
