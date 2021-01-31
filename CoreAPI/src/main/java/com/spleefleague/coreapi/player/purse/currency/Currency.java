package com.spleefleague.coreapi.player.purse.currency;

import com.spleefleague.coreapi.chat.Chat;
import com.spleefleague.coreapi.database.variable.DBVariable;
import org.bson.Document;

public abstract class Currency extends DBVariable<Document> {

    protected int amount = 0;

    public Currency() {

    }

    public abstract String getIdentifier();

    public abstract String getChatColor();

    public abstract String getName();

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
        return getChatColor() + " +" + amt + " " + getName() + (amt != 1 ? "s" : "") + Chat.DEFAULT + " | " + getChatColor() + amount + Chat.DEFAULT + " Total";
    }

    public void setAmount(int amt) {
        amount = amt;
    }

}
