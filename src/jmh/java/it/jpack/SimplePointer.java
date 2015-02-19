package it.jpack;

/**
 *
 * @author Flavio
 */
public interface SimplePointer extends StructPointer<SimplePointer> {

    @StructField(position = 0)
    int getValue1();
    void setValue1(int v);

    @StructField(position = 1)
    double getValue2();
    void setValue2(double v);

}
