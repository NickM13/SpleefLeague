/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.spleef.game;

import com.spleefleague.core.Core;
import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.chat.ChatChannel;
import com.spleefleague.core.party.Party;
import com.spleefleague.core.player.BattleState;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.util.database.DBPlayer;
import com.spleefleague.spleef.Spleef;
import com.spleefleague.spleef.player.SpleefPlayer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

/**
 * @author NickM13
 */
public class SpleefBattleTeam extends SpleefBattle {
    
    protected enum TeamName {
        BLUE(ChatColor.BLUE + "Blue", org.bukkit.Color.fromRGB(0, 0, 255)),
        RED(ChatColor.RED + "Red", org.bukkit.Color.fromRGB(255, 0, 0)),
        YELLOW(ChatColor.YELLOW + "Yellow", org.bukkit.Color.fromRGB(255, 255, 0)),
        GREEN(ChatColor.GREEN + "Green", org.bukkit.Color.fromRGB(0, 255, 0));
        
        String name;
        org.bukkit.Color color;
        
        TeamName(String name, org.bukkit.Color color) {
            this.name = name;
            this.color = color;
        }
        
        public String getName() {
            return name;
        }
        
        public org.bukkit.Color getColor() {
            return color;
        }
    }
    
    protected class BattleTeam {
        TeamName name;
        List<SpleefPlayer> players;
        int remainingPlayers;
        int points;
        
        BattleTeam(TeamName name) {
            this.name = name;
            players = new ArrayList<>();
            remainingPlayers = 0;
            points = 0;
        }
        
        public TeamName getTeamName() {
            return name;
        }
    }
    
    protected Set<BattleTeam> teams;
    protected Map<SpleefPlayer, BattleTeam> playerTeamMap;
    
    protected int remainingTeams = 0;
    
    /**
     * Pass only the lead players to this constructor, rest of players
     * are added in based on party
     * 
     * @param leadPlayers
     * @param arena
     */
    public SpleefBattleTeam(List<DBPlayer> leadPlayers, SpleefArena arena) {
        super(leadPlayers, arena);
    }
    
    @Override
    public void updateScoreboard() {
        chatGroup.setScoreboardName(Chat.DEFAULT + getRuntimeString() + "     " + Chat.SCORE + "Score");
        //chatGroup.setTeamScore("PlayTo", playToPoints);
        for (BattleTeam team : teams) {
            //chatGroup.setTeamScore(team.getTeamName().getName(), team.points);
        }
    }
    
    @Override
    protected void setupScoreboardTeams() {
        for (BattleTeam team : teams) {
            chatGroup.addTeam(team.getTeamName().getName(), team.getTeamName().getName());
        }
    }
    
    @Override
    protected void setupPlayers() {
        teams = new HashSet<>();
        playerTeamMap = new HashMap<>();
        int teamNum = 0;
        Set<SpleefPlayer> playersNew = new HashSet<>();
        for (SpleefPlayer dbp : players) {
            CorePlayer cp = Core.getInstance().getPlayers().get(dbp);
            Party party = cp.getParty();
            BattleTeam team = new BattleTeam(TeamName.values()[teamNum]);
            teamNum++;
            for (CorePlayer cp2 : party.getPlayers()) {
                SpleefPlayer sp = Spleef.getInstance().getPlayers().get(cp2);
                playersNew.add(sp);
                team.players.add(sp);
                playerTeamMap.put(sp, team);
            }
            teams.add(team);
        }
        players.clear();
        players.addAll(playersNew);
        super.setupPlayers();
    }
    
    protected ItemStack createColoredArmor(Material leatherArmor, org.bukkit.Color color) {
        ItemStack itemStack = new ItemStack(leatherArmor);
        ItemMeta itemMeta = itemStack.getItemMeta();
        ((LeatherArmorMeta) itemMeta).setColor(color);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }
    
    @Override
    protected void setupBattlers() {
        int i = 0;
        for (SpleefPlayer sp : players) {
            battlers.put(sp, new BattlePlayer(sp, arena.getSpawns().get(i)));
            sp.joinBattle(this, BattleState.BATTLER);
            
            sp.getPlayer().getInventory().setHeldItemSlot(0);
            sp.getPlayer().getInventory().clear();
            
            sp.getPlayer().getInventory().setHelmet(createColoredArmor(Material.LEATHER_HELMET, playerTeamMap.get(sp).getTeamName().getColor()));
            sp.getPlayer().getInventory().setChestplate(createColoredArmor(Material.LEATHER_CHESTPLATE, playerTeamMap.get(sp).getTeamName().getColor()));
            sp.getPlayer().getInventory().setLeggings(createColoredArmor(Material.LEATHER_LEGGINGS, playerTeamMap.get(sp).getTeamName().getColor()));
            sp.getPlayer().getInventory().setBoots(createColoredArmor(Material.LEATHER_BOOTS, playerTeamMap.get(sp).getTeamName().getColor()));
            
            sp.setGameMode(GameMode.SURVIVAL);
            
            chatGroup.addPlayer(sp);
            i++;
        }
    }
    
