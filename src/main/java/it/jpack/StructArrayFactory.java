package it.jpack;

/**
 * A factory of StructArray instances, all related to the same StructPointer.
 * <p>Instances of this interface are thread safe.</p>
 * @param <T> The type of the structures contained in the arrays generated
 * by this factory
 * @author fbaro 
 */
public interface StructArrayFactory<T extends StructPointer<T>> {
    /**
     * The class of the structure contained in the arrays
     * @return A {@code Class}, never {@code null}
     */
    Class<T> getPointerInterface();
    /**
     * Creates a new StructArray containing structures of type {@code T}.
     * @param length The total number of T elements contained in the array
     * @return A newly built {@code StructArray}; never {@code null}.
     */
    StructArray<T> newArray(int length);
    /**
     * The size in bytes of a single {@code T} structure instance
     * @return A positive number, in bytes
     */
    int getStructSize();
}
