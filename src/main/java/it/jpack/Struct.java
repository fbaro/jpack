package it.jpack;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation can be used to further qualify StructPointer instances.
 * It is not compulsory, but is necessary to specify the default alignment of the fields
 * in the structure.
 * @author fbaro
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Struct {
    /** 
     * The default value will align to 4 bytes on 32 bit JVMs, and to 8 bytes on 64 bit JVMs.
     * @return The alignment value, in bytes.
     */
    int align() default 0;
}
