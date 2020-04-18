package com.spleefleague.core.world.build;

import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.world.FakeWorldPlayer;
import org.bukkit.Material;

/**
 * @author NickM13
 * @since 4/16/2020
 */
public class BuildWorldPlayer extends FakeWorldPlayer {
    
    private Material selectedMaterial = null;
    
    public BuildWorldPlayer(CorePlayer cp) {
        super(cp);
    }
    
    public void setSelectedMaterial(Material selectedMaterial) {
        this.selectedMaterial = selectedMaterial;
    }
    
    public Material getSelectedMaterial() {
        return selectedMaterial;
    }
    
    public Material getSelectedMaterialDisplay() {
        if (selectedMaterial == null) return Material.CHEST;
        return selectedMaterial;
    }
    
}
