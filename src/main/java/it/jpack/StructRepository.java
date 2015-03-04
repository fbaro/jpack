package it.jpack;

/**
 * A StructRepository holds together all the implementation classes of the known
 * StructPointers. It is the starting point to create new pointer arrays.
 * <p>Instances of this interface are thread safe.</p>
 * @author fbaro
 */
public interface StructRepository {
    /**
     * Creates a new StructArray containing structures of type {@code T}.
     * @param <T> The type of structures contained in the returned array
     * @param pointerInterface The Class representing the structures in the array
     * @param length The total number of T elements contained in the array
     * @return A newly built {@code StructArray}; never {@code null}.
     */
    <T extends StructPointer<T>> StructArray<T> newArray(Class<T> pointerInterface, int length);

    /**
     * Creates a new StructArrayFactory, allowing to create StructArray instances for structures of type {@code T}.
     * Further invocation of this method with the same parameter can result
     * in always the same instance being returned.
     * @param <T> The type of structures dealt by the factory
     * @param pointerInterface The Class representing the structures in the array
     * @return A StructArrayFactory instance, useable to produce StructArray implementation
     * for the provided structure. 
     */
    <T extends StructPointer<T>> StructArrayFactory<T> getFactory(Class<T> pointerInterface);

    /**
     * Creates a new StructArrayFactory, allowing to create StructArray instances for structures of type {@code T}.
     * Further invocation of this method with the same parameter can result
     * in always the same instance being returned.
     * @param <T> The type of structures dealt by the factory
     * @param pointerInterface The Class representing the structures in the array
     * @param layout An implementation of the {@code StructLayout} interface,
     * to dinamically specify the position of fields in the structure. No reference to the
     * <i>layout</i> object will be held after construction of the factory.
     * @return A StructArrayFactory instance, useable to produce StructArray implementation
     * for the provided structure.
     */
    <T extends StructPointer<T>> StructArrayFactory<T> getFactory(Class<T> pointerInterface, StructLayout layout);
}
