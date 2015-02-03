package it.jpack.impl.unsafe;

import it.jpack.StructPointer;
import it.jpack.impl.StructArrayInternal;
import it.jpack.impl.StructPointerInternal;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import sun.misc.Unsafe;

/**
 *
 * @author fbaro
 * @param <T>
 */
public class UnsafeArray<T extends StructPointer<T>> implements StructArrayInternal<T> {

    static final Unsafe U = getUnsafe();

    private final Class<T> pointerInterface;
    private final Class<? extends T> pointerImplementation;
    private final Constructor<? extends T> constructor;
    private final long address;
    private final int structSize;
    private final int length;

    UnsafeArray(long address, int structSize, int length, Class<T> pointerInterface, Class<? extends T> pointerImplementation) {
        this.address = address;
        this.structSize = structSize;
        this.length = length;
        this.pointerInterface = pointerInterface;
        this.pointerImplementation = pointerImplementation;
        try {
            constructor = pointerImplementation.getConstructor(StructArrayInternal.class, StructPointerInternal.class, Integer.TYPE);
        } catch (NoSuchMethodException | SecurityException ex) {
            throw new IllegalStateException(ex);
        }
    }

    @Override
    public byte getByte(int offset) {
        return U.getByte(address + offset);
    }

    @Override
    public void putByte(int offset, byte value) {
        U.putByte(address + offset, value);
    }

    @Override
    public short getShort(int offset) {
        return U.getShort(address + offset);
    }

    @Override
    public void putShort(int offset, short value) {
        U.putShort(address + offset, value);
    }

    @Override
    public int getInt(int offset) {
        return U.getInt(address + offset);
    }

    @Override
    public void putInt(int offset, int value) {
        U.putInt(address + offset, value);
    }

    @Override
    public long getLong(int offset) {
        return U.getLong(address + offset);
    }

    @Override
    public void putLong(int offset, long value) {
        U.putLong(address + offset, value);
    }

    @Override
    public float getFloat(int offset) {
        return U.getFloat(address + offset);
    }

    @Override
    public void putFloat(int offset, float value) {
        U.putFloat(address + offset, value);
    }

    @Override
    public double getDouble(int offset) {
        return U.getDouble(address + offset);
    }

    @Override
    public void putDouble(int offset, double value) {
        U.putDouble(address + offset, value);
    }

    @Override
    public char getChar(int offset) {
        return U.getChar(address + offset);
    }

    @Override
    public void putChar(int offset, char value) {
        U.putChar(address + offset, value);
    }

    @Override
    public T newPointer() {
        try {
            return constructor.newInstance(this, null, 0);
        } catch (SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            throw new IllegalStateException(ex);
        }
    }

    @Override
    public int getLength() {
        return length;
    }

    @Override
    public int getStructSize() {
        return structSize;
    }

    @Override
    public void free() {
        U.freeMemory(address);
    }

    @SuppressWarnings("restriction")
    private static Unsafe getUnsafe() {
        try {
            Field singleoneInstanceField = Unsafe.class.getDeclaredField("theUnsafe");
            singleoneInstanceField.setAccessible(true);
            return (Unsafe) singleoneInstanceField.get(null);
        } catch (IllegalArgumentException | SecurityException | NoSuchFieldException | IllegalAccessException e) {
            throw new ExceptionInInitializerError(e);
        }
    }
}
