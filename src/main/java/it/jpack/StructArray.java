package it.jpack;

/**
 *
 * @author list
 * @param <T>
 */
public interface StructArray<T> {
    T newPointer();
    int getLength();
    int getStructSize();
}
