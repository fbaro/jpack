package it.jpack.impl.bytebuffer;

import it.jpack.StructLayout;
import it.jpack.StructPointer;
import it.jpack.impl.JavassistRepository;

/**
 *
 * @author fbaro
 */
public class ByteBufferRepository extends JavassistRepository {

    private final ByteBufferAllocator allocator;

    public ByteBufferRepository(ByteBufferAllocator allocator) {
        this.allocator = allocator;
    }

    @Override
    protected <T extends StructPointer<T>> ByteBufferArrayFactory<T> newFactory(Class<T> pointerInterface, StructLayout layout) {
        return ByteBufferBuilder.build(this, pointerInterface, layout);
    }

    public ByteBufferAllocator getAllocator() {
        return allocator;
    }
}
