package it.jpack.impl;

import it.jpack.impl.bytebuffer.ByteBufferArray;
import it.jpack.TestPointer1;
import it.jpack.TestPointer2;

/**
 *
 * @author fbaro
 */
public final class TestPointer2ImplManual extends AbstractPointer<TestPointer2> implements TestPointer2 {

    private final TestPointer1 inner;

    public TestPointer2ImplManual(ByteBufferArray<?> array, StructPointerInternal<?> parentPointer, int parentOffset) {
        super(array, parentPointer, parentOffset);
        this.inner = new TestPointer1ImplManual(array, this, 4);
    }

    @Override
    public float getFloat() {
        return array.getFloat(getFieldPosition(0));
    }

    @Override
    public void setFloat(float value) {
        array.putFloat(getFieldPosition(0), value);
    }

    @Override
    public TestPointer1 getInner() {
        return inner;
    }

    @Override
    public void setInner(TestPointer1 value) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getStructSize() {
        return 16;
    }
}
