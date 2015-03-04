package it.jpack.impl;

import it.jpack.Struct;
import it.jpack.StructLayout;
import it.jpack.StructPointer;

/**
 *
 * @author fbaro
 * @param <T>
 */
public class DefaultStructLayout<T extends StructPointer<T>> implements StructLayout {

    private final int alignment;
    private int size;

    public DefaultStructLayout(Class<T> pointerInterface) {
        this.alignment = getAlignment(pointerInterface.getAnnotation(Struct.class));
    }

    @Override
    public int addField(String name, Class<?> type, int size) {
        int ret = this.size;
        this.size += align(size);
        return ret;
    }

    @Override
    public int addArrayField(String name, Class<?> type, int elementSize, int arrayLength) {
        int ret = this.size;
        this.size += align(elementSize * arrayLength);
        return ret;
    }

    @Override
    public int close() {
        return size;
    }

    private int align(int size) {
        int rem = size % alignment;
        return rem == 0 ? size : size + (alignment - rem);
    }

    private static int getAlignment(Struct annotation) {
        int align = annotation == null ? 0 : annotation.align();
        if (align == 0) {
            switch (System.getProperty("os.arch")) {
                case "x86":
                    return 4;
                case "amd64":
                    return 8;
                default:
                    throw new IllegalStateException("Unknown architecture from 'os.arch' system property: " + System.getProperty("os.arch"));
            }
        }
        switch (align) {
            case 1:
            case 2:
            case 4:
            case 8:
            case 16:
                return align;
            default:
                throw new IllegalArgumentException("Unsupported alignment value: " + align);
        }
    }
}
