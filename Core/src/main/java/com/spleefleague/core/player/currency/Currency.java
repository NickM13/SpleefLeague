package com.spleefleague.core.player.currency;

import com.spleefleague.core.chat.Chat;
import com.spleefleague.coreapi.database.variable.DBVariable;
import org.bson.Document;
import org.bukkit.ChatColor;
import org.bukkit.Material;

public abstract class Currency extends DBVariable<Document> {

    protected int amount = 0;

    public Currency() {

    }

    public abstract String getIdentifier();

    public abstract String getChatColor();

    public abstract String getName();

    public abstract Material getDisplayIcon();

    @Override
    public void load(Document document) {
        amount = document.getInteger("amount");
    }

    @Override
    public Document save() {
        Document doc = new Document();
        doc.put("amount", amount);
        return doc;
    }

    public int getAmount() {
        return amount;
    }

    public String addAmount(int amt) {
        if (amt == 0) return "";
        amount += amt;
        return Chat.DEFAULT + " You have earned " + ChatColor.GREEN + amount + " " + getName() + (amt != 1 ? "s" : "");
    }

    public void setAmount(int amt) {
        amount = amt;
    }

}
