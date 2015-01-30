package it.jpack;

/**
 *
 * @author list
 * @param <T>
 */
public interface StructBuilder<T extends StructPointer<T>> {
    void addLong(String name);
    void addDouble(String name);
    void addFloat(String name);
    void addInt(String name);
    <S extends StructPointer<S>> void addStruct(String name, Class<S> pointerInterface);
    StructArrayFactory<T> build();
}
