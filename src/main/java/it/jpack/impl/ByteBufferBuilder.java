package it.jpack.impl;

import it.jpack.Struct;
import it.jpack.StructField;
import it.jpack.StructPointer;
import static it.jpack.impl.TypeHelper.*;
import java.beans.Introspector;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtNewConstructor;
import javassist.CtNewMethod;
import javassist.Modifier;
import javassist.NotFoundException;

/**
 * @author fbaro
 * @param <T>
 */
public class ByteBufferBuilder<T extends StructPointer<T>> {

    private static final List<Method> STRUCT_POINTER_METHODS = Arrays.asList(StructPointer.class.getMethods());
    private static final List<TypeHelper> PRIMITIVE_HELPERS = Arrays.asList(TByte, TShort, TInt, TLong, TFloat, TDouble, TPointer);

    protected final ByteBufferRepository repository;
    protected final ClassPool cPool;
    protected final Class<T> pointerInterface;
    protected final CtClass ctClass;
    protected final StringBuilder constructorBody = new StringBuilder();
    protected final int alignment;
    protected int offset;

    protected ByteBufferBuilder(ByteBufferRepository repository, Class<T> pointerInterface, ClassPool cPool, String className) {
        try {
            this.repository = repository;
            this.pointerInterface = pointerInterface;
            this.cPool = cPool;
            this.alignment = getAlignment(pointerInterface.getAnnotation(Struct.class));

            ctClass = cPool.makeClass(className, cPool.get(AbstractByteBufferPointer.class.getName()));
            ctClass.setModifiers(Modifier.FINAL + Modifier.PUBLIC);
            ctClass.addInterface(cPool.get(pointerInterface.getName()));
            ctClass.addMethod(CtNewMethod.make("public " + StructPointer.class.getName() + " at(int index) { this.index = index; return this; }", ctClass));
            constructorBody.append("super(array, parentPointer, parentOffset);");
        } catch (RuntimeException | NotFoundException | CannotCompileException ex) {
            throw new IllegalStateException("Error implementing class " + pointerInterface, ex);
        }
    }

    private <S extends StructPointer<S>> void addStruct(String name, Class<S> pointerInterface) {
        ByteBufferArrayFactory<S> innerFactory = repository.getFactory(pointerInterface);
        try {
            ctClass.addField(new CtField(innerFactory.getCtImplementation(), name, ctClass));
            constructorBody.append(String.format("this.%s = new %s(array, this, %d);", name, innerFactory.getCtImplementation().getName(), offset));

            String cName = Character.toUpperCase(name.charAt(0)) + name.substring(1);
            ctClass.addMethod(CtNewMethod.make("public " + pointerInterface.getName() + " get" + cName + "() { return " + name + "; }", ctClass));
            ctClass.addMethod(CtNewMethod.make("public void set" + cName + "(" + pointerInterface.getName() + " value) { throw new UnsupportedOperationException(); }", ctClass));
            offset += align(innerFactory.getStructSize());
        } catch (CannotCompileException ex) {
            throw new IllegalStateException(ex);
        }
    }

    private ByteBufferArrayFactory<T> build() {
        try {
            ctClass.addMethod(CtNewMethod.make("public int getStructSize() { return " + offset + "; }", ctClass));
            ctClass.addConstructor(CtNewConstructor.make("public " + ctClass.getSimpleName() + "(" + 
                    ByteBufferArray.class.getName() + " array, "
                    + StructPointerInternal.class.getName() + " parentPointer, "
                    + "int parentOffset) { " + constructorBody + " } ", ctClass));
            return new ByteBufferArrayFactory<>(pointerInterface, (Class<? extends T>) ctClass.toClass(), ctClass, offset);
        } catch (CannotCompileException ex) {
            throw new IllegalStateException("Error finalizing class creation", ex);
        }
    }

    private void addPrimitive(String name, String fieldType, int fieldSize) throws IllegalStateException {
        try {
            String cName = Character.toUpperCase(name.charAt(0)) + name.substring(1);
            String cOffset = "getFieldPosition(" + offset + ")";
            String cFieldType = Character.toUpperCase(fieldType.charAt(0)) + fieldType.substring(1);
            ctClass.addMethod(CtNewMethod.make("public " + fieldType + " get" + cName + "() { return array.get" + cFieldType + "(" + cOffset + "); }", ctClass));
            ctClass.addMethod(CtNewMethod.make("public void set" + cName + "(" + fieldType + " value) { return array.put" + cFieldType + "(" + cOffset + ", value); }", ctClass));
            offset += align(fieldSize);
        } catch (CannotCompileException ex) {
            throw new IllegalStateException("Error adding int field " + name, ex);
        }
    }        

    private int align(int size) {
        int rem = size % alignment;
        return rem == 0 ? size : size + (alignment - rem);
    }

