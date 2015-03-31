package it.jpack;

import it.jpack.impl.bytebuffer.ByteBufferAllocator;
import it.jpack.impl.bytebuffer.ByteBufferRepository;
import it.jpack.impl.unsafe.UnsafeRepository;
import java.nio.ByteOrder;

/**
 * Static factory methods to create StructRepository instances.
 * @author fbaro
 */
public class Repositories {

    private Repositories() { }

    /**
     * Creates a new StructRepository where StructArray instances are backed
     * by a ByteBuffer, created with the {@code ByteBuffer.allocate()} method.
     * @param byteOrder The byte ordering of the buffers
     * @return A new ByteBufferRepository
     */
    public static ByteBufferRepository newByteBufferRepository(ByteOrder byteOrder) {
        return new ByteBufferRepository(ByteBufferAllocator.plain(byteOrder));
    }

    /**
     * Creates a new StructRepository where StructArray instances are backed
     * by a ByteBuffer, created with the {@code ByteBuffer.allocateDirect()} method.
     * @param byteOrder The byte ordering of the buffers
     * @return A new ByteBufferRepository
     */
    public static StructRepository newDirectByteBufferRepository(ByteOrder byteOrder) {
        return new ByteBufferRepository(ByteBufferAllocator.direct(byteOrder));
    }

    /**
     * Creates a new StructRepository where StructArray instances are backed
     * by off-heap unsafe memory, created with the {@code sun.misc.Unsafe.allocateMemory()} method.
     * @return A new UnsafeRepository
     */
    public static UnsafeRepository newUnsafeRepository() {
        return new UnsafeRepository();
    }
}
