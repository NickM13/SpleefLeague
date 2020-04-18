/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.spleef.game;

import com.spleefleague.core.Core;
import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.database.variable.DBPlayer;
import com.spleefleague.core.game.BattlePlayer;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.spleef.game.spleef.SpleefBattlePlayer;
import com.spleefleague.spleef.player.SpleefPlayer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

/**
 * @author NickM13
 */
public abstract class SpleefBattleTeamed extends SpleefBattle {
    
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
    
    protected static class BattleTeam {
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
    protected Map<DBPlayer, BattleTeam> playerTeamMap;
    
    protected int remainingTeams = 0;
    
    /**
     * Pass only the lead players to this constructor, rest of players
     * are added in based on party
     * 
     * @param leadPlayers Party Lead Players
     * @param arena SpleefArena
     */
    public SpleefBattleTeamed(List<CorePlayer> leadPlayers,
                              SpleefArena arena,
                              Class<? extends BattlePlayer> battlePlayerClass) {
        super(leadPlayers, arena, battlePlayerClass);
    }
    
    @Override
    public void updateScoreboard() {
        chatGroup.setScoreboardName(Chat.DEFAULT + getRuntimeString() + "     " + Chat.SCORE + "Score");
    }

    @Override
    public void updateField() {

    }

    @Override
    public void updateExperience() {

    }

    /*
    @Override
    protected void setupPlayers() {
        teams = new HashSet<>();
        playerTeamMap = new HashMap<>();
        int teamNum = 0;
        Set<SpleefPlayer> playersNew = new HashSet<>();
        for (CorePlayer dbp : players) {
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
    }
    */
    
    protected ItemStack createColoredArmor(Material leatherArmor, org.bukkit.Color color) {
        ItemStack itemStack = new ItemStack(leatherArmor);
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta instanceof LeatherArmorMeta)
            ((LeatherArmorMeta) itemMeta).setColor(color);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    @Override
    protected void setupBattlers() {
        int i = 0;
        for (BattlePlayer bp : battlers.values()) {
            bp.getPlayer().getInventory().setHelmet(createColoredArmor(Material.LEATHER_HELMET, playerTeamMap.get(bp.getCorePlayer()).getTeamName().getColor()));
            bp.getPlayer().getInventory().setChestplate(createColoredArmor(Material.LEATHER_CHESTPLATE, playerTeamMap.get(bp.getCorePlayer()).getTeamName().getColor()));
            bp.getPlayer().getInventory().setLeggings(createColoredArmor(Material.LEATHER_LEGGINGS, playerTeamMap.get(bp.getCorePlayer()).getTeamName().getColor()));
            bp.getPlayer().getInventory().setBoots(createColoredArmor(Material.LEATHER_BOOTS, playerTeamMap.get(bp.getCorePlayer()).getTeamName().getColor()));
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
    protected void startRound() {
        super.startRound();
        for (BattleTeam team : teams) {
            team.remainingPlayers = team.players.size();
        }
        remainingTeams = teams.size();
        for (CorePlayer dbp : battlers.keySet()) {
            dbp.setGameMode(gameMode);
        }
    }

    /**
     * Applies elo change to all players in the battle
     * ELO change is 20 if players are the same rank
     * exponentially increasing/decreasing between (5, 40)
     *
     * @param winner Winning BattlePlayer
     */
    @Override
    protected void applyEloChange(BattlePlayer winner) {

    }

    /**
     * ELO change is 20 if players are the same rank
     * exponentially increasing/decreasing between (5, 40)
     */
    protected void applyEloChange(BattleTeam winner) {
        int avgRating = 0;
        int avgWinnerRating = 0;
        int eloChange = 0;
        
        for (BattlePlayer bp : battlers.values()) {
            avgRating += bp.getCorePlayer().getRating(getMode());
        }
        for (SpleefPlayer sp : winner.players) {
            avgWinnerRating += sp.getRating(getMode());
        }
        avgRating /= battlers.size();
        avgWinnerRating /= winner.players.size();
        if (avgWinnerRating > avgRating) {
            eloChange = 20 - (int)(Math.min((avgWinnerRating - avgRating) / 100.0, 1) * 15);
        } else {
            eloChange = 20 + (int)(Math.min((avgRating - avgWinnerRating) / 100.0, 1) * 20);
        }
        
        for (BattlePlayer bp : battlers.values()) {
            if (playerTeamMap.get(bp.getCorePlayer()).equals(winner)) {
                bp.getCorePlayer().addRating(getMode(), eloChange);
            } else {
                bp.getCorePlayer().addRating(getMode(), -eloChange);
            }
        }
    }
    
    @Override
    public void surrender(CorePlayer cp) {
        if (battlers.containsKey(cp)) {
            BattlePlayer bp = battlers.get(cp);
            BattleTeam bt = playerTeamMap.get(cp);
            chatGroup.sendMessage(bt.getTeamName().getName() + " has surrender");
            if (battlers.size() <= 1) {
                Core.getInstance().sendMessage(Chat.PLAYER_NAME + cp.getDisplayName() +
                        Chat.DEFAULT + " has surrendered");
                endBattle();
            } else {
                SpleefBattlePlayer winner =
                        (SpleefBattlePlayer) (battlers.keySet().toArray()[0].equals(cp)
                                ? battlers.values().toArray()[1]
                                : battlers.values().toArray()[0]);
                //endBattle(winner);
                endBattle();
                /*
                Core.getInstance().sendMessage(Chat.PLAYER_NAME + sp.getDisplayName() +
                        Chat.DEFAULT + " has surrendered to " +
                        Chat.PLAYER_NAME + winner.player.getDisplayName());
                */
            }
        }
    }
    
}
