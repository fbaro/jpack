package it.jpack;

import it.jpack.StructPointer;

/**
 *
 * @author list
 */
public interface TestPointer1 extends StructPointer<TestPointer1> {
    
    int getInt();
    void setInt(int value);
    double getDouble();
    void setDouble(double value);

}
