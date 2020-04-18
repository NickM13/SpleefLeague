/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.spleef.player;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.spleefleague.core.Core;
import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.database.annotation.DBLoad;
import com.spleefleague.core.database.annotation.DBSave;
import com.spleefleague.core.database.variable.DBPlayer;
import com.spleefleague.core.game.ArenaMode;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.spleef.Spleef;
import com.spleefleague.spleef.game.spleef.power.Power;
import com.spleefleague.spleef.game.Shovel;
import java.util.ArrayList;
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
public class SpleefPlayer extends DBPlayer {
    
    private static final Integer BASE_RATING = 1000;

    protected Shovel activeShovel;
    protected Set<Integer> shovels = new HashSet<>();
    
    //@DBField
    protected Integer[] activePowers = new Integer[4];
    
    protected Map<ArenaMode, Integer> ratings = new HashMap<>();
    
    public SpleefPlayer() {
        super();
        activeShovel = Shovel.getDefault();
        for (int i = 0; i < 4; i++) {
            activePowers[i] = Power.getDefaultPower(i).getDamage();
        }
    }

    @Override
    public void init() { }

    @Override
    public void close() { }

    @DBLoad(fieldName="activeShovel")
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
            if (shovel != null)
                cp.setSelectedItem(shovel.getType(), shovel.getIdentifier());
        });
    }
    @DBSave(fieldName="activeShovel")
    private Integer saveActiveShovel() {
        return activeShovel.getDamage();
    }
    
    @DBLoad(fieldName="activePowers")
    private void loadActivePowers(List<Integer> powers) {
        if (powers == null) return;
        for (int i = 0; i < Math.min(4, powers.size()); i++) {
            activePowers[i] = powers.get(i);
        }
    }
    
    @DBSave(fieldName="activePowers")
    private List<Integer> saveActivePowers() {
        return Lists.newArrayList(activePowers);
    }
    
    @DBLoad(fieldName="ratings")
    private void loadRatings(List<Object> ratings) {
        if (ratings == null) return;
        Document d;
        for (Object o : ratings) {
            d = (Document) o;
            this.ratings.put(ArenaMode.getArenaMode(d.get("mode", String.class)), d.get("rating", Integer.class));
        }
    }
    @DBSave(fieldName="ratings")
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
    
    @DBLoad(fieldName="shovels")
    private void loadShovels(List<Integer> list) {
        if (list == null) return;
        shovels = Sets.newHashSet(list);
    }
    @DBSave(fieldName="shovels")
    private List<Integer> saveShovels() {
        if (shovels == null) return new ArrayList<>();
        return Lists.newArrayList(shovels);
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
            }
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
    @Deprecated
    public void printStats(DBPlayer dbp) {
        for (Map.Entry<ArenaMode, Integer> rating : ratings.entrySet()) {
            dbp.getPlayer().sendMessage(Chat.DEFAULT + "[" + Chat.GAMEMODE + rating.getKey().getDisplayName() + Chat.DEFAULT + "]: " +
                    Chat.ELO + rating.getValue());
        }
    }
    
}
