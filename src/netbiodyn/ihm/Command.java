/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package netbiodyn.ihm;

/**
 *
 * @author riviere
 */
public interface Command {
    /*
	 * Executer la commande
	 */
	public void execute();
	
	/**
	 * Annuler la commande
	 */
	public void undo();
    
}
