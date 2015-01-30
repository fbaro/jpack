package it.jpack.impl.unsafe;

import it.jpack.StructPointer;
import it.jpack.impl.JavassistRepository;

/**
 *
 * @author fbaro
 */
public class UnsafeRepository extends JavassistRepository {

    @Override
    protected <T extends StructPointer<T>> UnsafeArrayFactory<T> newFactory(Class<T> pointerInterface) {
        return UnsafeBuilder.build(this, pointerInterface);
    }

}