    public static <T extends StructPointer<T>> ByteBufferArrayFactory<T> build(ByteBufferRepository repository, Class<T> pointerInterface) {
        ByteBufferBuilder<T> builder = new ByteBufferBuilder<>(repository, pointerInterface, repository.getClassPool(), pointerInterface.getName() + "Impl");
        Map<String, PropertyInfo> properties = new LinkedHashMap<>();
        for (Method m : pointerInterface.getMethods()) {
            if (STRUCT_POINTER_METHODS.contains(m)) {
                continue;
            }
            PropertyInfo newInfo;
            if (isGetterSignature(m)) {
                newInfo = PropertyInfo.forGetter(m);
            } else if (isSetterSignature(m)) {
                newInfo = PropertyInfo.forSetter(m);
            } else {
                throw new IllegalArgumentException("Method signature not recognized: " + m);
            }
            PropertyInfo oldInfo = properties.get(newInfo.name);
            if (oldInfo != null) {
                properties.put(newInfo.name, newInfo.merge(oldInfo));
            } else {
                properties.put(newInfo.name, newInfo);
            }
        }
        List<PropertyInfo> sortedProperties = new ArrayList<>(properties.values());
        Collections.sort(sortedProperties);
        for (PropertyInfo pi : sortedProperties) {
            pi.addTo(builder);
        }
        return builder.build();
    }

    private static boolean isSetterSignature(Method m) {
        return m.getName().startsWith("set") && m.getParameterTypes().length == 1 && m.getReturnType() == Void.TYPE;
    }

    private static boolean isGetterSignature(Method m) {
        return m.getName().startsWith("get") && m.getParameterTypes().length == 0 && !(m.getReturnType() == Void.TYPE);
    }

    private static int getAlignment(Struct annotation) {
        int align = annotation == null ? 0 : annotation.align();
        if (align == 0) {
            switch (System.getProperty("os.arch")) {
                case "x86":
                    return 4;
                case "amd64":
                    return 8;
                default:
                    throw new IllegalStateException("Unknown architecture from 'os.arch' system property: " + System.getProperty("os.arch"));
            }
        }
        switch (align) {
            case 1:
            case 2:
            case 4:
            case 8:
            case 16:
                return align;
            default:
                throw new IllegalArgumentException("Unsupported alignment value: " + align);
        }
    }

    private static final class PropertyInfo implements Comparable<PropertyInfo> {
        public final String name;
        public final Class<?> type;
        public final int position;
        public final boolean hasGetter;
        public final boolean hasSetter;
        public final TypeHelper typeHelper;

        private PropertyInfo(String name, Class<?> type, int position, boolean hasGetter, boolean hasSetter, TypeHelper typeHelper) {
            this.name = name;
            this.type = type;
            this.position = position;
            this.hasGetter = hasGetter;
            this.hasSetter = hasSetter;
            this.typeHelper = typeHelper;
        }

        public static PropertyInfo forGetter(Method m) {
            String name = Introspector.decapitalize(m.getName().substring(3));
            Class<?> type = m.getReturnType();
            StructField annotation = m.getAnnotation(StructField.class);
            return new PropertyInfo(name, type, annotation == null ? Integer.MAX_VALUE : annotation.position(), true, false, findHelper(name, type));
        }

        public static PropertyInfo forSetter(Method m) {
            String name = Introspector.decapitalize(m.getName().substring(3));
            Class<?> type = m.getParameterTypes()[0];
            StructField annotation = m.getAnnotation(StructField.class);
            return new PropertyInfo(name, type, annotation == null ? Integer.MAX_VALUE : annotation.position(), false, true, findHelper(name, type));
        }

        private static TypeHelper findHelper(String name, Class<?> type) {
            for (TypeHelper helper : PRIMITIVE_HELPERS) {
                if (helper.matches(type)) {
                    return helper;
                }
            }
            throw new IllegalArgumentException("Unsupported type " + type + " for property " + name); 
        }

        public PropertyInfo merge(PropertyInfo other) {
            if (!this.name.equals(other.name)) {
                throw new IllegalStateException("Property name mismatch");
            }
            if (this.position != Integer.MAX_VALUE && other.position != Integer.MAX_VALUE && this.position != other.position) {
                throw new IllegalArgumentException("Field position mismatch for property " + name);
            }
            if (this.type != other.type) {
                throw new IllegalArgumentException("Type mismatch for property " + name);
            }
            return new PropertyInfo(name, type, position, true, true, typeHelper);
        }

        @Override
        public int compareTo(PropertyInfo o) {
            return Integer.compare(this.position, o.position);
        }

        public void addTo(ByteBufferBuilder<?> builder) {
            if (type.isPrimitive()) {
                builder.addPrimitive(name, type.getSimpleName(), typeHelper.getBitSize() / 8);
            } else {
                builder.addStruct(name, (Class) type);
            }
        }

        @Override
        public String toString() {
            return "PropertyInfo{" + "name=" + name + ", type=" + type + ", index=" + position + '}';
        }
    }
}
