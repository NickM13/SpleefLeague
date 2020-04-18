/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.util.variable;

import org.bukkit.Location;
import org.bukkit.util.Vector;

/**
 * @author NickM13
 */
public class TpCoord {
    protected enum TpOrigin {
        NONE, RELATIVE, DIRECTIONAL
    }
    
    public TpOrigin origin = TpOrigin.NONE;
    public Double value;

    private TpCoord(TpOrigin origin, Double value) {
        this.origin = origin;
        this.value = value;
    }
    
    public static TpCoord create(String str) {
        TpOrigin origin = (str.charAt(0) == '~' ? TpOrigin.RELATIVE : (str.charAt(0) == '^' ? TpOrigin.DIRECTIONAL : TpOrigin.NONE));
        Double value = null;
        try {
            if (origin.equals(TpOrigin.NONE)) {
                value = Double.parseDouble(str);
            } else if (str.length() > 1) {
                value = Double.parseDouble(str.substring(1));
            }
        } catch (NumberFormatException e) {
            return null;
        }
        return new TpCoord(origin, value);
    }
    
    public static void apply(Location loc, TpCoord x, TpCoord y, TpCoord z) {
        switch (x.origin) {
            case NONE:
                loc.setX(x.value);
                break;
            case RELATIVE:
                loc.add(new Vector(x.value, 0D, 0D));
                break;
            case DIRECTIONAL:
                loc.add(new Vector(x.value, 0D, 0D));
                break;
        }
        switch (y.origin) {
            case NONE:
                loc.setY(y.value);
                break;
            case RELATIVE:
                loc.add(new Vector(0D, y.value, 0D));
                break;
            case DIRECTIONAL:
                loc.add(new Vector(0D, y.value, 0D));
                break;
        }
        switch (z.origin) {
            case NONE:
                loc.setZ(z.value);
                break;
            case RELATIVE:
                loc.add(new Vector(0D, 0D, z.value));
                break;
            case DIRECTIONAL:
                loc.add(new Vector(0D, 0D, z.value));
                break;
        }
    }

}
