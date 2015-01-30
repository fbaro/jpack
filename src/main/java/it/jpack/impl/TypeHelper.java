package it.jpack.impl;

import it.jpack.StructPointer;

/**
 *
 * @author list
 */
class TypeHelper {

    private final int bitSize;
    private final Class<?> type;

    public TypeHelper(int bitSize, Class<?> type) {
        this.bitSize = bitSize;
        this.type = type;
    }

    public int getBitSize() {
        return bitSize;
    }

    public Class<?> getType() {
        return type;
    }

    public boolean matches(Class<?> inputType) {
        return type.isAssignableFrom(inputType);
    }

    public static final TypeHelper TByte = new TypeHelper(Byte.SIZE, Byte.TYPE);
    public static final TypeHelper TShort = new TypeHelper(Short.SIZE, Short.TYPE);
    public static final TypeHelper TInt = new TypeHelper(Integer.SIZE, Integer.TYPE);
    public static final TypeHelper TLong = new TypeHelper(Long.SIZE, Long.TYPE);
    public static final TypeHelper TFloat = new TypeHelper(Float.SIZE, Float.TYPE);
    public static final TypeHelper TDouble = new TypeHelper(Double.SIZE, Double.TYPE);
    public static final TypeHelper TPointer = new TypeHelper(0, StructPointer.class);

}
