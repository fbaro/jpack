package it.jpack.impl.bytebuffer;

import it.jpack.StructPointer;
import it.jpack.impl.JavassistBuilder;
import it.jpack.impl.JavassistRepository;
import javassist.ClassPool;
import javassist.CtClass;

/**
 *
 * @author Flavio
 * @param <T>
 */
public class ByteBufferBuilder<T extends StructPointer<T>> extends JavassistBuilder<T, ByteBufferArrayFactory<T>> {

    public ByteBufferBuilder(JavassistRepository repository, Class<T> pointerInterface, ClassPool cPool, String className) {
        super(repository, pointerInterface, cPool, className);
    }

    @Override
    protected ByteBufferArrayFactory<T> build(Class<T> pointerInterface, Class<? extends T> pointerImplementation, CtClass ctImplementation, int size) {
        return new ByteBufferArrayFactory<>(pointerInterface, pointerImplementation, ctImplementation, size);
    }

    public static <T extends StructPointer<T>> ByteBufferArrayFactory<T> build(ByteBufferRepository repository, Class<T> pointerInterface) {
        ByteBufferBuilder<T> builder = new ByteBufferBuilder<>(repository, pointerInterface, repository.getClassPool(), pointerInterface.getName() + "Impl");
        return build(builder, pointerInterface);
    }
}
