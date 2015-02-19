package it.jpack;

/**
 * This interface must be extended by user structures.
 * User structures should add getter and setter methods, with JavaBeans convention,
 * to specify the structure fields. Implementations of this interface will be provided
 * by {@code StructArray} instances.
 * <p>Instances of this interface are <b>mutable</b>, and <b>not</b> thread safe.</p>
 * @author fbaro
 * @param <T> This type must be set to the class itself extending the interface
 */
public interface StructPointer<T extends StructPointer<T>> {

    /**
     * Retrieves the index of the element in the array this pointer currently 
     * points to.
     * @return A number between 0 (included) and the array length (excluded)
     */
    int getIndex();

    /**
     * Moves the index of the element in the array this pointer points to. There
     * is no functional between this method and the {@code at} method.
     * @param index The new index. Must be a number between 0 (included) and the array length (excluded)
     * @throws ArrayIndexOutOfBoundsException If <i>index</i> does not respect the constraints
     */
    void setIndex(int index);

    /**
     * Moves the index of the element in the array this pointer points to. There
     * is no functional between this method and the {@code setIndex} method.
     * @param index The new index. Must be a number between 0 (included) and the array length (excluded)
     * @return The receiver, to allow method chaining.
     * @throws ArrayIndexOutOfBoundsException If <i>index</i> does not respect the constraints
     */
    T at(int index);
}
