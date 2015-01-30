package it.jpack.impl;

import it.jpack.StructPointer;

/**
 *
 * @author fbaro
 */
public interface StructPointerInternal<T extends StructPointer<T>> extends StructPointer<T> {
    int getFieldPosition(int offset);
    int getStructSize();
}
