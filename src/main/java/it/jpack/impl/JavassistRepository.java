package it.jpack.impl;

import it.jpack.StructArray;
import it.jpack.StructLayout;
import it.jpack.StructPointer;
import it.jpack.StructRepository;
import java.util.concurrent.ConcurrentHashMap;
import javassist.ClassPool;

/**
 *
 * @author fbaro
 */
public abstract class JavassistRepository implements StructRepository {

    private final ClassPool classPool = ClassPool.getDefault();
    private final ConcurrentHashMap<Class<? extends StructPointer<?>>, JavassistArrayFactory<?>> factories = new ConcurrentHashMap<>();

    @Override
    public final <T extends StructPointer<T>> StructArray<T> newArray(Class<T> pointerInterface, int length) {
        JavassistArrayFactory<T> factory = getFactory(pointerInterface);
        return factory.newArray(length);
    }

    @Override
    public <T extends StructPointer<T>> JavassistArrayFactory<T> getFactory(Class<T> pointerInterface) {
        return getFactory(pointerInterface, new DefaultStructLayout<>(pointerInterface));
    }

    @Override
    public <T extends StructPointer<T>> JavassistArrayFactory<T> getFactory(Class<T> pointerInterface, StructLayout layout) {
        JavassistArrayFactory<T> ret = (JavassistArrayFactory<T>) factories.get(pointerInterface);
        if (ret != null) {
            return ret;
        }
        JavassistArrayFactory<T> newFactory = newFactory(pointerInterface, layout);
        ret = (JavassistArrayFactory<T>) factories.putIfAbsent(pointerInterface, newFactory);
        return (ret != null ? ret : newFactory);
    }

    protected abstract <T extends StructPointer<T>> JavassistArrayFactory<T> newFactory(Class<T> pointerInterface, StructLayout layout);

    public ClassPool getClassPool() {
        return classPool;
    }
}
