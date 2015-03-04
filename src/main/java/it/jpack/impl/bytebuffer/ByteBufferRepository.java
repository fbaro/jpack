package it.jpack.impl.bytebuffer;

import it.jpack.StructLayout;
import it.jpack.StructPointer;
import it.jpack.impl.JavassistRepository;

/**
 *
 * @author fbaro
 */
public class ByteBufferRepository extends JavassistRepository {

    @Override
    protected <T extends StructPointer<T>> ByteBufferArrayFactory<T> newFactory(Class<T> pointerInterface, StructLayout layout) {
        return ByteBufferBuilder.build(this, pointerInterface, layout);
    }

}
