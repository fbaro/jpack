package it.jpack;

/**
 *
 * @author fbaro
 * @param <T>
 */
public interface StructPointer<T extends StructPointer<T>> {

    int getIndex();
    void setIndex(int index);
    T at(int index);
}
