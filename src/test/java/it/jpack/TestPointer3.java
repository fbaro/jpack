package it.jpack;

/**
 *
 * @author list
 */
public interface TestPointer3 extends StructPointer<TestPointer3> {

    TestPointer2 getP1();
    void setP1(TestPointer2 value);
    int getValue();
    void setValue(int value);
    TestPointer2 getP2();
    void setP2(TestPointer2 value);

}
