package it.jpack.impl.unsafe;

import it.jpack.StructPointer;
import it.jpack.impl.JavassistArrayFactory;
import javassist.CtClass;

/**
 *
 * @author fbaro
 * @param <T>
 */
public final class UnsafeArrayFactory<T extends StructPointer<T>> extends JavassistArrayFactory<T> {

    public UnsafeArrayFactory(Class<T> pointerInterface, Class<? extends T> pointerImplementation, 
            CtClass ctImplementation, int size) {
        super(pointerInterface, pointerImplementation, ctImplementation, size);
    }

    @Override
    public UnsafeArray<T> newArray(int length) {
        long address = UnsafeArray.U.allocateMemory(length * size);
        try {
            return new UnsafeArray<>(address, size, length, pointerInterface, pointerImplementation);
        } catch (RuntimeException | Error ex) {
            UnsafeArray.U.freeMemory(address);
            throw ex;
        }
    }
}
