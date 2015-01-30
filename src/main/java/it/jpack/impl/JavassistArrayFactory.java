package it.jpack.impl;

import it.jpack.StructArrayFactory;
import it.jpack.StructPointer;
import javassist.CtClass;

/**
 *
 * @author fbaro
 * @param <T>
 */
public abstract class JavassistArrayFactory<T extends StructPointer<T>> implements StructArrayFactory<T> {

    protected final Class<T> pointerInterface;
    protected final Class<? extends T> pointerImplementation;
    protected final CtClass ctImplementation;
    protected final int size;

    protected JavassistArrayFactory(Class<T> pointerInterface, Class<? extends T> pointerImplementation, 
            CtClass ctImplementation, int size) {
        this.pointerInterface = pointerInterface;
        this.pointerImplementation = pointerImplementation;
        this.ctImplementation = ctImplementation;
        this.size = size;
    }

    @Override
    public Class<T> getPointerInterface() {
        return pointerInterface;
    }

    @Override
    public int getStructSize() {
        return size;
    }

    CtClass getCtImplementation() {
        return ctImplementation;
    }
}
