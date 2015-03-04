package it.jpack.impl;

import it.jpack.StructField;
import it.jpack.StructLayout;
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
 * @param <F>
 */
public abstract class JavassistBuilder<T extends StructPointer<T>, F extends JavassistArrayFactory<T>> {

    private static final List<Method> STRUCT_POINTER_METHODS = Arrays.asList(StructPointer.class.getMethods());
    private static final List<TypeHelper> TYPE_HELPERS = Arrays.asList(TByte, TShort, TInt, TLong, TFloat, TDouble, TChar, TCharSequence, TString, TPointer);

    private final JavassistRepository repository;
    private final ClassPool cPool;
    private final Class<T> pointerInterface;
    private final CtClass ctClass;
    private final StringBuilder constructorBody = new StringBuilder();
    private final StructLayout layout;

    protected JavassistBuilder(JavassistRepository repository, Class<T> pointerInterface, StructLayout layout, ClassPool cPool, String className) {
        try {
            this.repository = repository;
            this.pointerInterface = pointerInterface;
            this.layout = layout;
            this.cPool = cPool;

            ctClass = cPool.makeClass(className, cPool.get(AbstractPointer.class.getName()));
            ctClass.setModifiers(Modifier.FINAL + Modifier.PUBLIC);
            ctClass.addInterface(cPool.get(pointerInterface.getName()));
            constructorBody.append("super(array, parentPointer, parentOffset);");
        } catch (RuntimeException | NotFoundException ex) {
            throw new IllegalStateException("Error implementing class " + pointerInterface, ex);
        }
    }

    void addPrimitive(String name, Class<?> typeClass, int fieldSize) throws IllegalStateException {
        try {
            int offset = layout.addField(name, typeClass, fieldSize);
            String type = typeClass.getSimpleName();
            String cName = Character.toUpperCase(name.charAt(0)) + name.substring(1);
            String cOffset = "getFieldPosition(" + offset + ")";
            String cType = Character.toUpperCase(type.charAt(0)) + type.substring(1);
            ctClass.addMethod(CtNewMethod.make("public " + type + " get" + cName + "() { return array.get" + cType + "(" + cOffset + "); }", ctClass));
            ctClass.addMethod(CtNewMethod.make("public void set" + cName + "(" + type + " value) { return array.put" + cType + "(" + cOffset + ", value); }", ctClass));
        } catch (CannotCompileException ex) {
            throw new IllegalStateException("Error adding " + typeClass + " field " + name, ex);
        }
    }

    void addPrimitiveArray(String name, Class<?> typeClass, int fieldSize, int length) throws IllegalStateException {
        try {
            int offset = layout.addArrayField(name, typeClass, fieldSize, length);
            String type = typeClass.getSimpleName();
            String cName = Character.toUpperCase(name.charAt(0)) + name.substring(1);
            String cOffset = "i * " + fieldSize + " + getFieldPosition(" + offset + ")";
            String cType = Character.toUpperCase(type.charAt(0)) + type.substring(1);
            ctClass.addMethod(CtNewMethod.make("public " + type + " get" + cName + "(int i) { return array.get" + cType + "(" + cOffset + "); }", ctClass));
            ctClass.addMethod(CtNewMethod.make("public void set" + cName + "(int i, " + type + " value) { return array.put" + cType + "(" + cOffset + ", value); }", ctClass));
        } catch (CannotCompileException ex) {
            throw new IllegalStateException("Error adding " + typeClass + " field " + name, ex);
        }
    }

