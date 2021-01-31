package com.spleefleague.coreapi.database.converter.converters;

import com.spleefleague.coreapi.database.converter.FieldConverter;

/**
 * @author NickM13
 */
public class EnumConverter extends FieldConverter<Enum<?>, String> {

    @Override
    public Enum<?> toField(Class<?> fieldType, String docObj) {
        return Enum.valueOf((Class<Enum>) fieldType, docObj);
    }

    @Override
    public String toDocumentable(Object fieldObj) {
        return ((Enum<?>) fieldObj).name();
    }

}
