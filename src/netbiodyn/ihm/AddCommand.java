/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package netbiodyn.ihm;

import java.util.ArrayList;
import netbiodyn.Model;
import netbiodyn.Simulator;
import netbiodyn.util.UtilPoint3D;

/**
 *
 * @author riviere
 */
public class AddCommand implements Command {

    private final Model model;
    private final Simulator simulator;
    private final ArrayList<UtilPoint3D> points;
    private final String type;
    private Command opposite;

    public AddCommand(Model model, Simulator simulator, ArrayList<UtilPoint3D> points, String type) {
        this.model = model;
        this.simulator = simulator;
        this.type = type;
        this.points = points;        
    }
    
     public void setOpposite(Command opposite){
        this.opposite=opposite;
    }

    @Override
    public void execute() {
        if (simulator.isStopped()) {
            model.addEntityInstances(points, type);
        } else {
            simulator.addEntityInstances(points, type);
        }
    }

    @Override
    public void undo() {
        opposite.execute();
    }

}
