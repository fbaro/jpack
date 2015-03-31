# jpack
jpack is a library to provide something similar to C arrays of struct types.

## Demo

Define a structure:

```java
public interface MyPointer extends StructPointer<MyPointer> {
    @StructField(position = 0)
    int getFoo();
    void setFoo(int value);
    @StructField(position = 1)
    double getBar();
    void setBar(double value);
}

```

Then create and use an array of structures:

```java
public static void main(String[] args) {
    StructRepository repo = Repositories.newUnsafeRepository();
    StructArray<MyPointer> arr = repo.newArray(MyPointer.class, 16);
    MyPointer ptr = arr.newPointer();
    for (int i = 0; i < arr.getLength(); i++) {
        ptr.at(i).setFoo(i * 3);
        assertEquals(i * 3, ptr.getFoo());
    }
    arr.free();
}
```

## Introduction

Java memory management is quite good, and most applications never need to worry about the actual in-memory shape or position of objects.

For some applications however, it can be beneficial to allocate compact arrays of structured data, in order to leverage the use of memory caches, or to avoid putting too much pressure on the garbage collector.

jpack aims at providing support for such kind of structured data. The library strives to adhere to typical Java conventions, while trying to avoid as much as possible allocation of new objects while reading and writing data. The use of ```Unsafe``` operations provides very good performance.

## Tutorial

### Struct definition

The first step to use jpack is to define a structure! A structure is defined as a Java interface extending ```StructPointer```. The structure fields must be defined with the JavaBeans convention, as getter and setter methods in the interface. The types supported by jpack are all the primitive types, ```String```, ```CharSequence```, and any other interface extending ```StructPointer```. As ```StructPointer``` is generic on ```<T extends StructPointer>```, interfaces must be declared as extending a ```StructPointer``` on themselves.

```java
/** 
 * This interface defines a simple structure with two fields,
 * one called foo of type int, and the second called bar of type double.
 */
public interface MyPointer extends StructPointer<MyPointer> {
    int getFoo();
    void setFoo(int value);
    double getBar();
    void setBar(double value);
}
```

Arrays can be declared inside structures using the indexed property notation of JavaBeans; the array length is fixed at compile time, and must be specified through the ```length``` parameter of the ```StructField``` annotation.

Structures can contain other structures, both as arrays and as single fields. 

```java
/** 
 * This interface defines a structure with an array of 10 chars called george,
 * and a substructure called fred.
 */
public interface MyPointer2 extends StructPointer<MyPointer2> {
    @StructField(length = 10)
    char getGeorge(int index);
    void setGeorge(int index, char value);

    MyPointer getFred();
    void setFred(MyPointer value);
}
```

### Array creation

To create an array of structures, a ```StructRepository``` must be obtained first. This object defines the internal implementation of the structure array, and collects the known ```StructPointer``` implementors, in order to avoid unnecessary duplication. 

An instance of ```StructRepository``` can be obtained from the ```Repositories``` static methods; most users will need just a single instance.

```java
    StructRepository sRepo = Repositories.newByteBufferRepository(ByteOrder.nativeOrder());
```

From a ```StructRepository```, arrays can be directly created by specifying the ```StructPointer``` implementation, and the array length.

```java
    StructArray<MyPointer> arr = sRepo.newArray(MyPointer.class, 16);
```

### Data access

An array can be accessed through a ```StructPointer```. A pointer is a mutable, thread unsafe object, allowing to access the structure data present at in the array, at the index it is currently pointing to. A new pointer can be obtained from the array:

```java
    MyPointer ptr = arr.newPointer();
```

To access the value of property *foo* at position 10 in the array, the pointer must be moved to the desired position, then the data can be read. The pointer stays at the same position in the array until it is moved again, both with the ```at()``` and the ```setIndex``` methods.

```java
    ptr.at(10).getFoo();
    ptr.setIndex(5);
    ptr.setBar(3.5);
```

### Substructures access
A structure can contain other structures, both as a single element or as an array. Both situations are managed through ```StructPointer``` instances, which allow access to the substructure contents.

It is important to underline that the pointer returned by the substructure getter is always the same instance, created when the external pointer was created. This has the advantage of not requiring any extra allocation to access a substructure element. However, in order to have two pointers to different substructure elements, it is necessary to create two pointers to the external element.

### Internals

```StructPointer``` implementations are dynamically generated at runtime, with the javaassist library. This helps to achieve maximum performance, since reflection is called just when first analyzing a newly received ```StructPointer```, but never during normal use.

The ```at``` and ```setIndex``` methods are both very fast, requiring just a single assignment. The getter and setter implementors for primitive types are very fast as well, since they involve calculating the field position with respect to the current index - which in simple structures amounts to a multiplication and an addition.

#### Primitive getters and setters implementation
Getters and setters for primitive types are very fast, and involve only the calculation of the offset of the filed inside the array. No allocation is performed within a primitive getter or setter.

#### Structure getters and setters
When an array pointer is created, another pointer is created for each substructure in the main structure. These pointers are returned when calling the getter of a substructure; accessing a substructure therefore requires no allocation.

#### String getters and setters
For ```String``` types, due to the limitations of the ```String``` class, a new ```char[]``` is populated with the required data, and then transformed into a ```String```. Getting the value of a ```String``` property therefore requires two allocations.
