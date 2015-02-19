package it.jpack;

/**
 *
 * @author Flavio
 */
@Struct(align = 4)
public interface TestPointer7 extends StructPointer<TestPointer7> {

    @StructField(position = 0)
    short getShort();
    void setShort(short v);
    @StructField(position = 1, length = 12)
    String getString();
    void setString(String v);
    @StructField(position = 2)
    int getInt();
    void setInt(int v);

}
