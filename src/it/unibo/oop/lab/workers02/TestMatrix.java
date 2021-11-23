package it.unibo.oop.lab.workers02;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.TimeUnit;

import org.junit.Test;

/**
 *
 * TestMatrix for worker 2.
 *
 */
public class TestMatrix {

    private static final int SIZE = 10_000;
    private static final double EXPECTED_DELTA = 0.01;
    private static final String MSEC = " msec";

    /**
     * Base test for a multithreaded matrix sum.
     */
    @Test
    public void testBasic() {
        double sum = 0;
        final double[][] matrix = new double[SIZE][SIZE];
        for (double[] d : matrix) {
            for (int i = 0; i < SIZE; i++) {
                d[i] = i;
                sum += i;
            }
        }
        System.out.println("BTW: the sum with " + SIZE + "*" + SIZE + " elements is: " + sum);
        long time;
        for (final int threads: new int[] { 1, 2, 3, 8, 16, 32, 100 }) {
            final SumMatrix sumList = new MultiThreadedSumMatrix(threads);
            time = System.nanoTime();
            assertEquals(sum, sumList.sum(matrix), EXPECTED_DELTA);
            time = System.nanoTime() - time;
            System.out.println("Tried with " + threads + " thread"
                    + (threads == 1 ? "" : "s") + ": "
                    + TimeUnit.NANOSECONDS.toMillis(time) + MSEC);
        }
    }

}
