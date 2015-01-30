package it.jpack.impl.unsafe;

import it.jpack.StructPointer;
import it.jpack.impl.JavassistBuilder;
import javassist.ClassPool;
import javassist.CtClass;

/**
 *
 * @author Flavio
 * @param <T>
 */
public class UnsafeBuilder<T extends StructPointer<T>> extends JavassistBuilder<T, UnsafeArrayFactory<T>> {

    public UnsafeBuilder(UnsafeRepository repository, Class<T> pointerInterface, ClassPool cPool, String className) {
        super(repository, pointerInterface, cPool, className);
    }

    @Override
    protected UnsafeArrayFactory<T> build(Class<T> pointerInterface, Class<? extends T> pointerImplementation, CtClass ctImplementation, int size) {
        return new UnsafeArrayFactory<>(pointerInterface, pointerImplementation, ctImplementation, size);
    }

    public static <T extends StructPointer<T>> UnsafeArrayFactory<T> build(UnsafeRepository repository, Class<T> pointerInterface) {
        UnsafeBuilder<T> builder = new UnsafeBuilder<>(repository, pointerInterface, repository.getClassPool(), pointerInterface.getName() + "Impl");
        return build(builder, pointerInterface);
    }
}
