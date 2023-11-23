package com.casinthecloud.simpleperf.execution;

import com.casinthecloud.simpleperf.test.BaseTest;
import lombok.RequiredArgsConstructor;
import lombok.val;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;

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
        System.out.println("Execution started: " + textThread + ", " + textIteration);
        System.out.print("<");

        for (var i = 0; i < nbThreads; i++) {
            val test = supplierTest.get();
            test.setTime(time);
            val t = new ExecutionThread(i, nbIterationsPerThread, test, completed);
            t.start();
        }

        while (completed.get() < nbThreads) {}
        System.out.print(">");

        System.out.println();
        val finalTime = time.get();
        if (finalTime >= 5000) {
            System.out.println("Execution ended and took: " + finalTime/1000 + " s");
        } else {
            System.out.println("Execution ended and took: " + finalTime + " ms");
        }
    }
}
