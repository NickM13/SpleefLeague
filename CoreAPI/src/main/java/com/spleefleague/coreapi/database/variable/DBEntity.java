/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.coreapi.database.variable;

import com.mongodb.client.MongoCollection;

import java.lang.reflect.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.mongodb.client.model.UpdateOptions;
import com.spleefleague.coreapi.database.annotation.DBField;
import com.spleefleague.coreapi.database.annotation.DBLoad;
import com.spleefleague.coreapi.database.annotation.DBSave;
import com.spleefleague.coreapi.database.converter.FieldConverter;
import com.spleefleague.coreapi.database.converter.converters.EnumConverter;
import org.bson.Document;
import org.bson.types.ObjectId;

/**
 * A DBEntity is an object that can be
 * saved and loaded from a JSON Document
 *
 * @author NickM13
 */
public class DBEntity {

    // Set of classes that don't require converting
    private static final Set<Class<?>> noConvertSet = new HashSet<>();

    static {
        noConvertSet.add(Boolean.class);
        noConvertSet.add(Integer.class);
        noConvertSet.add(Long.class);
        noConvertSet.add(Double.class);
        noConvertSet.add(String.class);
        noConvertSet.add(Document.class);
    }
    
    protected ObjectId _id;
    @DBField protected String identifier = null;
    
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

    public String getIdentifierNoTag() {
        return identifier.contains(":") ? identifier.split(":")[1] : identifier;
    }

    public List<Field> getFields() {
        List<Field> fields = new ArrayList<>();

        Class<?> clazz = getClass();

        while (DBEntity.class.isAssignableFrom(clazz)) {
            for (Field field : clazz.getDeclaredFields()) {
                field.setAccessible(true);
                if (field.isAnnotationPresent(DBField.class)) {
                    fields.add(field);
                }
            }
            clazz = clazz.getSuperclass();
        }

        return fields;
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

    private Object toDocumentable(Class<?> clazz, Object obj) throws IllegalAccessException, ClassNotFoundException {
        if (noConvertSet.contains(clazz)) {
            return obj;
        }

        if (clazz == float.class) {
            return (double) obj;
        } else if (Enum.class.isAssignableFrom(clazz)) {
            return ((Enum<?>) obj).name();
        } else if (DBVariable.class.isAssignableFrom(clazz)) {
            return ((DBVariable<?>) obj).save();
        } else if (UUID.class.isAssignableFrom(clazz)) {
            return (obj).toString();
        } else if (DBEntity.class.isAssignableFrom(clazz)) {
            DBEntity dbe = (DBEntity) obj;
            return dbe.toDocument();
        } else {
            return obj;
        }
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
                DBField dbField = f.getAnnotation(DBField.class);
                if (dbField != null && dbField.save()) {
                    try {
                        f.setAccessible(true);
                        if (f.getName().equals("_id") || f.get(this) == null) continue;
                        String fieldName = dbField.fieldName().length() > 0 ? dbField.fieldName() : f.getName();
                        doc.append(fieldName, toDocumentable(f.getType(), f.get(this)));
                    } catch (IllegalArgumentException | IllegalAccessException | NullPointerException | ClassNotFoundException exception) {
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
        //Document query = new Document("identifier", identifier);
        //collection.updateOne(query, new Document("$set", toDocument()), new UpdateOptions().upsert(true).bypassDocumentValidation(true));
        unsave(collection);
        collection.insertOne(toDocument());
    }

    /**
     * Use with caution!
     *
     * @param collection
     */
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
                DBField dbField = f.getAnnotation(DBField.class);
                if (dbField != null && dbField.load()) {
                    try {
                        f.setAccessible(true);
                        String fieldName = dbField.fieldName().length() > 0 ? dbField.fieldName() : f.getName();
                        if (!doc.containsKey(fieldName)) continue;
                        Object obj = null;
                        if (f.getType() == boolean.class) {
                            f.setBoolean(this, doc.getBoolean(fieldName));
                            continue;
                        } else if (f.getType() == int.class) {
                            f.setInt(this, doc.getInteger(fieldName));
                            continue;
                        } else if (f.getType() == long.class) {
                            f.setLong(this, doc.getLong(fieldName));
                            continue;
                        } else if (f.getType() == float.class) {
                            f.setFloat(this, doc.getDouble(fieldName).floatValue());
                            continue;
                        } else if (f.getType() == double.class) {
                            f.setDouble(this, doc.getDouble(fieldName));
                            continue;
                        } else if (Enum.class.isAssignableFrom(f.getType())) {
                            try {
                                obj = f.getType().getDeclaredMethod("valueOf", String.class).invoke(f.getType(), doc.get(fieldName, String.class));
                            } catch (ClassCastException exception) {
                                fullSuccess = false;
                            }
                        } else if (DBVariable.class.isAssignableFrom(f.getType())) {
                            for (Method method : f.getType().getMethods()) {
                                if (method.getName().equalsIgnoreCase("load")) {
                                    if (f.get(this) == null) {
                                        obj = f.getType().getDeclaredConstructor().newInstance();
                                    } else {
                                        obj = f.get(this);
                                    }
                                    method.invoke(obj, doc.get(fieldName, method.getParameters()[0].getType()));
                                    break;
                                }
                            }
                        } else if (f.getType().equals(UUID.class)) {
                            obj = UUID.fromString(doc.get(fieldName, String.class));
                        } else if (DBEntity.class.isAssignableFrom(f.getType())) {
                            if (f.get(this) == null) {
                                obj = f.getType().getDeclaredConstructor().newInstance();
                            } else {
                                obj = f.get(this);
                            }
                            ((DBEntity) obj).load(doc.get(fieldName, Document.class));
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
