package it.jpack;

/**
 *
 * @author Flavio
 */
public interface SimplePointer extends StructPointer<SimplePointer> {

    int getValue1();
    void setValue1(int v);
    double getValue2();
    void setValue2(double v);

}
