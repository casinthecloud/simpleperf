package com.casinthecloud.simpleperf.execution;

import com.casinthecloud.simpleperf.test.BaseTest;
import lombok.RequiredArgsConstructor;
import lombok.val;

import java.net.http.HttpClient;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A thread dedicated to the test execution (sequential).
 *
 * @author Jerome LELEU
 * @since 1.0.0
 */
@RequiredArgsConstructor
public class ExecutionThread extends Thread {

    private final int id;

    private final int nbIterations;

    private final BaseTest test;

    private final AtomicInteger completed;

    private final boolean displayErrors;

    @Override
    public void run() {
        val client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .followRedirects(HttpClient.Redirect.NEVER)
                .build();

        test.setClient(client);

        val maxErrors = test.getMaxErrors();
        val smallInterval = test.getSmallInterval();
        val bigInterval = test.getBigInterval();

        var stopError = false;
        var nbError = 0;
        for (var i = 1; (nbIterations == -1 || i <= nbIterations) && !stopError; i++) {
            try {
                test.run();
                if (smallInterval != -1 && i % smallInterval == 0) {
                    System.out.print((char) (97 + id));
                }
                if (bigInterval != -1 && i % bigInterval == 0) {
                    System.out.print((char) (65 + id));
                }
            } catch (final Exception e) {
                if (displayErrors) {
                    e.printStackTrace();
                } else {
                    System.out.print("!");
                }
                if (maxErrors != -1) {
                    nbError++;
                    if (nbError >= maxErrors) {
                        stopError = true;
                    }
                }
            }
        }
        completed.incrementAndGet();
    }
}
