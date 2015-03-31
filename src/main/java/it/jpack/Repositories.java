package it.jpack;

import it.jpack.impl.bytebuffer.ByteBufferAllocator;
import it.jpack.impl.bytebuffer.ByteBufferRepository;
import it.jpack.impl.unsafe.UnsafeRepository;
import java.nio.ByteOrder;

/**
 *
 * @author fbaro
 */
public class Repositories {

    private Repositories() { }

    public static StructRepository newByteBufferRepository(ByteOrder byteOrder) {
        return new ByteBufferRepository(ByteBufferAllocator.plain(byteOrder));
    }

    public static StructRepository newDirectByteBufferRepository(ByteOrder byteOrder) {
        return new ByteBufferRepository(ByteBufferAllocator.direct(byteOrder));
    }

    public static StructRepository newUnsafeRepository() {
        return new UnsafeRepository();
    }
}
