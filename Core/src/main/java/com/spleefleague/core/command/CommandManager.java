/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.command;

import com.spleefleague.core.Core;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;

/**
 * @author NickM13
 */
public class CommandManager {
    
    private final ArrayList<CommandTemplate> commandsToRegister = new ArrayList<>();
    private CommandMap commandMap = null;
    
    public CommandManager() {
        try {
            final Field bukkitCommandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            
            bukkitCommandMap.setAccessible(true);
            commandMap = (CommandMap) bukkitCommandMap.get(Bukkit.getServer());
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException ex) {
            Logger.getLogger(Core.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void flushRegisters() {
        for (CommandTemplate ct : commandsToRegister) {
            commandMap.register("sl", ct);
        }
        commandsToRegister.clear();
    }
    
    public void addCommand(CommandTemplate command) {
        commandsToRegister.add(command);
    }
    
}
