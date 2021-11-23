package it.unibo.oop.lab.reactivegui03;

/**
 * Third reactive GUI.
 *
 */
public final class Test {

    private Test() {
    }

    /**
     *
     * @param args
     *            unused
     *
     */
    public static void main(final String... args) {
        new AnotherConcurrentGUI().startThreads();
    }
}
