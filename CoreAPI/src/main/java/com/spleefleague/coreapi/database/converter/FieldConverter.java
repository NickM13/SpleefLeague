package com.spleefleague.coreapi.database.converter;

import java.lang.reflect.Field;

/**
 * @author NickM13
 */
public abstract class FieldConverter<F, D> {

    public abstract F toField(Class<?> fieldType, D d);

    public abstract D toDocumentable(Object obj);

}
