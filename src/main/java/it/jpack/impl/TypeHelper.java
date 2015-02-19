package it.jpack.impl;

import it.jpack.StructPointer;

/**
 *
 * @author list
 */
abstract class TypeHelper {

    public abstract boolean matches(Class<?> inputType);

    public abstract void addTo(JavassistBuilder<?, ?> builder, String name, Class<?> inputType, int length);

    private static class PrimitiveTypeHelper extends TypeHelper {
        private final int bitSize;
        private final Class<?> type;

        public PrimitiveTypeHelper(int bitSize, Class<?> type) {
            this.bitSize = bitSize;
            this.type = type;
        }

        @Override
        public boolean matches(Class<?> inputType) {
            return type.isAssignableFrom(inputType);
        }

        @Override
        public void addTo(JavassistBuilder<?, ?> builder, String name, Class<?> inputType, int length) {
            if (length == 0 || length == 1) {
                builder.addPrimitive(name, type.getSimpleName(), bitSize / 8);
            } else {
                builder.addPrimitiveArray(name, type.getSimpleName(), bitSize / 8, length);
            }
        }
    }

    public static final TypeHelper TByte = new PrimitiveTypeHelper(Byte.SIZE, Byte.TYPE);
    public static final TypeHelper TShort = new PrimitiveTypeHelper(Short.SIZE, Short.TYPE);
    public static final TypeHelper TInt = new PrimitiveTypeHelper(Integer.SIZE, Integer.TYPE);
    public static final TypeHelper TLong = new PrimitiveTypeHelper(Long.SIZE, Long.TYPE);
    public static final TypeHelper TFloat = new PrimitiveTypeHelper(Float.SIZE, Float.TYPE);
    public static final TypeHelper TDouble = new PrimitiveTypeHelper(Double.SIZE, Double.TYPE);
    public static final TypeHelper TChar = new PrimitiveTypeHelper(Character.SIZE, Character.TYPE);
    public static final TypeHelper TPointer = new TypeHelper() {
        @Override
        public boolean matches(Class<?> inputType) {
            return inputType.isInterface() && StructPointer.class.isAssignableFrom(inputType);
        }

        @Override
        public void addTo(JavassistBuilder<?, ?> builder, String name, Class<?> inputType, int length) {
            builder.addStruct(name, (Class) inputType, length == 0 ? 1 : length);
        }
    };
    public static final TypeHelper TCharSequence = new TypeHelper() {
        @Override
        public boolean matches(Class<?> inputType) {
            return CharSequence.class.equals(inputType);
        }

        @Override
        public void addTo(JavassistBuilder<?, ?> builder, String name, Class<?> inputType, int length) {
            builder.addCharSequence(name, length);
        }
    };
    public static final TypeHelper TString = new TypeHelper() {
        @Override
        public boolean matches(Class<?> inputType) {
            return String.class.equals(inputType);
        }

        @Override
        public void addTo(JavassistBuilder<?, ?> builder, String name, Class<?> inputType, int length) {
            builder.addString(name, length);
        }
    };
}
