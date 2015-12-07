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
public class RemoveCommand implements Command {

    private final Model model;
    private final Simulator simulator;
    private UtilPoint3D point;
    private ArrayList<UtilPoint3D> points;
    private Command opposite;

    public RemoveCommand(Model model, Simulator simulator, UtilPoint3D point) {
        this.points = new ArrayList<>();
        this.model = model;
        this.simulator = simulator;
        this.point = point;
    }

    public RemoveCommand(Model model, Simulator simulator, ArrayList<UtilPoint3D> points) {
        this.model = model;
        this.simulator = simulator;
        this.points = points;
    }
    
    public void setOpposite(Command opposite){
        this.opposite=opposite;
    }

    @Override
    public void execute() {
        if (point != null) {
            int x = point.x;
            int y = point.y;
            int z = point.z;

            if (simulator.isStopped()) {
                model.removeEntityInstance(x, y, z);
            } else {
                simulator.removeEntityInstance(x, y, z);
            }
        } else {
            for (UtilPoint3D p : points) {
                int x = p.x;
                int y = p.y;
                int z = p.z;
                if (simulator.isStopped()) {
                    model.removeEntityInstance(x, y, z);
                } else {
                    simulator.removeEntityInstance(x, y, z);
                }
            }
        }
    }

    @Override
    public void undo() {
        opposite.execute();
    }

}
