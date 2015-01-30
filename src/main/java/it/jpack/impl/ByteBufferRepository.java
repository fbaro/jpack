package it.jpack.impl;

import it.jpack.StructPointer;
import it.jpack.StructRepository;
import java.util.concurrent.ConcurrentHashMap;
import javassist.ClassPool;

/**
 *
 * @author fbaro
 */
public class ByteBufferRepository implements StructRepository {

    private final ClassPool classPool = ClassPool.getDefault();
    private final ConcurrentHashMap<Class<? extends StructPointer<?>>, ByteBufferArrayFactory<?>> factories = new ConcurrentHashMap<>();

    @Override
    public <T extends StructPointer<T>> ByteBufferArray<T> newArray(Class<T> pointerInterface, int length) {
        ByteBufferArrayFactory<T> factory = getFactory(pointerInterface);
        return factory.newArray(length);
    }

    <T extends StructPointer<T>> ByteBufferArrayFactory<T> getFactory(Class<T> pointerInterface) {
        ByteBufferArrayFactory<T> ret = (ByteBufferArrayFactory<T>) factories.get(pointerInterface);
        if (ret != null) {
            return ret;
        }
        ByteBufferArrayFactory<T> newFactory = ByteBufferBuilder.build(this, pointerInterface);
        ret = (ByteBufferArrayFactory<T>) factories.putIfAbsent(pointerInterface, newFactory);
        return (ret != null ? ret : newFactory);
    }

    ClassPool getClassPool() {
        return classPool;
    }
}
