package it.jpack;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation can be used to mark a field inside a {@code StructPointer}.
 * It is not compulsory, as fields are identified through JavaBeans conventions.
 * It is necessary however in order to define fields order, and to specify 
 * substructure lengths. Only one of the getter or setter methods should be annotated;
 * if both methods are annotated, the annotation values should match.
 * 
 * @author fbaro
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface StructField {
    /**
     * Specifies the position (i.e. ordering) of the field inside the structure.
     * The default value relies on the order provided by Java reflection to 
     * decide the position of fields.
     * @return The position of the annotated field
     */
    int position() default Integer.MAX_VALUE;

    /**
     * Specifies the number of elements in array fields, both for primitive arrays
     * and for substructures. The default is for non-array fields.
     * @return The length of the annotated field
     */
    int length() default 0;
}
