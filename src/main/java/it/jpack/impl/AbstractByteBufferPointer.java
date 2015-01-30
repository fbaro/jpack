package it.jpack.impl;

import it.jpack.StructPointer;

/**
 *
 * @author list
 * @param <T>
 */
public abstract class AbstractByteBufferPointer<T extends StructPointer<T>> implements StructPointerInternal<T> {

    protected final ByteBufferArray<?> array;
    protected final StructPointerInternal<?> parentPointer;
    protected final int parentOffset;
    protected int index = 0;

    protected AbstractByteBufferPointer(ByteBufferArray<?> array, StructPointerInternal<?> parentPointer, int parentOffset) {
        this.array = array;
        this.parentPointer = parentPointer;
        this.parentOffset = parentOffset;
    }

    protected AbstractByteBufferPointer(ByteBufferArray<?> array) {
        this(array, null, 0);
    }

    @Override
    public final int getIndex() {
        return index;
    }

    @Override
    public final void setIndex(int index) {
        this.index = index;
    }

    @Override
    public final int getFieldPosition(int offset) {
        return (parentPointer == null ? 0 : parentPointer.getFieldPosition(parentOffset)) + index * getStructSize() + offset;
    }
}
