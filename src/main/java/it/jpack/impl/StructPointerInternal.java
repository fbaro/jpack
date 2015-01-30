package it.jpack.impl;

import it.jpack.StructPointer;

/**
 *
 * @author fbaro
 */
public interface StructPointerInternal<T extends StructPointer<T>> extends StructPointer<T> {
    /**
     * This gives the position over the backing memory area of the field, taking into account
     * its offset from the beginning of the structure, and the current pointer index.
     * @param offset The offset of the field inside the structure
     * @return The position of the field inside the backing memory area
     */
    int getFieldPosition(int offset);
    int getStructSize();
}
