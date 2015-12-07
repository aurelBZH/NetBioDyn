/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package netbiodyn.ihm;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.HashMap;
import netbiodyn.AllInstances;
import netbiodyn.util.Serialized;
import netbiodyn.Behavior;
import netbiodyn.Entity;

/**
 *
 * @author riviere
 */
public interface IhmListener extends EventListener{
    
    public void newEnvLoaded(Serialized saved,HashMap<String, Integer> entitesBook);
    public void newEnvParameters(Env_Parameters parameters);
    public void protoEntityUpdate(ArrayList<Entity> entities, HashMap<String, Integer> entitesBook);
    public void moteurReactionUpdate(ArrayList<Behavior> behaviours);
    public void matrixUpdate(AllInstances instances, HashMap<String, Integer> initialState, int i);
    public void ready();
    
}
