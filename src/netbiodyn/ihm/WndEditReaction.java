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
 * WndEditReaction.java
 *
 * Created on 9 novembre 2007, 17:06
 */
package netbiodyn.ihm;

import netbiodyn.util.UtilPointF;
import netbiodyn.util.UtilDivers;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.ArrayList;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import netbiodyn.Behavior;
import netbiodyn.Entity;
import netbiodyn.util.Lang;

/**
 *
 * @author ballet
 */
public class WndEditReaction extends javax.swing.JDialog {

    boolean _env_affiche3D = false;
    public Behavior _r3 = null;
    private String DialogResult = null;
    private int _mouseX = 0, _mouseY = 0;
    private final ArrayList<Behavior> lstCmpt;
    private final ArrayList<Entity> entities;

    /**
     * Creates new form WndEditReaction
     * @param entities
     * @param behaviours
     */
    public WndEditReaction(ArrayList<Entity> entities, ArrayList<Behavior> behaviours) {
        this.setModal(true);
        lstCmpt=behaviours;
        this.entities=entities;                
        setSize(new Dimension(644, 446));
        initComponents();
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        tracerPositionCellules(g, false);
    }

    private void tracerPositionCellules(Graphics gfx, boolean updatePositions) {
        if (gfx == null) {
            return;
        }
        //if(!this.jComboBox_type.getSelectedItem().toString().equals("Situee en absolue"))
        //   return;

        int cote = 10;
        int tailleX = 3 * cote, tailleY = 3 * cote;

        int posX0 = (jScrollPane1.getX() + jScrollPane2.getX() + jScrollPane2.getWidth()) / 2 - tailleX; //jScrollPane1.getX() + dataGridView_reactifs.getSize().width;
        int posY0 = jScrollPane1.getY() + 10;//-5;

        //int dx_prod_react = jScrollPane2.getX() - jScrollPane1.getX() - dataGridView_produits.getSize().width - 35;
        int decalY = dataGridView_reactifs.getRowHeight();

        int[] dxs = new int[9];
        int[] dys = new int[9];
        dxs[0] = 0;
        dys[0] = 0;
        dxs[1] = 1;
        dys[1] = 0;
        dxs[2] = 1;
        dys[2] = -1;
        dxs[3] = 0;
        dys[3] = -1;
        dxs[4] = -1;
        dys[4] = -1;
        dxs[5] = -1;
        dys[5] = 0;
        dxs[6] = -1;
        dys[6] = 1;
        dxs[7] = 0;
        dys[7] = 1;
        dxs[8] = 1;
        dys[8] = 1;

        int posX = posX0 + 10, posY = posY0;
        for (int i = 0; i < 3; i++) {
            // Trace de la grille 3x3
            int posYCombo = posY0 + (decalY) * i + 45 + 6;
            posY += decalY;//tailleY;

            if (!(dataGridView_reactifs.getValueAt(i, 0).equals("*")) || !(dataGridView_produits.getValueAt(i, 0).equals("-"))) { // Only selected are displayed           
                // Lines between Reactives and Products
                gfx.setColor(Color.WHITE);
                gfx.drawLine(posX - 30, posYCombo - 1, posX, posY + tailleY / 2 - 1);
                gfx.drawLine(posX + tailleX + 30, posYCombo - 1, posX + tailleX, posY + tailleY / 2 - 1);

                gfx.setColor(Color.DARK_GRAY);
                gfx.drawLine(posX - 30, posYCombo, posX, posY + tailleY / 2);
                gfx.drawLine(posX + tailleX + 30, posYCombo, posX + tailleX, posY + tailleY / 2);
                // Arow of the lines
                gfx.setColor(Color.WHITE);
                gfx.drawLine(posX + tailleX + 30, posY + tailleY / 2 - 1, posX + tailleX + 30 - 6, posY + tailleY / 2 - 1 - 6);
                gfx.drawLine(posX + tailleX + 30, posY + tailleY / 2 - 1, posX + tailleX + 30 - 6, posY + tailleY / 2 - 1 + 6);

                gfx.setColor(Color.DARK_GRAY);
                gfx.drawLine(posX + tailleX + 30, posY + tailleY / 2, posX + tailleX + 30 - 6, posY + tailleY / 2 - 6);
                gfx.drawLine(posX + tailleX + 30, posY + tailleY / 2, posX + tailleX + 30 - 6, posY + tailleY / 2 + 6);

                //gfx.fillOval(posX+tailleX+30-6, posYCombo-6, 12, 12);
                //gfx.setColor(Color.LIGHT_GRAY);
                //gfx.fillRect(posX, posY, tailleX, tailleY);
                // Trace des cases selectionnees
                String str_pos = _r3._positions.get(i);
                for (int j = 0; j < 9; j++) {
                    //int sx = tailleX/3, sy = tailleY/3;               // sizes (x and y)
                    int dx = dxs[j] * cote, dy = dys[j] * cote;
                    UtilPointF center = new UtilPointF((posX + dx + tailleX / 3) + cote / 2, (posY + dy + tailleY / 3) + cote / 2, 0);   // center

                    // Draw position-element background
                    if (j == 2 || j == 6) {
                        gfx.setColor(new java.awt.Color(204, 204, 255));
                    } else {
                        gfx.setColor(Color.LIGHT_GRAY);
                    }
                    gfx.fillRect((int) (center.X - cote / 2), (int) (center.Y - cote / 2), cote + 1, cote + 1);

                    int draw_state = 0;

                    char cc = str_pos.charAt(j);
                    //if(cc == '0') gfx.setColor(Color.RED); // Can be changed
                    if (cc == '1') {
                        gfx.setColor(Color.BLACK);
                        draw_state = 1;
                    } // Possible (black square)
                    if (cc == '2') {
                        gfx.setColor(Color.BLACK);
                        draw_state = -1;
                    } // Impossible (black crox)
                    //if(cc == '3') gfx.setColor(Color.RED); // Fixed
                    if (updatePositions == true && cc != '2' && i > 0) {
                        if (_mouseX >= posX + dx + tailleX / 3 && _mouseX < posX + dx + (2 * tailleX) / 3) {
                            if (_mouseY >= posY + dy + tailleY / 3 && _mouseY < posY + dy + (2 * tailleY) / 3) {
                                if (cc == '1') {
                                    cc = '0';
                                } else {
                                    cc = '1';
                                }
                                _r3._positions.set(i, UtilDivers.setCharAt(_r3._positions.get(i), j, cc));
                            }
                        }
                    }

                    if (cc == '1' || cc == '2') {
                        //if(j != 2 && j!= 6) {//gfx.setColor(Color.BLACK);
                        // draw centered entity...
                        if (j == 0) {
                            if (i == 0) {
                                gfx.setColor(Color.BLACK);
                            } else {
                                gfx.setColor(Color.GRAY);
                                //gfx.setColor(new java.awt.Color(204, 204, 255));
                            }
                            gfx.fillRect((int) center.X - 3, (int) center.Y - 3, cote - 4, cote - 4); // center
                        }
                        if (draw_state == 1) {

                            // then draw other entities
                            if (j == 1 || j == 3 || j == 5 || j == 7) {
                                float angle = 0;
                                if (j == 1) {
                                    angle = 0; // right
                                }
                                if (j == 3) {
                                    angle = (float) Math.PI / 2; // up
                                }
                                if (j == 5) {
                                    angle = (float) Math.PI; // left
                                }
                                if (j == 7) {
                                    angle = (float) (3 * Math.PI / 2); // down
                                }
                                UtilDivers.drawArrow(gfx, center, cote - 2, cote - 4, angle);
                            } else if (j == 4 || j == 8) {
                                if (j == 4) { // front
                                    gfx.drawOval((int) center.X - cote / 2 + 1, (int) center.Y - cote / 2 + 1, cote - 4, cote - 4);
                                    gfx.fillRect((int) center.X - 1, (int) center.Y - 1, 1, 1);
                                }
                                if (j == 8) { // back
                                    gfx.drawOval((int) center.X - cote / 2 + 1, (int) center.Y - cote / 2 + 1, cote - 3, cote - 3);
                                    float r = 0.3f * cote - 1;
                                    gfx.drawLine((int) (center.X - r), (int) (center.Y - r), (int) (center.X + r) - 1, (int) (center.Y + r) - 1);
                                    gfx.drawLine((int) (center.X + r), (int) (center.Y - r) - 1, (int) (center.X - r), (int) (center.Y + r) - 1);
                                }
                            }
                                //if(j==)
                            //gfx.fillRect(posX+dx+tailleX/3, posY+dy+tailleY/3, tailleX/3-1, tailleY/3-1);
                        } else {
                                //gfx.drawLine(posX+dx+tailleX/3, posY+dy+tailleY/3, posX+dx+tailleX/3+tailleX/3-1, posY+dy+tailleY/3+tailleY/3-1);
                            //gfx.drawLine(posX+dx+tailleX/3+tailleX/3-1, posY+dy+tailleY/3, posX+dx+tailleX/3, posY+dy+tailleY/3+tailleY/3-1);
                        }

                        //}
                    }
                }
                // Draw the outlines of Positions
                gfx.setColor(Color.WHITE);
                gfx.drawLine(posX - 1, posY - 1, posX + 2 * cote, posY - 1);                // 0
                gfx.drawLine(posX + 2 * cote, posY + cote, posX + 3 * cote, posY + cote);               // 2
                gfx.drawLine(posX + cote - 1, posY + 3 * cote + 1, posX + cote - 1, posY + 2 * cote);            // 5
                gfx.drawLine(posX - 1, posY + 2 * cote, posX - 1, posY - 1);            // 7

                gfx.setColor(Color.DARK_GRAY);
                gfx.drawLine(posX + 2 * cote, posY - 1, posX + 2 * cote, posY + cote - 1);          // 1
                gfx.drawLine(posX + 3 * cote + 1, posY + cote, posX + 3 * cote + 1, posY + 3 * cote + 1);        // 3
                gfx.drawLine(posX + 3 * cote + 1, posY + 3 * cote + 1, posX + cote - 1, posY + 3 * cote + 1);            // 4
                gfx.drawLine(posX + cote - 1, posY + 2 * cote, posX - 1, posY + 2 * cote);            // 6                
            }
            // Gestion automatiques des reactifs et produits
            // si reactif = "*" MAIS produit != "-" alors reactif = "0"
            if (dataGridView_reactifs.getValueAt(i, 0).equals("*") && !(dataGridView_produits.getValueAt(i, 0).equals("-"))) {
                dataGridView_reactifs.setValueAt("0", i, 0);
            }
            // si produit = "-" MAIS reactif != "*" alors produit = "0"
            if (dataGridView_produits.getValueAt(i, 0).equals("-") && !(dataGridView_reactifs.getValueAt(i, 0).equals("*"))) {
                dataGridView_produits.setValueAt("0", i, 0);
            }

        }

    }

