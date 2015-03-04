package it.jpack.impl.bytebuffer;

import it.jpack.StructLayout;
import it.jpack.StructPointer;
import it.jpack.impl.JavassistBuilder;
import javassist.ClassPool;
import javassist.CtClass;

/**
 *
 * @author fbaro
 * @param <T>
 */
public class ByteBufferBuilder<T extends StructPointer<T>> extends JavassistBuilder<T, ByteBufferArrayFactory<T>> {

    public ByteBufferBuilder(ByteBufferRepository repository, Class<T> pointerInterface, 
            StructLayout layout, ClassPool cPool, String className) {
        super(repository, pointerInterface, layout, cPool, className);
    }

    @Override
    protected ByteBufferArrayFactory<T> build(Class<T> pointerInterface, Class<? extends T> pointerImplementation, CtClass ctImplementation, int size) {
        return new ByteBufferArrayFactory<>(pointerInterface, pointerImplementation, ctImplementation, size);
    }

    public static <T extends StructPointer<T>> ByteBufferArrayFactory<T> build(ByteBufferRepository repository, Class<T> pointerInterface, StructLayout layout) {
        ByteBufferBuilder<T> builder = new ByteBufferBuilder<>(repository, pointerInterface, layout, repository.getClassPool(), pointerInterface.getName() + "Impl");
        return build(builder, pointerInterface);
    }
}
