/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package netbiodyn;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author riviere
 */
public class RunnableSimulator extends Simulator implements Runnable {

    private final ArrayBlockingQueue<Integer> go;

    public RunnableSimulator(Model model) {
        super(model);        
        go = new ArrayBlockingQueue<>(1);
        this.setSpeed(0);
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                int step=go.take();
                System.out.println("Coucou "+step);
            } catch (InterruptedException ex) {
                Logger.getLogger(RunnableSimulator.class.getName()).log(Level.SEVERE, null, ex);
            }
            super.start();
        }
    }
    
    public void setMaxStep(int step){
        super.setMaxStep(step);
        if(step != -1) go.add(step);
    }
}
