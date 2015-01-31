package it.jpack;

/**
 *
 * @author Flavio
 */
public class SimpleClass {

    private int value1;
    private double value2;

    public SimpleClass() {
    }

    public SimpleClass(int value1, double value2) {
        this.value1 = value1;
        this.value2 = value2;
    }

    public int getValue1() {
        return value1;
    }

    public void setValue1(int value1) {
        this.value1 = value1;
    }

    public double getValue2() {
        return value2;
    }

    public void setValue2(double value2) {
        this.value2 = value2;
    }

}
