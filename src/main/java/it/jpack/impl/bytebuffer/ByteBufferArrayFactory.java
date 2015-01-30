package it.jpack.impl.bytebuffer;

import it.jpack.StructPointer;
import it.jpack.impl.JavassistArrayFactory;
import java.nio.ByteBuffer;
import javassist.CtClass;

/**
 *
 * @author fbaro
 * @param <T>
 */
public final class ByteBufferArrayFactory<T extends StructPointer<T>> extends JavassistArrayFactory<T> {

    public ByteBufferArrayFactory(Class<T> pointerInterface, Class<? extends T> pointerImplementation, 
            CtClass ctImplementation, int size) {
        super(pointerInterface, pointerImplementation, ctImplementation, size);
    }

    @Override
    public ByteBufferArray<T> newArray(int length) {
        ByteBuffer buffer = ByteBuffer.allocate(length * size);
        return new ByteBufferArray<>(buffer, size, pointerInterface, pointerImplementation);
    }
}
