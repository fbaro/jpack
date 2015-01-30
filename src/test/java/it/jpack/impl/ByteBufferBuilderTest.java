package it.jpack.impl;

import it.jpack.TestPointer1;
import it.jpack.TestPointer2;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author fbaro
 */
public class ByteBufferBuilderTest {

    //@Test
    public void verifySimpleDirectCreation() {
        ByteBufferRepository repo = new ByteBufferRepository();
        ByteBufferBuilder<TestPointer1> bbb = new ByteBufferBuilder<>(repo, TestPointer1.class, repo.getClassPool(), TestPointer1.class.getName() + "Impl");
        bbb.addInt("int");
        bbb.addDouble("double");
        ByteBufferArrayFactory<TestPointer1> bb = bbb.build();
        ByteBufferArray<TestPointer1> arr = bb.newArray(10);
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

    //@Test
    public void verifySimpleClassIntrospectiveCreation() {
        ByteBufferRepository repo = new ByteBufferRepository();
        ByteBufferArray<TestPointer1> arr = repo.newArray(TestPointer1.class, 10);
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
        ByteBufferRepository repo = new ByteBufferRepository();
        ByteBufferArray<TestPointer2> arr = repo.newArray(TestPointer2.class, 10);
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

}
