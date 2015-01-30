package it.jpack.impl;

import it.jpack.impl.bytebuffer.ByteBufferRepository;
import it.jpack.StructArray;
import it.jpack.StructPointer;
import it.jpack.TestPointer1;
import it.jpack.TestPointer2;
import it.jpack.TestPointer3;
import it.jpack.TestPointer4;
import it.jpack.TestPointer5;
import static org.junit.Assert.assertEquals;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author fbaro
 */
public class ByteBufferBuilderTest {

    private static ByteBufferRepository REPO;

    @BeforeClass
    public static void setUpClass() {
        REPO = new ByteBufferRepository();
    }

    @Test
    public void verifySimpleClassIntrospectiveCreation() {
        StructArray<TestPointer1> arr = REPO.newArray(TestPointer1.class, 10);
        assertEquals(12, arr.getStructSize());
        TestPointer1 p = arr.newPointer();
        for (int i = 0; i < 10; i++) {
            p.at(i).setInt(i);
            p.setDouble(i * 2.0);
        }
        for (int i = 0; i < 10; i++) {
            assertEquals(i, p.at(i).getInt());
            assertEquals(i * 2.0, p.getDouble(), 0.0);
        }
    }

    @Test
    public void verifyNestedClassIntrospectiveCreation() {
        StructArray<TestPointer2> arr = REPO.newArray(TestPointer2.class, 10);
        assertEquals(16, arr.getStructSize());
        TestPointer2 p = arr.newPointer();
        TestPointer1 p1 = p.getInner();
        for (int i = 0; i < 10; i++) {
            p.at(i).setFloat(i * 1.0f);
            p1.setInt(i * 2);
            p1.setDouble(i * 3.0);
        }
        for (int i = 0; i < 10; i++) {
            assertEquals(i * 1.0f, p.at(i).getFloat(), 0.0f);
            assertEquals(i * 2, p1.getInt());
            assertEquals(i * 3.0, p1.getDouble(), 0.0);
        }
    }

    @Test
    public void verifyComplexClassIntrospectiveCreation() {
        StructArray<TestPointer3> arr = REPO.newArray(TestPointer3.class, 10);
        assertEquals(40, arr.getStructSize());
        TestPointer3 p = arr.newPointer();
        for (int i = 1; i <= 10; i++) {
            p.at(i - 1).setValue(i);
            p.getP1().setFloat(i * 1.0f);
            p.getP1().getInner().setInt(i * 2);
            p.getP1().getInner().setDouble(i * 3.0);
            p.getP2().setFloat(i * 4.0f);
            p.getP2().getInner().setInt(i * 5);
            p.getP2().getInner().setDouble(i * 6.0);
        }
        for (int i = 1; i <= 10; i++) {
            assertEquals(i, p.at(i - 1).getValue());
            assertEquals(i * 1.0f, p.getP1().getFloat(), 0.0f);
            assertEquals(i * 2   , p.getP1().getInner().getInt());
            assertEquals(i * 3.0 , p.getP1().getInner().getDouble(), 0.0);
            assertEquals(i * 4.0f, p.getP2().getFloat(), 0.0f);
            assertEquals(i * 5   , p.getP2().getInner().getInt());
            assertEquals(i * 6.0 , p.getP2().getInner().getDouble(), 0.0);
        }
    }

    @Test
    public void verifyAllPrimitiveTypesWork() {
        StructArray<AllPrimitiveTypes> arr = REPO.newArray(AllPrimitiveTypes.class, 10);
    }

    @Test
    public void verifySimplePrimitiveArray() {
        StructArray<TestPointer4> arr = REPO.newArray(TestPointer4.class, 10);
        assertEquals(64, arr.getStructSize());
        TestPointer4 p = arr.newPointer();
        for (int i = 0; i < 10; i++) {
            p.at(i);
            for (int j = 0; j < 8; j++) {
                p.setValue(j, (i + 1) * (j + 20));
            }
        }
        for (int i = 0; i < 10; i++) {
            p.at(i);
            for (int j = 0; j < 8; j++) {
                assertEquals((i + 1) * (j + 20), p.getValue(j));
            }
        }
    }

    @Test
    public void verifyNonPrimitiveArray() {
        StructArray<TestPointer5> arr = REPO.newArray(TestPointer5.class, 10);
        assertEquals(48, arr.getStructSize());
        TestPointer5 p = arr.newPointer();
        TestPointer1 p2 = p.getArray();
        for (int i = 0; i < 10; i++) {
            p.at(i);
            for (int j = 0; j < 4; j++) {
                p2.at(j);
                p.getArray().setInt(i + j * 10);
                p.getArray().setDouble(i + j * 20.0);
            }
        }
        for (int i = 0; i < 10; i++) {
            p.at(i);
            for (int j = 0; j < 4; j++) {
                p2.at(j);
                assertEquals(i + j * 10, p.getArray().getInt());
                assertEquals(i + j * 20.0, p.getArray().getDouble(), 0.0);
            }
        }
    }

    public interface AllPrimitiveTypes extends StructPointer<AllPrimitiveTypes> {
        public byte getV1();
        public void setV1(byte value);
        public short getV2();
        public void setV2(short value);
        public int getV3();
        public void setV3(int value);
        public long getV4();
        public void setV4(long value);
        public float getV5();
        public void setV5(float value);
        public double getV6();
        public void setV6(double value);
        public char getV7();
        public void setV7(char value);
    }
}
