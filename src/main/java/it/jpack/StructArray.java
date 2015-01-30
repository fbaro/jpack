package it.jpack;

/**
 *
 * @author fbaro
 * @param <T>
 */
public interface StructArray<T> {
    T newPointer();
    int getLength();
    int getStructSize();
    void free();
}
