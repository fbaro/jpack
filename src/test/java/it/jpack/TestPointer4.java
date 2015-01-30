package it.jpack;

/**
 *
 * @author list
 */
public interface TestPointer4 extends StructPointer<TestPointer4> {
    @StructField(length = 8)
    long getValue(int index);
    void setValue(int index, long value);
}
