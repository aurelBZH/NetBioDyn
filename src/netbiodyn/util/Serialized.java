/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package netbiodyn.util;

import netbiodyn.ihm.Env_Parameters;
import java.util.ArrayList;
import java.util.HashMap;
import netbiodyn.AllInstances;
import netbiodyn.InstanceReaxel;
import netbiodyn.Behavior;
import netbiodyn.Entity;

/**
 *
 * @author riviere
 */
public class Serialized {

    private ArrayList<Entity> _ListManipulesNoeuds; // Entity types
    private ArrayList<Behavior> _ListManipulesReactions; // Behaviour
    private AllInstances instances;

    private HashMap<String, Integer> entitesBook;

    //Environnement
    private Env_Parameters parameters;

    public Serialized() {
        _ListManipulesNoeuds = new ArrayList<>();
        _ListManipulesReactions = new ArrayList<>();
        parameters = new Env_Parameters("FR", 0, 0, 0, "", null, "");
        instances = new AllInstances(parameters.getX(), parameters.getY(), parameters.getZ());
        entitesBook = new HashMap<>();
    }

    public Serialized(Env_Parameters parameters) {
        _ListManipulesNoeuds = new ArrayList<>();
        _ListManipulesReactions = new ArrayList<>();
    }

    public ArrayList<Entity> getListManipulesNoeuds() {
        ArrayList<Entity> proto = new ArrayList<>();
        for (Entity r : _ListManipulesNoeuds) {
            proto.add(r.clone());
        }
        return proto;
    }

    public void setListManipulesNoeuds(ArrayList<Entity> _ListManipulesNoeuds) {
        this._ListManipulesNoeuds = _ListManipulesNoeuds;
    }

    public void addProtoReaxel(Entity r) {
        _ListManipulesNoeuds.add(r);
    }

    public void addMoteurReaction(Behavior m) {
        _ListManipulesReactions.add(m);
    }

    public ArrayList<Behavior> getListManipulesReactions() {
        ArrayList<Behavior> moteurs = new ArrayList<>();
        for (Behavior r : _ListManipulesReactions) {
            moteurs.add(r.clone());
        }
        return moteurs;
    }

    public void setListManipulesReactions(ArrayList<Behavior> _ListManipulesReactions) {
        this._ListManipulesReactions = _ListManipulesReactions;
    }

    public AllInstances getInstances() {
        return instances;
    }

    public void setInstances(AllInstances instances) {
        this.instances = instances;
    }

    public Env_Parameters getParameters() {
        return parameters;
    }

    public void setParameters(Env_Parameters parameters) {
        this.parameters = parameters;
    }

    public void initMatriceAndList() {
        instances = new AllInstances(parameters.getX(), parameters.getY(), parameters.getZ());
    }

    public HashMap<String, Integer> getEntitesBook() {
        return entitesBook;
    }

    public void setEntitesBook(HashMap<String, Integer> entitesBook) {
        this.entitesBook = entitesBook;
    }

    public InstanceReaxel CreerReaxel(String etiquette) {
        for (Entity reaxel : _ListManipulesNoeuds) {
            if (reaxel.TrouveEtiquette(etiquette) >= 0) {
                return InstanceReaxel.CreerReaxel(reaxel);
            }
        }
        return null;
    }

    public void AjouterReaxel(int i, int j, int k, String etiquette) {
        for (Entity _ListManipulesNoeud : _ListManipulesNoeuds) {
            if (_ListManipulesNoeud.TrouveEtiquette(etiquette) >= 0) {
                AjouterReaxel(i, j, k, _ListManipulesNoeud);
            }
        }
    }

    public void AjouterReaxel(int i, int j, int k, Entity cli) {
        InstanceReaxel r = InstanceReaxel.CreerReaxel(cli);
        while (i < 0) {
            i += parameters.getX();
        }
        while (i >= parameters.getX()) {
            i -= parameters.getX();
        }
        while (j < 0) {
            j += parameters.getY();
        }
        while (j >= parameters.getY()) {
            j -= parameters.getY();
        }
        while (k < 0) {
            k += parameters.getZ();
        }
        while (k >= parameters.getZ()) {
            k -= parameters.getZ();
        }
        r.setX(i);
        r.setY(j);
        r.setZ(k);
        instances.add(r);
    }

    public void setTaille(String dimension, String value) {
        switch (dimension) {
            case "tailleX": {
                int v;
                try {
                    v = Integer.parseInt(value);
                    parameters.setX(v);
                } catch (Exception e) {
                }
                break;
            }
            case "tailleY": {
                int v;
                try {
                    v = Integer.parseInt(value);
                    parameters.setY(v);
                } catch (Exception e) {
                }
                break;
            }
            case "tailleZ": {
                int v;
                try {
                    v = Integer.parseInt(value);
                    parameters.setZ(v);
                } catch (Exception e) {
                }
                break;
            }
        }
    }

}
