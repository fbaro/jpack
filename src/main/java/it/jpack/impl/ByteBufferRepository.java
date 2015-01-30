package it.jpack.impl;

import it.jpack.StructPointer;

/**
 *
 * @author fbaro
 */
public class ByteBufferRepository extends JavassistRepository {

    @Override
    protected <T extends StructPointer<T>> JavassistArrayFactory<T> newFactory(Class<T> pointerInterface) {
        return ByteBufferBuilder.build(this, pointerInterface);
    }

}
