/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.spleef.player;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.spleefleague.core.Core;
import com.spleefleague.core.annotation.DBLoad;
import com.spleefleague.core.annotation.DBSave;
import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.game.ArenaMode;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.util.database.DBPlayer;
import com.spleefleague.spleef.Spleef;
import com.spleefleague.spleef.game.spleef.power.Power;
import com.spleefleague.spleef.game.Shovel;
import com.spleefleague.spleef.game.SpleefBattle;
import com.spleefleague.spleef.game.splegg.classic.SpleggGun;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bson.Document;
import org.bukkit.Bukkit;

/**
 * @author NickM13
 */
public class SpleefPlayer extends DBPlayer<SpleefBattle> {
    
    private static final Integer BASE_RATING = 1000;
    
    // TODO: activePowers
    protected Shovel activeShovel;
    protected Set<Integer> shovels = new HashSet<>();
    
    protected SpleggGun activeSpleggGun;
    protected Set<Integer> spleggGuns = new HashSet<>();
    
    //@DBField
    protected Integer[] activePowers = new Integer[4];
    
    protected Map<ArenaMode, Integer> ratings = new HashMap<>();
    
    public SpleefPlayer() {
        super();
        activeShovel = Shovel.getDefault();
        activeSpleggGun = SpleggGun.getDefault();
        for (int i = 0; i < 4; i++) {
            activePowers[i] = Power.getDefaultPower(i).getDamage();
        }
    }
    
    @DBLoad(fieldname="activeShovel")
    private void loadActiveShovel(Integer id) {
        Bukkit.getScheduler().runTaskAsynchronously(Spleef.getInstance(), () -> {
            Shovel shovel;
            if ((shovel = Shovel.getShovel(id)) == null)
                shovel = Shovel.getDefault();
            activeShovel = shovel;
            CorePlayer cp = Core.getInstance().getPlayers().get(this);
            while (cp == null) {
                try {
                    Thread.sleep(500L);
                } catch (InterruptedException ex) {
                    Logger.getLogger(SpleefPlayer.class.getName()).log(Level.SEVERE, null, ex);
                }
                cp = Core.getInstance().getPlayers().get(this);
            }
            cp.setSelectedItem(shovel.getType(), shovel.getIdentifier());
        });
    }
    @DBSave(fieldname="activeShovel")
    private Integer saveActiveShovel() {
        return activeShovel.getDamage();
    }
    
    @DBLoad(fieldname="activeSpleggGun")
    private void loadActiveSpleggGun(Integer id) {
        SpleggGun gun;
        if ((gun = SpleggGun.getSpleggGun(id)) == null)
            gun = SpleggGun.getDefault();
        setActiveSpleggGun(gun.getDamage());
    }
    @DBSave(fieldname="activeSpleggGun")
    private Integer saveActiveSpleggGun() {
        return activeSpleggGun.getDamage();
    }
    
    @DBLoad(fieldname="activePowers")
    private void loadActivePowers(List<Integer> powers) {
        if (powers == null) return;
        for (int i = 0; i < Math.min(4, powers.size()); i++) {
            activePowers[i] = powers.get(i);
        }
    }
    
    @DBSave(fieldname="activePowers")
    private List<Integer> saveActivePowers() {
        return Lists.newArrayList(activePowers);
    }
    
    @DBLoad(fieldname="ratings")
    private void loadRatings(List<Object> ratings) {
        if (ratings == null) return;
        Document d;
        for (Object o : ratings) {
            d = (Document) o;
            this.ratings.put(ArenaMode.getArenaMode(d.get("mode", String.class)), d.get("rating", Integer.class));
        }
    }
    @DBSave(fieldname="ratings")
    private List<Document> saveRatings() {
        if (ratings.isEmpty()) return null;
        
        List<Document> list = new ArrayList<>();
        
        for (Map.Entry<ArenaMode, Integer> rating : ratings.entrySet()) {
            list.add(new Document("mode", rating.getKey().getName()).append("rating", rating.getValue()));
        }
        
        return list;
    }
    public String getDisplayElo(ArenaMode mode) {
        return (Chat.BRACE + "(" +
                Chat.ELO + getRating(mode) +
                Chat.BRACE + ")");
    }
    public int getRating(ArenaMode mode) {
        if (!ratings.containsKey(mode)) ratings.put(mode, BASE_RATING);
        return ratings.get(mode);
    }
    public void setRating(ArenaMode mode, int amt) {
        ratings.put(mode, amt);
    }
    public void addRating(ArenaMode mode, int amt) {
        Core.getInstance().sendMessage(this, "You have " +
                (amt > 0 ? "gained " : "lost ") +
                Chat.ELO + Math.abs(amt) +
                Chat.DEFAULT + " elo");
        ratings.put(mode, getRating(mode) + amt);
    }
    
