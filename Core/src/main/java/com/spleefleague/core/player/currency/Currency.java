package com.spleefleague.core.player.currency;

import com.spleefleague.coreapi.database.variable.DBVariable;
import org.bson.Document;

public class Currency extends DBVariable<Document> {

    protected int amount = 0;

    public Currency() {

    }

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

    public void addAmount(int amt) {
        amount += amt;
    }

    public void setAmount(int amt) {
        amount = amt;
    }

}
