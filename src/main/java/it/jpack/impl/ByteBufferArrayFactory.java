package it.jpack.impl;

import it.jpack.StructArrayFactory;
import it.jpack.StructPointer;
import java.nio.ByteBuffer;
import javassist.CtClass;

/**
 *
 * @author fbaro
 * @param <T>
 */
public class ByteBufferArrayFactory<T extends StructPointer<T>> implements StructArrayFactory<T> {

    private final Class<T> pointerInterface;
    private final Class<? extends T> pointerImplementation;
    private final CtClass ctImplementation;
    private final int size;

    public ByteBufferArrayFactory(Class<T> pointerInterface, Class<? extends T> pointerImplementation, 
            CtClass ctImplementation, int size) {
        this.pointerInterface = pointerInterface;
        this.pointerImplementation = pointerImplementation;
        this.ctImplementation = ctImplementation;
        this.size = size;
    }

    @Override
    public ByteBufferArray<T> newArray(int length) {
        ByteBuffer buffer = ByteBuffer.allocate(length * size);
        return new ByteBufferArray<>(buffer, size, pointerInterface, pointerImplementation);
    }

    CtClass getCtImplementation() {
        return ctImplementation;
    }

    @Override
    public int getStructSize() {
        return size;
    }
}
