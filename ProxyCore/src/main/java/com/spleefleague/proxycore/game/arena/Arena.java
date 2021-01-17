/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.proxycore.game.arena;

import java.util.HashSet;
import java.util.Set;

import com.spleefleague.coreapi.database.annotation.DBField;
import com.spleefleague.coreapi.database.variable.DBEntity;

/**
 * *** This is a read only version of Arena, see Core plugin for a writable version ***
 *
 * Arena is a set of variables loaded from a specified Database<br>
 * Arena is Used in Battle<br>
 * <br>
 * Arena Document:<br>
 * {<br>
 *     identifier:      <i>required</i>, Identifier name for commands<br>
 *     name:            <i>required</i>, Display name
 *     description:     <i>optional</i>, Description for arena<br>
 *     teamCount:       <i>optional</i>, Used for dynamically sized modes, number of teams (if team size is 1 this is number of players)<br>
 *     rated:           <i>optional</i>, Default true, Whether arena will apply an elo rating or not afterward<br>
 *     queued:          <i>optional</i>, Default true, If false, this arena can only be entered through challenges<br>
 *     paused:          <i>optional</i>, Default false, If true, arena cannot be played on<br>
 *     borders:         <i>optional</i>, List of dimensions that the arena is contained in<br>
 *     goals:           <i>optional</i>, List of dimensions that the arena defines as end points (SuperJump)<br>
 *     spectatorSpawn:  <i>optional</i>, Spawn location of spectators<br>
 *     modes:           <i>required</i>, List of mode names<br>
 *     spawns:          <i>optional</i>, List of spawn positions for battlers<br>
 *     checkpoints:     <i>optional</i>, List of checkpoint positions<br>
 *     structures:      <i>optional</i>, List of build structure names<br>
 * }
 *
 * @author NickM13
 */
public class Arena extends DBEntity {

    @DBField protected String name;
    @DBField protected Set<String> modes;
    @DBField protected Boolean paused = false;
    @DBField protected Integer teamCount = 1;
    @DBField protected Integer teamSize = 1;

    protected int ongoingMatches = 0;
    protected int ongoingQueues = 0;

    public Arena() {

    }

    public Arena(String identifier) {
        this.identifier = identifier;
        modes = new HashSet<>();
        paused = false;
    }

    /**
     * Returns the display name of this arena
     *
     * @return Display Name
     */
    public String getName() {
        return name;
    }

    /**
     * Return the modes this arena is used in
     *
     * @return Set of Modes
     */
    public Set<String> getModes() {
        return modes;
    }

    /**
     * Get the required number of players per team
     *
     * @return Team Size
     */
    public int getTeamSize() {
        return teamSize;
    }

    /**
     * Get the required number of teams (or players if teamSize = 1)
     *
     * @return Team Size
     */
    public int getTeamCount() {
        return teamCount;
    }

    /**
     * Set arena's pause state (can't start matches on paused arena)
     *
     * @param state Paused
     */
    public void setPaused(boolean state) {
        paused = state;
    }

    /**
     * Whether arena's queues are paused or not (can't start matches on paused arena)
     *
     * @return Paused
     */
    private boolean isPaused() {
        return paused;
    }

    /**
     * Add ongoing match
     */
    public void incrementMatches() {
        ongoingMatches++;
    }

    /**
     * Subtract ongoing match
     */
    public void decrementMatches() {
        ongoingMatches--;
    }

    /**
     * @return Ongoing Matches
     */
    public int getOngoingMatches() {
        return ongoingMatches;
    }

    /**
     * Returns whether arena can be used, for disabling arenas
     * during maintenance times
     *
     * @return Arena Availability
     */
    public boolean isAvailable() {
        return !isPaused();
    }

    /**
     * Add queued player
     */
    public void incrementQueues() {
        ongoingQueues++;
    }

    /**
     * Subtract queued player
     */
    public void decrementQueues() {
        ongoingQueues--;
    }

    /**
     * Get total number of queued players for this arena
     *
     * @return Queued Players
     */
    public int getOngoingQueues() {
        return ongoingQueues;
    }

}
