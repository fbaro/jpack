package it.jpack.impl;

import it.jpack.StructPointer;

/**
 *
 * @author fbaro
 * @param <T>
 */
public abstract class AbstractPointer<T extends StructPointer<T>> implements StructPointerInternal<T> {

    protected final StructArrayInternal<?> array;
    protected final StructPointerInternal<?> parentPointer;
    protected final int parentOffset;
    protected int index = 0;

    protected AbstractPointer(StructArrayInternal<?> array, StructPointerInternal<?> parentPointer, int parentOffset) {
        this.array = array;
        this.parentPointer = parentPointer;
        this.parentOffset = parentOffset;
    }

    protected AbstractPointer(StructArrayInternal<?> array) {
        this(array, null, 0);
    }

    @Override
    public final int getIndex() {
        return index;
    }

    @Override
    public final void setIndex(int index) {
        if (index < 0 || index > array.getLength()) {
            throw new ArrayIndexOutOfBoundsException(index);
        }
        this.index = index;
    }

    @Override
    public final T at(int index) {
        setIndex(index);
        return (T) this;
    }

    @Override
    public final int getFieldPosition(int offset) {
        return (parentPointer == null ? 0 : parentPointer.getFieldPosition(parentOffset)) + index * getStructSize() + offset;
    }

}
