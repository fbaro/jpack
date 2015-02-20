package it.jpack;

/**
 * An array of structured types.
 * <p>Instances of this interface are thread safe.</p>
 * @author fbaro
 * @param <T> The type of the structures contained in the array
 */
public interface StructArray<T extends StructPointer<T>> {

    /**
     * The class of the structure contained in the array
     * @return A {@code Class}, never {@code null}
     */
    Class<T> getPointerInterface();

    /**
     * Creates a new StructPointer to access the data in this array. The returned
     * pointer is initially set at position 0.
     * @return A new instance of {@code T}, never {@code null}.
     */
    T newPointer();

    /**
     * Retrieves the number of elements in the array.
     * @return A positive number
     */
    int getLength();

    /**
     * The size in bytes of a single {@code T} structure instance
     * @return A positive number, in bytes
     */
    int getStructSize();

    /**
     * Deallocates the array, freeing any resources possibly held. 
     * Calling other methods after {@code free()} is a programming error.
     */
    void free();
}
