package it.jpack;

import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;


/**
 *
 * @author Flavio
 */
@State(Scope.Thread)
public class PlainBenchmark {

    public static final int LENGTH = 16384;

    SimpleClass[] data = new SimpleClass[LENGTH];

    @Setup
    public void prepare() {
        for (int i = 0; i < data.length; i++) {
            data[i] = new SimpleClass(i, i);
        }
    }

    @TearDown
    public void destroy() {
        data = null;
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MICROSECONDS)
    public void accessTimesWithPlainArray() {
        double s = Math.PI;
        for (int i = 0; i < LENGTH; i++) {
            SimpleClass c = data[i];
            s += c.getValue1() + c.getValue2();
        }
    }

}
