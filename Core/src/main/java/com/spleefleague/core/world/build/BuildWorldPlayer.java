package com.spleefleague.core.world.build;

import com.comphenix.protocol.wrappers.BlockPosition;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.util.variable.Dimension;
import com.spleefleague.core.util.variable.Point;
import com.spleefleague.core.world.FakeWorldPlayer;

/**
 * @author NickM13
 * @since 4/16/2020
 */
public class BuildWorldPlayer extends FakeWorldPlayer {
    
    private BlockPosition pos1, pos2;
    
    public BuildWorldPlayer(CorePlayer cp) {
        super(cp);
    }
    
    public BlockPosition getPos1() {
        return pos1;
    }
    
    public void setPos1(BlockPosition pos) {
        pos1 = pos;
    }
    
    public BlockPosition getPos2() {
        return pos2;
    }
    
    public void setPos2(BlockPosition pos) {
        pos2 = pos;
    }
    
    public Dimension getPosBox() {
        return new Dimension(new Point(pos1.getX(), pos1.getY(), pos1.getZ()),
                             new Point(pos2.getX(), pos2.getY(), pos2.getZ()));
    }
    
}
