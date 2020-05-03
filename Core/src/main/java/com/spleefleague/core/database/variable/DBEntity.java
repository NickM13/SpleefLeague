/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.database.variable;

import com.google.common.collect.Sets;
import com.spleefleague.core.database.annotation.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.spleefleague.core.logger.CoreLogger;
import org.bson.Document;
import org.bson.types.ObjectId;

/**
 * A DBEntity is an object that can be
 * saved and loaded from a JSON Document
 *
 * @author NickM13
 */
public class DBEntity {
    
    protected ObjectId _id;
    
    public ObjectId getId() {
        return _id;
    }
    
    /**
     * Converts the DBEntity into a document
     *
     * @return Document
     */
    public Document save() {
        Document doc = new Document();
        Class<?> clazz = this.getClass();
        while (clazz != null && DBEntity.class.isAssignableFrom(clazz)) {
            for (Field f : clazz.getDeclaredFields()) {
                if (f.isAnnotationPresent(DBField.class)) {
                    try {
                        String fieldName;
                        f.setAccessible(true);
                        if (f.getName().equals("_id") || f.get(this) == null) continue;
                        if ((fieldName = f.getAnnotation(DBField.class).fieldName()).equals("")) {
                            fieldName = f.getName();
                        }
                        if (DBVariable.class.isAssignableFrom(f.getType())) {
                            doc.append(fieldName, ((DBVariable<?>) f.get(this)).save());
                        } else if (UUID.class.isAssignableFrom(f.getType())) {
                            doc.append(fieldName, ((UUID) f.get(this)).toString());
                        } else if (DBEntity.class.isAssignableFrom(f.getType())) {
                            DBEntity dbe = (DBEntity) f.get(this);
                            doc.append(fieldName, dbe.save());
                        } else if (f.getAnnotation(DBField.class).serializer() != Object.class) {
                            for (Method method : f.getAnnotation(DBField.class).serializer().getMethods()) {
                                if (method.getName().equalsIgnoreCase("save")) {
                                    try {
                                        doc.append(fieldName, method.invoke(null, f.get(this)));
                                    } catch (InvocationTargetException ex) {
                                        Logger.getLogger(DBEntity.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                    break;
                                }
                            }
                        } else {
                            doc.append(fieldName, f.get(this));
                        }
                    } catch (IllegalArgumentException | IllegalAccessException | NullPointerException ex) {
                        Logger.getLogger(DBEntity.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
            for (Method m : clazz.getDeclaredMethods()) {
                if (m.isAnnotationPresent(DBSave.class)) {
                    String fieldName = m.getAnnotation(DBSave.class).fieldName();
                    try {
                        m.setAccessible(true);
                        Object o = m.invoke(this);
                        if (o != null) doc.append(fieldName, o);
                    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                        Logger.getLogger(DBEntity.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
            clazz = clazz.getSuperclass();
        }
        return doc;
    }
    
    /**
     * Loads fields from a Document into the DBEntity
     *
     * @param doc Document
     * @return Success
     */
    public final boolean load(Document doc) {
        if (doc.containsKey("_id")) _id = doc.getObjectId("_id");
        Class<?> clazz = this.getClass();
        while (clazz != null && DBEntity.class.isAssignableFrom(clazz)) {
            for (Field f : clazz.getDeclaredFields()) {
                if (f.isAnnotationPresent(DBField.class)) {
                    try {
                        f.setAccessible(true);
                        String fieldName = f.getAnnotation(DBField.class).fieldName();
                        if (fieldName.equals("")) fieldName = f.getName();
                        if (doc.get(fieldName) == null) continue;
                        Object obj = null;
                        if (Enum.class.isAssignableFrom(f.getType())) {
                            obj = f.getType().getDeclaredMethod("valueOf", String.class).invoke(f.getType(), doc.get(fieldName, String.class));
                        } else if (DBVariable.class.isAssignableFrom(f.getType())) {
                            for (Method method : f.getType().getMethods()) {
                                if (method.getName().equalsIgnoreCase("load")) {
                                    obj = f.getType().getDeclaredConstructor().newInstance();
                                    method.invoke(obj, doc.get(fieldName, method.getParameters()[0].getType()));
                                    break;
                                }
                            }
                        } else if (f.getType().equals(UUID.class)) {
                            obj = UUID.fromString(doc.get(fieldName, String.class));
                        } else if (DBEntity.class.isAssignableFrom(f.getType())) {
                            obj = f.getType().getDeclaredConstructor().newInstance();
                            ((DBEntity) obj).load(doc.get(fieldName, Document.class));
                        } else if (f.getAnnotation(DBField.class).serializer() != Object.class) {
                            for (Method method : f.getAnnotation(DBField.class).serializer().getMethods()) {
                                if (method.getName().equalsIgnoreCase("load")) {
                                    obj = method.invoke(null, doc.get(fieldName, method.getParameters()[0].getType()));
                                    break;
                                }
                            }
                        } else if (Set.class.isAssignableFrom(f.getType())) {
                            obj = Sets.newHashSet(doc.get(fieldName, List.class));
                        } else {
                            obj = doc.get(fieldName, f.getType());
                        }
                        if (obj != null) {
                            if (f.getType().isAssignableFrom(obj.getClass())) {
                                f.set(this, obj);
                            }
                        }
                    } catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException | InstantiationException | NoSuchMethodException exception) {
                        CoreLogger.logError(exception.getMessage());
                        return false;
                    }
                }
            }
            for (Method m : clazz.getDeclaredMethods()) {
                if (m.isAnnotationPresent(DBLoad.class)) {
                    String fieldName = m.getAnnotation(DBLoad.class).fieldName();
                    if (!doc.containsKey(fieldName)) continue;
                    try {
                        m.setAccessible(true);
                        if (DBVariable.class.isAssignableFrom(m.getParameters()[0].getType())) {
                            for (Method method : m.getParameters()[0].getType().getMethods()) {
                                if (method.getName().equalsIgnoreCase("load")) {
                                    try {
                                        Object o = m.getParameters()[0].getType().getDeclaredConstructor().newInstance();
                                        method.invoke(o, doc.get(fieldName, method.getParameters()[0].getType()));
                                        m.invoke(this, o);
                                    } catch (InstantiationException | NoSuchMethodException ex) {
                                        Logger.getLogger(DBEntity.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                    break;
                                }
                            }
                        } else {
                            Object docobj = doc.get(fieldName);
                            if (docobj != null && m.getParameters()[0].getType().isAssignableFrom(docobj.getClass())) {
                                m.invoke(this, doc.get(fieldName, m.getParameters()[0].getType()));
                            }
                        }
                    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException exception) {
                        CoreLogger.logError(exception.getMessage());
                        return false;
                    }
                }
            }
            clazz = clazz.getSuperclass();
        }
        afterLoad();
        return true;
    }
    
    /**
     * Called after load to further initialize anything
     */
    public void afterLoad() {
    
    }
    
}
