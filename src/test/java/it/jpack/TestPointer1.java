package it.jpack;

/**
 *
 * @author fbaro
 */
@Struct(align = 4)
public interface TestPointer1 extends StructPointer<TestPointer1> {
    
    int getInt();
    void setInt(int value);
    double getDouble();
    void setDouble(double value);

}
