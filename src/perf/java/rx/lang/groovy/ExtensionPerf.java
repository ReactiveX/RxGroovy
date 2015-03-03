package rx.lang.groovy;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

import rx.Observable;
import rx.jmh.InputWithIncrementingInteger;
import groovy.lang.GroovyShell;
import groovy.lang.Script;

import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
public class ExtensionPerf {
    @State(Scope.Thread)
    public static class Input extends InputWithIncrementingInteger {
        public GroovyShell shell;
        public Script script;

        @Param({ "1", "1000", "1000000" })
        public int size;

        @Param({ "in.map({ it + 1})", "in.filter({ it != 0 })" })
        public String source;

        @Override
        public int getSize() {
            return size;
        }

        @Setup
        public void initGroovyStuff() {
            shell = new GroovyShell();
            script = shell.parse(source);
            shell.setVariable("in", firehose);
        }
    }

    @SuppressWarnings("unchecked")
    @Benchmark
    public void extension(Input input) throws InterruptedException {
        ((Observable<Integer>) input.script.run()).subscribe(input.observer);
    }
}
