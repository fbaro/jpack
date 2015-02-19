package it.jpack;

import it.jpack.impl.bytebuffer.ByteBufferRepository;
import it.jpack.impl.unsafe.UnsafeRepository;

/**
 *
 * @author fbaro
 */
public class Repositories {

    private Repositories() { }

    public static StructRepository newByteBufferRepository() {
        return new ByteBufferRepository();
    }

    public static StructRepository newUnsafeRepository() {
        return new UnsafeRepository();
    }
}
