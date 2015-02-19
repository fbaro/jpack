package it.jpack;

/**
 *
 * @author fbaro
 * @param <T>
 */
public interface StructArray<T> {
    Class<T> getPointerClass();
    T newPointer();
    int getLength();
    int getStructSize();
    void free();
}
