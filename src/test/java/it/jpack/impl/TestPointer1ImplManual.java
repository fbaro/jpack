package it.jpack.impl;

import it.jpack.TestPointer1;

/**
 *
 * @author fbaro
 */
public final class TestPointer1ImplManual extends AbstractByteBufferPointer<TestPointer1> implements TestPointer1 {

    public TestPointer1ImplManual(ByteBufferArray<?> array, StructPointerInternal<?> parentPointer, int parentOffset) {
        super(array, parentPointer, parentOffset);
    }

    @Override
    public int getInt() {
        return array.getInt(getFieldPosition(0));
    }

    @Override
    public void setInt(int value) {
        array.putInt(getFieldPosition(0), value);
    }

    @Override
    public double getDouble() {
        return array.getDouble(getFieldPosition(4));
    }

    @Override
    public void setDouble(double value) {
        array.putDouble(getFieldPosition(4), value);
    }

    @Override
    public TestPointer1 at(int index) {
        setIndex(index);
        return this;
    }

    @Override
    public int getStructSize() {
        return 12;
    }
}