    @DBLoad(fieldname="shovels")
    private void loadShovels(List list) {
        if (list == null) return;
        shovels = Sets.newHashSet((List<Integer>)list);
    }
    @DBSave(fieldname="shovels")
    private List<Integer> saveShovels() {
        if (shovels == null) return Collections.EMPTY_LIST;
        return Lists.newArrayList(shovels);
    }
    
    @DBLoad(fieldname="spleggGuns")
    private void loadSpleggGuns(List list) {
        if (list == null) return;
        spleggGuns = Sets.newHashSet((List<Integer>)list);
    }
    @DBSave(fieldname="spleggGuns")
    private List<Integer> saveSpleggGuns() {
        if (spleggGuns == null) return Collections.EMPTY_LIST;
        return Lists.newArrayList(spleggGuns);
    }
    
    public int addShovel(int id) {
        if (shovels.contains(id)) {
            //Core.getInstance().sendMessage(this, "You already have that shovel!");
            return 1;
        } else if (Shovel.getShovel(id).isDefault()) {
            //Core.getInstance().sendMessage(this, "That shovel is a default!");
            return 2;
        } else {
            if (Shovel.getShovel(id) != null) {
                shovels.add(id);
                Core.getInstance().sendMessage(this, "You have collected the " + Shovel.getShovelName(id) + "!");
                return 0;
            } else {
                //Core.getInstance().sendMessage(this, "Shovel doesn't exist!");
                return 3;
            }
        }
    }
    public boolean removeShovel(int id) {
        if (shovels.contains(id)) {
            shovels.remove(id);
            Core.getInstance().sendMessage("You have lost the shovel " + Shovel.getShovelName(id));
            return true;
        }
        return false;
    }
    public void setActiveShovel(int id) {
        Shovel shovel;
        if ((shovel = Shovel.getShovel(id)) != null) {
            if (shovels.contains(id) || (shovel.isDefault())) {
                //Core.getInstance().sendMessage(this, "Shovel set to " + shovel.getDisplayName());
                activeShovel = shovel;
                Bukkit.getScheduler().runTaskAsynchronously(Spleef.getInstance(), () -> {
                    CorePlayer cp = Core.getInstance().getPlayers().get(this);
                    while (cp == null) {
                        try {
                            Thread.sleep(500L);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(SpleefPlayer.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        cp = Core.getInstance().getPlayers().get(this);
                    }
                    cp.setSelectedItem(shovel.getType(), shovel.getIdentifier());
                });
            } else {
                //Core.getInstance().sendMessage(this, shovel.getDisplayName() + Chat.DEFAULT + " is not unlocked");
            }
        } else {
            //Core.getInstance().sendMessage(this, "Shovel does not exist");
        }
    }
    public Shovel getActiveShovel() {
        return activeShovel;
    }
    public boolean hasShovel(int id) {
        Shovel shovel;
        if ((shovel = Shovel.getShovel(id)) != null) {
            return shovels.contains(id) || shovel.isDefault();
        }
        return false;
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
                //Core.getInstance().sendMessage(this, "Splegg Gun set to " + shovel.getDisplayName());
                activeSpleggGun = gun;
            } else {
                //Core.getInstance().sendMessage(this, shovel.getDisplayName() + Chat.DEFAULT + " is not unlocked");
            }
        } else {
            //Core.getInstance().sendMessage(this, "Gun does not exist");
        }
        Bukkit.getScheduler().runTaskAsynchronously(Spleef.getInstance(), () -> {
            CorePlayer cp = Core.getInstance().getPlayers().get(this);
            while (cp == null) {
                try {
                    Thread.sleep(500L);
                } catch (InterruptedException ex) {
                    Logger.getLogger(SpleefPlayer.class.getName()).log(Level.SEVERE, null, ex);
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
    
    public void setActivePower(int slot, int powerId) {
        activePowers[slot] = powerId;
    }
    
    public Power getActivePower(int slot) {
        return Power.getPower(slot, activePowers[slot]);
    }
    
    public List<Power> getActivePowers() {
        List<Power> powers = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            powers.add(Power.getPower(i, activePowers[i]));
        }
        return powers;
    }
    
    
    
    @Override
    public void printStats(DBPlayer dbp) {
        for (Map.Entry<ArenaMode, Integer> rating : ratings.entrySet()) {
            dbp.getPlayer().sendMessage(Chat.DEFAULT + "[" + Chat.GAMEMODE + rating.getKey().getDisplayName() + Chat.DEFAULT + "]: " +
                    Chat.ELO + rating.getValue());
        }
    }
    
}
