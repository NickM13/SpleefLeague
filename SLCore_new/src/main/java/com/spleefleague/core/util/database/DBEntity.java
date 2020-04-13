/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.util.database;

import com.google.common.collect.Sets;
import com.spleefleague.core.annotation.DBField;
import com.spleefleague.core.annotation.DBLoad;
import com.spleefleague.core.annotation.DBSave;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bson.Document;
import org.bson.types.ObjectId;

/**
 * @author NickM13
 */
public class DBEntity {
    
    @DBField
    protected ObjectId _id;
    
    public ObjectId getId() {
        return _id;
    }
    
    public Document save() {
        Document doc = new Document();
        Class clazz = this.getClass();
        while (clazz != null) {
            for (Field f : clazz.getDeclaredFields()) {
                if (f.isAnnotationPresent(DBField.class)) {
                    try {
                        String fieldname;
                        f.setAccessible(true);
                        if (f.getName().equals("_id") || f.get(this) == null) continue;
                        if ((fieldname = f.getAnnotation(DBField.class).fieldname()).equals("")) {
                            fieldname = f.getName();
                        }
                        if (DBVariable.class.isAssignableFrom(f.getType())) {
                            for (Method method : f.getType().getMethods()) {
                                if (method.getName().equalsIgnoreCase("save")) {
                                    try {
                                        doc.append(fieldname, method.invoke(f.get(this)));
                                    } catch (InvocationTargetException ex) {
                                        Logger.getLogger(DBEntity.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                    break;
                                }
                            }
                        } else if (UUID.class.isAssignableFrom(f.getType())) {
                            doc.append(fieldname, ((UUID) f.get(this)).toString());
                        } else if (DBEntity.class.isAssignableFrom(f.getType())) {
                            DBEntity dbe = (DBEntity) f.get(this);
                            doc.append(fieldname, dbe.save());
                        } else if (f.getAnnotation(DBField.class).serializer() != Object.class) {
                            for (Method method : f.getAnnotation(DBField.class).serializer().getMethods()) {
                                if (method.getName().equalsIgnoreCase("save")) {
                                    try {
                                        doc.append(fieldname, method.invoke(null, f.get(this)));
                                    } catch (InvocationTargetException ex) {
                                        Logger.getLogger(DBEntity.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                    break;
                                }
                            }
                        } else {
                            doc.append(fieldname, f.get(this));
                        }
                    } catch (IllegalArgumentException | IllegalAccessException ex) {
                        Logger.getLogger(DBEntity.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
            for (Method m : clazz.getDeclaredMethods()) {
                if (m.isAnnotationPresent(DBSave.class)) {
                    String fieldname = m.getAnnotation(DBSave.class).fieldname();
                    try {
                        m.setAccessible(true);
                        Object o = m.invoke(this);
                        if (o != null) doc.append(fieldname, o);
                    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                        Logger.getLogger(DBEntity.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
            clazz = clazz.getSuperclass();
        }
        return doc;
    }
    
    public void load(Document doc) {
        Class clazz = this.getClass();
        while (clazz != null) {
            for (Field f : clazz.getDeclaredFields()) {
                if (f.isAnnotationPresent(DBField.class)) {
                    try {
                        f.setAccessible(true);
                        String fieldname = f.getAnnotation(DBField.class).fieldname();
                        if (fieldname.equals("")) fieldname = f.getName();
                        if (!doc.containsKey(fieldname)) continue;
                        Object obj = null;
                        if (f.getType().isEnum()) {
                            obj = Enum.valueOf((Class<Enum>)f.getType(), doc.get(fieldname, String.class));
                        } else if (DBVariable.class.isAssignableFrom(f.getType())) {
                            for (Method method : f.getType().getMethods()) {
                                if (method.getName().equalsIgnoreCase("load")) {
                                    obj = f.getType().newInstance();
                                    method.invoke(obj, doc.get(fieldname, method.getParameters()[0].getType()));
                                    break;
                                }
                            }
                        } else if (f.getType().equals(UUID.class)) {
                            obj = UUID.fromString(doc.get(fieldname, String.class));
                        } else if (DBEntity.class.isAssignableFrom(f.getType())) {
                            obj = f.getType().newInstance();
                            ((DBEntity) obj).load(doc.get(fieldname, Document.class));
                        } else if (f.getAnnotation(DBField.class).serializer() != Object.class) {
                            for (Method method : f.getAnnotation(DBField.class).serializer().getMethods()) {
                                if (method.getName().equalsIgnoreCase("load")) {
                                    obj = method.invoke(null, doc.get(fieldname, method.getParameters()[0].getType()));
                                    break;
                                }
                            }
                        } else if (Set.class.isAssignableFrom(f.getType())) {
                            obj = Sets.newHashSet(doc.get(fieldname, List.class));
                        } else {
                            obj = doc.get(fieldname, f.getType());
                        }
                        if (obj != null) {
                            if (f.getType().isAssignableFrom(obj.getClass())) {
                                f.set(this, obj);
                            }
                        }
                    } catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException | InstantiationException ex) {
                        Logger.getLogger(DBEntity.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
            for (Method m : clazz.getDeclaredMethods()) {
                if (m.isAnnotationPresent(DBLoad.class)) {
                    String fieldname = m.getAnnotation(DBLoad.class).fieldname();
                    if (!doc.containsKey(fieldname)) continue;
                    try {
                        m.setAccessible(true);
                        if (DBVariable.class.isAssignableFrom(m.getParameters()[0].getType())) {
                            for (Method method : m.getParameters()[0].getType().getMethods()) {
                                if (method.getName().equalsIgnoreCase("load")) {
                                    try {
                                        Object o = m.getParameters()[0].getType().newInstance();
                                        method.invoke(o, doc.get(fieldname, method.getParameters()[0].getType()));
                                        m.invoke(this, o);
                                    } catch (InstantiationException ex) {
                                        Logger.getLogger(DBEntity.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                    break;
                                }
                            }
                        } else {
                            Object docobj = doc.get(fieldname);
                            if (docobj != null && m.getParameters()[0].getType().isAssignableFrom(docobj.getClass())) {
                                m.invoke(this, doc.get(fieldname, m.getParameters()[0].getType()));
                            }
                        }
                    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                        Logger.getLogger(DBEntity.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
            clazz = clazz.getSuperclass();
        }
    }
    
}
