/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.io.converter;

import org.bukkit.ChatColor;

/**
 * @author NickM13
 */
public class ChatColorConverter {
    
    public static ChatColor load(String str) {
        return ChatColor.valueOf(str);
    }
    
    public static String save(ChatColor color) {
        return color.toString();
    }
    
}
