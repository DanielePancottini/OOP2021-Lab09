package it.unibo.oop.lab.reactivegui02;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.function.IntFunction;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * Simple Counter GUI with different operations (increment/decrement)
 * */
public class ConcurrentGUI extends JFrame {

    private static final long serialVersionUID = 1L;
    private static final Dimension SCREEN_SIZE = Toolkit.getDefaultToolkit().getScreenSize();
    private static final double WIDTH_FACTOR = 0.3;
    private static final double HEIGHT_FACTOR = 0.1;
    
    private final JLabel counterLabel = new JLabel("0");
    
    /**
     * Builds a new {@link ConcurrentGUI}
     * */
    public ConcurrentGUI() {
        super();
        /*
         * Counter Agent Declaration
         * */
        final Agent counterAgent = new Agent();
        /*
         * Inner Panel Define
         * */
        JPanel panel = new JPanel();
        /*
         * GUI Buttons Define
         * */
        JButton stopButton = new JButton("stop");
        JButton upButton = new JButton("up");
        JButton downButton = new JButton("down");
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
        upButton.addActionListener((e) -> {
            counterAgent.setIncrementOperation((count) -> ++count);
        });
        downButton.addActionListener((e) -> {
            counterAgent.setIncrementOperation((count) -> --count);
        });
        stopButton.addActionListener((e) -> {
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
        this.setSize((int) (ConcurrentGUI.SCREEN_SIZE.width * ConcurrentGUI.WIDTH_FACTOR),
                     (int) (ConcurrentGUI.SCREEN_SIZE.height * ConcurrentGUI.HEIGHT_FACTOR));
        
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
        
        new Thread(counterAgent).start();
        
    }
    
    private class Agent implements Runnable {
        private int count = 0;
        private volatile boolean stop = false;
        private IntFunction<Integer> incrementOperation = getDefaultOperation();
        
        @Override
        public void run() {
            while(!isStopped()) {
                /*
                 * Use increment policy to "increment" count
                 * */
                this.count = this.incrementOperation.apply(this.count);
                String toWrite = Integer.toString(this.count);
                SwingUtilities.invokeLater(() -> ConcurrentGUI.this.counterLabel.setText(toWrite));
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    System.err.println("ciao");
                }
            }
        }
        
        private IntFunction<Integer> getDefaultOperation() {
            return (count) -> count;
        }
        
        private boolean isStopped() {
            return this.stop;
        }
        
        /**
         * Sets the increment policy using an {@link IntFunction} interface, 
         * this method is thread safe
         *
         * @param incrementOperation increment policy for the counter
         * */
        public synchronized void setIncrementOperation(IntFunction<Integer> incrementOperation) {
            this.incrementOperation = incrementOperation;
        }
        
        /**
         * Stops thread action if called
         * */
        public void stopAction() {
            this.stop = true;
        }
        
    }
    
}
