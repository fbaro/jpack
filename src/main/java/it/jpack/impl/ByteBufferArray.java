package it.jpack.impl;

import it.jpack.StructPointer;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;

/**
 *
 * @author fbaro
 * @param <T>
 */
public class ByteBufferArray<T extends StructPointer<T>> implements StructArrayInternal<T> {

    private final Class<T> pointerInterface;
    private final Class<? extends T> pointerImplementation;
    private final Constructor<? extends T> constructor;
    private final ByteBuffer buffer;
    private final int structSize;
    private final int length;

    public ByteBufferArray(ByteBuffer buffer, int structSize, Class<T> pointerInterface, Class<? extends T> pointerImplementation) {
        this.buffer = buffer;
        this.structSize = structSize;
        this.length = buffer.capacity() / structSize;
        this.pointerInterface = pointerInterface;
        this.pointerImplementation = pointerImplementation;
        buffer.position(0);
        try {
            constructor = pointerImplementation.getConstructor(StructArrayInternal.class, StructPointerInternal.class, Integer.TYPE);
        } catch (NoSuchMethodException | SecurityException ex) {
            throw new IllegalStateException(ex);
        }
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
    public byte getByte(int offset) {
        return buffer.get(offset);
    }

    @Override
    public void putByte(int offset, byte value) {
        buffer.put(offset, value);
    }

    @Override
    public short getShort(int offset) {
        return buffer.getShort(offset);
    }

    @Override
    public void putShort(int offset, short value) {
        buffer.putShort(offset, value);
    }

    @Override
    public int getInt(int offset) {
        return buffer.getInt(offset);
    }

    @Override
    public void putInt(int offset, int value) {
        buffer.putInt(offset, value);
    }

    @Override
    public long getLong(int offset) {
        return buffer.getLong(offset);
    }

    @Override
    public void putLong(int offset, long value) {
        buffer.putLong(offset, value);
    }

    @Override
    public double getDouble(int offset) {
        return buffer.getDouble(offset);
    }

    @Override
    public void putDouble(int offset, double value) {
        buffer.putDouble(offset, value);
    }

    @Override
    public float getFloat(int offset) {
        return buffer.getFloat(offset);
    }

    @Override
    public void putFloat(int offset, float value) {
        buffer.putFloat(offset, value);
    }

    @Override
    public char getChar(int offset) {
        return buffer.getChar(offset);
    }

    @Override
    public void putChar(int offset, char value) {
        buffer.putChar(offset, value);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < buffer.limit(); i++) {
            String s = Integer.toHexString(0xff & buffer.get(i));
            if (s.length() == 1) {
                sb.append('0');
            }
            sb.append(s);
            if (i % 4 == 3 && i != buffer.limit() - 1) {
                sb.append(' ');
            }
        }
        return sb.toString();
    }
}
