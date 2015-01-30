package it.jpack;

/**
 *
 * @author fbaro
 * @param <T>
 */
public interface StructArrayFactory<T extends StructPointer<T>> {
    Class<T> getPointerInterface();
    StructArray<T> newArray(int length);
    int getStructSize();
}