    public void WndCliEditReaction3_Load(Behavior behaviour) {
        if(behaviour == null){
            _r3=new Behavior();
        }
        else{
            _r3=behaviour;
        }
        
        if (Lang.getInstance().getLang().equals("FR")) {
            jLabelNom.setText("Nom");
            jLabelComportement.setText("Comportement");
            jLabelProba.setText("Probabilité");
            jLabelPositions.setText("Positions");
            jLabelProduits.setText("Produits");
            button_annuler.setText("Annuler");
            jLabelReactifs.setText("Réactifs");
        } else {
            jLabelNom.setText("Name");
            jLabelComportement.setText("Behaviour");
            jLabelProba.setText("Probability");
            jLabelPositions.setText("Positions");
            jLabelProduits.setText("Products");
            button_annuler.setText("Cancel");
            jLabelReactifs.setText("Reactives");
        }

        this.jPanel_cplx.setVisible(false);
        jScrollPane4.setVisible(false);

        textBox_etiquette.setText(_r3.getEtiquettes());
        textBox_k.setText(((Double) _r3.get_k()).toString());

        // Table des reactifs
        Object[][] donnees_reactifs = {
            {new JComboBox()}, {new JComboBox()}, {new JComboBox()}, //{new JComboBox()}, {new JComboBox()}, {new JComboBox()},
        //{new JComboBox()}, {new JComboBox()}, {new JComboBox()},
        };
        String[] titreColonnes = new String[1];
        if (Lang.getInstance().getLang().equals("FR")) {
            titreColonnes[0] = "Entites";
        } else {
            titreColonnes[0] = "Entities";
        }
        DefaultTableModel m_reactifs = new DefaultTableModel(donnees_reactifs, titreColonnes);
        dataGridView_reactifs.setModel(m_reactifs);

        TableColumn colonne_reactifs = dataGridView_reactifs.getColumnModel().getColumn(0);
        JComboBox comboBox_reactifs = new JComboBox();
            //comboBox_reactifs.setBorder(BorderFactory.createLineBorder(Color.yellow));
        //comboBox_reactifs.setBackground(Color.green);
        comboBox_reactifs.addItem("*");
        comboBox_reactifs.addItem("0");

        // Table des produits
        Object[][] donnees_produits = {
            {new JComboBox()}, {new JComboBox()}, {new JComboBox()}, //{new JComboBox()}, {new JComboBox()}, {new JComboBox()},
        //{new JComboBox()}, {new JComboBox()}, {new JComboBox()},
        };
        DefaultTableModel m_produits = new DefaultTableModel(donnees_produits, titreColonnes);
        dataGridView_produits.setModel(m_produits);

        TableColumn colonne_produits = dataGridView_produits.getColumnModel().getColumn(0);
        JComboBox comboBox_produits = new JComboBox();
        comboBox_produits.addItem("-");
        comboBox_produits.addItem("0");

            // Pour les 2 tables (réactifs et produits)
        // Entites
        for (Entity lst_cli1 : entities) {
            comboBox_reactifs.addItem(lst_cli1._etiquettes);
            comboBox_produits.addItem(lst_cli1._etiquettes);
        }

        // Application du modèle de combobox
        colonne_reactifs.setCellEditor(new DefaultCellEditor(comboBox_reactifs));
        colonne_produits.setCellEditor(new DefaultCellEditor(comboBox_produits));

            // RAZ des combo
        // Remplissage des bons reactifs et produits de la reaction
        for (int i = 0; i < 3; i++) {
            dataGridView_reactifs.setValueAt("*", i, 0);
            dataGridView_produits.setValueAt("-", i, 0);
        }
        for (int i = 0; i < dataGridView_reactifs.getRowCount(); i++) {
            dataGridView_reactifs.setValueAt("*", i, 0);
        }
        for (int i = 0; i < dataGridView_produits.getRowCount(); i++) {
            dataGridView_produits.setValueAt("-", i, 0);
        }
        for (int i = 0; i < _r3._reactifs.size(); i++) {
            dataGridView_reactifs.setValueAt(_r3._reactifs.get(i), i, 0);
        }
        for (int i = 0; i < _r3._produits.size(); i++) {
            dataGridView_produits.setValueAt(_r3._produits.get(i), i, 0);
        }

        jTextDescription.setText(_r3._description.getText());

        // Ajout des elements de reaction
        for (int i = 0; i < _r3._ListElementsReactions.size(); i++) {
            WndEditElementDeReaction elt = _r3._ListElementsReactions.get(i);
            jPanel_cplx.add(elt);
            elt._parent = jPanel_cplx;
            //elt.WndEditElementDeReaction_Load();
        }

        // Ajout de l'element final pour la reaction complexe (FIN)
        WndEditElementDeReaction elt = null;
        elt = new WndEditElementDeReaction();
        elt._parent = jPanel_cplx;
        jPanel_cplx.add(elt);
        elt.WndEditElementDeReaction_Load();// elt.setLocation(0, 0);
        elt._parent = jPanel_cplx;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane5 = new javax.swing.JScrollPane();
        jTextDescription = new javax.swing.JTextArea();
        jLabelProba = new javax.swing.JLabel();
        jLabelPositions = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        dataGridView_reactifs = new javax.swing.JTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        dataGridView_produits = new javax.swing.JTable();
        jLabelProduits = new javax.swing.JLabel();
        textBox_k = new javax.swing.JTextField();
        jLabelComportement = new javax.swing.JLabel();
        button_valider = new javax.swing.JButton();
        button_annuler = new javax.swing.JButton();
        jLabelNom = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jScrollPane4 = new javax.swing.JScrollPane();
        jPanel_cplx = new javax.swing.JPanel();
        jLabelReactifs = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabelDescription = new javax.swing.JLabel();
        textBox_etiquette = new javax.swing.JTextField();

        setAlwaysOnTop(true);
        setBackground(new java.awt.Color(192, 189, 255));
        setResizable(false);
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                formMouseReleased(evt);
            }
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                formMouseClicked(evt);
            }
        });
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });
        addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                formMouseMoved(evt);
            }
        });
        getContentPane().setLayout(null);

        jScrollPane5.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane5.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

        jTextDescription.setColumns(19);
        jTextDescription.setLineWrap(true);
        jTextDescription.setRows(5);
        jTextDescription.setToolTipText("Write a short description");
        jScrollPane5.setViewportView(jTextDescription);

        getContentPane().add(jScrollPane5);
        jScrollPane5.setBounds(320, 40, 170, 60);

        jLabelProba.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        jLabelProba.setText("Probabilite ="); // NOI18N
        getContentPane().add(jLabelProba);
        jLabelProba.setBounds(10, 80, 90, 15);

        jLabelPositions.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        jLabelPositions.setText("Positions"); // NOI18N
        getContentPane().add(jLabelPositions);
        jLabelPositions.setBounds(240, 110, 63, 15);

        dataGridView_reactifs.setBackground(new java.awt.Color(200, 200, 200));
        dataGridView_reactifs.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        dataGridView_reactifs.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        dataGridView_reactifs.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null},
                {null},
                {null}
            },
            new String [] {
                "Title 1"
            }
        ));
        dataGridView_reactifs.setToolTipText("Reactive entities");
        dataGridView_reactifs.setColumnSelectionAllowed(true);
        dataGridView_reactifs.setRowHeight(36);
        dataGridView_reactifs.getTableHeader().setReorderingAllowed(false);
        dataGridView_reactifs.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                dataGridView_reactifsPropertyChange(evt);
            }
        });
        jScrollPane1.setViewportView(dataGridView_reactifs);
        dataGridView_reactifs.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

        getContentPane().add(jScrollPane1);
        jScrollPane1.setBounds(40, 130, 160, 130);

        dataGridView_produits.setBackground(new java.awt.Color(200, 200, 200));
        dataGridView_produits.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        dataGridView_produits.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        dataGridView_produits.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null},
                {null},
                {null}
            },
            new String [] {
                "Title 1"
            }
        ));
        dataGridView_produits.setToolTipText("Products of the behaviour");
        dataGridView_produits.setRowHeight(36);
        dataGridView_produits.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                dataGridView_produitsMouseReleased(evt);
            }
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                dataGridView_produitsMouseClicked(evt);
            }
        });
        dataGridView_produits.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                dataGridView_produitsPropertyChange(evt);
            }
        });
        jScrollPane2.setViewportView(dataGridView_produits);

        getContentPane().add(jScrollPane2);
        jScrollPane2.setBounds(320, 130, 170, 130);

        jLabelProduits.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        jLabelProduits.setText("Produits"); // NOI18N
        getContentPane().add(jLabelProduits);
        jLabelProduits.setBounds(380, 110, 63, 15);

        textBox_k.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        textBox_k.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        textBox_k.setText("1"); // NOI18N
        textBox_k.setToolTipText("Must be between 0 (never) and 1 (always)");
        getContentPane().add(textBox_k);
        textBox_k.setBounds(100, 75, 50, 21);

        jLabelComportement.setFont(new java.awt.Font("DejaVu Sans", 1, 24)); // NOI18N
        jLabelComportement.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelComportement.setText("Comportement"); // NOI18N
        getContentPane().add(jLabelComportement);
        jLabelComportement.setBounds(0, 0, 510, 27);

        button_valider.setBackground(new java.awt.Color(153, 255, 153));
        button_valider.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        button_valider.setText("Ok"); // NOI18N
        button_valider.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                button_validerMouseClicked(evt);
            }
        });
        getContentPane().add(button_valider);
        button_valider.setBounds(10, 280, 260, 40);

        button_annuler.setBackground(new java.awt.Color(255, 153, 153));
        button_annuler.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        button_annuler.setText("Annuler"); // NOI18N
        button_annuler.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                button_annulerMouseClicked(evt);
            }
        });
        button_annuler.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_annulerActionPerformed(evt);
            }
        });
        getContentPane().add(button_annuler);
        button_annuler.setBounds(270, 280, 230, 40);

        jLabelNom.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        jLabelNom.setText("Nom comport."); // NOI18N
        getContentPane().add(jLabelNom);
        jLabelNom.setBounds(10, 50, 90, 15);

        jButton1.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        jButton1.setForeground(new java.awt.Color(255, 0, 0));
        jButton1.setText("x");
        jButton1.setToolTipText("Clean the line");
        jButton1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton1MouseClicked(evt);
            }
        });
        getContentPane().add(jButton1);
        jButton1.setBounds(14, 150, 20, 30);

        jButton2.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        jButton2.setForeground(new java.awt.Color(255, 0, 0));
        jButton2.setText("x");
        jButton2.setToolTipText("Clean the line");
        jButton2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton2MouseClicked(evt);
            }
        });
        getContentPane().add(jButton2);
        jButton2.setBounds(14, 187, 20, 30);

        jButton3.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        jButton3.setForeground(new java.awt.Color(255, 0, 0));
        jButton3.setText("x");
        jButton3.setToolTipText("Clean the line");
        jButton3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton3MouseClicked(evt);
            }
        });
        getContentPane().add(jButton3);
        jButton3.setBounds(14, 223, 20, 30);

        jPanel_cplx.setBackground(java.awt.Color.white);
        jPanel_cplx.setLayout(new javax.swing.BoxLayout(jPanel_cplx, javax.swing.BoxLayout.Y_AXIS));
        jScrollPane4.setViewportView(jPanel_cplx);

        getContentPane().add(jScrollPane4);
        jScrollPane4.setBounds(10, 130, 490, 140);

        jLabelReactifs.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        jLabelReactifs.setText("Reactifs"); // NOI18N
        getContentPane().add(jLabelReactifs);
        jLabelReactifs.setBounds(90, 110, 63, 15);

        jLabel19.setBackground(new java.awt.Color(153, 153, 255));
        jLabel19.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jLabel19.setOpaque(true);
        getContentPane().add(jLabel19);
        jLabel19.setBounds(0, 0, 510, 30);

        jLabelDescription.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        jLabelDescription.setText("Descr."); // NOI18N
        getContentPane().add(jLabelDescription);
        jLabelDescription.setBounds(260, 50, 50, 15);

        textBox_etiquette.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        textBox_etiquette.setText("nom_reaction"); // NOI18N
        textBox_etiquette.setToolTipText("Give a simple but comprehensive name");
        textBox_etiquette.setFocusCycleRoot(true);
        textBox_etiquette.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                textBox_etiquetteActionPerformed(evt);
            }
        });
        textBox_etiquette.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                textBox_etiquetteKeyTyped(evt);
            }
        });
        getContentPane().add(textBox_etiquette);
        textBox_etiquette.setBounds(100, 45, 150, 21);

        setSize(new java.awt.Dimension(524, 372));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void button_validerMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_button_validerMouseClicked
        // TODO add your handling code here:
        // Verification que le premier reactif n'est ni "0" ni "*"
        if (textBox_etiquette.getText().equals("")) {
            if (Lang.getInstance().getLang().equals("FR")) {
                JOptionPane.showMessageDialog(this, "Merci de nommer le comportement.", "Information", JOptionPane.INFORMATION_MESSAGE, null);
            } else {
                JOptionPane.showMessageDialog(this, "Please name the behaviour.", "Information", JOptionPane.INFORMATION_MESSAGE, null);
            }
            return;
        }

        int type = 1;

        String contenu_0 = (dataGridView_reactifs.getValueAt(0, 0).toString());
        if (type != 2 && (contenu_0.equals("*") || contenu_0.equals("0"))) // || contenu_0.equals("0")))
        {
            //JOptionPane.showMessageDialog(this, "Il n'est pas possible d'avoir comme premier reactif \"*\" ou \"0\". Veuillez en changer svp.");
            if (Lang.getInstance().getLang().equals("FR")) {
                JOptionPane.showMessageDialog(this, "Il n'est pas possible d'avoir comme premier reactif \"*\" ou 0. Veuillez en changer svp.");
            } else {
                JOptionPane.showMessageDialog(this, "The first reactive cannot be \"*\" or 0.");
            }

            return;
        }
        String str2 = textBox_etiquette.getText();
        // Verification que le nom du comportement n'existe pas deja
        boolean nom_existe = false;

        for (Behavior lstCmpt1 : lstCmpt) {
            if (!lstCmpt1.equals(_r3)) {
                if (lstCmpt1.TrouveEtiquette(str2) >= 0) {
                    nom_existe = true;
                }
            }
        }

        for (Entity lst_cli1 : entities) {
            if (lst_cli1.TrouveEtiquette(str2) >= 0) {
                nom_existe = true;
            }
        }

        if (nom_existe == true) {
            if (Lang.getInstance().getLang().equals("FR")) {
                JOptionPane.showMessageDialog(this, "Ce nom existe deja. Veuillez en changer.");
            } else {
                JOptionPane.showMessageDialog(this, "This name already exists. Please change it.");
            }

            return;
        }

        String code_genere = genererCode();

        if (code_genere.startsWith("Erreur")) {
            JOptionPane.showMessageDialog(this, code_genere);
            return;
        }

        _r3._reactifs.clear();
        _r3._produits.clear();
        // Affectation de la valeur par defaut des combobox
        for (int r = 0; r < dataGridView_reactifs.getRowCount(); r++) {
            String str = dataGridView_reactifs.getValueAt(r, 0).toString();
            _r3._reactifs.add(str);
        }
        for (int p = 0; p < dataGridView_produits.getRowCount(); p++) {
            String str = dataGridView_produits.getValueAt(p, 0).toString();
            _r3._produits.add(str);
        }

        // Valeur d k
        try {
            _r3.set_k(Double.parseDouble(textBox_k.getText()));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e);
            _r3.set_k(0);
        }

        String etiq = textBox_etiquette.getText();
        _r3.setEtiquettes(etiq);

        String desc = jTextDescription.getText();
        _r3._description.setText(desc);
        //_r3._description.setText(richTextBox_description.getText());

        _r3.LblTitre.setVisible(true);
        _r3.LblTitre.setLocation(0, 0);
        _r3.LblTitre.setSize(_r3.getWidth(), _r3.LblTitre.getHeight());
        String titre = etiq;
        _r3.LblTitre.setText(titre);
  
        // Memorisation de la liste des elements de reaction
        _r3._ListElementsReactions.clear();
        for (int i = 0; i < jPanel_cplx.getComponentCount() - 1; i++) {
            WndEditElementDeReaction elt = (WndEditElementDeReaction) jPanel_cplx.getComponent(i);
            _r3._ListElementsReactions.add(elt);
        }

        _r3._code.setText(code_genere/*jTextArea_code.getText()*/);
        DialogResult = "OK";
