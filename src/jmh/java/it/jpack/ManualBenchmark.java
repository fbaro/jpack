package it.jpack;

import java.lang.reflect.Field;
import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import sun.misc.Unsafe;

/**
 *
 * @author Flavio
 */
@State(Scope.Thread)
public class ManualBenchmark {

    static final Unsafe U = getUnsafe();
    @SuppressWarnings("restriction")
    private static Unsafe getUnsafe() {
        try {
            Field singleoneInstanceField = Unsafe.class.getDeclaredField("theUnsafe");
            singleoneInstanceField.setAccessible(true);
            return (Unsafe) singleoneInstanceField.get(null);
        } catch (IllegalArgumentException | SecurityException | NoSuchFieldException | IllegalAccessException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private SimplePointerImpl pointer;
    
    @Setup
    public void prepare() {
        pointer = new SimplePointerImpl(U.allocateMemory(PlainBenchmark.LENGTH * 12));
    }

    @TearDown
    public void destroy() {
        U.freeMemory(pointer.base);
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MICROSECONDS)
    public void accessTimesWithManualArray() {
        for (int i = 0; i < PlainBenchmark.LENGTH; i++) {
            pointer.at(i);
            pointer.getValue1();
            pointer.getValue2();
        }
    }

    private static final class SimplePointerImpl implements SimplePointer {

        private final long base;
        private int index;

        public SimplePointerImpl(long base) {
            this.base = base;
        }
        
        @Override
        public int getValue1() {
            return U.getInt(base  + 12 * index);
        }

        @Override
        public void setValue1(int v) {
            U.putInt(base + 12 * index, v);
        }

        @Override
        public double getValue2() {
            return U.getDouble(base + 8 + 12 * index);
        }

        @Override
        public void setValue2(double v) {
            U.putDouble(base + 8 + 12 * index, v);
        }

        @Override
        public int getIndex() {
            return index;
        }

        @Override
        public void setIndex(int index) {
            this.index = index;
        }

        @Override
        public SimplePointerImpl at(int index) {
            this.index = index;
            return this;
        }
    }
}
