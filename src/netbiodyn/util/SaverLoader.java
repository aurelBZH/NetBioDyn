/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package netbiodyn.util;

/**
 *
 * @author riviere
 */
public abstract class SaverLoader {    
    
    public abstract void save(Serialized env);
    public abstract Serialized load();
}
