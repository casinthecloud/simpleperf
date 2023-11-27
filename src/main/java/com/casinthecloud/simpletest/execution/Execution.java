package com.casinthecloud.simpletest.execution;

import com.casinthecloud.simpletest.test.BaseTest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.val;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;

import static com.casinthecloud.simpletest.util.Utils.print;
import static com.casinthecloud.simpletest.util.Utils.println;

/**
 * The main class to use to launch the execution of a test.
 *
 * @author Jerome LELEU
 * @since 1.0.0
 */
@RequiredArgsConstructor
public class Execution {

    private final int nbThreads;

    private final int nbIterationsPerThread;

    private final Supplier<BaseTest> supplierTest;

    @Getter
    @Setter
    private boolean displayErrors;

    public void launch() throws Exception {
        val time = new AtomicLong(0);
        val completed = new AtomicInteger(0);

        String textThread = nbThreads + " thread";
        if (nbThreads > 1) {
            textThread += "s";
        }
        String textIteration;
        if (nbIterationsPerThread == -1) {
            textIteration = "infinite loop";
        } else {
            textIteration = nbIterationsPerThread + " iterations per thread";
        }
        println("Execution started: " + textThread + ", " + textIteration);
        print("<");

        for (var i = 0; i < nbThreads; i++) {
            val test = supplierTest.get();
            test.setTime(time);
            val t = new ExecutionThread(i, nbIterationsPerThread, test, completed, displayErrors);
            t.start();
        }

        while (completed.get() < nbThreads) {}
        print(">");

        println();
        val finalTime = time.get();
        if (finalTime >= 5000) {
            println("Execution ended and took: " + finalTime/1000 + " s");
        } else {
            println("Execution ended and took: " + finalTime + " ms");
        }
    }
}
