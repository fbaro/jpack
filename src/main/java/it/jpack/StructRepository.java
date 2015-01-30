package it.jpack;

/**
 *
 * @author fbaro
 */
public interface StructRepository {
    <T extends StructPointer<T>> StructArray<T> newArray(Class<T> pointerInterface, int length);
}
