package com.spleefleague.splegg.player;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.spleefleague.core.Core;
import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.database.annotation.DBLoad;
import com.spleefleague.core.database.annotation.DBSave;
import com.spleefleague.core.database.variable.DBPlayer;
import com.spleefleague.core.game.ArenaMode;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.splegg.Splegg;
import com.spleefleague.splegg.game.SpleggGun;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author NickM
 * @since 4/16/2020
 */
public class SpleggPlayer extends DBPlayer {

    protected SpleggGun activeSpleggGun;
    protected Set<Integer> spleggGuns = new HashSet<>();

    public SpleggPlayer() {
        super();
        activeSpleggGun = SpleggGun.getDefault();
    }

    @Override
    public void init() {

    }

    @Override
    public void close() {

    }

    @DBLoad(fieldName="activeSpleggGun")
    private void loadActiveSpleggGun(Integer id) {
        SpleggGun gun;
        if ((gun = SpleggGun.getSpleggGun(id)) == null)
            gun = SpleggGun.getDefault();
        if (gun != null)
            setActiveSpleggGun(gun.getDamage());
    }
    @DBSave(fieldName="activeSpleggGun")
    private Integer saveActiveSpleggGun() {
        return activeSpleggGun.getDamage();
    }

    @DBLoad(fieldName="spleggGuns")
    private void loadSpleggGuns(List<Integer> list) {
        if (list == null) return;
        spleggGuns = Sets.newHashSet(list);
    }
    @DBSave(fieldName="spleggGuns")
    private List<Integer> saveSpleggGuns() {
        if (spleggGuns == null) return new ArrayList<>();
        return Lists.newArrayList(spleggGuns);
    }

    public void addSpleggGun(int id) {
        if (spleggGuns.contains(id)) {
            Core.getInstance().sendMessage(this, "You already have that splegg gun!");
        } else if (SpleggGun.getSpleggGun(id).isDefault()) {
            Core.getInstance().sendMessage(this, "That splegg gun is a default!");
        } else {
            if (SpleggGun.getSpleggGun(id) != null) {
                spleggGuns.add(id);
                Core.getInstance().sendMessage(this, "You have collected the " + SpleggGun.getSpleggGun(id).getDisplayName() + Chat.DEFAULT + "!");
            } else {
                Core.getInstance().sendMessage(this, "Shovel doesn't exist!");
            }
        }
    }
    public void setActiveSpleggGun(int id) {
        SpleggGun gun;
        if ((gun = SpleggGun.getSpleggGun(id)) != null) {
            if (spleggGuns.contains(id) || (gun.isDefault())) {
                activeSpleggGun = gun;
            }
        }
        Bukkit.getScheduler().runTaskAsynchronously(Splegg.getInstance(), () -> {
            CorePlayer cp = Core.getInstance().getPlayers().get(this);
            while (cp == null) {
                try {
                    Thread.sleep(500L);
                } catch (InterruptedException ex) {
                    Logger.getLogger(SpleggPlayer.class.getName()).log(Level.SEVERE, null, ex);
                }
                cp = Core.getInstance().getPlayers().get(this);
            }
            cp.setSelectedItem(gun.getType(), gun.getIdentifier());
        });
    }
    public SpleggGun getActiveSpleggGun() {
        return activeSpleggGun;
    }
    public boolean hasSpleggGun(int id) {
        SpleggGun gun;
        if ((gun = SpleggGun.getSpleggGun(id)) != null) {
            return spleggGuns.contains(id) || gun.isDefault();
        }
        return false;
    }

    /**
     * @param dbPlayer
     * @deprecated
     */
    @Override
    public void printStats(DBPlayer dbPlayer) {

    }

    @Override
    public int getRating(ArenaMode arenaMode) {
        return 0;
    }

    @Override
    public void addRating(ArenaMode arenaMode, int i) {

    }

    @Override
    public String getDisplayElo(ArenaMode arenaMode) {
        return null;
    }

}
