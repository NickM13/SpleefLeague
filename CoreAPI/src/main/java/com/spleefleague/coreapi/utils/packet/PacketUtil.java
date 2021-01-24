package com.spleefleague.coreapi.utils.packet;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * @author NickM13
 * @since 9/19/2020
 */
public class PacketUtil {

    private static Object readToField(Class<?> type, ByteArrayDataInput input) {
        try {
            if (Enum.class.isAssignableFrom(type)) {
                Object[] values = (Object[]) type.getDeclaredMethod("values").invoke(null);
                return values[input.readInt()];
            } else if (type.equals(UUID.class)) {
                String str = input.readUTF();
                return str.isEmpty() ? null : UUID.fromString(str);
            } else if (type.equals(String.class)) {
                return input.readUTF();
            } else if (type.equals(boolean.class)) {
                return input.readBoolean();
            } else if (type.equals(int.class)) {
                return input.readInt();
            } else if (type.equals(double.class)) {
                return input.readDouble();
            } else if (type.equals(float.class)) {
                return input.readFloat();
            } else if (type.equals(long.class)) {
                return input.readLong();
            } else if (Collection.class.isAssignableFrom(type)) {
                int size = input.readInt();
                Collection col;
                if (List.class.isAssignableFrom(type)) {
                    col = new ArrayList<>();
                } else if (Set.class.isAssignableFrom(type)) {
                    col = new HashSet<>();
                } else {
                    col = new ArrayList<>();
                }
                ParameterizedType colType = (ParameterizedType) type.getGenericSuperclass();
                Class<?> colClass = (Class<?>) colType.getActualTypeArguments()[0];
                for (int i = 0; i < size; i++) {
                    col.add(readToField(colClass, input));
                }
                return col;
            } else if (PacketVariable.class.isAssignableFrom(type)) {
                PacketVariable var = (PacketVariable) type.getDeclaredConstructor().newInstance();
                var.fromInput(input);
                return var;
            }
        } catch (IllegalAccessException | NoSuchMethodException | InstantiationException | InvocationTargetException exception) {
            exception.printStackTrace();
        }
        return null;
    }

    private static Object readToField(Field field, ByteArrayDataInput input) {
        try {
            if (Enum.class.isAssignableFrom(field.getType())) {
                Object[] values = (Object[]) field.getType().getDeclaredMethod("values").invoke(null);
                return values[input.readInt()];
            } else if (field.getType().equals(UUID.class)) {
                String str = input.readUTF();
                return str.isEmpty() ? null : UUID.fromString(str);
            } else if (field.getType().equals(String.class)) {
                return input.readUTF();
            } else if (field.getType().equals(boolean.class)) {
                return input.readBoolean();
            } else if (field.getType().equals(int.class)) {
                return input.readInt();
            } else if (field.getType().equals(double.class)) {
                return input.readDouble();
            } else if (field.getType().equals(float.class)) {
                return input.readFloat();
            } else if (field.getType().equals(long.class)) {
                return input.readLong();
            } else if (Collection.class.isAssignableFrom(field.getType())) {
                int size = input.readInt();
                Collection col;
                if (List.class.isAssignableFrom(field.getType())) {
                    col = new ArrayList<>();
                } else if (Set.class.isAssignableFrom(field.getType())) {
                    col = new HashSet<>();
                } else {
                    col = new ArrayList<>();
                }
                ParameterizedType colType = (ParameterizedType) field.getGenericType();
                Class<?> colClass = (Class<?>) colType.getActualTypeArguments()[0];
                for (int i = 0; i < size; i++) {
                    col.add(readToField(colClass, input));
                }
                return col;
            } else if (PacketVariable.class.isAssignableFrom(field.getType())) {
                PacketVariable var = (PacketVariable) field.getType().getDeclaredConstructor().newInstance();
                var.fromInput(input);
                return var;
            }
        } catch (IllegalAccessException | NoSuchMethodException | InstantiationException | InvocationTargetException exception) {
            exception.printStackTrace();
        }
        return null;
    }

    public static void readToFields(Object o, ByteArrayDataInput input) {
        for (Field field : o.getClass().getDeclaredFields()) {
            try {
                field.setAccessible(true);
                field.set(o, readToField(field, input));
            } catch (IllegalAccessException exception) {
                exception.printStackTrace();
            }
        }
    }

    private static void writeFromField(Class<?> type, Object o, ByteArrayDataOutput output) {
        if (Enum.class.isAssignableFrom(type)) {
            output.writeInt(((Enum) o).ordinal());
        } else if (type.equals(UUID.class)) {
            output.writeUTF(o == null ? "" : o.toString());
        } else if (type.equals(String.class)) {
            output.writeUTF((String) o);
        } else if (type.equals(boolean.class)) {
            output.writeBoolean((boolean) o);
        } else if (type.equals(int.class)) {
            output.writeInt((int) o);
        } else if (type.equals(double.class)) {
            output.writeDouble((double) o);
        } else if (type.equals(float.class)) {
            output.writeFloat((float) o);
        } else if (type.equals(long.class)) {
            output.writeLong((long) o);
        } else if (Collection.class.isAssignableFrom(type)) {
            Collection<?> col = (Collection<?>) o;
            output.writeInt(col.size());
            if (!col.isEmpty()) {
                Class<?> colClass = col.iterator().next().getClass();
                for (Object obj2 : col) {
                    writeFromField(colClass, obj2, output);
                }
            }
        } else if (PacketVariable.class.isAssignableFrom(type)) {
            ((PacketVariable) o).toOutput(output);
        }
    }

    public static void writeFromFields(Object o, ByteArrayDataOutput output) {
        for (Field field : o.getClass().getDeclaredFields()) {
            try {
                field.setAccessible(true);
                writeFromField(field.getType(), field.get(o), output);
            } catch (IllegalAccessException exception) {
                exception.printStackTrace();
            }
        }
    }

}
