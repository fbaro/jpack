package it.jpack.impl.bytebuffer;

import it.jpack.StructPointer;
import it.jpack.impl.JavassistRepository;

/**
 *
 * @author fbaro
 */
public class ByteBufferRepository extends JavassistRepository {

    @Override
    protected <T extends StructPointer<T>> ByteBufferArrayFactory<T> newFactory(Class<T> pointerInterface) {
        return ByteBufferBuilder.build(this, pointerInterface);
    }

}
