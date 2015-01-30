package it.jpack;

/**
 *
 * @author fbaro
 */
public @interface StructField {
    int position() default Integer.MAX_VALUE;
}