//        if (_env != null && _env.frm3D != null) {
//            _env.frm3D.setVisible(_env_affiche3D);
//        }
        setVisible(false);
    }//GEN-LAST:event_button_validerMouseClicked

    public String genererCode() {
        // Generation du code equivalent aux des sequences complexes
        String code_genere = "";
        for (int i = 0; i < jPanel_cplx.getComponentCount() - 1; i++) {
            WndEditElementDeReaction elt = (WndEditElementDeReaction) jPanel_cplx.getComponent(i);
            String type_elt = elt.jComboBox_cdt_act.getSelectedItem().toString();

            String type0 = "", type1 = "", type2 = "", type3 = "", type4 = "", nom0 = "", nom1 = "", nom2 = "", nom3 = "", nom4 = "";
            if (elt.jComboBox_type0.getSelectedItem() != null) {
                type0 = elt.jComboBox_type0.getSelectedItem().toString();
            }
            if (elt.jComboBox_type1.getSelectedItem() != null) {
                type1 = elt.jComboBox_type1.getSelectedItem().toString();
            }
            if (elt.jComboBox_type2.getSelectedItem() != null) {
                type2 = elt.jComboBox_type2.getSelectedItem().toString();
            }
            if (elt.jComboBox_type3.getSelectedItem() != null) {
                type3 = elt.jComboBox_type3.getSelectedItem().toString();
            }
            if (elt.jComboBox_type4.getSelectedItem() != null) {
                type4 = elt.jComboBox_type4.getSelectedItem().toString();
            }
            if (elt.jComboBox_nom0.getSelectedItem() != null) {
                nom0 = elt.jComboBox_nom0.getSelectedItem().toString();
            }
            if (elt.jComboBox_nom1.getSelectedItem() != null) {
                nom1 = elt.jComboBox_nom1.getSelectedItem().toString();
            }
            if (elt.jComboBox_nom2.getSelectedItem() != null) {
                nom2 = elt.jComboBox_nom2.getSelectedItem().toString();
            }
            if (elt.jComboBox_nom3.getSelectedItem() != null) {
                nom3 = elt.jComboBox_nom3.getSelectedItem().toString();
            }
            if (elt.jComboBox_nom4.getSelectedItem() != null) {
                nom4 = elt.jComboBox_nom4.getSelectedItem().toString();
            }

            if (type_elt.equals(WndEditElementDeReaction.str_est_situe_en_absolu)) {
                if (type0.equals("") || nom0.equals("") || nom1.equals("") || nom2.equals("") || nom3.equals("")) {
                    return "Erreur ligne " + (new Integer(i + 1)).toString() + ".\nMerci de compléter tous les champs";
                }
                try {
                    Integer.parseInt(nom1);
                    Integer.parseInt(nom2);
                    Integer.parseInt(nom3);
                } catch (Exception ex) {
                    return "Erreur ligne " + (new Integer(i + 1)).toString() + ".\nMerci de placer une valeur numerique pour dx, dy et dz";
                }
                code_genere += "reaction.cdtReaxelEn(\"" + type0 + "\", \"" + nom0 + "\", " + nom1 + ", " + nom2 + ", " + nom3 + ");";
            }
            if (type_elt.equals(WndEditElementDeReaction.str_est_situe_en_relatif)) {
                if (type0.equals("") || nom0.equals("") || nom1.equals("") || nom2.equals("") || nom3.equals("") || nom4.equals("")) {
                    return "Erreur ligne " + (new Integer(i + 1)).toString() + ".\nMerci de compléter tous les champs";
                }
                try {
                    Integer.parseInt(nom1);
                    Integer.parseInt(nom2);
                    Integer.parseInt(nom3);
                } catch (Exception ex) {
                    return "Erreur ligne " + (new Integer(i + 1)).toString() + ".\nMerci de placer une valeur numerique pour dx, dy et dz";
                }
                code_genere += "reaction.cdtReaxelEnRelatif(\"" + type0 + "\", \"" + nom0 + "\", " + nom1 + ", " + nom2 + ", " + nom3 + ", \"" + nom4 + "\");";
            }
            if (type_elt.equals(WndEditElementDeReaction.str_est_en_contact_avec)) {
                if (type0.equals("") || nom0.equals("") || nom1.equals("")) {
                    return "Erreur ligne " + (new Integer(i + 1)).toString() + ".\nMerci de compléter tous les champs";
                }
                code_genere += "reaction.cdtReaxelTouche(\"" + type0 + "\", \"" + nom0 + "\", \"" + nom1 + "\");";
            }
            if (type_elt.equals(WndEditElementDeReaction.str_est_lie_a)) {
                if (type0.equals("") || nom0.equals("") || nom1.equals("") || type2.equals("") || nom2.equals("")) {
                    return "Erreur ligne " + (new Integer(i + 1)).toString() + ".\nMerci de compléter tous les champs";
                }
                code_genere += "reaction.cdtReaxelLieAvec(\"" + type0 + "\", \"" + nom0 + "\", \"" + nom1 + "\", \"" + type2 + "\", \"" + nom2 + "\");";
            }
            if (type_elt.equals(WndEditElementDeReaction.str_sera_cree_en)) {
                if (type0.equals("") || nom0.equals("") || nom1.equals("") || nom2.equals("") || nom3.equals("") || nom4.equals("")) {
                    return "Erreur ligne " + (new Integer(i + 1)).toString() + ".\nMerci de compléter tous les champs";
                }
                code_genere += "reaction.actAjouterReaxelEn(\"" + type0 + "\", \"" + nom0 + "\", " + nom1 + ", " + nom2 + ", " + nom3 + ", \"" + nom4 + "\");";
            }
            if (type_elt.equals(WndEditElementDeReaction.str_sera_cree_autour_de)) {
                if (type0.equals("") || nom0.equals("") || nom1.equals("")) {
                    return "Erreur ligne " + (new Integer(i + 1)).toString() + ".\nMerci de compléter tous les champs";
                }
                code_genere += "reaction.actAjouterReaxelAutour(\"" + type0 + "\", \"" + nom0 + "\", \"" + nom1 + "\");";
            }
            if (type_elt.equals(WndEditElementDeReaction.str_sera_cree_entre)) {
                if (type0.equals("") || nom0.equals("") || nom1.equals("") || nom2.equals("")) {
                    return "Erreur ligne " + (new Integer(i + 1)).toString() + ".\nMerci de compléter tous les champs";
                }
                code_genere += "reaction.actAjouterConnexelSur(\"" + type0 + "\", \"" + nom0 + "\", \"" + nom1 + "\", \"" + nom2 + "\");";
            }
            if (type_elt.equals(WndEditElementDeReaction.str_sera_reconnecte_sur)) {
                if (nom0.equals("") || nom1.equals("") || nom2.equals("")) {
                    return "Erreur ligne " + (new Integer(i + 1)).toString() + ".\nMerci de compléter tous les champs";
                }
                code_genere += "reaction.actConnecterConnexel(\"" + nom0 + "\", \"" + nom1 + "\", \"" + nom2 + "\");";
            }
            if (type_elt.equals(WndEditElementDeReaction.str_remplacera_l_entite)) {
                if (type0.equals("") || nom0.equals("") || nom1.equals("")) {
                    return "Erreur ligne " + (new Integer(i + 1)).toString() + ".\nMerci de compléter tous les champs";
                }
                code_genere += "reaction.actRemplacerReaxel(\"" + type0 + "\", \"" + nom0 + "\", \"" + nom1 + "\");";
            }
            if (type_elt.equals(WndEditElementDeReaction.str_remplacera_le_lien)) {
                if (type0.equals("") || nom0.equals("") || nom1.equals("")) {
                    return "Erreur ligne " + (new Integer(i + 1)).toString() + ".\nMerci de compléter tous les champs";
                }
                code_genere += "reaction.actRemplacerConnexel(\"" + type0 + "\", \"" + nom0 + "\", \"" + nom1 + "\");";
            }
            if (type_elt.equals(WndEditElementDeReaction.str_sera_supprime_entite)) {
                if (nom0.equals("")) {
                    return "Erreur ligne " + (new Integer(i + 1)).toString() + ".\nMerci de compléter le champ";
                }
                code_genere += "reaction.actSupprimerReaxel(\"" + nom0 + "\");";
            }
            if (type_elt.equals(WndEditElementDeReaction.str_sera_supprime_lien)) {
                if (nom0.equals("")) {
                    return "Erreur ligne " + (new Integer(i + 1)).toString() + ".\nMerci de compléter le champ";
                }
                code_genere += "reaction.actSupprimerConnexel(\"" + nom0 + "\");";
            }

        }
        return code_genere;
    }

    private void button_annulerMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_button_annulerMouseClicked
        // TODO add your handling code here:
        DialogResult = new String("CANCEL");
