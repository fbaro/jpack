package it.jpack;

/**
 *
 * @author Flavio
 */
@Struct(align = 4)
public interface TestPointer6 extends StructPointer<TestPointer6> {

    @StructField(position = 0)
    short getShort();
    void setShort(short v);
    @StructField(position = 1, length = 12)
    CharSequence getSequence();
    void setSequence(CharSequence v);
    @StructField(position = 2)
    int getInt();
    void setInt(int v);

}
