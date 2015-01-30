package it.jpack;

/**
 *
 * @author list
 */
public interface StructRepository {
    <T extends StructPointer<T>> StructArray<T> newArray(Class<T> pointerInterface, int length);
}
