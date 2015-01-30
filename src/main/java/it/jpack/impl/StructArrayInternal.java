package it.jpack.impl;

import it.jpack.StructArray;
import it.jpack.StructPointer;

/**
 *
 * @author fbaro
 */
public interface StructArrayInternal<T extends StructPointer<T>> extends StructArray<T> {

    byte getByte(int offset);

    void putByte(int offset, byte value);

    short getShort(int offset);

    void putShort(int offset, short value);

    int getInt(int offset);

    void putInt(int offset, int value);

    long getLong(int offset);

    void putLong(int offset, long value);

    float getFloat(int offset);

    void putFloat(int offset, float value);

    double getDouble(int offset);

    void putDouble(int offset, double value);

    char getChar(int offset);

    void putChar(int offset, char value);

}
