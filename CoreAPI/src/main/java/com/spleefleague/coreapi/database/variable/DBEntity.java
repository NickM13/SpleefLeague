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

import com.spleefleague.coreapi.database.annotation.DBField;
import com.spleefleague.coreapi.database.annotation.DBLoad;
import com.spleefleague.coreapi.database.annotation.DBSave;
import com.spleefleague.coreapi.utils.PacketUtils;
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
    private static final Map<Class<? extends DBEntity>, DBEntityForm> entityFormMap = new HashMap<>();

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
            return ((Double) obj).floatValue();
        } else if (Enum.class.isAssignableFrom(clazz)) {
            return ((Enum<?>) obj).name();
        } else if (DBVariable.class.isAssignableFrom(clazz)) {
            return ((DBVariable<?>) obj).save();
        } else if (UUID.class.isAssignableFrom(clazz)) {
            return obj.toString();
        } else if (DBEntity.class.isAssignableFrom(clazz)) {
            DBEntity dbe = (DBEntity) obj;
            return dbe.toDocument();
        } else if (Map.class.isAssignableFrom(clazz)) {
            Map map = (Map<?, ?>) obj;
            Document doc = new Document();
            for (Object mapObj : map.entrySet()) {
                Map.Entry entry = (Map.Entry) mapObj;
                doc.put(toDocumentable(entry.getKey().getClass(), entry.getKey()).toString(), toDocumentable(entry.getValue().getClass(), entry.getValue()));
            }
            return doc;
        } else if (List.class.isAssignableFrom(clazz)) {
            List<?> list = (List<?>) obj;
            List<Object> newList = new ArrayList<>();
            for (Object o : list) {
                newList.add(toDocumentable(o.getClass(), o));
            }
            return newList;
        } else if (Set.class.isAssignableFrom(clazz)) {
            Set<?> set = (Set<?>) obj;
            List<Object> newList = new ArrayList<>();
            for (Object o : set) {
                newList.add(toDocumentable(o.getClass(), o));
            }
            return newList;
        }
        return obj;
    }

    /**
     * Converts the DBEntity into a document
     *
     * @return Document
     */
    public Document toDocument() {
        if (!entityFormMap.containsKey(getClass())) {
            entityFormMap.put(getClass(), new DBEntityForm(getClass()));
        }
        DBEntityForm form = entityFormMap.get(getClass());
        Document doc = new Document();
        for (Map.Entry<String, Field> entry : form.getWriteFields().entrySet()) {
            String fieldName = entry.getKey();
            Field field = entry.getValue();
            try {
                Object obj = field.get(this);
                if (obj == null) continue;
                doc.append(fieldName, toDocumentable(field.getType(), obj));
            } catch (IllegalAccessException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        for (Map.Entry<String, Method> entry : form.getWriteMethods().entrySet()) {
            String fieldName = entry.getKey();
            Method method = entry.getValue();
            try {
                Object o = method.invoke(this);
                if (o == null) continue;
                doc.append(fieldName, o);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
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

    private Object convertToField(Class<?> clazz, Object obj) {
        try {
            if (noConvertSet.contains(clazz)) {
                return obj;
            } else if (clazz == float.class) {
                return ((Double) obj).floatValue();
            } else if (clazz.equals(UUID.class)) {
                return UUID.fromString((String) obj);
            } else if (Enum.class.isAssignableFrom(clazz)) {
                try {
                    return clazz.getDeclaredMethod("valueOf", String.class).invoke(clazz, obj.toString());
                } catch (ClassCastException exception) {
                    return false;
                }
            } else if (DBEntity.class.isAssignableFrom(clazz)) {
                DBEntity dbEntity = (DBEntity) clazz.getDeclaredConstructor().newInstance();
                dbEntity.load((Document) obj);
                return dbEntity;
            } else if (DBVariable.class.isAssignableFrom(clazz)) {
                DBVariable<?> dbVar = (DBVariable<?>) clazz.getDeclaredConstructor().newInstance();
                for (Method method : clazz.getMethods()) {
                    if (method.getName().equalsIgnoreCase("load")) {
                        method.invoke(dbVar, obj);
                        break;
                    }
                }
                return dbVar;
            }
        } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException | InstantiationException e) {
            Logger.getGlobal().log(Level.SEVERE, null, e);
        }
        return null;
    }

    private boolean loadField(Field field, Object docObj) {
        try {
            if (noConvertSet.contains(field.getType())) {
                field.set(this, docObj);
            } else if (field.getType() == float.class) {
                field.set(this, ((Double) docObj).floatValue());
            } else if (field.getType().equals(UUID.class)) {
                field.set(this, UUID.fromString((String) docObj));
            } else if (Map.class.isAssignableFrom(field.getType())) {
                Map mapObj = (Map<?, ?>) field.get(this);
                if (mapObj == null) {
                    try {
                        mapObj = (Map<?, ?>) field.getType().getConstructor().newInstance();
                    } catch (NoSuchMethodException exception) {
                        mapObj = new HashMap<>();
                    }
                } else {
                    mapObj.clear();
                }
                for (Map.Entry<String, ?> entry : ((Document) docObj).entrySet()) {
                    mapObj.put(entry.getKey(), entry.getValue());
                }
                field.set(this, mapObj);
            } else if (List.class.isAssignableFrom(field.getType())) {
                List listObj = (List<?>) field.get(this);
                if (listObj == null) {
                    try {
                        listObj = (List<?>) field.getType().getConstructor().newInstance();
                    } catch (NoSuchMethodException exception) {
                        listObj = new ArrayList<>();
                    }
                } else {
                    listObj.clear();
                }
                ParameterizedType type = (ParameterizedType) field.getGenericType();
                Class<?> clazz = (Class<?>) type.getActualTypeArguments()[0];
                for (Object o : (List<?>) docObj) {
                    listObj.add(convertToField(clazz, o));
                }
                field.set(this, listObj);
            } else if (Set.class.isAssignableFrom(field.getType())) {
                Set setObj = (Set<?>) field.get(this);
                if (setObj == null) {
                    try {
                        setObj = (Set<?>) field.getType().getConstructor().newInstance();
                    } catch (NoSuchMethodException exception) {
                        setObj = new HashSet<>();
                    }
                } else {
                    setObj.clear();
                }
                ParameterizedType type = (ParameterizedType) field.getGenericType();
                Class<?> clazz = (Class<?>) type.getActualTypeArguments()[0];
                for (Object o : (List<?>) docObj) {
                    setObj.add(convertToField(clazz, o));
                }
                field.set(this, setObj);
            } else if (Enum.class.isAssignableFrom(field.getType())) {
                try {
                    field.set(this, field.getType().getDeclaredMethod("valueOf", String.class).invoke(field.getType(), docObj.toString()));
                } catch (ClassCastException exception) {
                    return false;
                }
            } else if (DBEntity.class.isAssignableFrom(field.getType())) {
                DBEntity dbEntity = (DBEntity) field.get(this);
                if (field.get(this) == null) {
                    dbEntity = (DBEntity) field.getType().getDeclaredConstructor().newInstance();
                }
                dbEntity.load((Document) docObj);
                field.set(this, dbEntity);
            } else if (DBVariable.class.isAssignableFrom(field.getType())) {
                DBVariable<?> dbVar = (DBVariable<?>) field.get(this);
                if (dbVar == null) {
                    dbVar = (DBVariable<?>) field.getType().getDeclaredConstructor().newInstance();
                }
                dbVar.objLoad(docObj);
                field.set(this, dbVar);
            } else {
                return false;
            }
            return true;
        } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException | InstantiationException e) {
            Logger.getGlobal().log(Level.SEVERE, null, e);
        }
        return false;
    }

    public boolean reloadField(Document doc, Set<String> fieldNames) {
        Class<?> clazz = this.getClass();
        while (clazz != null && DBEntity.class.isAssignableFrom(clazz) && !fieldNames.isEmpty()) {
            for (Field field : clazz.getDeclaredFields()) {
                DBField dbField = field.getAnnotation(DBField.class);
                if (dbField != null && dbField.read()) {
                    field.setAccessible(true);
                    String fieldName = dbField.fieldName().length() > 0 ? dbField.fieldName() : field.getName();
                    if (!fieldNames.contains(fieldName) ||
                            !doc.containsKey(fieldName)) continue;
                    loadField(field, doc.get(fieldName));
                    fieldNames.remove(fieldName);
                    if (fieldNames.isEmpty()) break;
                }
            }
        }
        return true;
    }

    /**
     * Loads fields from a Document into the DBEntity
     *
     * @param doc Document
     * @return Success
     */
    public boolean load(Document doc) {
        if (doc.containsKey("_id")) _id = doc.getObjectId("_id");
        if (!entityFormMap.containsKey(getClass())) {
            entityFormMap.put(getClass(), new DBEntityForm(getClass()));
        }
        DBEntityForm form = entityFormMap.get(getClass());
        boolean fullSuccess = true;
        for (Map.Entry<String, Field> entry : form.getReadFields().entrySet()) {
            Object docObj = doc.get(entry.getKey());
            if (docObj == null) continue;
            if (!loadField(entry.getValue(), docObj)) {
                fullSuccess = false;
            }
        }
        for (Map.Entry<String, Method> entry : form.getReadMethods().entrySet()) {
            String fieldName = entry.getKey();
            Method method = entry.getValue();
            try {
                if (DBVariable.class.isAssignableFrom(method.getParameters()[0].getType())) {
                    DBVariable<?> dbVariable = (DBVariable<?>) method.getParameters()[0].getType().getDeclaredConstructor().newInstance();
                    Object docObj = doc.get(fieldName, method.getParameters()[0].getType());
                    dbVariable.objLoad(docObj);
                    method.invoke(this, dbVariable);
                } else {
                    Object docObj = doc.get(fieldName);
                    if (docObj != null && method.getParameters()[0].getType().isAssignableFrom(docObj.getClass())) {
                        method.invoke(this, doc.get(fieldName, method.getParameters()[0].getType()));
                    }
                }
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | InstantiationException e) {
                e.printStackTrace();
            }
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