    void addCharSequence(String name, int length) {
        try {
            int offset = layout.addField(name, CharSequence.class, length * Character.SIZE / 8);
            ctClass.addField(new CtField(cPool.get(CharSequenceImpl.class.getName()), name, ctClass));
            constructorBody.append(String.format("this.%s = new it.jpack.impl.CharSequenceImpl(%d, %d, this);", name, length, offset));
            
            String cName = Character.toUpperCase(name.charAt(0)) + name.substring(1);
            ctClass.addMethod(CtNewMethod.make("public CharSequence get" + cName + "() { return this." + name + "; }", ctClass));
            ctClass.addMethod(CtNewMethod.make("public void set" + cName + "(CharSequence value) { this." + name + ".set(value); }", ctClass));
        } catch (CannotCompileException ex) {
            throw new IllegalStateException("Error adding CharSequence field " + name, ex);
        } catch (NotFoundException ex) {
            throw new IllegalStateException("Error adding CharSequence field " + name, ex);
        }
    }

    void addString(String name, int length) {
        try {
            int offset = layout.addField(name, String.class, length * Character.SIZE / 8);
            String cName = Character.toUpperCase(name.charAt(0)) + name.substring(1);
            String cOffset = "getFieldPosition(" + offset + ")";
            ctClass.addMethod(CtNewMethod.make("public String get" + cName + "() { return array.getString(" + cOffset + ", " + length + "); }", ctClass));
            ctClass.addMethod(CtNewMethod.make("public void setString(String value) { if (value.length() != " + length + ") { throw new IllegalArgumentException(\"String length must be " + length + "\"); } return array.putString(" + cOffset + ", value); }", ctClass));
        } catch (CannotCompileException ex) {
            throw new IllegalStateException("Error adding String field " + name, ex);
        }
    }

    <S extends StructPointer<S>> void addStruct(String name, Class<S> pointerInterface, int length) {
        JavassistArrayFactory<S> innerFactory = repository.getFactory(pointerInterface);
        try {
            int offset = (length == 1 ? layout.addField(name, pointerInterface, innerFactory.getStructSize())
                    : layout.addArrayField(name, pointerInterface, innerFactory.getStructSize(), length));
            ctClass.addField(new CtField(innerFactory.getCtImplementation(), name, ctClass));
            constructorBody.append(String.format("this.%s = new %s(array, this, %d);", name, innerFactory.getCtImplementation().getName(), offset));

            String cName = Character.toUpperCase(name.charAt(0)) + name.substring(1);
            ctClass.addMethod(CtNewMethod.make("public " + pointerInterface.getName() + " get" + cName + "() { return " + name + "; }", ctClass));
            ctClass.addMethod(CtNewMethod.make("public void set" + cName + "(" + pointerInterface.getName() + " value) { throw new UnsupportedOperationException(); }", ctClass));
        } catch (CannotCompileException ex) {
            throw new IllegalStateException(ex);
        }
    }

    private F build() {
        try {
            int size = layout.close();
            ctClass.addMethod(CtNewMethod.make("public int getStructSize() { return " + size + "; }", ctClass));
            ctClass.addConstructor(CtNewConstructor.make("public " + ctClass.getSimpleName() + "(" + 
                    StructArrayInternal.class.getName() + " array, "
                    + StructPointerInternal.class.getName() + " parentPointer, "
                    + "int parentOffset) { " + constructorBody + " } ", ctClass));
            return build(pointerInterface, (Class<? extends T>) ctClass.toClass(), ctClass, size);
        } catch (CannotCompileException ex) {
            throw new IllegalStateException("Error finalizing class creation", ex);
        }
    }

    protected abstract F build(Class<T> pointerInterface, Class<? extends T> pointerImplementation, CtClass ctImplementation, int size);

    protected static <T extends StructPointer<T>, F extends JavassistArrayFactory<T>> F build(JavassistBuilder<T, F> builder, Class<T> pointerInterface) {
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
            for (TypeHelper helper : TYPE_HELPERS) {
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

        public void addTo(JavassistBuilder<?, ?> builder) {
            typeHelper.addTo(builder, name, type, length);
        }

        @Override
        public String toString() {
            return "PropertyInfo{" + "name=" + name + ", type=" + type + ", index=" + position + '}';
        }
    }
}
