/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.coreapi.database.variable;

import com.mongodb.client.MongoCollection;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.spleefleague.coreapi.database.annotation.DBField;
import com.spleefleague.coreapi.database.annotation.DBLoad;
import com.spleefleague.coreapi.database.annotation.DBSave;
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
    @DBField
    protected String identifier;
    
    public ObjectId getId() {
        return _id;
    }

    /**
     * Returns the identifying name, a unique and typically human readable string
     *
     * @return
     */
    public String getIdentifier() {
        return identifier;
    }

    /**
     * Sets the identifying String of this vendorable<br>
     * Should only be used when cloning
     *
     * @param identifier Identifier String
     */
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }
    
    /**
     * Converts the DBEntity into a document
     *
     * @return Document
     */
    public Document toDocument() {
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
                        if (Enum.class.isAssignableFrom(f.getType())) {
                            doc.append(fieldName, (f.get(this)).toString());
                        }
                        else if (DBVariable.class.isAssignableFrom(f.getType())) {
                            doc.append(fieldName, ((DBVariable<?>) f.get(this)).save());
                        } else if (UUID.class.isAssignableFrom(f.getType())) {
                            doc.append(fieldName, (f.get(this)).toString());
                        } else if (DBEntity.class.isAssignableFrom(f.getType())) {
                            DBEntity dbe = (DBEntity) f.get(this);
                            doc.append(fieldName, dbe.toDocument());
                        } else if (f.getAnnotation(DBField.class).serializer() != Object.class) {
                            for (Method method : f.getAnnotation(DBField.class).serializer().getMethods()) {
                                if (method.getName().equalsIgnoreCase("save")) {
                                    try {
                                        doc.append(fieldName, method.invoke(null, f.get(this)));
                                    } catch (InvocationTargetException exception) {
                                        Logger.getLogger(DBEntity.class.getName()).log(Level.SEVERE, null, exception);
                                    }
                                    break;
                                }
                            }
                        } else {
                            doc.append(fieldName, f.get(this));
                        }
                    } catch (IllegalArgumentException | IllegalAccessException | NullPointerException exception) {
                        Logger.getLogger(DBEntity.class.getName()).log(Level.SEVERE, null, exception);
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
                    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException exception) {
                        Logger.getLogger(DBEntity.class.getName()).log(Level.SEVERE, null, exception);
                    }
                }
            }
            clazz = clazz.getSuperclass();
        }
        return doc;
    }

    public void save(MongoCollection<Document> collection) {
        unsave(collection);
        collection.insertOne(toDocument());
    }

    public void unsave(MongoCollection<Document> collection) {
        Document query = new Document("identifier", identifier);
        if (collection.find(query).first() != null) {
            collection.deleteMany(query);
        }
    }

    /**
     * Loads fields from a Document into the DBEntity
     *
     * @param doc Document
     * @return Success
     */
    public boolean load(Document doc) {
        if (doc.containsKey("_id")) _id = doc.getObjectId("_id");
        Class<?> clazz = this.getClass();
        boolean fullSuccess = true;
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
                            try {
                                obj = f.getType().getDeclaredMethod("valueOf", String.class).invoke(f.getType(), doc.get(fieldName, String.class));
                            } catch (ClassCastException exception) {
                                obj = null;
                                fullSuccess = false;
                            }
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
                            obj = new HashSet<>();
                            ((HashSet<?>) obj).addAll(doc.get(fieldName, List.class));
                        } else {
                            try {
                                obj = doc.get(fieldName, f.getType());
                            } catch (ClassCastException exception) {
                                Logger.getGlobal().log(Level.SEVERE, null, exception);
                                fullSuccess = false;
                            }
                        }
                        if (obj != null) {
                            if (f.getType().isAssignableFrom(obj.getClass())) {
                                f.set(this, obj);
                            }
                        }
                    } catch (IllegalArgumentException | IllegalAccessException
                            | InvocationTargetException | InstantiationException
                            | NoSuchMethodException exception) {
                        Logger.getLogger(DBEntity.class.getName()).log(Level.SEVERE, null, exception);
                        fullSuccess = false;
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
                                        fullSuccess = false;
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
                    } catch (IllegalAccessException | IllegalArgumentException
                            | InvocationTargetException exception) {
                        Logger.getLogger(DBEntity.class.getName()).log(Level.SEVERE, null, exception);
                        fullSuccess = false;
                    }
                }
            }
            clazz = clazz.getSuperclass();
        }
        afterLoad();
        return fullSuccess;
    }
    
    /**
     * Called after load to further initialize anything
     */
    public void afterLoad() {
    
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof DBEntity
                && ((DBEntity) o).getIdentifier().equals(this.getIdentifier()));
    }

    @Override
    public final int hashCode() {
        return getIdentifier().hashCode();
    }
    
}