    @Override
    protected void sendStartMessage() {
        Core.getInstance().sendMessage("A " +
                Chat.GAMEMODE + getMode().getDisplayName() + " " +
                Chat.DEFAULT + "match between " +
                "some players" +
                Chat.DEFAULT + " has begun on " +
                Chat.GAMEMAP + arena.getDisplayName());
    }
    
    @Override
    protected void startBattle() {
        super.startBattle();
    }
    
    @Override
    protected void startRound() {
        super.startRound();
        for (BattleTeam team : teams) {
            team.remainingPlayers = team.players.size();
        }
        remainingTeams = teams.size();
        for (SpleefPlayer sp : battlers.keySet()) {
            sp.setGameMode(gameMode);
        }
    }
    
    protected void endRound(BattleTeam winner) {
        winner.points++;
        if (winner.points < playToPoints) {
            Core.getInstance().sendMessage(chatGroup, winner.getTeamName().getName() + Chat.DEFAULT + " team won the round");
        } else {
            endBattle(winner);
        }
    }
    
    protected void applyEloChange(BattleTeam winner) {
        int avgRating = 0;
        int avgWinnerRating = 0;
        int eloChange = 0;
        
        for (BattlePlayer bp : battlers.values()) {
            avgRating += bp.player.getRating(getMode());
        }
        for (SpleefPlayer sp : winner.players) {
            avgWinnerRating += sp.getRating(getMode());
        }
        avgRating /= battlers.size();
        avgWinnerRating /= winner.players.size();
        /**
         * ELO change is 20 if players are the same rank
         * exponentially increasing/decreasing between (5, 40)
         */
        if (avgWinnerRating > avgRating) {
            eloChange = 20 - (int)(Math.min((avgWinnerRating - avgRating) / 100.0, 1) * 15);
        } else {
            eloChange = 20 + (int)(Math.min((avgRating - avgWinnerRating) / 100.0, 1) * 20);
        }
        
        for (BattlePlayer sbp : battlers.values()) {
            if (playerTeamMap.get(sbp.player).equals(winner)) {
                sbp.player.addRating(getMode(), eloChange);
            } else {
                sbp.player.addRating(getMode(), -eloChange);
            }
        }
    }
    
    protected void endBattle(BattleTeam winner) {
        Core.getInstance().sendMessage(ChatChannel.getChannel(ChatChannel.Channel.SPLEEF),
                Chat.PLAYER_NAME + winner.getTeamName().getName() +
                Chat.DEFAULT + " has " + this.randomDefeatSynonym() + " all other teams " +
                Chat.DEFAULT + " in " +
                Chat.GAMEMODE + getMode().getDisplayName());
        applyEloChange(winner);
        super.endBattle();
    }
    
    @Override
    protected void failPlayer(SpleefPlayer sp) {
        for (BattlePlayer bp : battlers.values()) {
            if (bp.player.equals(sp)) {
                if (bp.fallen) return;
                bp.fallen = true;
                BattleTeam team = playerTeamMap.get(sp);
                team.remainingPlayers--;
                if (team.remainingPlayers <= 0) {
                    remainingTeams--;
                }
                bp.player.setGameMode(GameMode.SPECTATOR);
                gameWorld.doFailBlast(sp.getPlayer());
                break;
            }
        }
        if (remainingTeams <= 1) {
            BattleTeam winTeam = null;
            for (BattleTeam team : teams) {
                winTeam = team;
                if (team.remainingPlayers > 0) {
                    break;
                }
            }
            endRound(winTeam);
            startRound();
        }
        updateScoreboard();
    }
    
    @Override
    public boolean surrender(SpleefPlayer sp) {
        if (battlers.containsKey(sp)) {
            BattlePlayer bp = battlers.get(sp);
            BattleTeam bt = playerTeamMap.get(sp);
            chatGroup.sendMessage(bt.getTeamName().getName() + " has surrender");
            if (battlers.size() <= 1) {
                Core.getInstance().sendMessage(Chat.PLAYER_NAME + sp.getDisplayName() +
                        Chat.DEFAULT + " has surrendered");
                endBattle();
                return true;
            } else {
                BattlePlayer winner = (BattlePlayer) (battlers.keySet().toArray()[0].equals(sp) ? battlers.values().toArray()[1] : battlers.values().toArray()[0]);
                endBattle(winner);
                Core.getInstance().sendMessage(Chat.PLAYER_NAME + sp.getDisplayName() +
                        Chat.DEFAULT + " has surrendered to " +
                        Chat.PLAYER_NAME + winner.player.getDisplayName());
            }
            return true;
        }
        return false;
    }
    
}
