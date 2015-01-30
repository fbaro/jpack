package it.jpack.impl;

import it.jpack.TestPointer1;
import it.jpack.TestPointer2;
import it.jpack.TestPointer3;
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
        ByteBufferArray<TestPointer1> arr = REPO.newArray(TestPointer1.class, 10);
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
        ByteBufferArray<TestPointer2> arr = REPO.newArray(TestPointer2.class, 10);
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
        ByteBufferArray<TestPointer3> arr = REPO.newArray(TestPointer3.class, 10);
        assertEquals(36, arr.getStructSize());
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
}
