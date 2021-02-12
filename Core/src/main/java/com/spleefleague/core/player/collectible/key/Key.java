/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.player.collectible.key;

import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.collectible.Holdable;
import com.spleefleague.core.vendor.Vendorable;
import org.bukkit.Material;

/**
 * @author NickM13
 */
public class Key extends Holdable {

    public static void init() {
        Vendorable.registerParentType(Key.class);
    }

    public static void close() {

    }

    private static final Material DEFAULT_KEY_MAT = Material.BARRIER;

    /**
     * Constructor for DB loading
     */
    public Key() {
        super();
    }

    /**
     * Constructor for use with /key create
     *
     * @param identifier Identifier String
     * @param name       Display Name
     */
    public Key(String identifier, String name) {
        super();
        this.identifier = identifier;
        this.name = name;
        this.material = DEFAULT_KEY_MAT;
    }

    /**
     * Whether an item is available for purchasing for things
     * such as requiring prerequisites, levels or achievements
     *
     * @param cp Core Player
     * @return Availability
     */
    @Override
    public boolean isAvailableToPurchase(CorePlayer cp) {
        return false;
    }

    @Override
    public void onRightClick(CorePlayer cp) {

    }

}
