package com.spleefleague.coreapi.database.converter.converters;

import com.spleefleague.coreapi.database.converter.FieldConverter;

import java.util.UUID;

/**
 * @author NickM13
 */
public class UUIDConverter extends FieldConverter<UUID, String> {

    @Override
    public UUID toField(Class<?> fieldType, String docObj) {
        return UUID.fromString(docObj);
    }

    @Override
    public String toDocumentable(Object fieldObj) {
        return fieldObj.toString();
    }

}
