/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.queue;

import java.util.ArrayList;

/**
 * @author NickM13
 */
public class QueueManager {
    private ArrayList<PlayerQueue> queues;
    
    public QueueManager() {}
    
    public void initialize() {
        queues = new ArrayList<>();
    }
    public void terminate() {
        queues.clear();
    }
    
    public void addQueue(PlayerQueue queue) {
        queues.add(queue);
    }
    
    public void removeQueue(PlayerQueue queue) {
        queues.remove(queue);
    }
    
    public ArrayList<PlayerQueue> getQueues() {
        return queues;
    }
}
