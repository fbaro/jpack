package it.jpack;

/**
 *
 * @author fbaro
 */
public interface StructRepository {
    <T extends StructPointer<T>> StructArray<T> newArray(Class<T> pointerInterface, int length);
    <T extends StructPointer<T>> StructArrayFactory<T> getFactory(Class<T> pointerInterface);
}
