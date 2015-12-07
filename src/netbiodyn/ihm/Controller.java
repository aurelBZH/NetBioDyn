/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package netbiodyn.ihm;

import com.jogamp.opengl.awt.GLJPanel;
import com.jogamp.opengl.util.FPSAnimator;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import netbiodyn.AllInstances;
import netbiodyn.InstanceReaxel;
import netbiodyn.Model;
import netbiodyn.Behavior;
import netbiodyn.Entity;
import netbiodyn.Simulator;
import netbiodyn.util.FileSaverLoader;
import netbiodyn.util.Lang;
import netbiodyn.util.RandomGen;
import netbiodyn.util.UtilAnimatedGifEncoder;
import netbiodyn.util.UtilDivers;
import netbiodyn.util.UtilFileFilter;
import netbiodyn.util.UtilPoint3D;
import jadeAgentServer.util.Parameter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import netbiodyn.RunnableSimulator;

/**
 * Make the link between the views and the model according to the MVC pattern. -
 * creates the views (Environment and JOGL) - controls the user inputs - creates
 * the model (Model and Simulator) - modifies the model according to correct
 * user inputs
 *
 * @author riviere
 * @see Model, Simulator, Environment, JOGL2Setup_GLJPanel, Command
 */
public class Controller {

    private final static int FRAME_WIDTH = 900;
    private final static int FRAME_HEIGHT = 600;
    private final int init_x = 100, init_y = 100, init_z = 1;

    private final Environment env;
    private JFrame frame3D;
    private final Model model;
    private final Simulator simulator;
    private final JFrame frame;
    // An array of Command used to allow the "undo" operation
    private final ArrayList<Command> lastCommand;
    private final int maxMemory = 20;

