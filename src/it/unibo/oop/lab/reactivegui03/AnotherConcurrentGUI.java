package it.unibo.oop.lab.reactivegui03;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

/**
 * Simple Counter GUI with different increment policies (increment/decrement).
 * It will stops counting after 10 seconds.
 * */
public class AnotherConcurrentGUI extends JFrame {


    private static final long serialVersionUID = 1L;
    private static final Dimension SCREEN_SIZE = Toolkit.getDefaultToolkit().getScreenSize();
    private static final double WIDTH_FACTOR = 0.3;
    private static final double HEIGHT_FACTOR = 0.1;

    private final AnotherConcurrentGUI.Agent counterAgent = new Agent();

    private final JLabel counterLabel = new JLabel("0");
    private final List<JButton> uiButtons = new ArrayList<>();

    /**
     * Builds a new {@link AnotherConcurrentGUI}.
     * */
    public AnotherConcurrentGUI() {
        super();
        /*
         * Inner Panel Define
         * */
        final JPanel panel = new JPanel();
        /*
         * GUI Buttons Define
         * */
        final JButton upButton = new JButton("up");
        final JButton downButton = new JButton("down");
        final JButton stopButton = new JButton("stop");
        /*
         * Add buttons to list of ui buttons
         * */
        this.uiButtons.add(downButton);
        this.uiButtons.add(upButton);
        this.uiButtons.add(stopButton);
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
        upButton.addActionListener(e -> this.counterAgent.setIncrementOperation(count -> count + 1));
        downButton.addActionListener(e -> this.counterAgent.setIncrementOperation(count -> count - 1));
        stopButton.addActionListener(e -> this.stopCounting());
        /*
         * Add inner layout to content pane,
         * Configure frame with some settings
         * */
        this.getContentPane().add(panel);
        this.setSize((int) (AnotherConcurrentGUI.SCREEN_SIZE.width * AnotherConcurrentGUI.WIDTH_FACTOR),
                (int) (AnotherConcurrentGUI.SCREEN_SIZE.height * AnotherConcurrentGUI.HEIGHT_FACTOR));

        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setVisible(true);

    }

    private synchronized void stopCounting() {
        this.counterAgent.stopAction();
        SwingUtilities.invokeLater(() -> {
            for (final JButton button : this.uiButtons) {
                button.setEnabled(false);
            }
        });
    }

    /**
     * Starts threads outside constructor to avoid race conditions.
     * */
    public void startThreads() {

        final int sleepTime = 10_000;

        new Thread(counterAgent).start();
        new Thread(() -> {
            try {
                Thread.sleep(sleepTime);
                this.stopCounting();
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }

        }).start();
    }

    private class Agent implements Runnable {
        private int count;
        private volatile boolean stop;
        private Function<Integer, Integer> incrementOperation = getDefaultOperation();

        @Override
        public void run() {
            while (!isStopped()) {
                /*
                 * Use increment policy to "increment" count
                 * */
                synchronized (this) {
                    this.count = this.incrementOperation.apply(this.count);
                }
                final String toWrite = Integer.toString(this.count);
                SwingUtilities.invokeLater(() -> AnotherConcurrentGUI.this.counterLabel.setText(toWrite));
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    System.err.println("ciao");
                }
            }
        }

        private Function<Integer, Integer> getDefaultOperation() {
            return count -> count;
        }

        private boolean isStopped() {
            return this.stop;
        }

        /**
         * Sets the increment policy using an {@link Function} interface,
         * this method is thread safe.
         *
         * @param incrementOperation increment policy for the counter
         * */
        public synchronized void setIncrementOperation(final Function<Integer, Integer> incrementOperation) {
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
