package com.spleefleague.coreapi.player.purse.currency;

import com.spleefleague.coreapi.chat.Chat;
import com.spleefleague.coreapi.database.annotation.DBField;
import com.spleefleague.coreapi.database.variable.DBEntity;

public abstract class Currency extends DBEntity {

    @DBField protected int amount = 0;

    public Currency() {

    }

    public abstract String getIdentifier();

    public abstract String getChatColor();

    public abstract String getName();

    public int getAmount() {
        return amount;
    }

    public String addAmount(int amt) {
        if (amt == 0) return "";
        amount += amt;
        return getChatColor() + " +" + amt + " " + getName() + (amt != 1 ? "s" : "") + Chat.DEFAULT + " | " + getChatColor() + amount + Chat.DEFAULT + " Total";
    }

    public void setAmount(int amt) {
        amount = amt;
    }

}
