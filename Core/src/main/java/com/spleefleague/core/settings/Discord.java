package com.spleefleague.core.settings;

import com.mongodb.client.MongoCollection;
import com.spleefleague.coreapi.database.annotation.DBField;
import com.spleefleague.coreapi.database.variable.DBEntity;
import org.bson.Document;

/**
 * @author NickM13
 */
public class Discord extends DBEntity {

    @DBField private String url = "";

    public Discord() {
        identifier = "discord";
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

}
