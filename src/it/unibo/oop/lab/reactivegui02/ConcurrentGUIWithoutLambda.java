package it.unibo.oop.lab.reactivegui02;

import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

/**
 * Simple Counter GUI with different operations (increment/decrement).
 * */
public class ConcurrentGUIWithoutLambda extends JFrame {

    private static final long serialVersionUID = 1L;
    private static final Dimension SCREEN_SIZE = Toolkit.getDefaultToolkit().getScreenSize();
    private static final double WIDTH_FACTOR = 0.3;
    private static final double HEIGHT_FACTOR = 0.1;

    private final JLabel counterLabel = new JLabel("0");

    /**
     * Builds a new {@link ConcurrentGUIWithoutLambda}.
     * */
    public ConcurrentGUIWithoutLambda() {
        super();
        /*
         * Counter Agent Declaration
         * */
        final Agent counterAgent = new Agent();
        /*
         * Inner Panel Define
         * */
        final JPanel panel = new JPanel();
        /*
         * GUI Buttons Define
         * */
        final JButton stopButton = new JButton("stop");
        final JButton upButton = new JButton("up");
        final JButton downButton = new JButton("down");
        /*
         * Add buttons and counter label to inner panel
         * */
        panel.add(counterLabel);
        panel.add(upButton);
        panel.add(downButton);
        panel.add(stopButton);
        /*
         * Buttons Handlers
         * */
        upButton.addActionListener(e -> {
            counterAgent.setIncrementOperation(new IncrementPolicy() {
                @Override
                public int increment(final int count) {
                    return count + 1;
                }
            });
        });
        downButton.addActionListener(e -> {
            counterAgent.setIncrementOperation(new IncrementPolicy() {
                @Override
                public int increment(final int count) {
                    return count - 1;
                }
            });
        });
        stopButton.addActionListener(e -> {
            counterAgent.stopAction();
            upButton.setEnabled(false);
            downButton.setEnabled(false);
            stopButton.setEnabled(false);
        });
        /*
         * Add inner layout to content pane,
         * Configure frame with some settings
         * */
        this.getContentPane().add(panel);
        this.setSize((int) (ConcurrentGUIWithoutLambda.SCREEN_SIZE.width * ConcurrentGUIWithoutLambda.WIDTH_FACTOR),
                (int) (ConcurrentGUIWithoutLambda.SCREEN_SIZE.height * ConcurrentGUIWithoutLambda.HEIGHT_FACTOR));

        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setVisible(true);

        new Thread(counterAgent).start();

    }

    private interface IncrementPolicy {
        int increment(int count);
    }

    private class Agent implements Runnable {
        private int count;
        private volatile boolean stop;
        private IncrementPolicy incrementOperation = getDefaultOperation();

        @Override
        public void run() {
            while (!isStopped()) {
                /*
                 * Use increment policy to "increment" count
                 * */
                synchronized (this) {
                    this.count = this.incrementOperation.increment(this.count);
                }
                final String toWrite = Integer.toString(this.count);
                SwingUtilities.invokeLater(() -> ConcurrentGUIWithoutLambda.this.counterLabel.setText(toWrite));
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        private IncrementPolicy getDefaultOperation() {
            return new IncrementPolicy() {
                @Override
                public int increment(final int count) {
                    return count;
                }
            };
        }

        private boolean isStopped() {
            return this.stop;
        }

        /**
         * Sets the increment policy using a {@link Function} interface,
         * this method is thread safe.
         *
         * @param incrementOperation increment policy for the counter
         * */
        public synchronized void setIncrementOperation(final IncrementPolicy incrementOperation) {
            this.incrementOperation = incrementOperation;
        }

        /**
         * Stops thread action if called.
         * */
        public void stopAction() {
            this.stop = true;
        }

    }

}
