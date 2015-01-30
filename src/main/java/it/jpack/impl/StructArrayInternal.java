package it.jpack.impl;

import it.jpack.StructArray;
import it.jpack.StructPointer;

/**
 *
 * @author list
 */
public interface StructArrayInternal<T extends StructPointer<T>> extends StructArray<T> {

    public int getInt(int offset);

    public void putInt(int offset, int value);

    public double getDouble(int offset);

    public void putDouble(int offset, double value);

    public float getFloat(int offset);

    public void putFloat(int offset, float value);

}