//        if (_env != null && _env.frm3D != null) {
//            _env.frm3D.setVisible(_env_affiche3D);
//        }
        setVisible(false);
    }//GEN-LAST:event_button_annulerMouseClicked

    private void textBox_etiquetteKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_textBox_etiquetteKeyTyped
        // TODO add your handling code here:
        char c = evt.getKeyChar();
        if (c == '\\' || c == '/' || c == ':' || c == ' ' || c == '*' || c == '?' || c == '\"' || c == '<' || c == '>' || c == '|') {
            evt.consume();
            if (Lang.getInstance().getLang().equals("FR")) {
                JOptionPane.showMessageDialog(this, "Les caracteres \\ / : ESPACE * ? \" < > , et | sont interdits. Merci de votre comprehension", "ATTENTION", JOptionPane.INFORMATION_MESSAGE, null);
            } else {
                JOptionPane.showMessageDialog(this, "Characteres \\ / : SPACE * ? \" < > , and | are forbiden.", "ATTENTION", JOptionPane.INFORMATION_MESSAGE, null);
            }

        }
        //if (c == 'é' || c == 'è' || c == 'ê' || c == 'à' || c == 'â' || c == 'ï' || c == 'î' || c == 'ö' || c == 'ë' || c == 'ù' || c == 'ü' || c == 'û' || c == 'ç' || c == 'ÿ')
        //{
        //    evt.consume();
        //    JOptionPane.showMessageDialog(this, "Les caracteres accentues sont interdits. Merci de votre comprehension", "ATTENTION", JOptionPane.INFORMATION_MESSAGE, null);
        //}

    }//GEN-LAST:event_textBox_etiquetteKeyTyped

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        // TODO add your handling code here:
        this.tracerPositionCellules(this.getGraphics(), false);
    }//GEN-LAST:event_formComponentShown

    private void formMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseReleased
        // TODO add your handling code here:
        this.tracerPositionCellules(this.getGraphics(), false);
    }//GEN-LAST:event_formMouseReleased

    private void formMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseMoved
        // TODO add your handling code here:
        this.tracerPositionCellules(this.getGraphics(), false);
    }//GEN-LAST:event_formMouseMoved

    private void formMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseClicked
        // TODO add your handling code here:
        _mouseX = evt.getX();
        _mouseY = evt.getY();
        this.tracerPositionCellules(this.getGraphics(), true);
        repaint();
    }//GEN-LAST:event_formMouseClicked

    private void dataGridView_produitsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_dataGridView_produitsMouseClicked
        // TODO add your handling code here:
        this.tracerPositionCellules(this.getGraphics(), false);
        repaint();
    }//GEN-LAST:event_dataGridView_produitsMouseClicked

    private void dataGridView_produitsMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_dataGridView_produitsMouseReleased
        // TODO add your handling code here:
        this.tracerPositionCellules(this.getGraphics(), false);
        repaint();
    }//GEN-LAST:event_dataGridView_produitsMouseReleased

    private void dataGridView_produitsPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_dataGridView_produitsPropertyChange
        // TODO add your handling code here:
        this.tracerPositionCellules(this.getGraphics(), false);
        repaint();
    }//GEN-LAST:event_dataGridView_produitsPropertyChange

    private void dataGridView_reactifsPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_dataGridView_reactifsPropertyChange
        // TODO add your handling code here:
        this.tracerPositionCellules(this.getGraphics(), false);
        repaint();
    }//GEN-LAST:event_dataGridView_reactifsPropertyChange

    private void button_annulerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_annulerActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_button_annulerActionPerformed

    private void jButton1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton1MouseClicked
        // TODO add your handling code here:
        dataGridView_reactifs.setValueAt("*", 0, 0);
        dataGridView_produits.setValueAt("-", 0, 0);
        this.tracerPositionCellules(this.getGraphics(), false);
        repaint();
    }//GEN-LAST:event_jButton1MouseClicked

    private void jButton2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton2MouseClicked
        dataGridView_reactifs.setValueAt("*", 1, 0);
        dataGridView_produits.setValueAt("-", 1, 0);
        this.tracerPositionCellules(this.getGraphics(), false);
        repaint();
    }//GEN-LAST:event_jButton2MouseClicked

    private void jButton3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton3MouseClicked
        dataGridView_reactifs.setValueAt("*", 2, 0);
        dataGridView_produits.setValueAt("-", 2, 0);
        this.tracerPositionCellules(this.getGraphics(), false);
        repaint();
    }//GEN-LAST:event_jButton3MouseClicked

    private void textBox_etiquetteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_textBox_etiquetteActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_textBox_etiquetteActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton button_annuler;
    private javax.swing.JButton button_valider;
    private javax.swing.JTable dataGridView_produits;
    private javax.swing.JTable dataGridView_reactifs;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabelComportement;
    private javax.swing.JLabel jLabelDescription;
    private javax.swing.JLabel jLabelNom;
    private javax.swing.JLabel jLabelPositions;
    private javax.swing.JLabel jLabelProba;
    private javax.swing.JLabel jLabelProduits;
    private javax.swing.JLabel jLabelReactifs;
    public javax.swing.JPanel jPanel_cplx;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JTextArea jTextDescription;
    private javax.swing.JTextField textBox_etiquette;
    private javax.swing.JTextField textBox_k;
    // End of variables declaration//GEN-END:variables

    public String getDialogResult() {
        return DialogResult;
    }

}
