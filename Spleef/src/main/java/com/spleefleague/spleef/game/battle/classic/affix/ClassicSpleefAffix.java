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
public abstract class ClassicSpleefAffix extends DBEntity {

    @DBField
    private boolean active = false;

    public ClassicSpleefAffix() {
        this.identifier = getClass().getSimpleName();
    }

    public InventoryMenuItem createMenuItem() {
        return InventoryMenuAPI.createItemDynamic()
                .setName(identifier)
                .setDescription("")
                .setDisplayItem(cp -> new ItemStack(isActive() ? Material.GLOWSTONE : Material.REDSTONE_LAMP))
                .setAction(cp -> setActive(!isActive()))
                .setCloseOnAction(false);
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean state) {
        System.out.println("Activity!" + state);
        active = state;
        ClassicSpleefAffixes.updateAffix(this);
    }

    /**
     * Called at the start of a battle
     */
    public void startBattle(ClassicSpleefBattle battle) {

    }

    /**
     * Called at the start of a round
     */
    public void startRound(ClassicSpleefBattle battle) {

    }

    public void onBlockBreak(ClassicSpleefPlayer csp) {

    }

    public void onRightClick(ClassicSpleefPlayer csp) {

    }

    /**
     * Called every 2 ticks (1/10 of a second)
     */
    public void update(ClassicSpleefBattle battle) {

    }

}
