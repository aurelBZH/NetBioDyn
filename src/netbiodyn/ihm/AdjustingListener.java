/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package netbiodyn.ihm;

import java.util.ArrayList;
import java.util.EventListener;

/**
 *
 * @author riviere
 */
public interface AdjustingListener extends EventListener{    
    public void newAdjustedValues(ArrayList<String[]> valuesToDisplay);
    public void adjustmentDone(boolean success);   
    
}
