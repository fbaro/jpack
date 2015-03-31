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

    private final ByteBufferAllocator allocator;

    public ByteBufferArrayFactory(Class<T> pointerInterface, Class<? extends T> pointerImplementation, 
            CtClass ctImplementation, int size, ByteBufferAllocator allocator) {
        super(pointerInterface, pointerImplementation, ctImplementation, size);
        this.allocator = allocator;
    }

    /**
     * Creates a new array from the contents of the provided buffer.
     * The buffer position and limit will provide the boundaries for the array;
     * the remaining bytes in the buffer should be a multiple of the array size.
     * Changes to the buffer's content will be visible in the array, and vice
     * versa; the buffer position, limit, and mark values will not be used once
     * the function returns.
     * @param buffer The buffer to build a new array on
     * @return A new array based on the buffer contents
     * @throws IllegalArgumentException If the remaning bytes in the buffer are 
     * not a multiple of the structure size
     */
    public ByteBufferArray<T> wrap(ByteBuffer buffer) {
        if (buffer.remaining() % size != 0) {
            throw new IllegalArgumentException("The remaining bytes of the buffer should be a multiple of the structure size");
        }
        return new ByteBufferArray<>(buffer.slice(), size, pointerInterface, pointerImplementation);
    }

    @Override
    public ByteBufferArray<T> newArray(int length) {
        ByteBuffer buffer = allocator.allocate(length * size);
        return new ByteBufferArray<>(buffer, size, pointerInterface, pointerImplementation);
    }
}
