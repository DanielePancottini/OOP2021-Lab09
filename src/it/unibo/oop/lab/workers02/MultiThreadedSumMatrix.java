package it.unibo.oop.lab.workers02;

import java.util.Arrays;
import java.util.stream.IntStream;

/**
 * Computes multi thread matrix sum.
 * */
public class MultiThreadedSumMatrix implements SumMatrix {

    private final int nThreads;

    /**
     * Builds new {@link MultiThreadedSumMatrix}.
     *
     * @param nThreads
     *              number of threads to be used during matrix sum
     * */
    public MultiThreadedSumMatrix(final int nThreads) {
        this.nThreads = nThreads;
    }

    private class Worker extends Thread {

        private final double[][] matrix;
        private final int startRow;
        private final int nRows;
        private double res;

        /**
         * Builds new {@link Worker}.
         *
         * @param matrix
         *          matrix to be summed
         * @param startRow
         *          start row for current thread
         * @param nRows
         *          n rows to sum for current thread (starting from startRow)
         * */
        Worker(final double[][] matrix, final int startRow, final int nRows) {
            super();
            this.matrix = Arrays.copyOf(matrix, matrix.length);
            this.startRow = startRow;
            this.nRows = nRows;
            this.res = 0.0;
        }

        /**
         * Computes sum of matrix elements using defined start/end rows range.
         * */
        @Override
        public void run() {
            for (int i = this.startRow; i < this.matrix.length && i < this.nRows + this.startRow; i++) {
                for (final double x : this.matrix[i]) {
                    this.res += x;
                }
            }
        }

        /**
         * Gets sum of the matrix elements from a certain start, end rows.
         *
         * @return sum
         * */
        public double getResult() {
            return this.res;
        }

    }

    /**
     * {@inheritDoc}
     * */
    @Override
    public double sum(final double[][] matrix) {

        final int size = matrix.length / this.nThreads + matrix.length % this.nThreads;

        return IntStream.iterate(0, start -> start + size)
                        .limit(nThreads)
                        .mapToObj(start -> new Worker(matrix, start, size))
                        .peek(Worker::start)
                        .peek(MultiThreadedSumMatrix::joinThread)
                        .mapToDouble(Worker::getResult)
                        .sum();
    }

    private static void joinThread(final Thread target) {
        boolean isDead = false;
        while (!isDead) {
            try {
                target.join();
                isDead = true;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
