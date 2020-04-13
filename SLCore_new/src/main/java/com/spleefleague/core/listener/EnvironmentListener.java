/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.listener;

import com.spleefleague.core.Core;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.Rank;
import com.spleefleague.core.util.Warp;
import java.util.HashSet;
import java.util.Set;
import org.bukkit.GameMode;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.v1_15_R1.block.CraftSign;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

/**
 * @author NickM13
 */
public class EnvironmentListener implements Listener {
    
    protected static int BLOCKS_BELOW = 5;
    
    protected Double parseDouble(String l) {
        try {
            return Double.parseDouble(l);
        } catch(Exception e1) {
            try {
                return (double) Integer.parseInt(l);
            } catch(Exception e2) {
                return 0.;
            }
        }
    }
    
    protected Vector getSignVector(CraftSign sign) {
        Vector vec = new Vector();
        vec.setX(parseDouble(sign.getLine(1)));
        vec.setY(parseDouble(sign.getLine(2)));
        vec.setZ(parseDouble(sign.getLine(3)));
        return vec;
    }
    
    protected Set<Block> getBlocksBelow(Block block) {
        Set<Block> blocks = new HashSet<>();
        Block rel = block;
        for (int i = 0; i < BLOCKS_BELOW || rel == null; i++) {
            blocks.add(rel);
            rel = rel.getRelative(BlockFace.DOWN);
        }
        return blocks;
    }
    
    @EventHandler
    public void onPlayerMoveSign(PlayerMoveEvent e) {
        CorePlayer cp = Core.getInstance().getPlayers().get(e.getPlayer());
        for (Block block : getBlocksBelow(e.getTo().getBlock())) {
            BlockState state = block.getState();
            if (state instanceof CraftSign) {
                CraftSign sign = (CraftSign) state;
                switch (sign.getLine(0).toLowerCase()) {
                    case "[jump]":
                        e.getPlayer().setVelocity(e.getPlayer().getVelocity().add(getSignVector(sign)));
                        break;
                    case "[teleport]":
                        cp.teleport(getSignVector(sign));
                        break;
                    case "[min-rank]":
                        if (!cp.getRank().hasPermission(Rank.getRank(sign.getLine(1)))) {
                            e.setCancelled(true);
                        }
                        break;
                    case "[max-rank]":
                        if (cp.getRank().hasPermission(Rank.getRank(sign.getLine(1)))) {
                            e.setCancelled(true);
                        }
                        break;
                    case "[warp]":
                        Warp warp = Warp.getWarp(sign.getLine(1));
                        if (warp != null) {
                            cp.warp(warp);
                        }
                        break;
                    case "[effect]":
                        try {
                            PotionEffectType type = PotionEffectType.getByName(sign.getLine(1));
                            if (type == null) break;
                            int duration = Integer.valueOf(sign.getLine(2));
                            int amplifier = Integer.valueOf(sign.getLine(3));
                            e.getPlayer().addPotionEffect(type.createEffect(duration, amplifier));
                        } catch (NumberFormatException exception) {
                            
                        }
                        break;
                    default:
                        continue;
                }
                break;
            }
        }
    }
    
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        CorePlayer cp = Core.getInstance().getPlayers().get(e.getEntity());
        cp.saveLastLocation();
        e.setDeathMessage(cp.getDisplayName() + " somehow died on SpleefLeague?  Kinda sus.  Oh but here's the real reason: " + e.getDeathMessage());
        Core.getInstance().sendMessage(e.getDeathMessage());
    }
    
    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent e) {
        CorePlayer cp = Core.getInstance().getPlayers().get(e.getPlayer());
        e.setRespawnLocation(cp.getSpawnLocation());
    }
    
    @EventHandler
    public void onBlockExplode(BlockExplodeEvent e) {
        e.setCancelled(true);
    }
    
    @EventHandler
    public void onEntityExplode(EntityExplodeEvent e) {
        e.blockList().clear();
    }
    
    @EventHandler
    public void onEntityToggleGlide(EntityToggleGlideEvent e) {
        if (e.isGliding()
                && e.getEntityType().equals(EntityType.PLAYER)) {
            Player p = (Player) e.getEntity();
            CorePlayer cp = Core.getInstance().getPlayers().get(p);
            if (!cp.getGameMode().equals(GameMode.CREATIVE)) {
                e.setCancelled(true);
            }
        }
    }
    
    @EventHandler
    public void onBlockFade(BlockFadeEvent e) {
        e.setCancelled(true);
    }
    
    @EventHandler
    public void onBlockPhysics(BlockPhysicsEvent e) {
        
    }
    
}
