package it.jpack.impl;

/**
 *
 * @author Flavio
 */
public final class CharSequenceImpl implements CharSequence {
    private final int length;
    private final int offset;
    private final AbstractPointer<?> outer;

    public CharSequenceImpl(int length, int offset, final AbstractPointer<?> outer) {
        this.outer = outer;
        this.length = length;
        this.offset = offset;
    }

    @Override
    public int length() {
        return length;
    }

    @Override
    public char charAt(int index) {
        return outer.array.getChar(getArrayIndex() + 2 * index);
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        return new CharSequenceImpl(end - start, offset + start * 2, outer);
    }

    public void set(CharSequence value) {
        if (value.length() != length) {
            throw new IllegalArgumentException("Input length " + value.length() + ", required " + length);
        }
        if (value instanceof String) {
            outer.array.putString(getArrayIndex(), (String) value);
        } else {
            int iOffset = getArrayIndex();
            for (int i = 0; i < length; i++, iOffset += 2) {
                outer.array.putChar(iOffset, value.charAt(i));
            }
        }
    }

    @Override
    public String toString() {
        return outer.array.getString(getArrayIndex(), length);
    }

    private int getArrayIndex() {
        return outer.index * outer.getStructSize() + outer.getFieldPosition(offset);
    }
}
