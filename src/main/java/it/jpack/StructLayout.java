package it.jpack;

/**
 * Through this interface, users can dinamically decide the position of
 * fields in a structure, i.e. their offset inside the structure.
 * @author fbaro
 */
public interface StructLayout {

    /**
     * Called to add a new field to this layout.
     * @param name The name of the field being added
     * @param type The type of the field
     * @param size The size of the field
     * @return The offset from the beginning of the structure the field
     * should be located at
     */
    int addField(String name, Class<?> type, int size);

    /**
     * Called to add a new array field to this layout.
     * @param name The name of the field being added
     * @param type The type of a single element of the field
     * @param elementSize The size of a single element of the array
     * @param arrayLength The total number of elements in the array
     * @return The offset from the beginning of the structure the field
     * should be located at
     */
    int addArrayField(String name, Class<?> type, int elementSize, int arrayLength);

    /**
     * Signals the end of the structure.
     * @return The total size of the structure
     */
    int close();
}
