/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package netbiodyn;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.swing.event.EventListenerList;
import netbiodyn.ihm.IhmListener;
import netbiodyn.ihm.Env_Parameters;
import netbiodyn.util.RandomGen;
import netbiodyn.util.UtilPoint3D;

/**
 *
 * @author riviere
 */
public class Simulator {

    private final EventListenerList listeners;

    private int speed = 1;
    private int time = 0;
    private boolean pause = false;
    private boolean stopped = true;
    private int maxStep = -1;
    private final Model model;
    private javax.swing.Timer timer_play;

    private AllInstances instances;
    private AllInstances instancesFutur;

    private int nb_processus_a_traiter = 0;

    public Simulator(Model model) {
        listeners = new EventListenerList();

        this.model = model;
        init();
        initTimer();
    }

    private void init() {
        instances = new AllInstances(model.getInstances());
    }

    public void addListener(IhmListener listen) {
        listeners.add(IhmListener.class, listen);
    }

    private void initTimer() {
        timer_play = new javax.swing.Timer(this.getSpeed(), new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                play();
            }
        });
    }

    public void start() {
        if (getTime() == 0) {
            init();
        }
        setStopped(false);
        setPause(false);
    }

    public void stop() {
        setStopped(true);
        timer_play.stop();
        init();
        pause = false;
        setTime(0);

        for (final IhmListener listen : listeners.getListeners(IhmListener.class)) {
            listen.matrixUpdate(getInstances(), this.updateList(), getTime());
        }
    }

    private void play() {        
        // 1/2 vie
        // --------
        for (int pos_in_list = instances.getSize() - 1; pos_in_list >= 0; pos_in_list--) {
            InstanceReaxel c = instances.getInList(pos_in_list);//_matrice_cubes[i, j];
            // Gestion de la demie-vie
            if (c.getDemie_vie() > 0 && !c.isSelectionne()) {
                double proba_mort = 1 - Math.pow(0.5, 1.0 / c.getDemie_vie());
                if (RandomGen.getInstance().nextDouble() < proba_mort) {
                    instances.removeReaxel(c);
                }
            }
        }

        // ***************************************
        // Prise en compte des diverses reactions
        // ***************************************
        // Liste de ts les moteurs de reactions
        List<Behavior> lst_react = model.getListManipulesReactions();
        // RAZ de la matrice future des reaxels        
        Env_Parameters param = model.getParameters();
        instancesFutur = new AllInstances(param.getX(), param.getY(), param.getZ());

        // Lancement en // de toutes les reactions
        setNb_processus_a_traiter(lst_react.size());
        for (Behavior lst_react1 : lst_react) {
            lst_react1.computeReactions(this, param, instances.clone(), getTime());
        }

        // Attente passive de tous les retours
        while (getNb_processus_a_traiter() > 0) {
            // TODO pour le moment, c une attente active, à corriger !
        }

        // Concatenation de toutes les reactions possibles
        ArrayList<InstanceReaction> lst_rp = new ArrayList<>();
        for (Behavior lst_react1 : lst_react) {
            lst_rp.addAll(lst_react1._reactionsPossibles);
        }

        // Index de parcours melanges
        ArrayList<Integer> lst_int = RandomGen.getInstance().liste_entiers_melanges(lst_rp.size());
        // Execution du choix effectif des reactions
        for (int r = 0; r < lst_rp.size(); r++) {
            InstanceReaction rp = lst_rp.get(lst_int.get(r));
            // On tente d'appliquer la transformation
            boolean possible = true;

            if (rp._type != 2) { // Reaction situees et semi-situee
                // Reactifs tjs présents ?
                for (int i = 0; i < rp._reactifs_noms.size(); i++) {
                    int x = rp._reactifs_pos.get(i).x;
                    int y = rp._reactifs_pos.get(i).y;
                    int z = rp._reactifs_pos.get(i).z;
                    // Cas du vide
                    if (instances.getFast(x, y, z) == null) {
                        if (!rp._reactifs_noms.get(i).equals("0")) { // La reaction veut un vide sinon...
                            possible = false;
                            i = rp._reactifs_noms.size();
                        }
                    } else // cas non vide
                    {
                        if (!instances.getFast(x, y, z).getNom().equals(rp._reactifs_noms.get(i))) { // La reaction peut trouver le bon nom de reaxel ds la présente matrice
                            possible = false;
                            i = rp._reactifs_noms.size(); // mais s'il n'y est pas, il n'y est pas
                        }
                    }
                }

                // Place encore là pour les produits ?
                if (possible == true) {
                    for (int i = 0; i < rp._produits_noms.size(); i++) {
                        int x = rp._produits_pos.get(i).x;
                        int y = rp._produits_pos.get(i).y;
                        int z = rp._produits_pos.get(i).z;
                        if (instancesFutur.getFast(x, y, z) != null) { // Si espace occupé...
                            if (!rp._produits_noms.get(i).equals("")) { // et si volonté d'y placer un produit alors impossible !
                                possible = false;
                                i = rp._produits_noms.size();
                            }
                        }
                    }
                }

                // On effectue la transformation si tjs possible
                if (possible == true) {
                    // on ajoute les produits dans la matrice future
                    for (int i = 0; i < rp._produits_noms.size(); i++) {
                        int x = rp._produits_pos.get(i).x;
                        int y = rp._produits_pos.get(i).y;
                        int z = rp._produits_pos.get(i).z;
                        this.AjouterFuturReaxel(x, y, z, rp._produits_noms.get(i));
                    }

                    // on enleve les reactifs de la matrice courante pour eviter qu'il ne reagissent a nouveau (conservation E et matiere)
                    for (int i = 0; i < rp._reactifs_noms.size(); i++) {
                        int x = rp._reactifs_pos.get(i).x;
                        int y = rp._reactifs_pos.get(i).y;
                        int z = rp._reactifs_pos.get(i).z;
                        if (instances.getFast(x, y, z) != null) {
                            instances.removeReaxel(x, y, z);
                        }
                    }
                }
            }
            // Reaction possible suivante...
        }

        // Fin de l'application effective des reactions
        // On verse les réaxels qui n'ont pas réagit dans la liste future et dans la matrice future
        instancesFutur.setMatrixAndList(instances.getList());
        // Attente passive de tous les retours
        while (getNb_processus_a_traiter() > 0) {
        }

        instances = new AllInstances(instancesFutur.getList(), instancesFutur.getMatrix(), instancesFutur.getX(), instancesFutur.getY(), instancesFutur.getZ());
        this.setTime(getTime() + 1);
        int t = getTime();

        if (getMaxStep() != -1) {
            if (getTime() == getMaxStep()) {                
                setStopped(true);
                timer_play.stop();
                pause = false;
                setMaxStep(-1);
//                setTime(0); // ATTENTION !! 
            }
        }

        for (final IhmListener listen : listeners.getListeners(IhmListener.class)) {
            listen.matrixUpdate(getInstances(), this.updateList(), t);
        }

    }

    public void play_one() {
        if (this.getTime() == 0) {
            init();
        }
        this.play();
    }

    /**
     * Used to automatically remove all instances of the entities in reaxels
     *
     * @param reaxels
     */
    public void ProtoReaxelDeleted(ArrayList<String> reaxels) {
        for (String p : reaxels) {
            this.removeEntityInstances(p);
        }
        for (final IhmListener listen : listeners.getListeners(IhmListener.class)) {
            listen.matrixUpdate(getInstances(), updateList(), getTime());
        }
    }

    /**
     *
     * @param entities
     * @return
     */
    public HashMap<String, Integer> updateList() {
        HashMap<String, Integer> entities = instances.getBook();
        ArrayList<Entity> reaxels = model.getListManipulesNoeuds();
        for (Entity entity : reaxels) {
            if (entities.containsKey(entity._etiquettes) == false) {
                entities.put(entity._etiquettes, 0);
            }
        }

        return entities;
    }

    private void removeEntityInstances(String nom) {
        for (int c = instances.getSize() - 1; c >= 0; c--) {
            InstanceReaxel cube = instances.getInList(c);
            if (cube.getNom().equals(nom)) {
                instances.removeReaxel(cube);
            }
        }
    }

    public void addEntityInstances(ArrayList<UtilPoint3D> points, String etiquette) {
        boolean toUpdate = false;
        for (UtilPoint3D point : points) {
            int x = point.x;
            int y = point.y;
            int z = point.z;
            if (this.AjouterReaxel(x, y, z, etiquette)) {
                toUpdate = true;
            }
        }
        if (toUpdate) {
            for (final IhmListener listen : listeners.getListeners(IhmListener.class)) {
                listen.matrixUpdate(getInstances(), updateList(), getTime());
            }
        }
    }

    public void removeEntityInstance(int x, int y, int z) {
        boolean changed = instances.removeReaxel(x, y, z);
        if (changed) {
            for (final IhmListener listen : listeners.getListeners(IhmListener.class)) {
                listen.matrixUpdate(getInstances(), updateList(), getTime());
            }
        }
    }

    private boolean AjouterReaxel(int i, int j, int k, String etiquette) {
        boolean changed = false;
        ArrayList<Entity> reaxels = model.getListManipulesNoeuds();
        for (int n = 0; n < reaxels.size(); n++) {
            if (reaxels.get(n).TrouveEtiquette(etiquette) >= 0) {
                InstanceReaxel r = InstanceReaxel.CreerReaxel(reaxels.get(n));
                r.setX(i);
                r.setY(j);
                r.setZ(k);
                changed = instances.addReaxel(r);
                n = reaxels.size();
            }
        }
        return changed;
    }

    public String getType(int x, int y, int z) {
        InstanceReaxel r = instances.getFast(x, y, z);
        if (r != null) {
            return r.getNom();
        } else {
            return "";
        }
    }

    private boolean AjouterFuturReaxel(int i, int j, int k, String etiquette) {
        boolean changed = false;
        ArrayList<Entity> reaxels = model.getListManipulesNoeuds();
        for (int n = 0; n < reaxels.size(); n++) {
            if (reaxels.get(n).TrouveEtiquette(etiquette) >= 0) {
                InstanceReaxel r = InstanceReaxel.CreerReaxel(reaxels.get(n));
                r.setX(i);
                r.setY(j);
                r.setZ(k);
                changed = instancesFutur.addReaxel(r);
                n = reaxels.size();
            }
        }
        return changed;
    }

    public void ProtoReaxelEdited(Entity entity, String old_name) {
        instances.editReaxels(entity, old_name);
        for (final IhmListener listen : listeners.getListeners(IhmListener.class)) {
            listen.matrixUpdate(getInstances(), updateList(), getTime());
        }
    }

    public boolean isRunning() {
        return timer_play.isRunning();
    }

    public boolean isPause() {
        return pause;
    }

    public void setPause(boolean pause) {
        this.pause = pause;
        if (isPause()) {
            timer_play.stop();
        } else {
            timer_play.start();
        }
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
        if (!isStopped()) {
            timer_play.stop();
            initTimer();
        } else {
            initTimer();
        }
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getNb_processus_a_traiter() {
        return nb_processus_a_traiter;
    }

    public void setNb_processus_a_traiter(int nb_processus_a_traiter) {
        this.nb_processus_a_traiter = nb_processus_a_traiter;
    }

    public synchronized void decrementer_nb_processus_a_traiter() {
        setNb_processus_a_traiter(getNb_processus_a_traiter() - 1);
    }

    public boolean isStopped() {
        return stopped;
    }

    public void setStopped(boolean stopped) {
        this.stopped = stopped;
    }

    public int getMaxStep() {
        return maxStep;
    }

    public void setMaxStep(int maxStep) {
        this.maxStep = maxStep;
    }

    public AllInstances getInstances() {
        return instances;
    }

}
