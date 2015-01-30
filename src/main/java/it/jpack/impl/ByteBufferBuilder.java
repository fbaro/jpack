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
    private static final List<TypeHelper> PRIMITIVE_HELPERS = Arrays.asList(TByte, TShort, TInt, TLong, TFloat, TDouble, TChar, TPointer);

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

    private void addPrimitive(String name, String type, int fieldSize) throws IllegalStateException {
        try {
            String cName = Character.toUpperCase(name.charAt(0)) + name.substring(1);
            String cOffset = "getFieldPosition(" + offset + ")";
            String cType = Character.toUpperCase(type.charAt(0)) + type.substring(1);
            ctClass.addMethod(CtNewMethod.make("public " + type + " get" + cName + "() { return array.get" + cType + "(" + cOffset + "); }", ctClass));
            ctClass.addMethod(CtNewMethod.make("public void set" + cName + "(" + type + " value) { return array.put" + cType + "(" + cOffset + ", value); }", ctClass));
            offset += align(fieldSize);
        } catch (CannotCompileException ex) {
            throw new IllegalStateException("Error adding " + type + " field " + name, ex);
        }
    }

    private void addPrimitiveArray(String name, String type, int fieldSize, int length) throws IllegalStateException {
        try {
            String cName = Character.toUpperCase(name.charAt(0)) + name.substring(1);
            String cOffset = "i * " + fieldSize + " + getFieldPosition(" + offset + ")";
            String cType = Character.toUpperCase(type.charAt(0)) + type.substring(1);
            ctClass.addMethod(CtNewMethod.make("public " + type + " get" + cName + "(int i) { return array.get" + cType + "(" + cOffset + "); }", ctClass));
            ctClass.addMethod(CtNewMethod.make("public void set" + cName + "(int i, " + type + " value) { return array.put" + cType + "(" + cOffset + ", value); }", ctClass));
            offset += align(fieldSize * length);
        } catch (CannotCompileException ex) {
            throw new IllegalStateException("Error adding " + type + " field " + name, ex);
        }
    }

    private <S extends StructPointer<S>> void addStruct(String name, Class<S> pointerInterface, int length) {
        ByteBufferArrayFactory<S> innerFactory = repository.getFactory(pointerInterface);
        try {
            ctClass.addField(new CtField(innerFactory.getCtImplementation(), name, ctClass));
            constructorBody.append(String.format("this.%s = new %s(array, this, %d);", name, innerFactory.getCtImplementation().getName(), offset));

            String cName = Character.toUpperCase(name.charAt(0)) + name.substring(1);
            ctClass.addMethod(CtNewMethod.make("public " + pointerInterface.getName() + " get" + cName + "() { return " + name + "; }", ctClass));
            ctClass.addMethod(CtNewMethod.make("public void set" + cName + "(" + pointerInterface.getName() + " value) { throw new UnsupportedOperationException(); }", ctClass));
            offset += align(innerFactory.getStructSize() * length);
        } catch (CannotCompileException ex) {
            throw new IllegalStateException(ex);
        }
    }

    private int align(int size) {
        int rem = size % alignment;
        return rem == 0 ? size : size + (alignment - rem);
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

    private static boolean isGetterSignature(Method m) {
        return m.getName().startsWith("get") && !(m.getReturnType() == Void.TYPE)
                && (m.getParameterTypes().length == 0 || (m.getParameterTypes().length == 1 && m.getParameterTypes()[0] == Integer.TYPE));
    }

    private static boolean isSetterSignature(Method m) {
        return m.getName().startsWith("set") && (m.getReturnType() == Void.TYPE)
                && (m.getParameterTypes().length == 1 || (m.getParameterTypes().length == 2 && m.getParameterTypes()[0] == Integer.TYPE));
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
        public final int length;
        public final boolean hasGetter;
        public final boolean hasSetter;
        public final boolean arrayMethod;
        public final TypeHelper typeHelper;

        private PropertyInfo(String name, Class<?> type, int position, int length, boolean arrayMethod, boolean hasGetter, boolean hasSetter, TypeHelper typeHelper) {
            if (length < 0) {
                throw new IllegalArgumentException("Array field length must be positive");
            }
            this.name = name;
            this.type = type;
            this.position = position;
            this.length = length;
            this.arrayMethod = arrayMethod;
            this.hasGetter = hasGetter;
            this.hasSetter = hasSetter;
            this.typeHelper = typeHelper;
        }

        public static PropertyInfo forGetter(Method m) {
            String name = Introspector.decapitalize(m.getName().substring(3));
            Class<?> type = m.getReturnType();
            StructField annotation = m.getAnnotation(StructField.class);
            if (m.getParameterTypes().length == 1 && !type.isPrimitive()) {
                throw new IllegalArgumentException("Non-primitive inner arrays do not support array method signature: error on " + m);
            }
            return new PropertyInfo(name, type, 
                    annotation == null ? Integer.MAX_VALUE : annotation.position(), 
                    annotation == null ? 0 : annotation.length(),
                    m.getParameterTypes().length == 1, true, false, findHelper(name, type));
        }

        public static PropertyInfo forSetter(Method m) {
            String name = Introspector.decapitalize(m.getName().substring(3));
            Class<?> type = m.getParameterTypes()[m.getParameterTypes().length - 1];
            StructField annotation = m.getAnnotation(StructField.class);
            if (m.getParameterTypes().length == 2 && !type.isPrimitive()) {
                throw new IllegalArgumentException("Non-primitive inner arrays do not support array method signature: error on method " + m);
            }
            return new PropertyInfo(name, type, 
                    annotation == null ? Integer.MAX_VALUE : annotation.position(),
                    annotation == null ? 0 : annotation.length(),
                    m.getParameterTypes().length == 2, false, true, findHelper(name, type));
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
            if (this.type != other.type) {
                throw new IllegalArgumentException("Type mismatch for property " + name);
            }
            if (this.arrayMethod != other.arrayMethod) {
                throw new IllegalArgumentException("Array method signature mismatch for property " + name);
            }
            if (this.typeHelper != other.typeHelper) {
                throw new IllegalStateException("Type helper mismatch for property " + name);
            }
            if (this.position != Integer.MAX_VALUE && other.position != Integer.MAX_VALUE && this.position != other.position) {
                throw new IllegalArgumentException("Field position mismatch for property " + name);
            }
            if (this.length != 0 && other.length != 0 && this.length != other.length) {
                throw new IllegalStateException("Array length mismatch for property " + name);
            }
            return new PropertyInfo(name, type, 
                    this.position == Integer.MAX_VALUE ? other.position : this.position,
                    this.length == 0 ? other.length : this.length, this.arrayMethod,
                    this.hasGetter || other.hasGetter, this.hasSetter || other.hasSetter, typeHelper);
        }

        @Override
        public int compareTo(PropertyInfo o) {
            return Integer.compare(this.position, o.position);
        }

        public void addTo(ByteBufferBuilder<?> builder) {
            if (type.isPrimitive()) {
                if (length == 0 || length == 1) {
                    builder.addPrimitive(name, type.getSimpleName(), typeHelper.getBitSize() / 8);
                } else {
                    builder.addPrimitiveArray(name, type.getSimpleName(), typeHelper.getBitSize() / 8, length);
                }
            } else {
                builder.addStruct(name, (Class) type, length == 0 ? 1 : length);
            }
        }

        @Override
        public String toString() {
            return "PropertyInfo{" + "name=" + name + ", type=" + type + ", index=" + position + '}';
        }
    }
}
