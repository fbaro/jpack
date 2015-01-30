package it.jpack.impl;

import it.jpack.StructBuilder;
import it.jpack.StructPointer;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
public class ByteBufferBuilder<T extends StructPointer<T>> implements StructBuilder<T> {

    protected final ByteBufferRepository repository;
    protected final ClassPool cPool;
    protected final Class<T> pointerInterface;
    protected final CtClass ctClass;
    protected final StringBuilder constructorBody = new StringBuilder();
    protected int offset;

    protected ByteBufferBuilder(ByteBufferRepository repository, Class<T> pointerInterface, ClassPool cPool, String className) {
        this.repository = repository;
        this.pointerInterface = pointerInterface;
        this.cPool = cPool;
        try {
            ctClass = cPool.makeClass(className, cPool.get(AbstractByteBufferPointer.class.getName()));
            ctClass.setModifiers(Modifier.FINAL + Modifier.PUBLIC);
            ctClass.addInterface(cPool.get(pointerInterface.getName()));
            ctClass.addMethod(CtNewMethod.make("public " + StructPointer.class.getName() + " at(int index) { this.index = index; return this; }", ctClass));
            constructorBody.append("super(array, parentPointer, parentOffset);");
        } catch (NotFoundException | CannotCompileException ex) {
            throw new IllegalStateException(ex);
        }
    }

    @Override
    public void addDouble(String name) {
        addPrimitive(name, "double", 8);
    }

    @Override
    public void addLong(String name) {
        addPrimitive(name, "long", 8);
    }

    @Override
    public void addFloat(String name) {
        addPrimitive(name, "float", 4);
    }

    @Override
    public void addInt(String name) {
        addPrimitive(name, "int", 4);
    }

    @Override
    public <S extends StructPointer<S>> void addStruct(String name, Class<S> pointerInterface) {
        ByteBufferArrayFactory<S> innerFactory = repository.getFactory(pointerInterface);
        try {
            ctClass.addField(new CtField(innerFactory.getCtImplementation(), name, ctClass));
            constructorBody.append(String.format("this.%s = new %s(array, this, %d);", name, innerFactory.getCtImplementation().getName(), offset));

            String cName = Character.toUpperCase(name.charAt(0)) + name.substring(1);
            ctClass.addMethod(CtNewMethod.make("public " + pointerInterface.getName() + " get" + cName + "() { return " + name + "; }", ctClass));
            ctClass.addMethod(CtNewMethod.make("public void set" + cName + "(" + pointerInterface.getName() + " value) { throw new UnsupportedOperationException(); }", ctClass));
            offset += innerFactory.getStructSize();
        } catch (CannotCompileException ex) {
            throw new IllegalStateException(ex);
        }
    }

    @Override
    public ByteBufferArrayFactory<T> build() {
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

    protected void addPrimitive(String name, String fieldType, int fieldSize) throws IllegalStateException {
        try {
            String cName = Character.toUpperCase(name.charAt(0)) + name.substring(1);
            String cOffset = "getFieldPosition(" + offset + ")";
            String cFieldType = Character.toUpperCase(fieldType.charAt(0)) + fieldType.substring(1);
            ctClass.addMethod(CtNewMethod.make("public " + fieldType + " get" + cName + "() { return array.get" + cFieldType + "(" + cOffset + "); }", ctClass));
            ctClass.addMethod(CtNewMethod.make("public void set" + cName + "(" + fieldType + " value) { return array.put" + cFieldType + "(" + cOffset + ", value); }", ctClass));
            offset += fieldSize;
        } catch (CannotCompileException ex) {
            throw new IllegalStateException("Error adding int field " + name, ex);
        }
    }        

    public static <T extends StructPointer<T>> ByteBufferArrayFactory<T> build(ByteBufferRepository repository, Class<T> pointerInterface) {
        try {
            ByteBufferBuilder<T> builder = new ByteBufferBuilder<>(repository, pointerInterface, repository.getClassPool(), pointerInterface.getName() + "Impl");
            BeanInfo beanInfo = Introspector.getBeanInfo(pointerInterface);
            List<PropertyInfo> properties = new ArrayList<>();
            for (PropertyDescriptor pd : beanInfo.getPropertyDescriptors()) {
                properties.add(PropertyInfo.create(pd));
            }
            Collections.sort(properties);
            for (PropertyInfo pi : properties) {
                pi.addTo(builder);
            }
            return builder.build();
        } catch (IntrospectionException ex) {
            throw new IllegalArgumentException("Error accessing " + pointerInterface, ex);
        }
    }

    private static final class PropertyInfo implements Comparable<PropertyInfo> {
        public final String name;
        public final Class<?> type;
        public final int index;

        public PropertyInfo(String name, Class<?> type, int index) {
            this.name = name;
            this.type = type;
            this.index = index;
        }

        public static PropertyInfo create(PropertyDescriptor pd) throws IntrospectionException {
            if (pd.getReadMethod() == null || pd.getWriteMethod() == null) {
                throw new IntrospectionException("Property must have both read and write methods: " + pd.getName());
            }
            return new PropertyInfo(pd.getName(), pd.getPropertyType(), Integer.MAX_VALUE);
        }

        @Override
        public int compareTo(PropertyInfo o) {
            return Integer.compare(this.index, o.index);
        }
    
        public void addTo(ByteBufferBuilder<?> builder) throws IntrospectionException {
            if (Integer.TYPE == type) {
                builder.addInt(name);
            } else if (Double.TYPE == type) {
                builder.addDouble(name);
            } else if (Float.TYPE == type) {
                builder.addFloat(name);
            } else if (Long.TYPE == type) {
                builder.addLong(name);
            } else if (StructPointer.class.isAssignableFrom(type)) {
                builder.addStruct(name, (Class) type);
            } else {
                throw new IntrospectionException("Unsupported type: " + type);
            }
        }

        @Override
        public String toString() {
            return "PropertyInfo{" + "name=" + name + ", type=" + type + ", index=" + index + '}';
        }
    }
}
