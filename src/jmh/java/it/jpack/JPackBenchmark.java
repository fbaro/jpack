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
public class JPackBenchmark {

    private static final StructRepository REPOSITORY = Repositories.newUnsafeRepository();
    private static final StructArrayFactory<SimplePointer> FACTORY = REPOSITORY.getFactory(SimplePointer.class);

    StructArray<SimplePointer> data;
    SimplePointer pointer;
    
    @Setup
    public void prepare() {
        data = FACTORY.newArray(PlainBenchmark.LENGTH);
        pointer = data.newPointer();
    }

    @TearDown
    public void destroy() {
        data.free();
        data = null;
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MICROSECONDS)
    public void accessTimesWithJPackArray() {
        double s = Math.PI;
        for (int i = 0; i < PlainBenchmark.LENGTH; i++) {
            pointer.at(i);
            s += pointer.getValue1() + pointer.getValue2();
        }
    }

}
