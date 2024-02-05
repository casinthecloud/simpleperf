package com.casinthecloud.simpletest.execution;

import com.casinthecloud.simpletest.test.BaseTest;
import lombok.RequiredArgsConstructor;
import lombok.val;

import java.net.http.HttpClient;
import java.util.concurrent.atomic.AtomicInteger;

import static com.casinthecloud.simpletest.util.Utils.print;
import static com.casinthecloud.simpletest.util.Utils.println;

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

    private final boolean displayInfos;

    private final boolean displayErrors;

    @Override
    public void run() {
        val client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .followRedirects(HttpClient.Redirect.NEVER)
                .build();

        test.setClient(client);
        test.setDisplayInfos(displayInfos);

        val maxErrors = test.getMaxErrors();
        val interval = test.getInterval();

        val ctx = new Context();

        var stopError = false;
        var nbError = 0;
        for (var i = 1; (nbIterations == -1 || i <= nbIterations) && !stopError; i++) {
            try {
                if (displayInfos) {
                    println("## iteration: " + i);
                }
                test.run(ctx);
                if (displayInfos) {
                    println();
                }
                if (interval != -1 && i % interval == 0) {
                    char c = '%';
                    if (id > 1 && id <= 10) {
                        c = (char) (37 + id);
                    } else if (id >= 11) {
                        c = (char) (58 + id - 11);
                    }
                    print((i *100 ) / nbIterations  + "" + c + " ");
                }
            } catch (final Throwable t) {
                if (displayErrors) {
                    t.printStackTrace();
                } else {
                    print("!");
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
