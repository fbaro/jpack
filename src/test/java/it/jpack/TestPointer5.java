package it.jpack;

/**
 *
 * @author list
 */
public interface TestPointer5 extends StructPointer<TestPointer5> {
    @StructField(length = 4)
    public TestPointer1 getArray();
    public void setArray(TestPointer1 value);
}
