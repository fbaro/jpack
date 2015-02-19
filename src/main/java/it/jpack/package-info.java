/**
 * <p>
 * This is the main JPack package, and the only one users should be concerned about.
 * Users should begin by retrieving a {@link StructRepository} from the {@link Repositories} 
 * static factory; most user will only need a single {@code StructRepository} instance.
 * From the {@code StructRepository}, {@link StructArray} instances can be built
 * as necessary. The contents of an array can be accessed through {@code StructPointer}
 * instances, which can be obtained from {@code StructArray}.
 * </p>
 * <p>
 * Usage example:
 * </p>
 * <pre><code>
 * 
 * public class MyPointer extends StructPointer{@literal <MyPointer>} {
 *     &#64;StructField(position = 0)
 *     int getFoo();
 *     void setFoo(int value);
 *     &#64;StructField(position = 1)
 *     double getBar();
 *     void setBar(double value);
 * }
 * 
 * public static void main(String[] args) {
 *     StructRepository repo = Repositories.newUnsafeRepository();
 *     StructArray{@literal <MyPointer>} arr = repo.newArray(MyPointer.class, 16);
 *     MyPointer ptr = arr.newPointer();
 *     for (int i = 0; i &lt; arr.getLength(); i++) {
 *         ptr.setFoo(i);
 *         assertEquals(i, ptr.getFoo());
 *     }
 * }
 * </code></pre>
 */
package it.jpack;