    /**
     * Creates instances of the view classes and the model classes Connects them
     * with listeners Launches the main view (Environment) and testes the 3D
     * view
     */
    public Controller() {
        this.lastCommand = new ArrayList<>();
        frame = new JFrame();
        Env_Parameters parameters = new Env_Parameters("EN", init_x, init_y, init_z, "Empty Environment", null, "");
        env = new Environment(this, parameters);
        model = new Model(parameters);
        model.addListener(env);
        simulator = new Simulator(model);
        simulator.addListener(env);

        frame.add(env);
        frame.setName("NetBioDyn");
        frame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
        frame.setTitle("NetBioDyn - UEB - UBO - Lab STICC - IHSEV - Pascal Ballet - Free Software under GPL License");
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                int res;
                if (Lang.getInstance().getLang().equalsIgnoreCase("FR")) {
                    res = JOptionPane.showConfirmDialog(env, "Voulez-vous sauvegarder le modèle avant de quitter ?", "Question", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    res = JOptionPane.showConfirmDialog(env, "Do you want to save your model ?", "Question", JOptionPane.INFORMATION_MESSAGE);
                }

                if (res == JOptionPane.YES_OPTION) {
                    int s = saveModel(env.getNom_sauvegarde());
                    if (s == 0) {
                        System.exit(0);
                    }
                } else if (res == JOptionPane.NO_OPTION) {
                    System.exit(0);
                }
            }

        });

        env.launch();
        init3D();
        this.addKeyListener(frame);
        frame.setVisible(true);
    }

    /**
     * Initializing and testing the 3D view (an instance of the JOGL class)
     */
    private void init3D() {
        frame3D = new JFrame("3D View");
        Container topAncestor = env.getTopLevelAncestor();
        frame3D.setBounds(topAncestor.getBounds().x + topAncestor.getBounds().width, topAncestor.getBounds().y, 640, topAncestor.getBounds().height);

        try {
            JOGL2Setup_GLJPanel panel3d = new JOGL2Setup_GLJPanel(env.getTailleX(), env.getTailleY(), env.getTailleZ(), model.getInstances().getList());
            GLJPanel canvas = panel3d;
            canvas.setPreferredSize(new Dimension(640, topAncestor.getBounds().height - 24));

            frame3D.setContentPane(canvas);  // GLJPanel is a Container
            frame3D.pack();
            model.addListener(panel3d);
            simulator.addListener(panel3d);

            // Create a animator that drives canvas' display() at the specified FPS. 
            final FPSAnimator animator = new FPSAnimator(canvas, 60, true);
            // start the animation loop
            animator.start();
            frame3D.setVisible(false);
            this.addKeyListener(frame3D);
        } catch (UnsatisfiedLinkError e) {
            disable3D(e.toString());
        }
    }

    private void disable3D(String message) {
        env.disabled3D();
        File file = new File("./log_netbiodyn.txt");
        try {
            file.createNewFile();
            FileSaverLoader.saveAsText(file.getPath(), message);
        } catch (IOException ex) {
            Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (Lang.getInstance().getLang().equalsIgnoreCase("FR")) {
            JOptionPane.showMessageDialog(env, "Impossible d'initialiser la 3D - Démarrage en 2D. Pour plus de détails sur l'erreur, voir le fichier log_netbiodyn.txt", "Attention", JOptionPane.ERROR_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(env, "3D impossible to initialize - Staying in 2D. For more details, see the file log_netbiodyn.txt", "Warning", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Makes visible / invisible the 3D frame. Called by Environment
     */
    public void hideShow3DView() {
        frame3D.setVisible(!frame3D.isVisible());
    }

    /**
     * Creates a new entity type. Called by Environment
     *
     * @see Entity and WndEditNoeud
     */
    public void addEntity() {
        if (simulator.isRunning()) {
            this.pauseSimulation();
        }

        WndEditNoeud wc = new WndEditNoeud(model.getListManipulesNoeuds(), model.getListManipulesReactions());
        wc.WndCliValue_Load(null);
        wc.setVisible(true);
        if (wc.getDialogResult().equals("OK") && !wc._cli._etiquettes.equals("")) {
            model.addProtoReaxel(wc._cli);
        }
    }

    /**
     * Creates a new Behaviour. Called by Environment
     *
     * @see Behavior and WndEditReaction
     */
    public void addBehaviour() {
        if (simulator.isRunning()) {
            this.pauseSimulation();
        }

        WndEditReaction w = new WndEditReaction(model.getListManipulesNoeuds(), model.getListManipulesReactions());
        w.WndCliEditReaction3_Load(null);
        w.setVisible(true);
        String r = w.getDialogResult();
        if (r != null) {
            if (r.equals("OK")) {
                model.addMoteurReaction(w._r3);
            }
        }
    }

    /**
     * Change the probability of the Behaviour name by value. Called by
     * Environment
     *
     * @param name of the Behaviour to modify
     * @param value the new value of the propability
     */
    public void changeProba(String name, double value) {
        model.editBehaviourProba(name, value);
    }

    /**
     * Change the simulation parameters : probability of behaviours, half-life
     * of entities and initial number of entities. Called by Launcher
     *
     * @param param
     * @see Parameter and Launcher
     */
    public void changeParameters(HashMap<String, ArrayList<Parameter>> param) {
        model.changeParameters(param, env.getPictureBoxDimensions());
    }

    /**
     * Edit an existing Behaviour. Called by Environment
     *
     * @see Behavior and WndEditReaction
     */
    public void editBehaviour() {
        if (simulator.isRunning()) {
            this.pauseSimulation();
        }

        int i = env.getDataGridView_comportements().getSelectedIndex();
        if (i >= 0) {
            String name = env.getDataGridView_comportements().getSelectedValue().toString();
            Behavior cpt = model.getBehaviour((String) env.getDataGridView_comportements().getModel().getElementAt(i));
            WndEditReaction wc = new WndEditReaction(model.getListManipulesNoeuds(), model.getListManipulesReactions());
            wc.WndCliEditReaction3_Load(cpt);
            wc.setVisible(true);
            String r = wc.getDialogResult();
            if (r != null && r.equals("OK")) {
                model.editMoteurReaction(wc._r3, name);
            }
        }
    }

    /**
     * Remove one or several existing Behaviours. Called by Environment
     *
     * @param tab an array of indexes - the behaviours selected by the user in
     * the the main ihm
     */
    public void delBehaviour(int[] tab) {
        if (simulator.isRunning()) {
            this.pauseSimulation();
        }

        ArrayList<String> reactions = new ArrayList<>();
        for (int k = tab.length - 1; k >= 0; k--) {
            int i = tab[k];
            if (i >= 0) {
                String name = (String) env.getDataGridView_comportements().getModel().getElementAt(i);
                reactions.add(name);
            }
        }
        model.delMoteurReaction(reactions);
    }

    /**
     * Remove one or several existing Entities. Called by Environment
     *
     * @param tab an array of indexes - the entities selected by the user in the
     * the main ihm
     */
    public void delEntity(int[] tab) {
        if (simulator.isRunning()) {
            this.pauseSimulation();
        }
        ArrayList<String> entities = new ArrayList<>();
        for (int k = tab.length - 1; k >= 0; k--) {
            int i = tab[k];
            if (i >= 0) {
                String name = UtilDivers.str_originale((String) env.getDataGridView_entites().getModel().getElementAt(i));
                entities.add(name);
            }
        }
        model.delProtoReaxel(entities);
        if (!simulator.isStopped()) {
            simulator.ProtoReaxelDeleted(entities);
        }
    }

    /**
     * Randomly adds Entity's instances in the rectangle defined by the 4 points
     * bottom_rightX, bottom_rightY, top_leftX and top_leftY, at a depth of z
     * Called by Environment
     *
     * @param bottom_rightX
     * @param bottom_rightY
     * @param top_leftX
     * @param top_leftY
     * @param z
     */
    public void randomlyPopulate(int bottom_rightX, int bottom_rightY, int top_leftX, int top_leftY, int z) {
        int num_col = env.getDataGridView_entites().getSelectedIndex();
        if (num_col >= 0) {
            String etiquette = UtilDivers.str_originale(env.getDataGridView_entites().getModel().getElementAt(num_col).toString());
            ArrayList<UtilPoint3D> presents = this.instancesInSelection(top_leftX, top_leftY, bottom_rightX, bottom_rightY, z);
            int max = (Math.max(top_leftX, bottom_rightX) - Math.min(top_leftX, bottom_rightX))
                    * (Math.max(top_leftY, bottom_rightY) - Math.min(top_leftY, bottom_rightY)) - presents.size();
            if (max > 0) {
                if (simulator.isRunning()) {
                    this.pauseSimulation();
                }
                String res;
                int nbr = max + 1;
                while (nbr > max) {
                    if (Lang.getInstance().getLang().equalsIgnoreCase("FR")) {
                        res = JOptionPane.showInputDialog(env, "Combien d'entités " + etiquette + " voulez-vous ajouter ? Max : " + max, "Ajouter aléatoirement", JOptionPane.OK_CANCEL_OPTION);
                    } else {
                        res = JOptionPane.showInputDialog(env, "How many " + etiquette + " entities do you want to add ? Max : " + max, "Populate randomly", JOptionPane.OK_CANCEL_OPTION);
                    }
                    if (res != null) {
                        nbr = (int) Double.parseDouble(res);
                        if (nbr > max) {
                            if (Lang.getInstance().getLang().equalsIgnoreCase("FR")) {
                                JOptionPane.showMessageDialog(env, "Nombre d'entités supérieur au maximum possible !", "Attention !", JOptionPane.ERROR_MESSAGE);
                            } else {
                                JOptionPane.showMessageDialog(env, "The entities number is greater than the maximum !", "Warning !", JOptionPane.ERROR_MESSAGE);
                            }
                        } else {
                            ArrayList<UtilPoint3D> points = this.populate(top_leftX, top_leftY, bottom_rightX, bottom_rightY, z, nbr, presents);
                            addEntityInstances(points);
                        }
                    } else {
                        nbr = 0;
                    }
                }
            }
        }
    }

    /**
     * Counts the number of entities' instances in the rectangle defined by the
     * 4 points bottom_rightX, bottom_rightY, top_leftX and top_leftY, at the
     * depth z
     *
     * @param top_leftX
     * @param top_leftY
     * @param bottom_rightX
     * @param bottom_rightY
     * @param z
     * @return a number of entities' instances
     */
    private ArrayList<UtilPoint3D> instancesInSelection(int top_leftX, int top_leftY, int bottom_rightX, int bottom_rightY, int z) {
        ArrayList<UtilPoint3D> points = new ArrayList<>();
        AllInstances instances;
        if (simulator.isRunning()) {
            instances = simulator.getInstances();
        } else {
            instances = model.getInstances();
        }

        for (int i = Math.min(top_leftX, bottom_rightX); i < Math.max(top_leftX, bottom_rightX); i++) {
            for (int j = Math.min(top_leftY, bottom_rightY); j < Math.max(top_leftY, bottom_rightY); j++) {
                InstanceReaxel r = instances.getFast(i, j, z);
                if (r != null) {
                    points.add(new UtilPoint3D(r.getX(), r.getY(), r.getZ()));
                }
            }
        }
        return points;
    }

    /**
     * Creates an array of nbr 3D points to be added in a specific rectangle,
     * according to an array of existing points
     *
     * @param top_leftX
     * @param top_leftY
     * @param bottom_rightX
     * @param bottom_rightY
     * @param z
     * @param nbr
     * @param existing
     * @return an array of nbr 3D points
     */
    private ArrayList<UtilPoint3D> populate(int top_leftX, int top_leftY, int bottom_rightX, int bottom_rightY, int z, int nbr, ArrayList<UtilPoint3D> existing) {
        ArrayList<UtilPoint3D> points = new ArrayList<>();
        int maxX = Math.max(top_leftX, bottom_rightX);
        int minX = Math.min(top_leftX, bottom_rightX);
        int maxY = Math.max(top_leftY, bottom_rightY);
        int minY = Math.min(top_leftY, bottom_rightY);
        for (int i = 0; i < nbr; i++) {
            int x = RandomGen.getInstance().nextInt(maxX - minX) + minX;
            int y = RandomGen.getInstance().nextInt(maxY - minY) + minY;
            UtilPoint3D p = new UtilPoint3D(x, y, z);
            if ((points.contains(p)) || (existing.contains(p))) {
                i = i - 1;
            } else {
                points.add(p);
            }
        }
        return points;
    }

    /**
     * Add Entity's instances defined by an array of 3D coordinates. Called by
     * Environment
     *
     * @param points an array of 3D coordinates
     * @see AddCommand, RemoveCommand
     */
    public void addEntityInstances(ArrayList<UtilPoint3D> points) {
        int num_col = env.getDataGridView_entites().getSelectedIndex();
        if (num_col >= 0) {
            String etiquette = UtilDivers.str_originale(env.getDataGridView_entites().getModel().getElementAt(num_col).toString());
            AddCommand command = new AddCommand(model, simulator, points, etiquette);
            command.setOpposite(new RemoveCommand(model, simulator, points));
            this.memorizeCommand(command);
            command.execute();
        }
    }

    /**
     * Edit an existing Entity. Called by Environment
     *
     * @see Entity and WndEditNoeud
     */
    public void editEntity() {
        if (simulator.isRunning()) {
            this.pauseSimulation();
        }

        if (env.getDataGridView_entites().getSelectedIndex() >= 0) {
            String name = UtilDivers.str_originale(env.getDataGridView_entites().getSelectedValue().toString());
            Entity p = model.getProtoReaxel(name);

            WndEditNoeud wc = new WndEditNoeud(model.getListManipulesNoeuds(),
                    model.getListManipulesReactions());
            wc.WndCliValue_Load(p);
            wc.setVisible(true);
            if (wc.getDialogResult().equals("OK") && !p._etiquettes.equals("")) {
                int time = simulator.getTime();
                model.editProtoReaxel(p, name, time);
                if (!simulator.isStopped()) {
                    simulator.ProtoReaxelEdited(p, name);
                }
            }
        }
    }

    /**
     * Removes an Entity's instance defined by the coordinates x, y and z.
     * Called by Environment
     *
     * @param x
     * @param y
     * @param z
     */
    public void removeEntityInstance(int x, int y, int z) {
        ArrayList<UtilPoint3D> points = new ArrayList<>();
        UtilPoint3D point = new UtilPoint3D(x, y, z);
        points.add(point);
        RemoveCommand command = new RemoveCommand(model, simulator, point);
        if (simulator.isStopped()) {
            command.setOpposite(new AddCommand(model, simulator, points, model.getType(x, y, z)));
        } else {
            command.setOpposite(new AddCommand(model, simulator, points, simulator.getType(x, y, z)));
        }
        this.memorizeCommand(command);
        command.execute();
    }

    /**
     * Remove all entities' instances in the model. Called by Environment
     *
     */
    public void clearEnvironment() {
        if (simulator.isRunning()) {
            this.pauseSimulation();
        }

        int res = 0;
        if (Lang.getInstance().getLang().equalsIgnoreCase("FR")) {
            res = JOptionPane.showConfirmDialog(env, "Voulez-vous vider tout l'environnement ?", "Attention !", JOptionPane.INFORMATION_MESSAGE);
        } else {
            res = JOptionPane.showConfirmDialog(env, "Do you want to clean all the environment ?", "Warning !", JOptionPane.INFORMATION_MESSAGE);
        }

        if (res == JOptionPane.YES_OPTION) {
            model.clear_OnlyCleanable();
            this.stopWithoutAsking();
        }
    }

    /**
     * Load a new Model saved in file _nom_sauvegarde. Called by Environment
     *
     * @param nameSaved the saved model
     */
    public void loadModel(String nameSaved) {
        if (simulator.isRunning()) {
            this.pauseSimulation();
        }

        UtilFileFilter filtre = new UtilFileFilter("NetBioDyn", "nbd");
        File file = FileSaverLoader.chooseFileToLoad(nameSaved, filtre);

        if (file != null) {
            env.setNom_sauvegarde(UtilDivers.removeExtension(file.getPath()));
            this.stopWithoutAsking();
            model.load(env, file.getPath());
        }
    }

    /**
     * Export the recorded curves of the simulation. Called by Environment.
     *
     * @param nameSaved name of the simulation
     * @param curves recorded from the Environment
     */
    public void exportCurves(String nameSaved, SimulationCurves curves) {
        if (simulator.isRunning()) {
            this.pauseSimulation();
        }

//        nameSaved = nameSaved.substring(0, nameSaved.length() - 4) + ".r";
        File file = FileSaverLoader.chooseFileToSave(nameSaved, "R files, CSV (Excel) files - .r by default", new String[]{"r", "csv"});

        if (file != null) {
            curves.export(file.getPath(), model.getEntitiesNames());
        }
    }

    /**
     * Save the Model file _nom_sauvegarde. Called by Environment
     *
     * @param nameSaved
     */
    public int saveModel(String nameSaved) {
        if (simulator.isRunning()) {
            this.pauseSimulation();
        }

        File file = FileSaverLoader.chooseFileToSave(nameSaved, "NetBioDyn", new String[]{"nbd"});

        if (file != null) {
            env.setNom_sauvegarde(UtilDivers.removeExtension(UtilDivers.fichier(file.getPath())));
            model.save(env, file.getPath(), file.getParent());
            return 0;
        } else {
            return -1;
        }
    }

    /**
     * Change the environment parameters (size, image ...). Called by
     * Environment
     */
    public void updateEnvParameters() {
        if (simulator.isRunning()) {
            this.pauseSimulation();
        }

        WndEditEnvironnement w = new WndEditEnvironnement(env.getParameters());
        w.setVisible(true);

        if (w.getDialogResult().equals("OK")) {
            model.clearEnvironment();
            model.setParameters(w.getParameters());
            this.stopWithoutAsking();
        }
    }

    /**
     * Launch or pause the Simulator according to its state. Called by
     * Environment
     */
    public void play() {
        if (simulator.isRunning() && !simulator.isPause()) { // On est en PLAY et on fait PAUSE
            this.pauseSimulation();
        } else if (!simulator.isRunning() && simulator.isPause()) { // On est en PAUSE et on fait PLAY
            env.unpause_simulation();
            simulator.setPause(false);
        } else { // On est en STOP et on fait PLAY
            env.simulationStarted();
            simulator.start();
        }
    }

    /**
     * Launch just one step of simulation. Called by Environment
     */
    public void play_one() {
        if (!simulator.isRunning()) {
            simulator.play_one();
        }
    }

    /**
     * Launch the simulation for step steps. Called by Launcher
     *
     * @param step the number of step to simulate
     */
    public void playUntil(int step) {
        if (!simulator.isRunning()) {
            env.simulationStarted();
            simulator.setMaxStep(step);
            simulator.start();
        }
    }

    /**
     * Stop the simulation after user's confirmation. Called by Environment.
     */
    public void stop() {
        if (env.isFreezed()) {
            adjustmentStopped(false);
        }
        if (simulator.isRunning()) {
            this.pauseSimulation();
        }

        if (simulator.getTime() != 0) {
            int res = 0;
            if (Lang.getInstance().getLang().equals("FR")) {
                res = JOptionPane.showConfirmDialog(env, "Voulez-vous vraiment relancer la simulation depuis le debut ?", "Question", JOptionPane.INFORMATION_MESSAGE);
            } else {
                res = JOptionPane.showConfirmDialog(env, "Do you really want to relaunch the simulation from the beginning ?", "Question", JOptionPane.INFORMATION_MESSAGE);
            }

            if (res == JOptionPane.YES_OPTION) {
                stopWithoutAsking();
            }
        }
    }

    /**
     * Self-adjustment is either done or the user ended it. Called by Launcher
     * and Environment
     *
     * @param finished true if adjustment is done, false otherwise
     */
    public void adjustmentStopped(boolean finished) {
        // TODO Arreter l'ajustement auto et recharger le modèle initial !
        env.freeze(false);
        stopWithoutAsking();
        if (!finished) {
            // recharger le modèle initial
        } else {
            int res;
            System.out.println("Ajustement terminé avec succès !");
//            if (Lang.getInstance().getLang().equals("FR")) {
//                res = JOptionPane.showConfirmDialog(env, "Ajustement terminé avec succès ! Voulez-vous conserver ces paramètres ?", "Question", JOptionPane.INFORMATION_MESSAGE);
//            } else {
//                res = JOptionPane.showConfirmDialog(env, "Adjustment successfully done ! Do you want to keep these parameters ?", "Question", JOptionPane.INFORMATION_MESSAGE);
//            }
//            if (res != 0) {
//                // recharger le modèle initial
//            }
        }
    }

    /**
     * Stop the simulation without the user's confirmation.
     */
    public void stopWithoutAsking() {
        if (simulator.getTime() != 0) {
            env.stopSimulation();
            simulator.stop();
        }
    }

    /**
     * Pause the simulation in both the Simulator and the Environment.
     */
    private void pauseSimulation() {
        env.pauseSimulation();
        simulator.setPause(true);
    }

    public void export() {
        String str_model = "\\fs32 \\b Entites:\n\\par\n\\b0";

        // Entites
        ArrayList<Entity> lstc = model.getListManipulesNoeuds();
        for (Entity cli : lstc) {
            str_model += cli.getEtiquettes() + "\\b:\\b0";
            str_model += " 1/2 Vie =";
            if (cli.DemieVie > 0) {
                str_model += ((Double) cli.DemieVie).toString();
            } else {
                str_model += "+00";
            }
            str_model += "\n\\par\n";
        }
        str_model += "\n\\par\n";

        // Comportements
        str_model += "\\b Comportements:\n\\par\n\\b0";

        List<Behavior> lst = model.getListManipulesReactions();
        for (Behavior reac : lst) {
            str_model += reac.getEtiquettes() + " \\b : \\b0 ";
            str_model += reac._reactifs.get(0);
            int nb_reactifs = 0;
            for (int r = 1; r < 3; r++) {
                if (reac._reactifs.get(r) != null) {
                    if (!reac._reactifs.get(r).equals("*")) {
                        str_model += " \\b+ \\b0 " + reac._reactifs.get(r);
                        nb_reactifs++;
                    }
                }
            }
            str_model += " \\b=" + ((Double) reac.get_k()).toString() + "=> \\b0 ";
            if (!reac._produits.get(0).equals("")) {
                str_model += reac._produits.get(0);
            } else {
                str_model += "_";
            }
            for (int p = 1; p < 3; p++) {
                String prod = "";
                if (reac._produits.get(p) != null) {
                    if (!reac._produits.get(p).equals("-")) {
                        prod = " \\b+ \\b0 " + reac._produits.get(p);
                    }
                }
                if (prod.equals("") && p < nb_reactifs) {
                    prod = " \\b+\\b0 _";
                }
                str_model += prod;
            }
            str_model += "\n\\par\n";
        }
        if (simulator.isRunning()) {
            this.pauseSimulation();
        }

        WndDescriptionSimulation w = new WndDescriptionSimulation();
        w.jEditorPane1.setContentType("text/rtf");
        w.jEditorPane1.setText("{\\rtf1\\ansi\\ansicpg1252\\deff0\\deflang1036{\\fonttbl{\\f0\\fnil\\fcharset0 Microsoft Sans Serif;}}" + str_model);
        w.setVisible(true);
    }

    public void about() {
        WndAbout wnd = new WndAbout();
        wnd.setVisible(true);
    }

    public void record_animation() {
        if (simulator.isRunning()) {
            this.pauseSimulation();
        }

        String nomFichier = "film.gif";
        File file = FileSaverLoader.chooseFileToSave(nomFichier, "Animated Gif", new String[]{"gif"});

        if (file != null) {
            nomFichier = file.getPath();
            int reponse;
            if (Lang.getInstance().getLang().equals("FR")) {
                reponse = JOptionPane.showConfirmDialog(env, "Voulez-vous enregistrer les courbes en plus ?");
            } else {
                reponse = JOptionPane.showConfirmDialog(env, "Do you want to record the curves as well ?");
            }

            if (reponse == JOptionPane.OK_OPTION || reponse == JOptionPane.YES_OPTION) {
                env.setAnimation_courbes(true);
            } else {
                env.setAnimation_courbes(false);
            }
            if (nomFichier.endsWith(".gif")) {
                UtilAnimatedGifEncoder gif = new UtilAnimatedGifEncoder();
                gif.setFrameRate(30);
                gif.start(nomFichier);
                gif.setFrameRate(30);
                env.setAnimation_gif(gif);
            }
        } else {
            env.unselectCheckBoxAvi();
        }
    }

    public void launchSelfAdjustment() {  
        // Work in progress
    }


    public void changeSpeed(int value) {
        int v;
        switch (value) {
            case 0:
                v = 1000;
                break;
            case 1:
                v = 500;
                break;
            case 2:
                v = 100;
                break;
            default:
                v = 1;
                break;
        }
        simulator.setSpeed(v);
    }

    public void newModel() {
        if (simulator.isRunning()) {
            this.pauseSimulation();
        }
        int res = 0;
        if (Lang.getInstance().getLang().equalsIgnoreCase("FR")) {
            res = JOptionPane.showConfirmDialog(env, "Voulez-vous vraiment creer une nouvelle simulation ?", "Question", JOptionPane.INFORMATION_MESSAGE);
        } else {
            res = JOptionPane.showConfirmDialog(env, "Do you really want to create a new simulation ?", "Question", JOptionPane.INFORMATION_MESSAGE);
        }

        if (res == JOptionPane.YES_OPTION) {
            this.stopWithoutAsking();
            model.newModel();
        }
    }

    public void select(int x, int y, int z) {
        if (simulator.isStopped()) {
            InstanceReaxel r = model.getInstances().getFast(x, y, z);
            if (r == null) {
                model.unselect(env.getCubes_selectionnes());
                env.setCubes_selectionnes(new ArrayList<InstanceReaxel>());
            } else if (r.isSelectionne()) {
                model.unselect(x, y, z);
                env.unselect(r);
            } else {
                env.addCube_selectionnes(r);
                model.select(x, y, z);
            }
        }
    }

    public void deplacer(ArrayList<InstanceReaxel> _cubes_selectionnes, int new_x, int new_y, int new_z) {
        model.deplacer(_cubes_selectionnes, new_x, new_y, new_z);
        env.setCubes_selectionnes(new ArrayList<InstanceReaxel>());
    }

    private void addKeyListener(JFrame f) {
        final Controller controller = this;
        final String space = "Space";
        InputMap map = f.getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        map.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), space);
        ActionMap action = f.getRootPane().getActionMap();
        action.put(space, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.play();
            }
        }
        );

        final String escape = "Escape";
        map.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), escape);
        action.put(escape, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.stop();
            }
        }
        );

        final String undo = "Undo";
        map.put(KeyStroke.getKeyStroke(KeyEvent.VK_Z, KeyEvent.CTRL_DOWN_MASK), undo);
        action.put(undo, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.undo();
            }
        }
        );
    }

    public Model getModel() {
        return model;
    }

    public Simulator getSimulator() {
        return simulator;
    }

    protected void undo() {
        if (lastCommand.size() > 0) {
            lastCommand.get(lastCommand.size() - 1).undo();
            lastCommand.remove(lastCommand.size() - 1);
        }
    }

    private void memorizeCommand(Command command) {
        lastCommand.add(command);
        if (lastCommand.size() > maxMemory) {
            lastCommand.remove(0);
        }
    }

    public Environment getEnv() {
        return env;
    }

}
