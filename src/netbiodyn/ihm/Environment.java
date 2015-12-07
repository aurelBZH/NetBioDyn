/* This file is part of NetBioDyn.
 *
 *   NetBioDyn is free software; you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation; either version 3 of the License, or
 *   any later version.
 *
 *   NetBioDyn is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with NetBioDyn; if not, write to the Free Software
 *   Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */
/*
 * Environment.java
 *
 * Created on 14 octobre 2007, 13:43
 */
package netbiodyn.ihm;

import netbiodyn.util.UtilAnimatedGifEncoder;
import netbiodyn.util.UtilPoint3D;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import javax.imageio.ImageIO;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import javax.swing.JList;
import netbiodyn.AllInstances;
import netbiodyn.util.Serialized;
import netbiodyn.InstanceReaxel;
import netbiodyn.Behavior;
import netbiodyn.Entity;
import netbiodyn.ProtoSimplexel;
import netbiodyn.util.Lang;
import netbiodyn.util.RandomGen;
import netbiodyn.util.UtilDivers;

/**
 *
 * @author ballet
 */
public class Environment extends javax.swing.JPanel implements IhmListener, AdjustingListener {

    private final Controller controller;
    private Env_Parameters parameters;
    public String _site_publication = "http://virtulab.univ-brest.fr/?page_id=172";

    // Animation
    private UtilAnimatedGifEncoder _animation_gif = null;

    private int _case_x0 = 0;
    private int _case_y0 = 0;
    private boolean movingCubes = false;
    private boolean freezed = false;

    private ArrayList<Entity> _ListManipulesNoeuds = new ArrayList<>();
    private ArrayList<Behavior> _ListManipulesReactions = new ArrayList<>();
    private AllInstances instances;

    private boolean _animation_courbes = false;
    private HashMap<String, Integer> dico_courbes;
    private String PARAM_fichier = null;
    private String _objet_presse = "";

    // ATTRIBUTS
    private int _time = 0;

    // Observed area
    private float _observed_width; // Entre 0 et getTailleX()
    private float _observed_height; // Entre 0 et getTailleY()
    private float _observed_left = 0;     // Entre 0 et getTailleX()
    private float _observed_top = 0;      // Entre 0 et getTailleY()
    private int _x0_mouse_down = 0;
    private int _y0_mouse_down = 0;
    private boolean _mouse_left_down = false;
    private boolean _mouse_right_down = false;
    private boolean _mouse_zoom_down = false;
    private ArrayList<InstanceReaxel> _cubes_selectionnes = null;
//    private ArrayList<elementCourbe> _courbes = new ArrayList<>();
    private final SimulationCurves curves;

    private BufferedImage _image_deco = null;

    // Sauvegarde / Chargement
    private String _nom_sauvegarde = "simulation.nbd";

    private boolean _dataGridView_entitesIsChanging = false;

    private int init_width = -1;
    private int init_height = -1;

    private Rectangle init_bounds_sim = new Rectangle();
    private Rectangle init_bounds_env = new Rectangle();
    private Rectangle init_bounds_ent = new Rectangle();
    private Rectangle init_bounds_beh = new Rectangle();
    private Rectangle init_bounds_curv = new Rectangle();
    private Rectangle init_bounds_misc = new Rectangle();

    private BufferedImage bmp_memory = null;
    private Graphics g_mem = null;

    // Images de décoration des boutons
    private ImageIcon icon_paint_move, icon_paint_stylo, icon_paint_spray, icon_paint_ligne, icon_paint_random, icon_paint_gomme;
    private ImageIcon icon_bouton_play, icon_bouton_pause, icon_bouton_step, icon_bouton_stop;
    private ImageIcon icon_bouton_new, icon_bouton_open, icon_bouton_save;
    private ImageIcon icon_interogation;

    public Environment(Controller controller, Env_Parameters parameters) {
        super();
        this.parameters = parameters;
        this.controller = controller;
        curves = new SimulationCurves();
    }

    @Override
    public void newEnvLoaded(Serialized saved, HashMap<String, Integer> entitesBook) {
        dataGridView_entites.clearSelection();
        this.pictureBox_Env.setIcon(null);
        _ListManipulesNoeuds = saved.getListManipulesNoeuds();
        _ListManipulesReactions = saved.getListManipulesReactions();
        instances = new AllInstances(saved.getInstances());
        setDico_courbes((HashMap<String, Integer>) entitesBook.clone());
        setParameters(saved.getParameters());
        fillDataGridEntities();
        fillDataGridBehaviours();
        placerComportementsVisiblesDansPanel();
        majEntites();
        initAbscissaBox();
        drawAll(0, 0, 0, 0, 0);
    }

    @Override
    public void protoEntityUpdate(ArrayList<Entity> entities, HashMap<String, Integer> entitesBook) {
        checkForChangesInCurves(_ListManipulesNoeuds, entities);
        _dataGridView_entitesIsChanging = true;
        _ListManipulesNoeuds = entities;
        setDico_courbes((HashMap<String, Integer>) entitesBook.clone());
        fillDataGridEntities();
        majEntites();
        initAbscissaBox();
        _dataGridView_entitesIsChanging = false;
    }

    @Override
    public void matrixUpdate(AllInstances instances, HashMap<String, Integer> entitesBook, int time) {
        this.instances = new AllInstances(instances);
        setDico_courbes((HashMap<String, Integer>) entitesBook.clone());
        this._time = time;
        labelTime.setText("" + _time);
        maj_pas_de_temps();
    }

    @Override
    public void newEnvParameters(Env_Parameters parameters) {
        this.setParameters(parameters);
    }

    @Override
    public void moteurReactionUpdate(ArrayList<Behavior> behaviours) {
        this._ListManipulesReactions = behaviours;
        fillDataGridBehaviours();
    }

    @Override
    public void ready() {
        controller.stopWithoutAsking();
        // Récupérer first step !
    }

    @Override
    public void newAdjustedValues(ArrayList<String[]> valuesToDisplay) {
        // TODO Auto-ajustement en cours, avec valeurs des paramètres en évolution
        System.err.println("MISE A JOUR");
        for (String[] v : valuesToDisplay) {
            System.err.println(v.toString());
        }
    }

    @Override
    public void adjustmentDone(boolean success) {
        // TODO si success, demander si garder nouvelles valeurs, sinon demander une mise à jour du modèle
        controller.adjustmentStopped(true);
    }

    /**
     * Permet d'initialiser le controle Environment (composants, images des
     * boutons et site de publication de l'applet)
     *
     *
     * @see initComponents()
     */
    public void launch() {
        initComponents();
        fillDataGridEntities();
        fillDataGridBehaviours();
        if (_site_publication != null) {
            if (_site_publication.length() > 0) {
                if (!_site_publication.startsWith("http://")) {
                    //JOptionPane.showMessageDialog(this, "4) OK : _site_publication DO NOT starts with http://\n" + _site_publication);
                    //_site_publication = new String("file://") + new String(_site_publication);
                    //JOptionPane.showMessageDialog(this, "5) OK : _site_publication=\n" + _site_publication);
                }
            }
        }
        lireImagesURL();

        instances = new AllInstances(getTailleX(), getTailleY(), getTailleZ());

        _observed_width = getTailleX();
        _observed_height = getTailleY();
        drawAll(0, 0, 0, 0, 0);
        releverToutesCheckBoxPaint(checkBox_paint_move);

        jSliderZ.setMaximum(getTailleZ() - 1);
        bouton_lang.setLocation(240, 5);
        bouton_about.setLocation(310, 5);
        initLanguage();
        initAbscissaBox();
    }

