package com.casinthecloud.simpletest.execution;

import com.casinthecloud.simpletest.test.BaseTest;
import lombok.Getter;
import lombok.Setter;
import lombok.val;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import static com.casinthecloud.simpletest.util.Utils.*;

/**
 * The main class to use to launch the execution of a test.
 *
 * @author Jerome LELEU
 * @since 1.0.0
 */
public class Execution {

    private int nbThreads;

    private int nbIterationsPerThread;

    private Supplier<BaseTest> supplierTest;

    public Execution(final Supplier<BaseTest> supplierTest) {
        this(1, 1, supplierTest);
    }

    public Execution(final int nbIterationsPerThread, final Supplier<BaseTest> supplierTest) {
        this(1, nbIterationsPerThread, supplierTest);
    }

    public Execution(final int nbThreads, final int nbIterationsPerThread, final Supplier<BaseTest> supplierTest) {
        this.nbThreads = nbThreads;
        this.nbIterationsPerThread = nbIterationsPerThread;
        this.supplierTest = supplierTest;
        if (nbThreads == 1 && nbIterationsPerThread < NB_ITERATIONS_LIMIT) {
            displayInfos = true;
            displayErrors = true;
        }
    }

    @Getter
    @Setter
    private boolean displayInfos;

    @Getter
    @Setter
    private boolean displayErrors;

    public void launch() throws Exception {
        val completed = new AtomicInteger(0);

        String typeText = "Performance";
        if (nbIterationsPerThread < NB_ITERATIONS_LIMIT) {
            typeText = "Functional";
        }
        String textThread = nbThreads + " thread";
        if (nbThreads > 1) {
            textThread += "s";
        }
        String textIteration;
        if (nbIterationsPerThread == -1) {
            textIteration = "infinite loop";
        } else if (nbIterationsPerThread == 1) {
            textIteration = nbIterationsPerThread + " iteration per thread";
        } else {
            textIteration = nbIterationsPerThread + " iterations per thread";
        }
        println(typeText + " execution started: " + textThread + ", " + textIteration);
        if (!displayInfos) {
            print("<");
        }

        val t0 = System.currentTimeMillis();
        for (var i = 0; i < nbThreads; i++) {
            val test = supplierTest.get();
            val executionThread = new ExecutionThread(i, nbIterationsPerThread, test, completed, displayInfos, displayErrors);
            executionThread.start();
        }

        while (completed.get() < nbThreads) {}
        val t1 = System.currentTimeMillis();

        if (!displayInfos) {
            println(">");
        }

        val finalTime = t1 - t0;
        if (finalTime >= 5000) {
            println("Execution ended and took: " + finalTime/1000 + " s");
        } else {
            println("Execution ended and took: " + finalTime + " ms");
        }
    }
}
