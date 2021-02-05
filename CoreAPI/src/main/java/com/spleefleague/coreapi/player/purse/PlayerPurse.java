package com.spleefleague.coreapi.player.purse;

import com.spleefleague.coreapi.database.annotation.DBField;
import com.spleefleague.coreapi.database.variable.DBEntity;

import java.util.HashMap;
import java.util.Map;

/**
 * @author NickM13
 */
public class PlayerPurse extends DBEntity {

    @DBField private Map<String, Integer> currencies = new HashMap<>();

    public int getCurrency(String currency) {
        return currencies.getOrDefault(currency, 0);
    }

    public int setCurrency(String currency, int amount) {
        currencies.put(currency, amount);
        return amount;
    }

    public int addCurrency(String currency, int amount) {
        int newAmount = currencies.getOrDefault(currency, 0) + amount;
        currencies.put(currency, newAmount);
        return newAmount;
    }

}
