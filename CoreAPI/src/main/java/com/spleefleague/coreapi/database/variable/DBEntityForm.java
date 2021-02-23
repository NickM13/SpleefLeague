package com.spleefleague.coreapi.database.variable;

import com.spleefleague.coreapi.database.annotation.DBField;
import com.spleefleague.coreapi.database.annotation.DBLoad;
import com.spleefleague.coreapi.database.annotation.DBSave;
import org.bson.Document;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author NickM13
 * @since 2/18/2021
 */
public class DBEntityForm {

    private final Map<String, Field> readFields = new HashMap<>();
    private final Map<String, Field> writeFields = new HashMap<>();
    private final Map<String, Field> readTopFields = new HashMap<>();
    private final Map<String, Field> writeTopFields = new HashMap<>();

    private final Map<String, Method> readMethods = new HashMap<>();
    private final Map<String, Method> writeMethods = new HashMap<>();
    private final Map<String, Method> readTopMethods = new HashMap<>();
    private final Map<String, Method> writeTopMethods = new HashMap<>();

    public DBEntityForm(Class<? extends DBEntity> entityClazz) {
        Class<?> clazz = entityClazz;
        boolean isFirst = true;
        while (clazz != null && DBEntity.class.isAssignableFrom(clazz)) {
            for (Field field : clazz.getDeclaredFields()) {
                DBField dbField = field.getAnnotation(DBField.class);
                if (dbField != null) {
                    field.setAccessible(true);
                    String fieldName = dbField.fieldName().length() > 0 ? dbField.fieldName() : field.getName();
                    if (dbField.write()) {
                        writeFields.put(fieldName, field);
                        if (isFirst) writeTopFields.put(fieldName, field);
                    }
                    if (dbField.read()) {
                        readFields.put(fieldName, field);
                        if (isFirst) readTopFields.put(fieldName, field);
                    }
                }
            }
            for (Method method : clazz.getDeclaredMethods()) {
                if (method.isAnnotationPresent(DBSave.class)) {
                    String fieldName = method.getAnnotation(DBSave.class).fieldName();
                    method.setAccessible(true);
                    writeMethods.put(fieldName, method);
                    if (isFirst) writeTopMethods.put(fieldName, method);
                } else if (method.isAnnotationPresent(DBLoad.class)) {
                    String fieldName = method.getAnnotation(DBLoad.class).fieldName();
                    method.setAccessible(true);
                    readMethods.put(fieldName, method);
                    if (isFirst) readTopMethods.put(fieldName, method);
                }
            }
            clazz = clazz.getSuperclass();
            isFirst = false;
        }
    }

    public Map<String, Field> getReadFields() {
        return readFields;
    }

    public Map<String, Field> getWriteFields() {
        return writeFields;
    }

    public Map<String, Method> getReadMethods() {
        return readMethods;
    }

    public Map<String, Method> getWriteMethods() {
        return writeMethods;
    }

    public Map<String, Field> getReadTopFields() {
        return readTopFields;
    }

    public Map<String, Field> getWriteTopFields() {
        return writeTopFields;
    }

    public Map<String, Method> getReadTopMethods() {
        return readTopMethods;
    }

    public Map<String, Method> getWriteTopMethods() {
        return writeTopMethods;
    }
}