    public boolean lireImagesURL() {
        // Mise en place des images dans les boutons

        URL url;
        try {
            // Bouton Move
            {
                url = getClass().getClassLoader().getResource("Images/paint_move.png");
                BufferedImage img;
                img = ImageIO.read(url);
                icon_paint_move = new ImageIcon(img);
                this.checkBox_paint_move.setIcon(icon_paint_move);
            }
            // Bouton Stylo
            {
                url = getClass().getClassLoader().getResource("Images/paint_stylo.png");
                BufferedImage img;
                img = ImageIO.read(url);
                icon_paint_stylo = new ImageIcon(img);
                this.checkBox_paint_stylo.setIcon(icon_paint_stylo);
            }
            // Bouton Spray
            {
                url = getClass().getClassLoader().getResource("Images/paint_spray.png");
                BufferedImage img;
                img = ImageIO.read(url);
                icon_paint_spray = new ImageIcon(img);
                this.checkBox_paint_spray.setIcon(icon_paint_spray);
            }
            // Bouton Ligne
            {
                url = getClass().getClassLoader().getResource("Images/paint_ligne.png");
                BufferedImage img;
                img = ImageIO.read(url);
                icon_paint_ligne = new ImageIcon(img);
                this.checkBox_paint_ligne.setIcon(icon_paint_ligne);
            }
            // Bouton Random
            {
                url = getClass().getClassLoader().getResource("Images/paint_random.png");
                BufferedImage img;
                img = ImageIO.read(url);
                icon_paint_random = new ImageIcon(img);
                this.checkBox_paint_random.setIcon(icon_paint_random);
            }
            // Bouton Gomme
            {
                url = getClass().getClassLoader().getResource("Images/paint_gomme.png");
                BufferedImage img;
                img = ImageIO.read(url);
                icon_paint_gomme = new ImageIcon(img);
                this.checkBox_paint_gomme.setIcon(icon_paint_gomme);
            }
            // Bouton Play
            {
                url = getClass().getClassLoader().getResource("Images/bouton_play.png");
                BufferedImage img;
                img = ImageIO.read(url);
                icon_bouton_play = new ImageIcon(img);
                this.button_play.setIcon(icon_bouton_play);
            }
            // Bouton Pause
            {
                url = getClass().getClassLoader().getResource("Images/bouton_pause.png");
                BufferedImage img;
                img = ImageIO.read(url);
                icon_bouton_pause = new ImageIcon(img);
            }
            // Bouton Step
            {
                url = getClass().getClassLoader().getResource("Images/bouton_step.png");
                BufferedImage img;
                img = ImageIO.read(url);
                icon_bouton_step = new ImageIcon(img);
                this.button_play1.setIcon(icon_bouton_step);
            }
            // Bouton Record
            {
                url = getClass().getClassLoader().getResource("Images/bouton_stop.png");
                BufferedImage img;
                img = ImageIO.read(url);
                icon_bouton_stop = new ImageIcon(img);
                this.button_init.setIcon(icon_bouton_stop);
            }
            // Bouton New
            {
                url = getClass().getClassLoader().getResource("Images/bouton_new.png");
                BufferedImage img;
                img = ImageIO.read(url);
                icon_bouton_new = new ImageIcon(img);
                this.bouton_new.setIcon(icon_bouton_new);
            }
            // Bouton Open
            {
                url = getClass().getClassLoader().getResource("Images/bouton_open.png");
                BufferedImage img;
                img = ImageIO.read(url);
                icon_bouton_open = new ImageIcon(img);
                this.bouton_open.setIcon(icon_bouton_open);
            }
            // Bouton New
            {
                url = getClass().getClassLoader().getResource("Images/bouton_save.png");
                BufferedImage img;
                img = ImageIO.read(url);
                icon_bouton_save = new ImageIcon(img);
                this.bouton_save.setIcon(icon_bouton_save);
            }
            // Bouton Interrogation
            {
                url = getClass().getClassLoader().getResource("Images/interogation.png");
                BufferedImage img;
                img = ImageIO.read(url);
                icon_interogation = new ImageIcon(img);
                this.bouton_about.setIcon(icon_interogation);
            }

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        bouton_save = new javax.swing.JButton();
        bouton_new = new javax.swing.JButton();
        bouton_open = new javax.swing.JButton();
        bouton_about = new javax.swing.JButton();
        jLabel_version = new javax.swing.JLabel();
        jLabel_NetBioDyn = new javax.swing.JLabel();
        bouton_lang = new javax.swing.JButton();
        jPanelEntities = new javax.swing.JPanel();
        jLabelEntites = new javax.swing.JLabel();
        jButtonAddEntity = new javax.swing.JButton();
        jButtonEditEntity = new javax.swing.JButton();
        jButtonDelEntity = new javax.swing.JButton();
        jScrollPane_Entities = new javax.swing.JScrollPane();
        dataGridView_entites = new javax.swing.JList();
        checkBox_paint_move = new javax.swing.JButton();
        checkBox_paint_stylo = new javax.swing.JButton();
        checkBox_paint_spray = new javax.swing.JButton();
        checkBox_paint_ligne = new javax.swing.JButton();
        checkBox_paint_random = new javax.swing.JButton();
        checkBox_paint_gomme = new javax.swing.JButton();
        jPanelBehaviors = new javax.swing.JPanel();
        jLabelComportements = new javax.swing.JLabel();
        jButtonAddBehav = new javax.swing.JButton();
        jButtonEditBehav = new javax.swing.JButton();
        jButtonDelBehav = new javax.swing.JButton();
        jScrollPaneBehaviors = new javax.swing.JScrollPane();
        dataGridView_comportements = new javax.swing.JList();
        jSliderProba = new javax.swing.JSlider();
        jLabelProba = new javax.swing.JLabel();
        jPanelEnv = new javax.swing.JPanel();
        jLabelEnvironnement = new javax.swing.JLabel();
        bouton_environnement = new javax.swing.JButton();
        bouton_vider = new javax.swing.JButton();
        jLabelZoom = new javax.swing.JLabel();
        trackBar_zoom = new javax.swing.JSlider();
        jLabelZ = new javax.swing.JLabel();
        jSliderZ = new javax.swing.JSlider();
        jPanelCurves = new javax.swing.JPanel();
        jLabelCourbes = new javax.swing.JLabel();
        pictureBox_courbes = new javax.swing.JLabel();
        label_courbe_y_max = new javax.swing.JLabel();
        jLabel_t0 = new javax.swing.JLabel();
        label_courbe_x_max = new javax.swing.JLabel();
        abscissaBox = new javax.swing.JComboBox();
        jPanelMisc = new javax.swing.JPanel();
        jLabelDivers = new javax.swing.JLabel();
        Ajustement = new javax.swing.JButton();
        bouton_export_model = new javax.swing.JButton();
        bouton_export_courbes = new javax.swing.JButton();
        checkBox_avi = new javax.swing.JCheckBox();
        checkBox_Flou = new javax.swing.JCheckBox();
        jButton3D = new javax.swing.JButton();
        jPanelSimulator = new javax.swing.JPanel();
        pictureBox_Env = new javax.swing.JLabel();
        jLabelSimulateur = new javax.swing.JLabel();
        button_play = new javax.swing.JButton();
        button_play1 = new javax.swing.JButton();
        button_init = new javax.swing.JButton();
        jLabelTps = new javax.swing.JLabel();
        labelTime = new javax.swing.JLabel();
        jLabelSpeed = new javax.swing.JLabel();
        jSliderSpeed = new javax.swing.JSlider();

        setBackground(new java.awt.Color(204, 204, 255));
        setAutoscrolls(true);
        setFocusCycleRoot(true);
        setPreferredSize(new java.awt.Dimension(1000, 550));
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                formComponentResized(evt);
            }
        });
        setLayout(null);

        bouton_save.setToolTipText("Save");
        bouton_save.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        bouton_save.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                bouton_saveMouseClicked(evt);
            }
        });
        add(bouton_save);
        bouton_save.setBounds(220, 5, 20, 20);

        bouton_new.setToolTipText("New");
        bouton_new.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        bouton_new.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                bouton_newMouseClicked(evt);
            }
        });
        add(bouton_new);
        bouton_new.setBounds(180, 5, 20, 20);

        bouton_open.setToolTipText("Load");
        bouton_open.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        bouton_open.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                bouton_openMouseClicked(evt);
            }
        });
        add(bouton_open);
        bouton_open.setBounds(200, 5, 20, 20);

        bouton_about.setFont(new java.awt.Font("DejaVu Sans", 0, 8)); // NOI18N
        bouton_about.setToolTipText("About NetBioDyn");
        bouton_about.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        bouton_about.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                bouton_aboutMouseClicked(evt);
            }
        });
        add(bouton_about);
        bouton_about.setBounds(310, 10, 20, 20);

        jLabel_version.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        jLabel_version.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel_version.setText("Version 05/2015 - University of Brest - Lab STICC- IHSEV");
        add(jLabel_version);
        jLabel_version.setBounds(600, 10, 270, 16);
        jLabel_version.getAccessibleContext().setAccessibleName("version 20/05/2012 - Universite de Brest - Lab-STICC- Equipe IHSEV");

        jLabel_NetBioDyn.setFont(new java.awt.Font("Chalkduster", 3, 20)); // NOI18N
        jLabel_NetBioDyn.setForeground(new java.awt.Color(102, 0, 153));
        jLabel_NetBioDyn.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel_NetBioDyn.setText("NetBioDyn");
        add(jLabel_NetBioDyn);
        jLabel_NetBioDyn.setBounds(0, 0, 160, 30);

        bouton_lang.setBackground(new java.awt.Color(255, 255, 255));
        bouton_lang.setFont(new java.awt.Font("DejaVu Sans", 0, 9)); // NOI18N
        bouton_lang.setText("English");
        bouton_lang.setToolTipText("Change language");
        bouton_lang.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        bouton_lang.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                bouton_langMouseClicked(evt);
            }
        });
        add(bouton_lang);
        bouton_lang.setBounds(240, 10, 70, 20);

        jPanelEntities.setBackground(new java.awt.Color(153, 153, 255));
        jPanelEntities.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanelEntities.setMinimumSize(new java.awt.Dimension(160, 230));
        jPanelEntities.setPreferredSize(new java.awt.Dimension(160, 230));

        jLabelEntites.setFont(new java.awt.Font("Lucida Grande", 1, 14)); // NOI18N
        jLabelEntites.setForeground(new java.awt.Color(102, 0, 153));
        jLabelEntites.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelEntites.setText("Entites");

        jButtonAddEntity.setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N
        jButtonAddEntity.setText("+");
        jButtonAddEntity.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAddEntityActionPerformed(evt);
            }
        });

        jButtonEditEntity.setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N
        jButtonEditEntity.setText("Edit");
        jButtonEditEntity.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonEditEntityActionPerformed(evt);
            }
        });

        jButtonDelEntity.setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N
        jButtonDelEntity.setText("-");
        jButtonDelEntity.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDelEntityActionPerformed(evt);
            }
        });

        dataGridView_entites.setFont(new java.awt.Font("DejaVu Sans", 0, 11)); // NOI18N
        dataGridView_entites.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        dataGridView_entites.setToolTipText("Created Entities");
        dataGridView_entites.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                dataGridView_entitesMouseClicked(evt);
            }
        });
        dataGridView_entites.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                dataGridView_entitesValueChanged(evt);
            }
        });
        dataGridView_entites.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                dataGridView_entitesPropertyChange(evt);
            }
        });
        jScrollPane_Entities.setViewportView(dataGridView_entites);

        checkBox_paint_move.setToolTipText("Move entities / View");
        checkBox_paint_move.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        checkBox_paint_move.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        checkBox_paint_move.setIconTextGap(0);
        checkBox_paint_move.setPreferredSize(new java.awt.Dimension(30, 30));
        checkBox_paint_move.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                checkBox_paint_moveMouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                checkBox_paint_moveMousePressed(evt);
            }
        });

        checkBox_paint_stylo.setToolTipText("Pen");
        checkBox_paint_stylo.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        checkBox_paint_stylo.setPreferredSize(new java.awt.Dimension(30, 30));
        checkBox_paint_stylo.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                checkBox_paint_styloMouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                checkBox_paint_styloMousePressed(evt);
            }
        });

        checkBox_paint_spray.setToolTipText("Spray");
        checkBox_paint_spray.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        checkBox_paint_spray.setPreferredSize(new java.awt.Dimension(30, 30));
        checkBox_paint_spray.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                checkBox_paint_sprayMousePressed(evt);
            }
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                checkBox_paint_sprayMouseClicked(evt);
            }
        });

        checkBox_paint_ligne.setToolTipText("Line");
        checkBox_paint_ligne.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        checkBox_paint_ligne.setPreferredSize(new java.awt.Dimension(30, 30));
        checkBox_paint_ligne.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                checkBox_paint_ligneMouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                checkBox_paint_ligneMousePressed(evt);
            }
        });

        checkBox_paint_random.setToolTipText("Random");
        checkBox_paint_random.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        checkBox_paint_random.setPreferredSize(new java.awt.Dimension(30, 30));
        checkBox_paint_random.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                checkBox_paint_randomMouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                checkBox_paint_randomMousePressed(evt);
            }
        });

        checkBox_paint_gomme.setToolTipText("Eraser");
        checkBox_paint_gomme.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        checkBox_paint_gomme.setPreferredSize(new java.awt.Dimension(30, 30));
        checkBox_paint_gomme.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                checkBox_paint_gommeMousePressed(evt);
            }
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                checkBox_paint_gommeMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanelEntitiesLayout = new javax.swing.GroupLayout(jPanelEntities);
        jPanelEntities.setLayout(jPanelEntitiesLayout);
        jPanelEntitiesLayout.setHorizontalGroup(
            jPanelEntitiesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelEntitiesLayout.createSequentialGroup()
                .addComponent(jButtonAddEntity, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonEditEntity, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonDelEntity, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE))
            .addGroup(jPanelEntitiesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelEntitiesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabelEntites, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanelEntitiesLayout.createSequentialGroup()
                        .addComponent(checkBox_paint_move, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(checkBox_paint_stylo, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(checkBox_paint_spray, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(checkBox_paint_ligne, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(checkBox_paint_random, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(checkBox_paint_gomme, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(jScrollPane_Entities, javax.swing.GroupLayout.Alignment.TRAILING)
        );
        jPanelEntitiesLayout.setVerticalGroup(
            jPanelEntitiesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelEntitiesLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabelEntites)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelEntitiesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jButtonAddEntity, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanelEntitiesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButtonDelEntity, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addComponent(jButtonEditEntity, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane_Entities, javax.swing.GroupLayout.DEFAULT_SIZE, 123, Short.MAX_VALUE)
                .addGap(12, 12, 12)
                .addGroup(jPanelEntitiesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(checkBox_paint_move, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(checkBox_paint_stylo, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(checkBox_paint_spray, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(checkBox_paint_ligne, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(checkBox_paint_random, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(checkBox_paint_gomme, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        add(jPanelEntities);
        jPanelEntities.setBounds(0, 30, 160, 230);

        jPanelBehaviors.setBackground(new java.awt.Color(153, 153, 255));
        jPanelBehaviors.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabelComportements.setFont(new java.awt.Font("Lucida Grande", 1, 14)); // NOI18N
        jLabelComportements.setForeground(new java.awt.Color(102, 0, 153));
        jLabelComportements.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelComportements.setText("Comportements");

        jButtonAddBehav.setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N
        jButtonAddBehav.setText("+");
        jButtonAddBehav.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAddBehavActionPerformed(evt);
            }
        });

        jButtonEditBehav.setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N
        jButtonEditBehav.setText("Edit");
        jButtonEditBehav.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonEditBehavActionPerformed(evt);
            }
        });

        jButtonDelBehav.setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N
        jButtonDelBehav.setText("-");
        jButtonDelBehav.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDelBehavActionPerformed(evt);
            }
        });

        dataGridView_comportements.setFont(new java.awt.Font("DejaVu Sans", 0, 11)); // NOI18N
        dataGridView_comportements.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        dataGridView_comportements.setToolTipText("Created Behaviours");
        dataGridView_comportements.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                dataGridView_comportementsMouseClicked(evt);
            }
        });
        dataGridView_comportements.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                dataGridView_comportementsValueChanged(evt);
            }
        });
        jScrollPaneBehaviors.setViewportView(dataGridView_comportements);

        jSliderProba.setBackground(new java.awt.Color(153, 153, 255));
        jSliderProba.setMaximum(1000);
        jSliderProba.setToolTipText("Proba of selected behaviour");
        jSliderProba.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jSliderProba.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jSliderProbaMouseReleased(evt);
            }
        });
        jSliderProba.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSliderProbaStateChanged(evt);
            }
        });

        jLabelProba.setBackground(new java.awt.Color(153, 153, 255));
        jLabelProba.setFont(new java.awt.Font("Lucida Grande", 0, 11)); // NOI18N
        jLabelProba.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelProba.setText("p=");
        jLabelProba.setToolTipText("Proba of selected behaviour");

        javax.swing.GroupLayout jPanelBehaviorsLayout = new javax.swing.GroupLayout(jPanelBehaviors);
        jPanelBehaviors.setLayout(jPanelBehaviorsLayout);
        jPanelBehaviorsLayout.setHorizontalGroup(
            jPanelBehaviorsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelBehaviorsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelBehaviorsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSliderProba, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jLabelProba, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
            .addComponent(jLabelComportements, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelBehaviorsLayout.createSequentialGroup()
                .addGroup(jPanelBehaviorsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPaneBehaviors, javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelBehaviorsLayout.createSequentialGroup()
                        .addComponent(jButtonAddBehav, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonEditBehav, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonDelBehav, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(2, 2, 2))
        );
        jPanelBehaviorsLayout.setVerticalGroup(
            jPanelBehaviorsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelBehaviorsLayout.createSequentialGroup()
                .addComponent(jLabelComportements, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelBehaviorsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButtonAddBehav, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanelBehaviorsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButtonEditBehav, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButtonDelBehav, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPaneBehaviors, javax.swing.GroupLayout.DEFAULT_SIZE, 108, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSliderProba, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelProba)
                .addContainerGap())
        );

        add(jPanelBehaviors);
        jPanelBehaviors.setBounds(0, 260, 160, 220);

        jPanelEnv.setBackground(new java.awt.Color(153, 153, 255));
        jPanelEnv.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabelEnvironnement.setFont(new java.awt.Font("Lucida Grande", 1, 14)); // NOI18N
        jLabelEnvironnement.setForeground(new java.awt.Color(102, 0, 153));
        jLabelEnvironnement.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelEnvironnement.setText("Environnement");

        bouton_environnement.setFont(new java.awt.Font("DejaVu Sans", 0, 10)); // NOI18N
        bouton_environnement.setText("Edit");
        bouton_environnement.setToolTipText("Modify the environment");
        bouton_environnement.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                bouton_environnementMouseClicked(evt);
            }
        });

        bouton_vider.setFont(new java.awt.Font("DejaVu Sans", 0, 10)); // NOI18N
        bouton_vider.setText("Vider");
        bouton_vider.setToolTipText("Clean the environment");
        bouton_vider.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                bouton_viderMouseClicked(evt);
            }
        });

        jLabelZoom.setFont(new java.awt.Font("Lucida Grande", 0, 12)); // NOI18N
        jLabelZoom.setForeground(new java.awt.Color(102, 0, 153));
        jLabelZoom.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabelZoom.setText("Zoom");

        trackBar_zoom.setBackground(new java.awt.Color(153, 153, 255));
        trackBar_zoom.setMinimum(1);
        trackBar_zoom.setPaintTicks(true);
        trackBar_zoom.setToolTipText("Zoom");
        trackBar_zoom.setValue(1);
        trackBar_zoom.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                trackBar_zoomMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                trackBar_zoomMouseReleased(evt);
            }
        });
        trackBar_zoom.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                trackBar_zoomMouseDragged(evt);
            }
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                trackBar_zoomMouseMoved(evt);
            }
        });

        jLabelZ.setFont(new java.awt.Font("Lucida Grande", 0, 12)); // NOI18N
        jLabelZ.setForeground(new java.awt.Color(102, 0, 153));
        jLabelZ.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabelZ.setText("Z=0");

        jSliderZ.setBackground(new java.awt.Color(153, 153, 255));
        jSliderZ.setMaximum(10);
        jSliderZ.setPaintTicks(true);
        jSliderZ.setSnapToTicks(true);
        jSliderZ.setValue(0);
        jSliderZ.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSliderZStateChanged(evt);
            }
        });

        javax.swing.GroupLayout jPanelEnvLayout = new javax.swing.GroupLayout(jPanelEnv);
        jPanelEnv.setLayout(jPanelEnvLayout);
        jPanelEnvLayout.setHorizontalGroup(
            jPanelEnvLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelEnvLayout.createSequentialGroup()
                .addGroup(jPanelEnvLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelEnvLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabelEnvironnement, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanelEnvLayout.createSequentialGroup()
                        .addComponent(bouton_environnement, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(bouton_vider, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(29, 29, 29)
                .addGroup(jPanelEnvLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabelZ, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabelZoom, javax.swing.GroupLayout.DEFAULT_SIZE, 52, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelEnvLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(trackBar_zoom, javax.swing.GroupLayout.DEFAULT_SIZE, 333, Short.MAX_VALUE)
                    .addComponent(jSliderZ, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanelEnvLayout.setVerticalGroup(
            jPanelEnvLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelEnvLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelEnvLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanelEnvLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabelEnvironnement, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabelZ, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jSliderZ, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelEnvLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelEnvLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(bouton_environnement, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(bouton_vider, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabelZoom, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(trackBar_zoom, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        add(jPanelEnv);
        jPanelEnv.setBounds(0, 480, 590, 70);

        jPanelCurves.setBackground(new java.awt.Color(153, 153, 255));
        jPanelCurves.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabelCourbes.setFont(new java.awt.Font("Lucida Grande", 1, 14)); // NOI18N
        jLabelCourbes.setForeground(new java.awt.Color(102, 0, 153));
        jLabelCourbes.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelCourbes.setText("Courbes");

        pictureBox_courbes.setBackground(java.awt.Color.white);
        pictureBox_courbes.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        pictureBox_courbes.setOpaque(true);
        pictureBox_courbes.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                pictureBox_courbesMouseMoved(evt);
            }
        });

        label_courbe_y_max.setFont(new java.awt.Font("DejaVu Sans", 0, 11)); // NOI18N
        label_courbe_y_max.setText("0");

        jLabel_t0.setFont(new java.awt.Font("DejaVu Sans", 0, 11)); // NOI18N
        jLabel_t0.setText("0");

        label_courbe_x_max.setFont(new java.awt.Font("DejaVu Sans", 0, 11)); // NOI18N
        label_courbe_x_max.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_courbe_x_max.setText("0");

        abscissaBox.setFont(new java.awt.Font("Tahoma", 2, 10)); // NOI18N
        abscissaBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        abscissaBox.setToolTipText("Change the abscissa");
        abscissaBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                abscissaBoxActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelCurvesLayout = new javax.swing.GroupLayout(jPanelCurves);
        jPanelCurves.setLayout(jPanelCurvesLayout);
        jPanelCurvesLayout.setHorizontalGroup(
            jPanelCurvesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelCurvesLayout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addComponent(jLabel_t0, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(48, 48, 48)
                .addComponent(abscissaBox, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(label_courbe_x_max, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelCurvesLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(pictureBox_courbes, javax.swing.GroupLayout.PREFERRED_SIZE, 266, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelCurvesLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(label_courbe_y_max, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabelCourbes, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(98, 98, 98))
        );
        jPanelCurvesLayout.setVerticalGroup(
            jPanelCurvesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelCurvesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelCurvesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelCourbes, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_courbe_y_max))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pictureBox_courbes, javax.swing.GroupLayout.PREFERRED_SIZE, 325, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addGroup(jPanelCurvesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel_t0)
                    .addComponent(label_courbe_x_max)
                    .addComponent(abscissaBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        abscissaBox.getAccessibleContext().setAccessibleName("");

        add(jPanelCurves);
        jPanelCurves.setBounds(590, 30, 280, 390);

        jPanelMisc.setBackground(new java.awt.Color(153, 153, 255));
        jPanelMisc.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabelDivers.setFont(new java.awt.Font("Lucida Grande", 1, 14)); // NOI18N
        jLabelDivers.setForeground(new java.awt.Color(102, 0, 153));
        jLabelDivers.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelDivers.setText("Divers");

        Ajustement.setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N
        Ajustement.setText("Ajustement des parametres");
        Ajustement.setToolTipText("Self-adaptive parameters tool");
        Ajustement.setEnabled(false);
        Ajustement.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                LaunchSelfAdjust(evt);
            }
        });

        bouton_export_model.setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N
        bouton_export_model.setText("Export modele");
        bouton_export_model.setToolTipText("Export the model (text)");
        bouton_export_model.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                bouton_export_modelMouseClicked(evt);
            }
        });

        bouton_export_courbes.setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N
        bouton_export_courbes.setText("Export courbes");
        bouton_export_courbes.setToolTipText("Export curves (text)");
        bouton_export_courbes.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                bouton_export_courbesMouseClicked(evt);
            }
        });

        checkBox_avi.setBackground(new java.awt.Color(153, 153, 255));
        checkBox_avi.setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N
        checkBox_avi.setText("Filmer");
        checkBox_avi.setToolTipText("Record an animated GIF");
        checkBox_avi.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                checkBox_aviMouseClicked(evt);
            }
        });

        checkBox_Flou.setBackground(new java.awt.Color(153, 153, 255));
        checkBox_Flou.setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N
        checkBox_Flou.setText("Flouter");
        checkBox_Flou.setToolTipText("Follow the entity movements");

        jButton3D.setBackground(new java.awt.Color(255, 255, 51));
        jButton3D.setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N
        jButton3D.setText("3D <=>");
        jButton3D.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3DActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelMiscLayout = new javax.swing.GroupLayout(jPanelMisc);
        jPanelMisc.setLayout(jPanelMiscLayout);
        jPanelMiscLayout.setHorizontalGroup(
            jPanelMiscLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelMiscLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelMiscLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelMiscLayout.createSequentialGroup()
                        .addGroup(jPanelMiscLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(jPanelMiscLayout.createSequentialGroup()
                                .addComponent(checkBox_avi, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(checkBox_Flou, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(bouton_export_model, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanelMiscLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jButton3D, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(bouton_export_courbes, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 6, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelMiscLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jLabelDivers, javax.swing.GroupLayout.PREFERRED_SIZE, 238, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelMiscLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(Ajustement, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(36, 36, 36))
        );
        jPanelMiscLayout.setVerticalGroup(
            jPanelMiscLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelMiscLayout.createSequentialGroup()
                .addGap(4, 4, 4)
                .addComponent(jLabelDivers, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(Ajustement, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelMiscLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(bouton_export_model, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(bouton_export_courbes, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jPanelMiscLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelMiscLayout.createSequentialGroup()
                        .addGap(13, 13, 13)
                        .addGroup(jPanelMiscLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(checkBox_avi, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(checkBox_Flou, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanelMiscLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton3D)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        add(jPanelMisc);
        jPanelMisc.setBounds(590, 420, 280, 130);

        jPanelSimulator.setBackground(new java.awt.Color(153, 153, 255));
        jPanelSimulator.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanelSimulator.setMinimumSize(new java.awt.Dimension(430, 455));

        pictureBox_Env.setBackground(new java.awt.Color(255, 255, 255));
        pictureBox_Env.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        pictureBox_Env.setMaximumSize(new java.awt.Dimension(410, 410));
        pictureBox_Env.setMinimumSize(new java.awt.Dimension(410, 410));
        pictureBox_Env.setOpaque(true);
        pictureBox_Env.addMouseWheelListener(new java.awt.event.MouseWheelListener() {
            public void mouseWheelMoved(java.awt.event.MouseWheelEvent evt) {
                pictureBox_EnvMouseWheelMoved(evt);
            }
        });
        pictureBox_Env.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                pictureBox_EnvMouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                pictureBox_EnvMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                pictureBox_EnvMouseReleased(evt);
            }
        });
        pictureBox_Env.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                pictureBox_EnvMouseDragged(evt);
            }
        });

        jLabelSimulateur.setFont(new java.awt.Font("Lucida Grande", 1, 14)); // NOI18N
        jLabelSimulateur.setForeground(new java.awt.Color(102, 0, 153));
        jLabelSimulateur.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelSimulateur.setText("Simulateur");

        button_play.setBackground(new java.awt.Color(255, 255, 255));
        button_play.setToolTipText("Play (ENTER)");
        button_play.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        button_play.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                button_playMouseClicked(evt);
            }
        });

        button_play1.setBackground(new java.awt.Color(255, 255, 255));
        button_play1.setToolTipText("Play 1 step");
        button_play1.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        button_play1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                button_play1MouseClicked(evt);
            }
        });

        button_init.setToolTipText("Stop (ESCAPE)");
        button_init.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        button_init.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                button_initMouseClicked(evt);
            }
        });

        jLabelTps.setBackground(new java.awt.Color(248, 241, 181));
        jLabelTps.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        jLabelTps.setText("Tps =");
        jLabelTps.setToolTipText("Nbr of time steps");

        labelTime.setBackground(new java.awt.Color(248, 241, 181));
        labelTime.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        labelTime.setText("0");
        labelTime.setToolTipText("Nbr of time steps");

        jLabelSpeed.setFont(new java.awt.Font("Lucida Grande", 2, 10)); // NOI18N
        jLabelSpeed.setForeground(new java.awt.Color(102, 0, 153));
        jLabelSpeed.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabelSpeed.setText("Speed");
        jLabelSpeed.setToolTipText("Simulation's speed : from 1s to 1ms between time steps");

        jSliderSpeed.setBackground(new java.awt.Color(153, 153, 255));
        jSliderSpeed.setForeground(new java.awt.Color(153, 153, 255));
        jSliderSpeed.setMajorTickSpacing(1);
        jSliderSpeed.setMaximum(3);
        jSliderSpeed.setMinorTickSpacing(1);
        jSliderSpeed.setSnapToTicks(true);
        jSliderSpeed.setToolTipText("Simulation's speed : from 1s to 1ms between time steps");
        jSliderSpeed.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jSliderSpeedMouseReleased(evt);
            }
        });

        javax.swing.GroupLayout jPanelSimulatorLayout = new javax.swing.GroupLayout(jPanelSimulator);
        jPanelSimulator.setLayout(jPanelSimulatorLayout);
        jPanelSimulatorLayout.setHorizontalGroup(
            jPanelSimulatorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pictureBox_Env, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanelSimulatorLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabelSimulateur, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, 58, Short.MAX_VALUE)
                .addGroup(jPanelSimulatorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabelSpeed, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jSliderSpeed, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(button_play, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(button_play1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(button_init, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(jLabelTps, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(labelTime, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanelSimulatorLayout.setVerticalGroup(
            jPanelSimulatorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelSimulatorLayout.createSequentialGroup()
                .addGroup(jPanelSimulatorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelSimulatorLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanelSimulatorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanelSimulatorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(jLabelSimulateur, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(labelTime, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(button_play, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(button_play1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(button_init, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabelTps, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanelSimulatorLayout.createSequentialGroup()
                        .addComponent(jLabelSpeed)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSliderSpeed, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pictureBox_Env, javax.swing.GroupLayout.DEFAULT_SIZE, 416, Short.MAX_VALUE))
        );

        add(jPanelSimulator);
        jPanelSimulator.setBounds(160, 30, 430, 455);
    }// </editor-fold>//GEN-END:initComponents

    private void button_play1MouseClicked(java.awt.event.MouseEvent evt) {
        if (_time == 0) {
            memoriserCourbes();
            dessinerCourbes();
        }
        controller.play_one();
    }

    private void button_playMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_button_playMouseClicked
        controller.play();
    }//GEN-LAST:event_button_playMouseClicked

    private void checkBox_paint_moveMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_checkBox_paint_moveMouseClicked
        releverToutesCheckBoxPaint(checkBox_paint_move);
	}//GEN-LAST:event_checkBox_paint_moveMouseClicked

    private void checkBox_paint_styloMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_checkBox_paint_styloMouseClicked
        releverToutesCheckBoxPaint(checkBox_paint_stylo);
    }//GEN-LAST:event_checkBox_paint_styloMouseClicked

    private void checkBox_paint_sprayMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_checkBox_paint_sprayMouseClicked
        releverToutesCheckBoxPaint(checkBox_paint_spray);
    }//GEN-LAST:event_checkBox_paint_sprayMouseClicked

    private void checkBox_paint_gommeMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_checkBox_paint_gommeMouseClicked
        releverToutesCheckBoxPaint(checkBox_paint_gomme);
    }//GEN-LAST:event_checkBox_paint_gommeMouseClicked


    private void button_initMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_button_initMouseClicked
        controller.stop();
    }//GEN-LAST:event_button_initMouseClicked

    private void trackBar_zoomMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_trackBar_zoomMousePressed
        _mouse_zoom_down = true;
        timer_refresh.start();
    }//GEN-LAST:event_trackBar_zoomMousePressed

    private void trackBar_zoomMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_trackBar_zoomMouseReleased
        _mouse_zoom_down = false;
        timer_refresh.stop();
    }//GEN-LAST:event_trackBar_zoomMouseReleased

    private void trackBar_zoomMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_trackBar_zoomMouseDragged
        zoomAgain();
    }//GEN-LAST:event_trackBar_zoomMouseDragged

    private void trackBar_zoomMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_trackBar_zoomMouseMoved
        if (_mouse_zoom_down == true) {
            zoomAgain();
        }
    }//GEN-LAST:event_trackBar_zoomMouseMoved

    private void bouton_viderMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_bouton_viderMouseClicked
        controller.clearEnvironment();
    }//GEN-LAST:event_bouton_viderMouseClicked

    private void bouton_newMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_bouton_newMouseClicked
        controller.newModel();
    }//GEN-LAST:event_bouton_newMouseClicked

    private void bouton_environnementMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_bouton_environnementMouseClicked
        controller.updateEnvParameters();
	}//GEN-LAST:event_bouton_environnementMouseClicked

    private void bouton_saveMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_bouton_saveMouseClicked
        controller.saveModel(_nom_sauvegarde);
    }//GEN-LAST:event_bouton_saveMouseClicked

    private void bouton_openMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_bouton_openMouseClicked
        controller.loadModel(_nom_sauvegarde);
    }//GEN-LAST:event_bouton_openMouseClicked

    private void bouton_export_modelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_bouton_export_modelMouseClicked
        controller.export();
    }//GEN-LAST:event_bouton_export_modelMouseClicked

    private void bouton_aboutMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_bouton_aboutMouseClicked
        controller.about();
    }//GEN-LAST:event_bouton_aboutMouseClicked

    private void checkBox_paint_ligneMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_checkBox_paint_ligneMouseClicked
        releverToutesCheckBoxPaint(checkBox_paint_ligne);
	}//GEN-LAST:event_checkBox_paint_ligneMouseClicked

    private void deplacer_vue(java.awt.event.MouseEvent evt) {
        if (_mouse_left_down == true) {
            if (checkBox_paint_move.getBackground().equals(Color.GREEN)) { //.getX() == jLabel_sel.getX()) {
                float dx_fenetre = (_x0_mouse_down - evt.getX());// / pictureBox1.Width;
                float dy_fenetre = (_y0_mouse_down - evt.getY());// / pictureBox1.Height;

                float dx_univers = deltaWindowToUniverse(_observed_left, _observed_left + _observed_width, pictureBox_Env.getWidth(), dx_fenetre);
                float dy_univers = deltaWindowToUniverse(_observed_top, _observed_top + _observed_height, pictureBox_Env.getHeight(), dy_fenetre);

                if (_observed_left + dx_univers < 0) {
                    dx_univers = -_observed_left;
                }
                if (_observed_top + dy_univers < 0) {
                    dy_univers = -_observed_top;
                }
                if (_observed_left + _observed_width + dx_univers > getTailleX()) {
                    dx_univers = getTailleX() - (_observed_left + _observed_width);
                }
                if (_observed_top + _observed_height + dy_univers > getTailleY()) {
                    dy_univers = getTailleY() - (_observed_top + _observed_height);
                }

                boolean deplace_vue = false;
                if (_cubes_selectionnes == null) {
                    deplace_vue = true;
                } else {
                    if (_cubes_selectionnes.isEmpty()) {
                        deplace_vue = true;
                    }
                }
                if (deplace_vue) {
                    _observed_left += dx_univers;
                    _observed_top += dy_univers;
                }
                _x0_mouse_down = evt.getX();
                _y0_mouse_down = evt.getY();
            }

            if (getDataGridView_entites().getSelectedIndex() < 0) {
                dataGridView_entites.setSelectedIndex(0);
            }

            if (checkBox_paint_stylo.getBackground().equals(Color.GREEN)) {
                Paint_Stylo(evt.getX(), evt.getY());
            }
            if (checkBox_paint_spray.getBackground().equals(Color.GREEN)) {
                Paint_Spray(evt.getX(), evt.getY());
            }
            if (checkBox_paint_ligne.getBackground().equals(Color.GREEN)) {
                Paint_Ligne(evt.getX(), evt.getY());
            }
            if (checkBox_paint_random.getBackground().equals(Color.GREEN)) {
                Paint_Aleat(evt.getX(), evt.getY());
            }
            if (checkBox_paint_gomme.getBackground().equals(Color.GREEN)) {
                Paint_Gomme(evt.getX(), evt.getY());
            }
        }
    }


    private void pictureBox_EnvMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pictureBox_EnvMouseDragged
        if (getCubes_selectionnes() != null) {
            if (!getCubes_selectionnes().isEmpty()) {
                setMovingCubes(true);
            } else {
                deplacer_vue(evt);
            }
        } else {
            deplacer_vue(evt);
        }
    }//GEN-LAST:event_pictureBox_EnvMouseDragged

    private void pictureBox_EnvMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pictureBox_EnvMouseReleased
        //// De-memorisation du cube lego pris
        _mouse_left_down = false;
        _mouse_right_down = false;

        timer_refresh.stop();

        // Ajout d'un lien ou tracage d'une ligne de reaxels
        if (checkBox_paint_ligne.getBackground().equals(Color.GREEN)) { //.getX() == jLabel_sel.getX()) {
            if (dataGridView_entites.getSelectedIndex() >= 0) {
                int mouseX = evt.getX();
                int mouseY = evt.getY();
                int new_x = (int) windowToUniverse(_observed_left, _observed_left + _observed_width, pictureBox_Env.getWidth(), mouseX);
                int new_y = (int) windowToUniverse(_observed_top, _observed_top + _observed_height, pictureBox_Env.getHeight(), mouseY);
                int new_z = jSliderZ.getValue();
                if (new_x >= 0 && new_y >= 0 && new_x < getTailleX() && new_y < getTailleY()) {
                    ArrayList<UtilPoint3D> lst_pts = UtilPoint3D.BresenhamLigne3D(_case_x0, _case_y0, new_z, new_x, new_y, new_z);
                    controller.addEntityInstances(lst_pts);
                }
            }
        } else if (checkBox_paint_random.getBackground().equals(Color.GREEN)) {
            if (dataGridView_entites.getSelectedIndex() >= 0) {
                int mouseX = evt.getX();
                int mouseY = evt.getY();
                int new_x = (int) windowToUniverse(_observed_left, _observed_left + _observed_width, pictureBox_Env.getWidth(), mouseX);
                int new_y = (int) windowToUniverse(_observed_top, _observed_top + _observed_height, pictureBox_Env.getHeight(), mouseY);
                int new_z = jSliderZ.getValue();
                if (new_x > getTailleX()) {
                    new_x = getTailleX();
                }
                if (new_y > getTailleY()) {
                    new_y = getTailleY();
                }
                if ((mouseX == new_x) && (mouseY == new_y)) {
                    mouseX = 0;
                    new_x = getTailleX();
                    mouseY = 0;
                    new_y = getTailleY();
                }
                controller.randomlyPopulate(new_x, new_y, _case_x0, _case_y0, new_z);
            }
        } else if (isMovingCubes()) {
            int new_x = (int) windowToUniverse(_observed_left, _observed_left + _observed_width, pictureBox_Env.getWidth(), evt.getX());
            int new_y = (int) windowToUniverse(_observed_top, _observed_top + _observed_height, pictureBox_Env.getHeight(), evt.getY());
            int new_z = UtilPoint3D.centreDeGravite(getCubes_selectionnes()).z;
            if (new_x >= 0 && new_y >= 0 && new_x < getTailleX() && new_y < getTailleY()) {
                this.setMovingCubes(false);
                controller.deplacer(getCubes_selectionnes(), new_x, new_y, new_z);
            }
        }
    }//GEN-LAST:event_pictureBox_EnvMouseReleased

    private void pictureBox_EnvMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pictureBox_EnvMousePressed
        if (evt.getButton() == MouseEvent.BUTTON1 && _mouse_left_down == false) {
            _x0_mouse_down = evt.getX();
            _y0_mouse_down = evt.getY();
            _mouse_left_down = true;
            timer_refresh.start();
        }
        if (evt.getButton() == MouseEvent.BUTTON3 && _mouse_right_down == false) {
            _x0_mouse_down = evt.getX();
            _y0_mouse_down = evt.getY();
            _mouse_right_down = true;
            timer_refresh.start();
        }

        float x_univers = windowToUniverse(_observed_left, _observed_left + _observed_width, pictureBox_Env.getWidth(), evt.getX());
        float y_univers = windowToUniverse(_observed_top, _observed_top + _observed_height, pictureBox_Env.getHeight(), evt.getY());
        if (x_univers >= getTailleX() || y_univers >= getTailleY() || x_univers < 0 || y_univers < 0) {
            return;
        }
        //// Memorisation du possible reaxel pris
        _case_x0 = (int) (x_univers);
        _case_y0 = (int) (y_univers);
        if (checkBox_paint_move.getBackground().equals(Color.GREEN)) { //.getX() == jLabel_sel.getX() /*|| checkBox_paint_ligne.getBackground().equals(Color.GREEN)*/) {
            controller.select(_case_x0, _case_y0, jSliderZ.getValue());
        }
    }//GEN-LAST:event_pictureBox_EnvMousePressed

    public void unselect(InstanceReaxel r) {
        if (_cubes_selectionnes == null || r == null) {
            return;
        }
        _cubes_selectionnes.remove(r);
    }


    private void pictureBox_EnvMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pictureBox_EnvMouseClicked
        if (evt.getButton() == java.awt.event.MouseEvent.BUTTON1) {
            if (getDataGridView_entites().getSelectedIndex() < 0) {
                dataGridView_entites.setSelectedIndex(0);
            }

            if ((evt.getClickCount() == 2) && (checkBox_paint_random.getBackground().equals(Color.GREEN))) {
                controller.randomlyPopulate(0, 0, getTailleX(), getTailleY(), jSliderZ.getValue());
            }

            if (checkBox_paint_stylo.getBackground().equals(Color.GREEN)) { //.getX() == jLabel_sel.getX()) {
                Paint_Stylo(evt.getX(), evt.getY());
            }
            if (checkBox_paint_spray.getBackground().equals(Color.GREEN)) { //.getX() == jLabel_sel.getX()) {
                Paint_Spray(evt.getX(), evt.getY());
            }
            if (checkBox_paint_gomme.getBackground().equals(Color.GREEN)) { //.getX() == jLabel_sel.getX()) {
                Paint_Gomme(evt.getX(), evt.getY());
            }
        } else if (evt.getButton() == java.awt.event.MouseEvent.BUTTON3) {
            afficherInformations(evt.getX(), evt.getY());
        }
    }//GEN-LAST:event_pictureBox_EnvMouseClicked

    private void checkBox_aviMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_checkBox_aviMouseClicked
        if (checkBox_avi.isSelected()) {
            controller.record_animation();
        } else {
            if (Lang.getInstance().getLang().equals("FR")) {
                JOptionPane.showMessageDialog(this, "Animation enregistree.");
            } else {
                JOptionPane.showMessageDialog(this, "Animation created");
            }
            if (_animation_gif != null) {
                _animation_gif.finish();
                _animation_gif = null;
            }
        }
    }//GEN-LAST:event_checkBox_aviMouseClicked


    private void bouton_export_courbesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_bouton_export_courbesMouseClicked
        controller.exportCurves(_nom_sauvegarde, curves);
    }//GEN-LAST:event_bouton_export_courbesMouseClicked

    private void checkBox_paint_moveMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_checkBox_paint_moveMousePressed
        releverToutesCheckBoxPaint(checkBox_paint_move);
    }//GEN-LAST:event_checkBox_paint_moveMousePressed

    private void checkBox_paint_styloMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_checkBox_paint_styloMousePressed
        releverToutesCheckBoxPaint(checkBox_paint_stylo);
    }//GEN-LAST:event_checkBox_paint_styloMousePressed

    private void checkBox_paint_sprayMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_checkBox_paint_sprayMousePressed
        releverToutesCheckBoxPaint(checkBox_paint_spray);
    }//GEN-LAST:event_checkBox_paint_sprayMousePressed

    private void checkBox_paint_ligneMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_checkBox_paint_ligneMousePressed
        releverToutesCheckBoxPaint(checkBox_paint_ligne);
    }//GEN-LAST:event_checkBox_paint_ligneMousePressed

    private void checkBox_paint_gommeMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_checkBox_paint_gommeMousePressed
        releverToutesCheckBoxPaint(checkBox_paint_gomme);
    }//GEN-LAST:event_checkBox_paint_gommeMousePressed

    private void dataGridView_entitesPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_dataGridView_entitesPropertyChange
        dessinerCourbes();
    }//GEN-LAST:event_dataGridView_entitesPropertyChange

    private void dataGridView_entitesValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_dataGridView_entitesValueChanged
        dessinerCourbes();
    }//GEN-LAST:event_dataGridView_entitesValueChanged

    private void jButtonAddEntityActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAddEntityActionPerformed
        controller.addEntity();
    }//GEN-LAST:event_jButtonAddEntityActionPerformed


    private void jButtonDelEntityActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDelEntityActionPerformed
        controller.delEntity(dataGridView_entites.getSelectedIndices());
    }//GEN-LAST:event_jButtonDelEntityActionPerformed

    private void jButtonEditEntityActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonEditEntityActionPerformed
        controller.editEntity();
    }//GEN-LAST:event_jButtonEditEntityActionPerformed

    private void dataGridView_comportementsValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_dataGridView_comportementsValueChanged
        int i = dataGridView_comportements.getSelectedIndex();
        if (i < 0) {
            return;
        }
        String nom_moteur = dataGridView_comportements.getSelectedValue().toString();
        Behavior moteur = getBehaviour(nom_moteur);
        jSliderProba.setValue((int) (moteur.get_k() * 1000));
        setProba(moteur.get_k());
    }//GEN-LAST:event_dataGridView_comportementsValueChanged

    private void jButtonAddBehavActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAddBehavActionPerformed
        controller.addBehaviour();
    }//GEN-LAST:event_jButtonAddBehavActionPerformed

    private void jButtonEditBehavActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonEditBehavActionPerformed
        controller.editBehaviour();
    }//GEN-LAST:event_jButtonEditBehavActionPerformed


    private void jButtonDelBehavActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDelBehavActionPerformed
        int[] tab = dataGridView_comportements.getSelectedIndices();
        controller.delBehaviour(tab);
    }//GEN-LAST:event_jButtonDelBehavActionPerformed

    private void jSliderZStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSliderZStateChanged
        setZLabel();
        drawAll(0, 0, 0, 0, 0);
    }//GEN-LAST:event_jSliderZStateChanged

    private void jSliderProbaStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSliderProbaStateChanged
        int i = dataGridView_comportements.getSelectedIndex();
        if (i >= 0) {
            jLabelProba.setText("p=" + (jSliderProba.getValue() / 10) / 100.0);
        }
    }//GEN-LAST:event_jSliderProbaStateChanged

    private void dataGridView_entitesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_dataGridView_entitesMouseClicked
        if (evt.getClickCount() == 2 && !evt.isConsumed() && !freezed) {
            evt.consume();
            controller.editEntity();
        }
    }//GEN-LAST:event_dataGridView_entitesMouseClicked

    private void dataGridView_comportementsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_dataGridView_comportementsMouseClicked
        if (evt.getClickCount() == 2 && !evt.isConsumed() && !freezed) {
            evt.consume();
            controller.editBehaviour();
        }
    }//GEN-LAST:event_dataGridView_comportementsMouseClicked

    private void initAbscissaBox() {
        abscissaBox.removeAllItems();
        if (Lang.getInstance().getLang().equals("FR")) {
            abscissaBox.addItem("Temps");
        } else {
            abscissaBox.addItem("Time");
        }
        for (Entity r : _ListManipulesNoeuds) {
            abscissaBox.addItem(r.getEtiquettes());
        }
    }

    private void initLanguage() {
        if (Lang.getInstance().getLang().equals("EN")) {
            if (freezed) {
                Ajustement.setText("Adjusting in progress ...");
            } else {
                Ajustement.setText("Self-adjusting parameters");
            }
            bouton_lang.setText("Français");
            jLabelSpeed.setText("Speed");
            jLabelEntites.setText("Entities");
            jLabelComportements.setText("Behaviours");
            jLabelEnvironnement.setText("Environment");
            jLabelSimulateur.setText("Simulator");
            jLabelCourbes.setText("Curves");
            jLabelDivers.setText("Miscellaneous");
            bouton_export_model.setText("Export model");
            bouton_export_courbes.setText("Export curves");
            checkBox_avi.setText("Record");
            checkBox_Flou.setText("Blur");
            jLabelTps.setText("Time:");
            bouton_vider.setText("Clean");
        } else {
            bouton_lang.setText("English");
            if (freezed) {
                Ajustement.setText("Auto-ajustement en cours ...");
            } else {
                Ajustement.setText("Auto-ajustement des paramètres");
            }
            jLabelSpeed.setText("Vitesse");
            jLabelEntites.setText("Entités");
            jLabelComportements.setText("Comportements");
            jLabelEnvironnement.setText("Environnement");
            jLabelSimulateur.setText("Simulateur");
            jLabelCourbes.setText("Courbes");
            jLabelDivers.setText("Divers");
            bouton_export_model.setText("Exporter modèle");
            bouton_export_courbes.setText("Exporter courbes");
            checkBox_avi.setText("Filmer");
            checkBox_Flou.setText("Flouter");
            jLabelTps.setText("Tps =");
            bouton_vider.setText("Vider");
        }
    }

    private void bouton_langMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_bouton_langMouseClicked
        Lang.getInstance().changeLang();
        initLanguage();
    }//GEN-LAST:event_bouton_langMouseClicked

    private void LaunchSelfAdjust(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_LaunchSelfAdjust
        controller.launchSelfAdjustment();
    }//GEN-LAST:event_LaunchSelfAdjust

    private void pictureBox_courbesMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pictureBox_courbesMouseMoved
        drawCoordinates();
    }//GEN-LAST:event_pictureBox_courbesMouseMoved

    private void formComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentResized
        // First display on screen
        if (init_width == -1) {
            // Main panel init size
            init_width = getBounds().width;
            init_height = getBounds().height;
            // Simulator panel init size
            init_bounds_sim = (Rectangle) jPanelSimulator.getBounds().clone();
            init_bounds_env = (Rectangle) jPanelEnv.getBounds().clone();
            init_bounds_ent = (Rectangle) jPanelEntities.getBounds().clone();
            init_bounds_beh = (Rectangle) jPanelBehaviors.getBounds().clone();
            init_bounds_curv = (Rectangle) jPanelCurves.getBounds().clone();
            init_bounds_misc = (Rectangle) jPanelMisc.getBounds().clone();
            return;
        }

        // Move/resize the panels according to the size of the main window
        int dfx; // delta frame size x
        int dfy; // delta frame size y
        // Minimum size reached
        if (getBounds().width < init_width) {
            dfx = 0;
        } else {
            dfx = getBounds().width - init_width;//Main.FRAME_WIDTH;
        }
        if (getBounds().height < init_height) {
            dfy = 0;
        } else {
            dfy = getBounds().height - init_height; // Main.FRAME_HEIGHT;
        }

        // Move the Panels
        jPanelSimulator.setBounds(init_bounds_sim.x, init_bounds_sim.y, init_bounds_sim.width + dfx, init_bounds_sim.height + dfy);
        jPanelEnv.setBounds(init_bounds_env.x, init_bounds_env.y + dfy, init_bounds_env.width + dfx, init_bounds_env.height);
        jPanelEntities.setBounds(init_bounds_ent.x, init_bounds_ent.y, init_bounds_ent.width, init_bounds_ent.height + dfy / 2);
        jPanelBehaviors.setBounds(init_bounds_beh.x, init_bounds_beh.y + dfy / 2, init_bounds_beh.width, init_bounds_beh.height + dfy / 2);
        jPanelCurves.setBounds(init_bounds_curv.x + dfx, init_bounds_curv.y, init_bounds_curv.width, init_bounds_curv.height + dfy / 2);
        jPanelMisc.setBounds(init_bounds_misc.x + dfx, init_bounds_misc.y + dfy / 2, init_bounds_misc.width, init_bounds_misc.height + dfy / 2);

        jPanelSimulator.validate();
        jPanelCurves.validate();
        jPanelMisc.validate();

        initBufferedImageSimulator();
        drawAll(0, 0, 0, 0, 0);
    }//GEN-LAST:event_formComponentResized


    private void jButton3DActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3DActionPerformed
        controller.hideShow3DView();
    }//GEN-LAST:event_jButton3DActionPerformed

    private void checkBox_paint_randomMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_checkBox_paint_randomMouseClicked
        releverToutesCheckBoxPaint(checkBox_paint_random);
    }//GEN-LAST:event_checkBox_paint_randomMouseClicked

    private void checkBox_paint_randomMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_checkBox_paint_randomMousePressed
        releverToutesCheckBoxPaint(checkBox_paint_random);
    }//GEN-LAST:event_checkBox_paint_randomMousePressed

    private void jSliderProbaMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jSliderProbaMouseReleased
        int i = dataGridView_comportements.getSelectedIndex();
        if (i >= 0) {
            String nom_moteur = dataGridView_comportements.getSelectedValue().toString();
            Double value = ((jSliderProba.getValue() / 10) / 100.0);
            controller.changeProba(nom_moteur, value);
        }
    }//GEN-LAST:event_jSliderProbaMouseReleased

    private void jSliderSpeedMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jSliderSpeedMouseReleased
        controller.changeSpeed(jSliderSpeed.getValue());
    }//GEN-LAST:event_jSliderSpeedMouseReleased

    private void pictureBox_EnvMouseWheelMoved(java.awt.event.MouseWheelEvent evt) {//GEN-FIRST:event_pictureBox_EnvMouseWheelMoved
        int zoom = evt.getWheelRotation();
        int value = trackBar_zoom.getValue();
        if (zoom < 0) {
            zoom = zoom * -1;
            value = zoom + value;
            if (value > trackBar_zoom.getMaximum()) {
                value = trackBar_zoom.getMaximum();
            }
        } else {
            value = value - zoom;
            if (value < trackBar_zoom.getMinimum()) {
                value = trackBar_zoom.getMinimum();
            }
        }
        trackBar_zoom.setValue(value);
        _mouse_zoom_down = true;
        zoomAgain();
        timer_refresh_Tick();
        _mouse_zoom_down = false;
    }//GEN-LAST:event_pictureBox_EnvMouseWheelMoved

    private void abscissaBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_abscissaBoxActionPerformed
        String absc = (String) abscissaBox.getSelectedItem();
        if (absc != null) {
            if ((absc.equalsIgnoreCase("Time")) || (absc.equalsIgnoreCase("Temps"))) {
                curves.changeAbsc("time");
            } else {
                curves.changeAbsc(absc);
            }
            dessinerCourbes();
        }
    }//GEN-LAST:event_abscissaBoxActionPerformed

    public void setZLabel() {
        jLabelZ.setText("Z=" + jSliderZ.getValue());
    }

    public Behavior getBehaviour(String nom_moteur) {
        ArrayList<Behavior> allMoteurs = getListManipulesReactions();
        // Get all the Reactions
        for (int i = allMoteurs.size() - 1; i >= 0; i--) {
            Behavior moteur = allMoteurs.get(i);
            //if(moteur.getEtiquettes().contains(nom_moteur)) { // Old bug!
            if (moteur.getEtiquettes().equals(nom_moteur)) {
                return moteur;
            }
        }
        return null;
    }

    public void afficherInformations(int mouseX, int mouseY) {
        int new_x = (int) windowToUniverse(_observed_left, _observed_left + _observed_width, pictureBox_Env.getWidth(), mouseX);
        int new_y = (int) windowToUniverse(_observed_top, _observed_top + _observed_height, pictureBox_Env.getHeight(), mouseY);
        if (new_x >= 0 && new_y >= 0 && new_x < getTailleX() && new_y < getTailleY()) {
            InstanceReaxel r = instances.getFast(new_x, new_y, jSliderZ.getValue());
            if (r == null) {
                try {
                    JOptionPane.showMessageDialog(this, "Simulation :\n" + parameters.getDescription());
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Dans afficherInformations : " + ex);
                }

            } else {
                Entity n = getReaxelByName(r.getNom());
                try {
                    JOptionPane.showMessageDialog(this, "Entite :" + r.getNom() + "\n" + n.getDescription().getDocument().getText(0, n.getDescription().getDocument().getLength()));
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Dans afficherInformations : " + ex);
                }
            }
        }
    }

    private void drawCoordinates() {
        if (pictureBox_courbes.getMousePosition() == null) {
            return;
        } else {
            try {
                int posX = pictureBox_courbes.getMousePosition().x;
                int posY = pictureBox_courbes.getMousePosition().y;

                Double maxX = Double.parseDouble(label_courbe_x_max.getText());
                Double maxY = Double.parseDouble(label_courbe_y_max.getText());

                int valX = (posX * maxX.intValue()) / pictureBox_courbes.getWidth() + 1;
                int valY = maxY.intValue() - (posY * maxY.intValue()) / pictureBox_courbes.getHeight();

                String strX = ((Integer) valX).toString();
                String strY = ((Integer) valY).toString();
                pictureBox_courbes.setToolTipText(strX + "," + strY);
            } catch (Exception ex) {
                System.err.println("Erreur affichage coordonnées :" + ex.toString());
            }
        }
    }

    public void zoomAgain() {
        float old_width = _observed_width;
        float old_height = _observed_height;

        // Zoom
        float zoom = 1.0f + (trackBar_zoom.getValue() - 1.0f) / 10.0f;

        _observed_width = (getTailleX() * 1.0f) / zoom;
        _observed_height = (getTailleY() * 1.0f) / zoom;

        // Zoom au centre
        _observed_left -= (_observed_width - old_width) / 2.0f;
        _observed_top -= (_observed_height - old_height) / 2.0f;

        if (_observed_left < 0) {
            _observed_left = 0;
        }
        if (_observed_top < 0) {
            _observed_top = 0;
        }
        if (_observed_left + _observed_width >= getTailleX()) {
            _observed_left = getTailleX() - _observed_width;
        }
        if (_observed_top + _observed_height >= getTailleY()) {
            _observed_top = getTailleY() - _observed_height;
        }
    }

    private void releverToutesCheckBoxPaint(JButton sauf) {
        if (sauf != checkBox_paint_move) {
            checkBox_paint_move.setBackground(Color.RED);
        }
        if (sauf != checkBox_paint_stylo) {
            checkBox_paint_stylo.setBackground(Color.RED);
        }
        if (sauf != checkBox_paint_spray) {
            checkBox_paint_spray.setBackground(Color.RED);
        }
        if (sauf != checkBox_paint_ligne) {
            checkBox_paint_ligne.setBackground(Color.RED);
        }
        if (sauf != checkBox_paint_random) {
            checkBox_paint_random.setBackground(Color.RED);
        }
        if (sauf != checkBox_paint_gomme) {
            checkBox_paint_gomme.setBackground(Color.RED);
        }

        if (sauf != null) {
            sauf.setBackground(Color.GREEN);
        }
    }

    private void Paint_Stylo(int mouseX, int mouseY) {
        int new_x = (int) windowToUniverse(_observed_left, _observed_left + _observed_width, pictureBox_Env.getWidth(), mouseX);
        int new_y = (int) windowToUniverse(_observed_top, _observed_top + _observed_height, pictureBox_Env.getHeight(), mouseY);
        int new_z = jSliderZ.getValue();
        if (new_x >= 0 && new_y >= 0 && new_x < getTailleX() && new_y < getTailleY()) {
            ArrayList<UtilPoint3D> points = new ArrayList<>();
            points.add(new UtilPoint3D(new_x, new_y, new_z));
            controller.addEntityInstances(points);
        }
    }

    private void Paint_Spray(int mouseX, int mouseY) {
        ArrayList<UtilPoint3D> points = new ArrayList<>();
        for (int nb = 0; nb < 10; nb++) {
            int new_x = (int) windowToUniverse(_observed_left, _observed_left + _observed_width, pictureBox_Env.getWidth(), mouseX) + RandomGen.getInstance().nextInt(21) - 10;
            int new_y = (int) windowToUniverse(_observed_top, _observed_top + _observed_height, pictureBox_Env.getHeight(), mouseY) + RandomGen.getInstance().nextInt(21) - 10;
            int new_z = jSliderZ.getValue();
            if (new_x >= 0 && new_y >= 0 && new_x < getTailleX() && new_y < getTailleY()) {
                UtilPoint3D p = new UtilPoint3D(new_x, new_y, new_z);
                points.add(p);
            }
        }
        controller.addEntityInstances(points);
    }

    private void Paint_Ligne(int mouseX, int mouseY) {
        int new_x = (int) windowToUniverse(_observed_left, _observed_left + _observed_width, pictureBox_Env.getWidth(), mouseX);
        int new_y = (int) windowToUniverse(_observed_top, _observed_top + _observed_height, pictureBox_Env.getHeight(), mouseY);
        if (new_x >= 0 && new_y >= 0 && new_x < getTailleX() && new_y < getTailleY()) {
            int x0 = (int) universeToWindow(_observed_left, _observed_width + _observed_left, _case_x0 + 0.5f, pictureBox_Env.getWidth());
            int y0 = (int) universeToWindow(_observed_top, _observed_height + _observed_top, _case_y0 + 0.5f, pictureBox_Env.getHeight());
            drawAll(x0, y0, mouseX, mouseY, 1);
        }
    }

    private void Paint_Aleat(int mouseX, int mouseY) {
        int new_x = (int) windowToUniverse(_observed_left, _observed_left + _observed_width, pictureBox_Env.getWidth(), mouseX);
        int new_y = (int) windowToUniverse(_observed_top, _observed_top + _observed_height, pictureBox_Env.getHeight(), mouseY);
        if (new_x >= 0 && new_y >= 0 && new_x < getTailleX() && new_y < getTailleY()) {
            int x0 = (int) universeToWindow(_observed_left, _observed_width + _observed_left, _case_x0 + 0.0f, pictureBox_Env.getWidth());
            int y0 = (int) universeToWindow(_observed_top, _observed_height + _observed_top, _case_y0 + 0.0f, pictureBox_Env.getHeight());
            drawAll(x0, y0, mouseX, mouseY, 2);
        }
    }

    private void Paint_Gomme(int mouseX, int mouseY) {
        int new_x = (int) windowToUniverse(_observed_left, _observed_left + _observed_width, pictureBox_Env.getWidth(), mouseX);
        int new_y = (int) windowToUniverse(_observed_top, _observed_top + _observed_height, pictureBox_Env.getHeight(), mouseY);
        int new_z = jSliderZ.getValue();
        if (new_x >= 0 && new_y >= 0 && new_x < getTailleX() && new_y < getTailleY()) {
            controller.removeEntityInstance(new_x, new_y, new_z);
        }
    }

    public String str_aujout_nbr(String nom, Integer nombre) {
        return (nom).concat(", ").concat(nombre.toString());
    }

    public void placerComportementsVisiblesDansPanel() {
        ArrayList<Behavior> lstCmpt = getListManipulesReactions();

        int pos = 0;
        int dX = 240;
        int dY = 67;
        int decalY = 550;//this.getHeight()+1;
        int nb_par_lignes = this.getWidth() / dX;
        for (Behavior cpt : lstCmpt) {
            String k_str = ((Double) cpt.get_k()).toString();
            if (cpt._visibleDansPanel == true) {
                int X = pos % nb_par_lignes;
                int Y = (int) Math.floor(pos / nb_par_lignes);
                int X0 = X * dX;
                int Y0 = Y * dY + decalY;
                cpt.setBounds(X0, Y0, dX, dY);
                cpt.LblTitre.setSize(cpt.getWidth(), 20);
                cpt.setTitre(cpt.getEtiquettes() + ", p=" + k_str);
                pos++;
            } else {
                cpt.setBounds(-300, -300, dX, dY);
            }
        }
        repaint();
    }

    // Changement d'echelle
    public float universeToWindow(float univers_min, float univers_max, float x_univers, float fenetre_max) {
        float a = (1.0f * fenetre_max) / (univers_max - univers_min);
        float b = -a * univers_min;
        float x_fenetre = a * x_univers + b;
        return x_fenetre;
    }

    public float windowToUniverse(float univers_min, float univers_max, float fenetre_max, float x_fenetre) {
        float a = (1.0f * fenetre_max) / (univers_max - univers_min);
        float b = -a * univers_min;
        float x_univers = (x_fenetre - b) / a;
        return x_univers;
    }

    public float deltaUniverseToWindow(float univers_min, float univers_max, float delta_univers, float fenetre_max) {
        float x_univers;

        // x0
        x_univers = 0;
        float a = (1.0f * fenetre_max) / (univers_max - univers_min);
        float b = -a * univers_min;
        float x_fenetre_0 = a * x_univers + b;
        // x1
        x_univers = delta_univers;
        float x_fenetre_1 = a * x_univers + b;

        float delta = x_fenetre_1 - x_fenetre_0;
        return delta;
    }

    public float deltaWindowToUniverse(float univers_min, float univers_max, float fenetre_max, float delta_fenetre) {
        float x_fenetre;

        // x0
        x_fenetre = 0;
        float a = (fenetre_max) / (univers_max - univers_min);
        float b = -a * univers_min;
        float x_univers_0 = (x_fenetre - b) / a;
        // x1
        x_fenetre = delta_fenetre;
        float x_univers_1 = (x_fenetre - b) / a;

        float delta = x_univers_1 - x_univers_0;

        return delta;
    }

    public void fillDataGridEntities() {
        // Entites a placer dans la dataGrid
        DefaultListModel model = new DefaultListModel();
        for (Entity _ListManipulesNoeud : this._ListManipulesNoeuds) {
            if (((ProtoSimplexel) _ListManipulesNoeud)._visibleDansPanel) {
                String nom_cli = str_aujout_nbr(((ProtoSimplexel) _ListManipulesNoeud).getEtiquettes(), 0);
                model.addElement(nom_cli);
            }
        }
        dataGridView_entites.setModel(model);
    }

    public void fillDataGridBehaviours() {
        // Comportements a placer dans la dataGrid
        DefaultListModel model = new DefaultListModel();
        for (Behavior moteur : this._ListManipulesReactions) {
            String nom_moteur = moteur.getEtiquettes();
            model.addElement(nom_moteur);
        }
        dataGridView_comportements.setModel(model);
    }

    private ArrayList<String> getSelectedEntities() {
        ArrayList<String> lst_entites = new ArrayList<>();
        if (dataGridView_entites.getModel().getSize() != 0) {
            int[] collec = dataGridView_entites.getSelectedIndices();
            if (collec != null) {
                for (int i : collec) {
                    if (i < _ListManipulesNoeuds.size()) {
                        lst_entites.add(UtilDivers.str_originale(dataGridView_entites.getModel().getElementAt(i).toString()));
                    }
                }
            }
            return lst_entites;
        } else {
            return lst_entites;
        }

    }

    public void dessinerCourbes() {
        if (_dataGridView_entitesIsChanging || pictureBox_courbes.getWidth() <= 0) {
            return;
        }

        BufferedImage bmp = new BufferedImage(pictureBox_courbes.getWidth(), pictureBox_courbes.getHeight() - 3, BufferedImage.TYPE_INT_RGB);
        // RAZ de l'image            
        Graphics g = bmp.getGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, bmp.getWidth(), bmp.getHeight());

        // Bornes
        int max_x = curves.getMaxAbsc().intValue();

        ArrayList<String> selectedEntities = getSelectedEntities();
        Double max_y = curves.getMaxInSelected(selectedEntities);

        if (max_x <= 0) {
            max_x = 1;
        }
        if (max_y <= 0) {
            max_y = 0.0;
        }
        // Affichage effectif de la courbe
        double scale_x = (pictureBox_courbes.getWidth() - 3) / (1.0 * max_x);
        double scale_y = (pictureBox_courbes.getHeight() - 8) / (1.0 * max_y);

        bmp = curves.buildOnlySelectedCurves(bmp, scale_x, scale_y, pictureBox_courbes.getHeight());

        pictureBox_courbes.setIcon(new ImageIcon(bmp));
        label_courbe_x_max.setText(((Integer) max_x).toString());
        label_courbe_y_max.setText("" + max_y.intValue());

        drawCoordinates();
    }

    private void initBufferedImageSimulator() {
        bmp_memory = new BufferedImage(pictureBox_Env.getWidth(), pictureBox_Env.getHeight(), BufferedImage.TYPE_INT_ARGB);
        g_mem = bmp_memory.getGraphics();
        g_mem.setColor(Color.WHITE);
        g_mem.fillRect(0, 0, bmp_memory.getWidth(null), bmp_memory.getHeight(null));
    }

    public void drawAll(int x0, int y0, int x1, int y1, int forme) { // forme=1 => Ligne; forme=2 => rectangle
        if (pictureBox_Env.getWidth() <= 0) {
            return;
        }
        dessinerCourbes();

        // Creation d'un bitmap
        if (bmp_memory == null) {
            initBufferedImageSimulator();
        }

        if (this.checkBox_Flou.isSelected() == false) {
            g_mem.setColor(Color.WHITE);
            g_mem.fillRect(0, 0, bmp_memory.getWidth(null), bmp_memory.getHeight(null));
        }

        if (pictureBox_Env.getIcon() == null) {
            pictureBox_Env.setIcon(new ImageIcon(bmp_memory));
        }

        if (_image_deco != null) {
            float x_min_back_image = universeToWindow(0, getTailleX(), _observed_left, _image_deco.getWidth());
            float x_max_back_image = universeToWindow(0, getTailleX(), _observed_left + _observed_width, _image_deco.getWidth());

            float y_min_back_image = universeToWindow(0, getTailleY(), _observed_top, _image_deco.getHeight());
            float y_max_back_image = universeToWindow(0, getTailleY(), _observed_top + _observed_width, _image_deco.getHeight());
            g_mem.drawImage(_image_deco, 0, 0, pictureBox_Env.getWidth(), pictureBox_Env.getHeight(), (int) x_min_back_image, (int) y_min_back_image, (int) x_max_back_image, (int) y_max_back_image, null);//, GraphicsUnit.Pixel);
        }

        // Creation des bitmaps de toutes les entites
        // en fonction du niveau de zoom
        // afin d'accelerer l'affichage
        HashMap<String, Image> dico_cube_image_adaptee = new HashMap<>();
        {
            for (Entity cli : _ListManipulesNoeuds) {
                if (cli.BackgroundImage != null) {
                    float width_in_bmp_memory = (float) (deltaUniverseToWindow(_observed_left, _observed_width + _observed_left, 1, (float) (bmp_memory.getWidth(null))) / (cli._taille * 0.75 + 1)); //scale_x * (1 / _observed_width);
                    float height_in_bmp_memory = (float) (deltaUniverseToWindow(_observed_top, _observed_height + _observed_top, 1, bmp_memory.getHeight(null)) / (cli._taille * 0.75 + 1)); //scale_y * (1 / _observed_height);
                    BufferedImage dst_bmp = new BufferedImage((int) width_in_bmp_memory, (int) height_in_bmp_memory, BufferedImage.TYPE_INT_RGB);
                    Graphics gfx_dst_bmp = dst_bmp.getGraphics();
                    gfx_dst_bmp.drawImage(cli.BackgroundImage, 0, 0, (int) width_in_bmp_memory, (int) height_in_bmp_memory, 0, 0, cli.BackgroundImage.getWidth(null), cli.BackgroundImage.getHeight(null), null); //GraphicsUnit.Pixel, attr);
                    dico_cube_image_adaptee.put(cli.getEtiquettes(), dst_bmp);
                }
            }
        }
            //////////////////////////////////////////

        // Affichage de toutes les entites à la profondeur sélectionnée
        if (instances == null) {
            return;
        }
        int k = jSliderZ.getValue();
        for (int i = 0; i < getTailleX(); i++) {
            for (int j = 0; j < getTailleY(); j++) {
                // Affichage des cubes
                InstanceReaxel c = instances.getFast(i, j, k);
                if (c != null) {
                    if (c.getImage() == null) {
                        // Draw _forme to screen
                        float ratio_taille = (float) (c.getTaille() * 0.75 + 1);
                        float width_in_bmp_memory = (deltaUniverseToWindow(_observed_left, _observed_width + _observed_left, 1, bmp_memory.getWidth(null))); //scale_x * (1 / _observed_width);
                        float height_in_bmp_memory = (deltaUniverseToWindow(_observed_top, _observed_height + _observed_top, 1, bmp_memory.getHeight(null))); //scale_y * (1 / _observed_height);
                        float left_in_bmp_memory = universeToWindow(_observed_left, _observed_width + _observed_left, i, bmp_memory.getWidth(null));
                        float top_in_bmp_memory = universeToWindow(_observed_top, _observed_height + _observed_top, j, bmp_memory.getHeight(null));
                        left_in_bmp_memory += (float) (0.5 * width_in_bmp_memory * (1f - 1f / ratio_taille));
                        top_in_bmp_memory += (float) (0.5 * height_in_bmp_memory * (1f - 1f / ratio_taille));
                        width_in_bmp_memory = 1f + width_in_bmp_memory / ratio_taille;
                        height_in_bmp_memory = 1f + height_in_bmp_memory / ratio_taille;
                        // Choix de la forme
                        g_mem.setColor(c.getCouleur());
                        if (c.getForme() == 0) {// Disque
                            g_mem.fillOval((int) left_in_bmp_memory, (int) top_in_bmp_memory, (int) width_in_bmp_memory, (int) height_in_bmp_memory);
                        } else if (c.getForme() == 1) { // Carre
                            g_mem.fillRect((int) left_in_bmp_memory, (int) top_in_bmp_memory, (int) (width_in_bmp_memory), (int) (height_in_bmp_memory));
                        } else if (c.getForme() == 2) // Triangle
                        {
                            int nbPts = 3;
                            int[] Xpts = new int[nbPts];
                            int[] Ypts = new int[nbPts];
                            Xpts[0] = (int) (left_in_bmp_memory + width_in_bmp_memory / 2.0f);
                            Ypts[0] = (int) (top_in_bmp_memory);
                            Xpts[1] = (int) (left_in_bmp_memory);
                            Ypts[1] = (int) (top_in_bmp_memory + height_in_bmp_memory);
                            Xpts[2] = (int) (left_in_bmp_memory + width_in_bmp_memory);
                            Ypts[2] = (int) (top_in_bmp_memory + height_in_bmp_memory);
                            g_mem.fillPolygon(Xpts, Ypts, nbPts);
                        } else if (c.getForme() == 3) // Losange
                        {
                            int nbPts = 4;
                            int[] Xpts = new int[nbPts];
                            int[] Ypts = new int[nbPts];
                            Xpts[0] = (int) (left_in_bmp_memory + width_in_bmp_memory / 2.0f);
                            Ypts[0] = (int) (top_in_bmp_memory);
                            Xpts[1] = (int) (left_in_bmp_memory);
                            Ypts[1] = (int) (top_in_bmp_memory + height_in_bmp_memory / 2.0f);
                            Xpts[2] = Xpts[0];
                            Ypts[2] = (int) (top_in_bmp_memory + height_in_bmp_memory);
                            Xpts[3] = (int) (left_in_bmp_memory + width_in_bmp_memory);
                            Ypts[3] = Ypts[1];
                            g_mem.fillPolygon(Xpts, Ypts, nbPts);
                        } else if (c.getForme() == 4) // Etoile
                        {
                            int nbPts = 8;
                            int[] Xpts = new int[nbPts];
                            int[] Ypts = new int[nbPts];
                            Xpts[0] = (int) (left_in_bmp_memory + width_in_bmp_memory / 2.0f);
                            Ypts[0] = (int) (top_in_bmp_memory);
                            Xpts[1] = (int) (left_in_bmp_memory + 3.0f * width_in_bmp_memory / 8.0f);
                            Ypts[1] = (int) (top_in_bmp_memory + 3.0f * height_in_bmp_memory / 8.0f);
                            Xpts[2] = (int) (left_in_bmp_memory);
                            Ypts[2] = (int) (top_in_bmp_memory + height_in_bmp_memory / 2.0f);
                            Xpts[3] = (int) (left_in_bmp_memory + 3.0f * width_in_bmp_memory / 8.0f);
                            Ypts[3] = (int) (top_in_bmp_memory + height_in_bmp_memory - 3.0f * height_in_bmp_memory / 8.0f);
                            Xpts[4] = Xpts[0];
                            Ypts[4] = (int) (top_in_bmp_memory + height_in_bmp_memory);
                            Xpts[5] = (int) (left_in_bmp_memory + width_in_bmp_memory - 3.0f * width_in_bmp_memory / 8.0f);
                            Ypts[5] = Ypts[3];
                            Xpts[6] = (int) (left_in_bmp_memory + width_in_bmp_memory);
                            Ypts[6] = Ypts[2];
                            Xpts[7] = Xpts[5];
                            Ypts[7] = Ypts[1];
                            g_mem.fillPolygon(Xpts, Ypts, nbPts);
                        } else if (c.getForme() == 5) { // Pois
                            g_mem.fillOval((int) left_in_bmp_memory, (int) top_in_bmp_memory, (int) (width_in_bmp_memory / 4.0f), (int) (height_in_bmp_memory / 4.0f));
                            g_mem.fillOval((int) (left_in_bmp_memory + 3.0f * width_in_bmp_memory / 4.0f), (int) (top_in_bmp_memory + height_in_bmp_memory / 2.0f), (int) (width_in_bmp_memory / 4.0f), (int) (height_in_bmp_memory / 4.0f));
                            g_mem.fillOval((int) left_in_bmp_memory, (int) (3.0f * top_in_bmp_memory / 4.0f), (int) (width_in_bmp_memory / 4.0f), (int) (height_in_bmp_memory / 4.0f));
                        } else if (c.getForme() == 6) // Bruit
                        {
                            Graphics gg = bmp_memory.getGraphics();
                            gg.setColor(c.getCouleur());
                            for (float ii = left_in_bmp_memory; ii < left_in_bmp_memory + width_in_bmp_memory; ii++) {
                                for (float jj = top_in_bmp_memory; jj < top_in_bmp_memory + height_in_bmp_memory; jj++) {
                                    if (RandomGen.getInstance().nextDouble() < 0.1) {
                                        if (ii >= 0 && ii < bmp_memory.getWidth(null) && jj >= 0 && jj < bmp_memory.getHeight(null)) {
                                            gg.drawRect((int) ii, (int) jj, 1, 1);
                                        }
                                    }
                                }
                            }
                        }

                        if (c.isSelectionne()) {
                            g_mem.setColor(Color.white);
                            g_mem.fillOval((int) (left_in_bmp_memory - width_in_bmp_memory / 4.0f), (int) (top_in_bmp_memory - height_in_bmp_memory / 4.0f), (int) (width_in_bmp_memory / 2.0f), (int) (height_in_bmp_memory / 2.0f));
                            g_mem.setColor(Color.black);
                            g_mem.drawOval((int) (left_in_bmp_memory - width_in_bmp_memory / 4.0f), (int) (top_in_bmp_memory - height_in_bmp_memory / 4.0f), (int) (width_in_bmp_memory / 2.0f), (int) (height_in_bmp_memory / 2.0f));
                        }

                    } else {
                        // Dictionnaire des images (nom, image)
                        // Affichage rapide des images
                        float left_in_bmp_memory = universeToWindow(_observed_left, _observed_width + _observed_left, c.getX(), bmp_memory.getWidth(null));
                        float top_in_bmp_memory = universeToWindow(_observed_top, _observed_height + _observed_top, c.getY(), bmp_memory.getHeight(null));
                        g_mem.drawImage(dico_cube_image_adaptee.get(c.getNom()), (int) left_in_bmp_memory, (int) top_in_bmp_memory, null);
                    }
                }
            }
        }

        // Affichage du lien en creation
        //Graphics gfx = pictureBox1.getGraphics();
        if (forme == 1) {
            g_mem.setColor(Color.BLACK);
            g_mem.drawLine(x0, y0, x1, y1);
        }
        if (forme == 2) {
            g_mem.setColor(Color.BLACK);
            g_mem.drawLine(x0, y0, x1, y0);
            g_mem.drawLine(x1, y0, x1, y1);
            g_mem.drawLine(x0, y0, x0, y1);
            g_mem.drawLine(x1, y1, x0, y1);
        }

        // Fuzzy
        if (this.checkBox_Flou.isSelected() == true) {
            float[] blurMatrix = {0.12f, 0.12f, 0.12f, 0.3f, 0.12f, 0.12f, 0.12f, 0.12f, 0.12f};
            //float[] blurMatrix = { 1.0f / 9.0f, 1.0f / 9.0f, 1.0f / 9.0f, 1.0f / 9.0f, 1.0f / 9.0f, 1.0f / 9.0f, 1.0f / 9.0f, 1.0f / 9.0f, 1.0f / 9.0f };
            BufferedImageOp blurFilter = new ConvolveOp(new Kernel(3, 3, blurMatrix), ConvolveOp.EDGE_NO_OP, null);
            BufferedImage src_bmp = new BufferedImage(pictureBox_Env.getWidth(), pictureBox_Env.getHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics g_src = src_bmp.getGraphics();
            g_src.drawImage(bmp_memory, 0, 0, null);
            blurFilter.filter(src_bmp, bmp_memory);
        }
        // Affichage a l'ecran
        pictureBox_Env.setIcon(new ImageIcon(bmp_memory));

        // Enregistrement d'une video au format GIF anime
        if (checkBox_avi.isSelected() == true) {
            if (_animation_gif != null) {// || _animation_mov != null) {
                if (_animation_courbes == false) {
                    _animation_gif.addFrame(bmp_memory); // Uniquement l'env
                } else {
                    // L'env + les courbes
                    BufferedImage bmp_memory_mega = new BufferedImage(pictureBox_Env.getWidth() * 2, pictureBox_Env.getHeight(), BufferedImage.TYPE_INT_ARGB);
                    Graphics gg = bmp_memory_mega.getGraphics();
                    gg.drawImage(bmp_memory, 0, 0, null);
                    gg.drawImage(((ImageIcon) (pictureBox_courbes.getIcon())).getImage(), pictureBox_Env.getWidth(), 0, pictureBox_Env.getWidth(), pictureBox_Env.getHeight(), null);
                    gg.setColor(Color.black);
                    gg.drawLine(pictureBox_Env.getWidth(), pictureBox_Env.getHeight() - 1, pictureBox_Env.getWidth() * 2 - 1, pictureBox_Env.getHeight() - 1);
                    gg.drawLine(pictureBox_Env.getWidth(), pictureBox_Env.getHeight() - 1, pictureBox_Env.getWidth(), 0);
                    gg.drawString(label_courbe_x_max.getText(), 2 * pictureBox_Env.getWidth() - 50, pictureBox_Env.getHeight() - 10);
                    gg.drawString(label_courbe_y_max.getText(), pictureBox_Env.getWidth() + 2, 10);
                    _animation_gif.addFrame(bmp_memory_mega);
                }
            }
        }
    }

    protected javax.swing.Timer timer_refresh = new javax.swing.Timer(1, new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            timer_refresh_Tick();
        }
    });

    private void timer_refresh_Tick() {
        if (_mouse_left_down == true) {
            if (checkBox_paint_ligne.getBackground().equals(Color.GREEN) || checkBox_paint_random.getBackground().equals(Color.GREEN)) {
                if (pictureBox_Env.getMousePosition() != null) {
                    if (getDataGridView_entites().getSelectedIndex() < 0) {
                        dataGridView_entites.setSelectedIndex(0);
                    }
                    if (checkBox_paint_ligne.getBackground().equals(Color.GREEN)) {
                        Paint_Ligne(pictureBox_Env.getMousePosition().x, pictureBox_Env.getMousePosition().y);
                    }
                    if (checkBox_paint_random.getBackground().equals(Color.GREEN)) {
                        Paint_Aleat(pictureBox_Env.getMousePosition().x, pictureBox_Env.getMousePosition().y);
                    }
                }
            } else {
                drawAll(0, 0, 0, 0, 0);
            }
        }
        if (_mouse_zoom_down == true) {
            drawAll(0, 0, 0, 0, 0);
        }
    }

    public void simulationStarted() {       
        memoriserCourbes();
        dessinerCourbes();
        button_play.setIcon(icon_bouton_pause);
    }

    public void pauseSimulation() {
        timer_pause_play2.start();
    }

    public void stopSimulation() {
        timer_pause_play2.stop();
        this.button_play.setIcon(icon_bouton_play);
    }

    public void unpause_simulation() {
        timer_pause_play2.stop();
        this.button_play.setIcon(icon_bouton_pause);
    }

    public Entity getReaxelByName(String etiquette) {
        for (Entity proto : _ListManipulesNoeuds) {
            if (proto.TrouveEtiquette(etiquette) >= 0) {
                return proto;
            }
        }
        return null;
    }

    private void maj_pas_de_temps() {
        if (_time == 0) {
            curves.clear();
        }
        memoriserCourbes();
        majEntites();
        drawAll(0, 0, 0, 0, 0);
    }

    private void checkForChangesInCurves(ArrayList<Entity> old, ArrayList<Entity> update) {
        if (old.size() > update.size()) {
            // chercher celui qui a été supprimé pour supprimer les points

        } else {
            if (old.size() == update.size()) {
                //chercher s'il y eu un changement de nom
                for (Entity reaxel : old) {
                    for (Entity up : update) {
                        if (!old.contains(up)) {
                            if (!update.contains(reaxel)) {
                                curves.changeName(reaxel.getEtiquettes(), up.getEtiquettes());
                                if (reaxel.Couleur != up.Couleur) {
                                    curves.changeColor(up.getEtiquettes(), up.Couleur);
                                }
                            }
                        } else {
                            if (reaxel.getEtiquettes().equals(up.getEtiquettes())) {
                                if (reaxel.Couleur != up.Couleur) {
                                    curves.changeColor(up.getEtiquettes(), up.Couleur);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void memoriserCourbes() {
        ArrayList<String> lst_noms = new ArrayList<>();
        ArrayList<Color> lst_couls = new ArrayList<>();
        ArrayList<Integer> lst_pts = new ArrayList<>();
        for (Entity proto : _ListManipulesNoeuds) {
            int nb_p = 0;
            if (dico_courbes != null) {
                if (dico_courbes.containsKey(proto.getEtiquettes())) {
                    nb_p = dico_courbes.get(proto.getEtiquettes());
                }
            }
            lst_noms.add(proto._etiquettes);
            lst_couls.add(proto.Couleur);
            lst_pts.add(nb_p);
        }

        for (int ii = 0; ii < lst_noms.size(); ii++) {
            curves.addPoint(lst_noms.get(ii), lst_couls.get(ii), _time * 1.0, lst_pts.get(ii) * 1.0);
        }
    }

    // MAJ de la liste des entites et de leur compte
    private void majEntites() {
        String nom;

        for (int i = 0; i < dataGridView_entites.getModel().getSize(); i++) {
            nom = UtilDivers.str_originale(dataGridView_entites.getModel().getElementAt(i).toString());

            if (!dico_courbes.containsKey(nom)) {
                dico_courbes.put(nom, 0);
            }

            DefaultListModel model = (DefaultListModel) dataGridView_entites.getModel();
            String str = str_aujout_nbr(nom, dico_courbes.get(nom));
            model.setElementAt(str, i);
        }
    }

    private void timer_pause_play2_Tick() {

        if (button_play.getIcon() == null) {
            this.button_play.setIcon(icon_bouton_pause);
        } else {
            button_play.setIcon(null);
        }

    }

    // TIMERS
    protected javax.swing.Timer timer_pause_play2 = new javax.swing.Timer(300, new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            timer_pause_play2_Tick();
        }
    });

    public Env_Parameters getParameters() {
        return parameters;
    }

    public void setParameters(Env_Parameters parameters) {
        this.parameters = parameters;
        this.bmp_memory = null;
        initLanguage();
        if (parameters.getImage() != null) {
            this._image_deco = parameters.getImage();
            this.pictureBox_Env.setIcon(new ImageIcon(parameters.getImage()));
        }
        this.jSliderSpeed.setValue(3);
        controller.changeSpeed(jSliderSpeed.getValue());
        this.jSliderZ.setMaximum(getTailleZ() - 1);
        this.zoomAgain();
    }

    public ArrayList<InstanceReaxel> getCubes_selectionnes() {
        return _cubes_selectionnes;
    }

    public void setCubes_selectionnes(ArrayList<InstanceReaxel> _cubes_selectionnes) {
        this._cubes_selectionnes = _cubes_selectionnes;
    }

    public void addCube_selectionnes(InstanceReaxel _cube_selectionne) {
        if (_cubes_selectionnes == null) {
            _cubes_selectionnes = new ArrayList<>();
        }
        _cubes_selectionnes.add(_cube_selectionne);
    }

    public int getTailleX() {
        return getParameters().getX();
    }

    public int getTailleY() {
        return getParameters().getY();
    }

    public int getTailleZ() {
        return getParameters().getZ();
    }

    public boolean isMovingCubes() {
        return movingCubes;
    }

    public void setMovingCubes(boolean movingCubes) {
        this.movingCubes = movingCubes;
    }

    public ArrayList<Entity> getListManipulesNoeuds() {
        return _ListManipulesNoeuds;
    }

    public ArrayList<Behavior> getListManipulesReactions() {
        return _ListManipulesReactions;
    }

    public void setListManipulesReactions(ArrayList<Behavior> _ListManipulesReactions) {
        this._ListManipulesReactions = _ListManipulesReactions;
    }

    public HashMap<String, Integer> getDico_courbes() {
        return dico_courbes;
    }

    public void setDico_courbes(HashMap<String, Integer> dico_courbes) {
        this.dico_courbes = dico_courbes;
    }

    public String getNom_sauvegarde() {
        return _nom_sauvegarde;
    }

    public boolean isFreezed() {
        return freezed;
    }

    public void setFreezed(boolean freezed) {
        this.freezed = freezed;
    }

    public void setNom_sauvegarde(String _nom_sauvegarde) {
        this._nom_sauvegarde = _nom_sauvegarde;
    }

    public JList getDataGridView_entites() {
        return dataGridView_entites;
    }

    public JList getDataGridView_comportements() {
        return dataGridView_comportements;
    }

    public UtilAnimatedGifEncoder getAnimation_gif() {
        return _animation_gif;
    }

    public void setAnimation_gif(UtilAnimatedGifEncoder _animation_gif) {
        this._animation_gif = _animation_gif;
    }

    public boolean isAnimation_courbes() {
        return _animation_courbes;
    }

    public void setAnimation_courbes(boolean _animation_courbes) {
        this._animation_courbes = _animation_courbes;
    }

    public void unselectCheckBoxAvi() {
        checkBox_avi.setSelected(false);
    }

    public void setProba(double value) {
        jLabelProba.setText("p=" + value);
    }

    public void disabled3D() {
        jButton3D.setEnabled(false);
    }

    public SimulationCurves getCurves() {
        return curves;
    }
    
    

    public UtilPoint3D getPictureBoxDimensions() {
        int x = (int) windowToUniverse(_observed_left, _observed_left + _observed_width, pictureBox_Env.getWidth(), pictureBox_Env.getWidth());
        int y = (int) windowToUniverse(_observed_top, _observed_top + _observed_height, pictureBox_Env.getHeight(), pictureBox_Env.getHeight());
        return new UtilPoint3D(x, y, getParameters().getZ());
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton Ajustement;
    private javax.swing.JComboBox abscissaBox;
    private javax.swing.JButton bouton_about;
    private javax.swing.JButton bouton_environnement;
    private javax.swing.JButton bouton_export_courbes;
    private javax.swing.JButton bouton_export_model;
    private javax.swing.JButton bouton_lang;
    private javax.swing.JButton bouton_new;
    private javax.swing.JButton bouton_open;
    private javax.swing.JButton bouton_save;
    private javax.swing.JButton bouton_vider;
    private javax.swing.JButton button_init;
    private javax.swing.JButton button_play;
    private javax.swing.JButton button_play1;
    private javax.swing.JCheckBox checkBox_Flou;
    private javax.swing.JCheckBox checkBox_avi;
    private javax.swing.JButton checkBox_paint_gomme;
    private javax.swing.JButton checkBox_paint_ligne;
    private javax.swing.JButton checkBox_paint_move;
    private javax.swing.JButton checkBox_paint_random;
    private javax.swing.JButton checkBox_paint_spray;
    private javax.swing.JButton checkBox_paint_stylo;
    private javax.swing.JList dataGridView_comportements;
    private javax.swing.JList dataGridView_entites;
    private javax.swing.JButton jButton3D;
    private javax.swing.JButton jButtonAddBehav;
    private javax.swing.JButton jButtonAddEntity;
    private javax.swing.JButton jButtonDelBehav;
    private javax.swing.JButton jButtonDelEntity;
    private javax.swing.JButton jButtonEditBehav;
    private javax.swing.JButton jButtonEditEntity;
    private javax.swing.JLabel jLabelComportements;
    private javax.swing.JLabel jLabelCourbes;
    private javax.swing.JLabel jLabelDivers;
    private javax.swing.JLabel jLabelEntites;
    private javax.swing.JLabel jLabelEnvironnement;
    private javax.swing.JLabel jLabelProba;
    private javax.swing.JLabel jLabelSimulateur;
    private javax.swing.JLabel jLabelSpeed;
    private javax.swing.JLabel jLabelTps;
    private javax.swing.JLabel jLabelZ;
    private javax.swing.JLabel jLabelZoom;
    private javax.swing.JLabel jLabel_NetBioDyn;
    private javax.swing.JLabel jLabel_t0;
    private javax.swing.JLabel jLabel_version;
    private javax.swing.JPanel jPanelBehaviors;
    private javax.swing.JPanel jPanelCurves;
    private javax.swing.JPanel jPanelEntities;
    private javax.swing.JPanel jPanelEnv;
    private javax.swing.JPanel jPanelMisc;
    private javax.swing.JPanel jPanelSimulator;
    private javax.swing.JScrollPane jScrollPaneBehaviors;
    private javax.swing.JScrollPane jScrollPane_Entities;
    private javax.swing.JSlider jSliderProba;
    private javax.swing.JSlider jSliderSpeed;
    public javax.swing.JSlider jSliderZ;
    public javax.swing.JLabel labelTime;
    private javax.swing.JLabel label_courbe_x_max;
    private javax.swing.JLabel label_courbe_y_max;
    public javax.swing.JLabel pictureBox_Env;
    private javax.swing.JLabel pictureBox_courbes;
    private javax.swing.JSlider trackBar_zoom;
    // End of variables declaration//GEN-END:variables
    // Courbes

    public void freeze(boolean bool) {
        freezed = bool;
        jButtonAddEntity.setEnabled(!bool);
        jButtonAddBehav.setEnabled(!bool);
        jButtonDelBehav.setEnabled(!bool);
        jButtonDelEntity.setEnabled(!bool);
        jButtonEditBehav.setEnabled(!bool);
        jButtonEditEntity.setEnabled(!bool);
        checkBox_paint_gomme.setEnabled(!bool);
        checkBox_paint_ligne.setEnabled(!bool);
        checkBox_paint_move.setEnabled(!bool);
        checkBox_paint_random.setEnabled(!bool);
        checkBox_paint_spray.setEnabled(!bool);
        checkBox_paint_stylo.setEnabled(!bool);
        jSliderProba.setEnabled(!bool);
        bouton_environnement.setEnabled(!bool);
        bouton_vider.setEnabled(!bool);
        bouton_new.setEnabled(!bool);
        bouton_open.setEnabled(!bool);
        bouton_save.setEnabled(!bool);
        Ajustement.setEnabled(!bool);
        if (freezed) {
            if (Lang.getInstance().getLang().equals("EN")) {
                Ajustement.setText("Adjusting in progress ...");
            } else {
                Ajustement.setText("Ajustement en cours ...");
            }
        } else {
            if (Lang.getInstance().getLang().equals("EN")) {
                Ajustement.setText("Self-adjusting parameters");
            } else {
                Ajustement.setText("Auto-ajustement des paramètres");
            }
        }
    }

}
