/*
 * WndEditElementDeReaction.java
 *
 * Created on 4 fevrier 2008, 14:24
 */

package netbiodyn.ihm;

import java.awt.Color;
import java.io.BufferedWriter;
import java.util.ArrayList;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 *
 * @author  user
 */
public class WndEditElementDeReaction extends javax.swing.JPanel {
    // Diverses cdts/acts
    public static String str_FIN = "FIN";
    public static String str_est_situe_en_absolu = "est situe en absolu";
    public static String str_est_situe_en_relatif = "est situe en relatif";
    public static String str_est_en_contact_avec = "est en contact avec";
    public static String str_est_lie_a = "est lie a";
    public static String str_sera_cree_en = "sera cree en";
    public static String str_sera_cree_autour_de= "sera cree autour de";
    public static String str_remplacera_l_entite = "remplacera l'entite";
    public static String str_sera_cree_entre= "sera cree entre";
    public static String str_remplacera_le_lien= "remplacera le lien";
    public static String str_sera_reconnecte_sur = "sera reconnecte sur";
    public static String str_sera_supprime_entite= "sera supprime (entite)";
    public static String str_sera_supprime_lien = "sera supprime (lien)";
        
    /** Creates new form WndEditElementDeReaction */
    public WndEditElementDeReaction() {
        initComponents();
    }
    
    public void WndEditElementDeReaction_Load() {
              
        // Remplissage de la combo des types de cdts/acts
        javax.swing.DefaultComboBoxModel model = new DefaultComboBoxModel();
        
        model.addElement(str_FIN);
        model.addElement(str_est_situe_en_absolu);
        model.addElement(str_est_situe_en_relatif);
        model.addElement(str_est_en_contact_avec);
        model.addElement(str_est_lie_a);
        model.addElement(str_sera_cree_en);
        model.addElement(str_sera_cree_autour_de);
        model.addElement(str_sera_cree_entre);
        model.addElement(str_sera_reconnecte_sur);
        model.addElement(str_remplacera_l_entite);
        model.addElement(str_remplacera_le_lien);
        model.addElement(str_sera_supprime_entite);
        model.addElement(str_sera_supprime_lien);

        jComboBox_cdt_act.setModel(model);
        
        //if(_parent == null)
        //    _parent = new JPanel();

        initConditionAction(str_FIN);

        setSize(900, 73);
    }
    
    public void initConditionAction(String nom) {
          // FIN
        
        if(nom.equals(str_FIN)) {
            jComboBox_type0.setVisible(false);
            jComboBox_type1.setVisible(false);
            jComboBox_type2.setVisible(false);
            jComboBox_type3.setVisible(false);
            jComboBox_type4.setVisible(false);

            jComboBox_nom0.setVisible(false);
            jComboBox_nom1.setVisible(false);
            jComboBox_nom2.setVisible(false);
            jComboBox_nom3.setVisible(false);
            jComboBox_nom4.setVisible(false);

            jLabel1.setVisible(false);
            jLabel2.setVisible(false);
            jLabel3.setVisible(false);
            
            // S'il n'est pas le dernier element, on le supprime
            if(_parent.getComponentCount() > 0) {
                if(this != _parent.getComponent(_parent.getComponentCount()-1)) {
                    _parent.remove(this);
                    this.setVisible(false);
                    return;
                }
            }
            
        } else {
            // Si c'est le dernier element, on ajoute un nouveau element FIN
            if(_parent.getComponentCount() > 0) {
                if(this == _parent.getComponent(_parent.getComponentCount()-1)) {
                    WndEditElementDeReaction elt = null;
                    elt = new WndEditElementDeReaction(); elt._env = _env; elt._parent = _parent;
                    elt._parent.add(elt);
                    elt.WndEditElementDeReaction_Load();// elt.setLocation(0, 0);
                }   
            }
        }
        
        if(nom.equals(str_est_situe_en_absolu)) {
            
            jComboBox_type0.setVisible(true);
            jComboBox_type1.setVisible(true);
            jComboBox_type2.setVisible(true);
            jComboBox_type3.setVisible(true);
            jComboBox_type4.setVisible(false);

            placerModeleTypesNoeuds(jComboBox_type0);
            placerModeleTypeDx(jComboBox_type1);
            placerModeleTypeDy(jComboBox_type2);
            placerModeleTypeDz(jComboBox_type3);

            jComboBox_nom0.setVisible(true);
            jComboBox_nom1.setVisible(true);
            jComboBox_nom2.setVisible(true);
            jComboBox_nom3.setVisible(true);
            jComboBox_nom4.setVisible(false);

            jLabel1.setVisible(true);
            jLabel2.setVisible(true);
            jLabel3.setVisible(false);
            
            jLabel1.setText("et");
            jLabel2.setText("et");            

            jComboBox_nom0.setEditable(true);
            jComboBox_nom1.setEditable(true);
            jComboBox_nom2.setEditable(true);
            jComboBox_nom3.setEditable(true);

            setBackground(Color.white);
        }
        
        if(nom.equals(str_est_situe_en_relatif)) {
            
            jComboBox_type0.setVisible(true);
            jComboBox_type1.setVisible(true);
            jComboBox_type2.setVisible(true);
            jComboBox_type3.setVisible(true);
              jComboBox_type4.setVisible(false);

            placerModeleTypesNoeuds(jComboBox_type0);
            placerModeleTypeDx(jComboBox_type1);
            placerModeleTypeDy(jComboBox_type2);
            placerModeleTypeDz(jComboBox_type3);
            //placerModeleTypesNoeuds(jComboBox_type4);

            jComboBox_nom0.setVisible(true);
            jComboBox_nom1.setVisible(true);
            jComboBox_nom2.setVisible(true);
            jComboBox_nom3.setVisible(true);
            jComboBox_nom4.setVisible(true);

            jLabel1.setVisible(true);
            jLabel2.setVisible(true);
            jLabel3.setVisible(true);
            
            jLabel1.setText("et");
            jLabel2.setText("et");
            jLabel3.setText("relatif a");            

            jComboBox_nom0.setEditable(true);
            jComboBox_nom1.setEditable(true);
            jComboBox_nom2.setEditable(true);
            jComboBox_nom3.setEditable(true);
            jComboBox_nom4.setEditable(false);
            
            setBackground(Color.lightGray);
        }
        
        if(nom.equals(str_est_en_contact_avec)) {
            
            jComboBox_type0.setVisible(true);
              jComboBox_type1.setVisible(false);
            jComboBox_type2.setVisible(false);
            jComboBox_type3.setVisible(false);
            jComboBox_type4.setVisible(false);

            placerModeleTypesNoeuds(jComboBox_type0);
            //placerModeleTypesNoeuds(jComboBox_type1);

            jComboBox_nom0.setVisible(true);
            jComboBox_nom1.setVisible(true);
            jComboBox_nom2.setVisible(false);
            jComboBox_nom3.setVisible(false);
            jComboBox_nom4.setVisible(false);

            jLabel1.setVisible(false);
            jLabel2.setVisible(false);
            jLabel3.setVisible(false);

            jComboBox_nom0.setEditable(true);
            jComboBox_nom1.setEditable(false);
            
            setBackground(Color.gray);
        }

        if(nom.equals(str_est_lie_a)) {
            
            jComboBox_type0.setVisible(true);
              jComboBox_type1.setVisible(false);
            jComboBox_type2.setVisible(true);
            jComboBox_type3.setVisible(false);
            jComboBox_type4.setVisible(false);

            placerModeleTypesNoeuds(jComboBox_type0);
            //placerModeleTypesNoeuds(jComboBox_type1);
            placerModeleTypesLiens(jComboBox_type2);

            jComboBox_nom0.setVisible(true);
            jComboBox_nom1.setVisible(true);
            jComboBox_nom2.setVisible(true);
            jComboBox_nom3.setVisible(false);
            jComboBox_nom4.setVisible(false);

            jLabel1.setVisible(true);
            jLabel2.setVisible(false);
            jLabel3.setVisible(false);
            
            jLabel1.setText("avec");
            jLabel1.setForeground(Color.white);

            jComboBox_nom0.setEditable(true);
            jComboBox_nom1.setEditable(false);
            jComboBox_nom2.setEditable(true);
            
            setBackground(Color.darkGray);
        } else {
            jLabel1.setForeground(Color.black);
        }

        if(nom.equals(str_sera_cree_en)) {
            
            jComboBox_type0.setVisible(true);
            jComboBox_type1.setVisible(true);
            jComboBox_type2.setVisible(true);
            jComboBox_type3.setVisible(true);
              jComboBox_type4.setVisible(false);

            placerModeleTypesNoeuds(jComboBox_type0);
            placerModeleTypeDx(jComboBox_type1);
            placerModeleTypeDy(jComboBox_type2);
            placerModeleTypeDz(jComboBox_type3);
            //placerModeleTypesNoeuds(jComboBox_type4);

            jComboBox_nom0.setVisible(true);
            jComboBox_nom1.setVisible(true);
            jComboBox_nom2.setVisible(true);
            jComboBox_nom3.setVisible(true);
            jComboBox_nom4.setVisible(true);

            jLabel1.setVisible(true);
            jLabel2.setVisible(true);
            jLabel3.setVisible(true);
            
            jLabel1.setText("et");
            jLabel2.setText("et");
            jLabel3.setText("relatif a");            

            jComboBox_nom0.setEditable(true);
            jComboBox_nom1.setEditable(true);
            jComboBox_nom2.setEditable(true);
            jComboBox_nom3.setEditable(true);
            jComboBox_nom4.setEditable(false);
            
            setBackground(Color.green);
        }
        
        if(nom.equals(str_sera_cree_entre)) {
            
            jComboBox_type0.setVisible(true);
            jComboBox_type1.setVisible(false);
            jComboBox_type2.setVisible(false);
            jComboBox_type3.setVisible(false);
            jComboBox_type4.setVisible(false);

            placerModeleTypesLiens(jComboBox_type0);
            //placerModeleTypesNoeuds(jComboBox_type1);
            //placerModeleTypesNoeuds(jComboBox_type2);

            jComboBox_nom0.setVisible(true);
            jComboBox_nom1.setVisible(true);
            jComboBox_nom2.setVisible(true);
            jComboBox_nom3.setVisible(false);
            jComboBox_nom4.setVisible(false);

            jLabel1.setVisible(true);
            jLabel2.setVisible(false);
            jLabel3.setVisible(false);
            
            jLabel1.setText("et");

            jComboBox_nom0.setEditable(true);
            jComboBox_nom1.setEditable(false);
            jComboBox_nom2.setEditable(false);
            
            setBackground(new Color(0,128,0)); // darkGreen
        }
        
        if(nom.equals(str_sera_cree_autour_de)) {
            
            jComboBox_type0.setVisible(true);
              jComboBox_type1.setVisible(false);
            jComboBox_type2.setVisible(false);
            jComboBox_type3.setVisible(false);
            jComboBox_type4.setVisible(false);

            placerModeleTypesNoeuds(jComboBox_type0);
            //placerModeleTypesNoeuds(jComboBox_type1);

            jComboBox_nom0.setVisible(true);
            jComboBox_nom1.setVisible(true);
            jComboBox_nom2.setVisible(false);
            jComboBox_nom3.setVisible(false);
            jComboBox_nom4.setVisible(false);

            jLabel1.setVisible(false);
            jLabel2.setVisible(false);
            jLabel3.setVisible(false);

            jComboBox_nom0.setEditable(true);
            jComboBox_nom1.setEditable(false);
            
            setBackground(new Color(0,128+64,0));
        }
        
        if(nom.equals(str_sera_reconnecte_sur)) {
            
            jComboBox_type0.setVisible(false);
            jComboBox_type1.setVisible(false);
            jComboBox_type2.setVisible(false);
            jComboBox_type3.setVisible(false);
            jComboBox_type4.setVisible(false);

            //placerModeleTypesLiens(jComboBox_type0);
            //placerModeleTypesNoeuds(jComboBox_type1);
            //placerModeleTypesNoeuds(jComboBox_type2);

            jComboBox_nom0.setVisible(true);
            jComboBox_nom1.setVisible(true);
            jComboBox_nom2.setVisible(true);
            jComboBox_nom3.setVisible(false);
            jComboBox_nom4.setVisible(false);

            jLabel1.setVisible(true);
            jLabel2.setVisible(false);
            jLabel3.setVisible(false);
            
            jLabel1.setText("et");

            jComboBox_nom0.setEditable(false);
            jComboBox_nom1.setEditable(false);
            jComboBox_nom2.setEditable(false);

            setBackground(Color.blue);
        }
        
        if(nom.equals(str_remplacera_l_entite)) {
            
            jComboBox_type0.setVisible(true);
            jComboBox_type1.setVisible(false);
            jComboBox_type2.setVisible(false);
            jComboBox_type3.setVisible(false);
            jComboBox_type4.setVisible(false);

            placerModeleTypesNoeuds(jComboBox_type0);
            //placerModeleTypesNoeuds(jComboBox_type1);

            jComboBox_nom0.setVisible(true);
            jComboBox_nom1.setVisible(true);
            jComboBox_nom2.setVisible(false);
            jComboBox_nom3.setVisible(false);
            jComboBox_nom4.setVisible(false);

            jLabel1.setVisible(false);
            jLabel2.setVisible(false);
            jLabel3.setVisible(false);

            jComboBox_nom0.setEditable(true);
            jComboBox_nom1.setEditable(false);

            setBackground(Color.yellow);
        }
        
        if(nom.equals(str_remplacera_le_lien)) {
            
            jComboBox_type0.setVisible(true);
              jComboBox_type1.setVisible(false);
            jComboBox_type2.setVisible(false);
            jComboBox_type3.setVisible(false);
            jComboBox_type4.setVisible(false);

            placerModeleTypesLiens(jComboBox_type0);
            //placerModeleTypesLiens(jComboBox_type1);

            jComboBox_nom0.setVisible(true);
            jComboBox_nom1.setVisible(true);
            jComboBox_nom2.setVisible(false);
            jComboBox_nom3.setVisible(false);
            jComboBox_nom4.setVisible(false);

            jLabel1.setVisible(false);
            jLabel2.setVisible(false);
            jLabel3.setVisible(false);

            jComboBox_nom0.setEditable(true);
            jComboBox_nom1.setEditable(false);

            setBackground(new Color(128,128,0)); // DarkYellow
        }   
        
        if(nom.equals(str_sera_supprime_entite)) {
            
              jComboBox_type0.setVisible(false);
            jComboBox_type1.setVisible(false);
            jComboBox_type2.setVisible(false);
            jComboBox_type3.setVisible(false);
            jComboBox_type4.setVisible(false);

            //placerModeleTypesNoeuds(jComboBox_type0);

            jComboBox_nom0.setVisible(true);
            jComboBox_nom1.setVisible(false);
            jComboBox_nom2.setVisible(false);
            jComboBox_nom3.setVisible(false);
            jComboBox_nom4.setVisible(false);

            jLabel1.setVisible(false);
            jLabel2.setVisible(false);
            jLabel3.setVisible(false);

            jComboBox_nom0.setEditable(false);

            setBackground(Color.red);
        }     
        
        if(nom.equals(str_sera_supprime_lien)) {
            
              jComboBox_type0.setVisible(false);
            jComboBox_type1.setVisible(false);
            jComboBox_type2.setVisible(false);
            jComboBox_type3.setVisible(false);
            jComboBox_type4.setVisible(false);

            //placerModeleTypesLiens(jComboBox_type0);

            jComboBox_nom0.setVisible(true);
            jComboBox_nom1.setVisible(false);
            jComboBox_nom2.setVisible(false);
            jComboBox_nom3.setVisible(false);
            jComboBox_nom4.setVisible(false);

            jLabel1.setVisible(false);
            jLabel2.setVisible(false);
            jLabel3.setVisible(false);

            jComboBox_nom0.setEditable(false);
            
            setBackground(new Color(128,0,0)); // DarkRed
        }   
        
        // Finalement, les comboBox des noms est pre-remplie
        remplirNomsConditionAction();
        
    }

    /** Cette fonction remplie les listes deroulantes des noms
     * avec les noms precedemment declares
     * 
     * @param aucun
     * @return void
     * @see placerModeleNomsNoeuds(JComboBox cb)
     */ 
    public void remplirNomsConditionAction() {
        // les noms utilisable par et element de reation
        // depend des noms delares par les precedents elements de reaction
        
        // Position de l'actuel composant dans la liste
        int pos = -1;
        for(int i=0;i<_parent.getComponentCount()-1;i++) {
            WndEditElementDeReaction elt = (WndEditElementDeReaction)_parent.getComponent(i);
            if(elt == this) {
                pos = i; i = _parent.getComponentCount();
            }
        }
        
        // Si l'element est trouve, on memorise tous les noms delares
        ArrayList<String> lst_noms_reaxels = new ArrayList<String>();
        ArrayList<String> lst_noms_connexels = new ArrayList<String>();
        if(pos >= 0) {
            for(int i=0;i<pos;i++) {
                WndEditElementDeReaction elt = (WndEditElementDeReaction)_parent.getComponent(i);
                if(elt.jComboBox_cdt_act.getSelectedItem().toString().equals(str_est_situe_en_absolu)) {
                    if(elt.jComboBox_nom0.getSelectedItem() != null)
                        lst_noms_reaxels.add(elt.jComboBox_nom0.getSelectedItem().toString());
                }
                if(elt.jComboBox_cdt_act.getSelectedItem().toString().equals(str_est_situe_en_relatif)) {
                    if(elt.jComboBox_nom0.getSelectedItem() != null)
                        lst_noms_reaxels.add(elt.jComboBox_nom0.getSelectedItem().toString());
                }
                if(elt.jComboBox_cdt_act.getSelectedItem().toString().equals(str_est_en_contact_avec)) {
                    if(elt.jComboBox_nom0.getSelectedItem() != null)
                        lst_noms_reaxels.add(elt.jComboBox_nom0.getSelectedItem().toString());
                }
                if(elt.jComboBox_cdt_act.getSelectedItem().toString().equals(str_est_lie_a)) {
                    if(elt.jComboBox_nom0.getSelectedItem() != null)
                        lst_noms_reaxels.add(elt.jComboBox_nom0.getSelectedItem().toString());
                    if(elt.jComboBox_nom2.getSelectedItem() != null)
                        lst_noms_connexels.add(elt.jComboBox_nom2.getSelectedItem().toString());
                }
                if(elt.jComboBox_cdt_act.getSelectedItem().toString().equals(str_sera_cree_en)) {
                    if(elt.jComboBox_nom0.getSelectedItem() != null)
                        lst_noms_reaxels.add(elt.jComboBox_nom0.getSelectedItem().toString());
                }
                if(elt.jComboBox_cdt_act.getSelectedItem().toString().equals(str_sera_cree_autour_de)) {
                    if(elt.jComboBox_nom0.getSelectedItem() != null)
                        lst_noms_reaxels.add(elt.jComboBox_nom0.getSelectedItem().toString());
                }
                if(elt.jComboBox_cdt_act.getSelectedItem().toString().equals(str_sera_cree_entre)) {
                    if(elt.jComboBox_nom0.getSelectedItem() != null)
                        lst_noms_connexels.add(elt.jComboBox_nom0.getSelectedItem().toString());
                }
                if(elt.jComboBox_cdt_act.getSelectedItem().toString().equals(str_remplacera_l_entite)) {
                    if(elt.jComboBox_nom0.getSelectedItem() != null)
                        lst_noms_reaxels.add(elt.jComboBox_nom0.getSelectedItem().toString());
                }
                if(elt.jComboBox_cdt_act.getSelectedItem().toString().equals(str_remplacera_le_lien)) {
                    if(elt.jComboBox_nom0.getSelectedItem() != null)
                        lst_noms_connexels.add(elt.jComboBox_nom0.getSelectedItem().toString());
                }

            }
        }
        
        // creation des modeles pour les comboBox
        // --------------------------------------
        // modeles noms noeuds et liens
        javax.swing.DefaultComboBoxModel model_reaxels0   = new DefaultComboBoxModel();
        javax.swing.DefaultComboBoxModel model_reaxels1   = new DefaultComboBoxModel();
        javax.swing.DefaultComboBoxModel model_reaxels2   = new DefaultComboBoxModel();
        javax.swing.DefaultComboBoxModel model_reaxels3   = new DefaultComboBoxModel();
        javax.swing.DefaultComboBoxModel model_reaxels4   = new DefaultComboBoxModel();
        javax.swing.DefaultComboBoxModel model_connexels0 = new DefaultComboBoxModel();        
        javax.swing.DefaultComboBoxModel model_connexels1 = new DefaultComboBoxModel();        
        javax.swing.DefaultComboBoxModel model_connexels2 = new DefaultComboBoxModel();        
        javax.swing.DefaultComboBoxModel model_connexels3 = new DefaultComboBoxModel();        
        javax.swing.DefaultComboBoxModel model_connexels4 = new DefaultComboBoxModel();        
        // modeles nombres entiers
        javax.swing.DefaultComboBoxModel model_entiers0   = new DefaultComboBoxModel();        
        javax.swing.DefaultComboBoxModel model_entiers1   = new DefaultComboBoxModel();        
        javax.swing.DefaultComboBoxModel model_entiers2   = new DefaultComboBoxModel();        
        javax.swing.DefaultComboBoxModel model_entiers3   = new DefaultComboBoxModel();        
        javax.swing.DefaultComboBoxModel model_entiers4   = new DefaultComboBoxModel();        
        // modele vide
        javax.swing.DefaultComboBoxModel model_vide   = new DefaultComboBoxModel();        
        
        for(int i=0;i<lst_noms_reaxels.size();i++) {
            model_reaxels0.addElement(lst_noms_reaxels.get((i)));
            model_reaxels1.addElement(lst_noms_reaxels.get((i)));
            model_reaxels2.addElement(lst_noms_reaxels.get((i)));
            model_reaxels3.addElement(lst_noms_reaxels.get((i)));
            model_reaxels4.addElement(lst_noms_reaxels.get((i)));
        }
        for(int i=0;i<lst_noms_connexels.size();i++) {
            model_connexels0.addElement(lst_noms_connexels.get((i)));
            model_connexels1.addElement(lst_noms_connexels.get((i)));
            model_connexels2.addElement(lst_noms_connexels.get((i)));
            model_connexels3.addElement(lst_noms_connexels.get((i)));
            model_connexels4.addElement(lst_noms_connexels.get((i)));
        }
        for(int i=-3;i<4;i++) {
            model_entiers0.addElement(new Integer(i).toString());
            model_entiers1.addElement(new Integer(i).toString());
            model_entiers2.addElement(new Integer(i).toString());
            model_entiers3.addElement(new Integer(i).toString());
            model_entiers4.addElement(new Integer(i).toString());
        }
        
        
        // Enfin, on ajoute ces noms aux listes deroulantes des noms de l'elt courant
        // selon le type de la condition/action courante
        if(jComboBox_cdt_act.getSelectedItem().toString().equals(str_est_situe_en_absolu)) {
            jComboBox_nom1.setModel(model_entiers1);
            jComboBox_nom2.setModel(model_entiers2);
            jComboBox_nom3.setModel(model_entiers3);
            jComboBox_nom1.setSelectedItem("0");
            jComboBox_nom2.setSelectedItem("0");
            jComboBox_nom3.setSelectedItem("0");
            
        }
        if(jComboBox_cdt_act.getSelectedItem().toString().equals(str_est_situe_en_absolu)) {
            jComboBox_nom1.setModel(model_entiers1);
            jComboBox_nom2.setModel(model_entiers2);
            jComboBox_nom3.setModel(model_entiers3);
            jComboBox_nom1.setSelectedItem("0");
            jComboBox_nom2.setSelectedItem("0");
            jComboBox_nom3.setSelectedItem("0");
        }
        if(jComboBox_cdt_act.getSelectedItem().toString().equals(str_est_situe_en_relatif)) {
            jComboBox_nom1.setModel(model_entiers1);
            jComboBox_nom2.setModel(model_entiers2);
            jComboBox_nom3.setModel(model_entiers3);
            jComboBox_nom4.setModel(model_reaxels4);
            jComboBox_nom1.setSelectedItem("0");
            jComboBox_nom2.setSelectedItem("0");
            jComboBox_nom3.setSelectedItem("0");
        }
        if(jComboBox_cdt_act.getSelectedItem().toString().equals(str_est_en_contact_avec)) {
            jComboBox_nom0.setModel(model_reaxels0);
            jComboBox_nom1.setModel(model_reaxels1);
        }
        if(jComboBox_cdt_act.getSelectedItem().toString().equals(str_est_lie_a)) {
            jComboBox_nom0.setModel(model_reaxels0);
            jComboBox_nom1.setModel(model_reaxels1);
            jComboBox_nom2.setModel(model_connexels2);
        }
        if(jComboBox_cdt_act.getSelectedItem().toString().equals(str_sera_cree_en)) {
            jComboBox_nom0.setModel(model_vide);
            jComboBox_nom1.setModel(model_entiers1);
            jComboBox_nom2.setModel(model_entiers2);
            jComboBox_nom3.setModel(model_entiers3);
            jComboBox_nom4.setModel(model_reaxels4);
            jComboBox_nom1.setSelectedItem("0");
            jComboBox_nom2.setSelectedItem("0");
            jComboBox_nom3.setSelectedItem("0");
        }
        if(jComboBox_cdt_act.getSelectedItem().toString().equals(str_sera_cree_autour_de)) {
            jComboBox_nom0.setModel(model_vide);
            jComboBox_nom1.setModel(model_reaxels1);
        }
        if(jComboBox_cdt_act.getSelectedItem().toString().equals(str_sera_cree_entre)) {
            jComboBox_nom0.setModel(model_vide);
            jComboBox_nom1.setModel(model_reaxels1);
            jComboBox_nom2.setModel(model_reaxels2);
        }
        if(jComboBox_cdt_act.getSelectedItem().toString().equals(str_sera_reconnecte_sur)) {
            jComboBox_nom0.setModel(model_connexels0);
            jComboBox_nom1.setModel(model_reaxels1);
            jComboBox_nom2.setModel(model_reaxels2);
        }
        if(jComboBox_cdt_act.getSelectedItem().toString().equals(str_remplacera_l_entite)) {
            jComboBox_nom0.setModel(model_vide);
            jComboBox_nom1.setModel(model_reaxels1);
        }
        if(jComboBox_cdt_act.getSelectedItem().toString().equals(str_remplacera_le_lien)) {
            jComboBox_nom0.setModel(model_vide);
            jComboBox_nom1.setModel(model_connexels1);
        }
        if(jComboBox_cdt_act.getSelectedItem().toString().equals(str_sera_supprime_entite)) {
            jComboBox_nom0.setModel(model_reaxels0);
        }
        if(jComboBox_cdt_act.getSelectedItem().toString().equals(str_sera_supprime_lien)) {
            jComboBox_nom0.setModel(model_connexels0);
        }
        
    }
    
    /** Cette fonction remplie les listes deroulantes des types
     * avec les types declares (noeuds et liens).
     * 
     * @param aucun
     * @return void
     * @see remplirNomsConditionAction()
     */ 
    public void placerModeleTypesNoeuds(JComboBox cb) {
        _combo_changing++;
        // Valeur selectionne avant la maj
////        String selected_item = "";
////        DefaultComboBoxModel model_old = (DefaultComboBoxModel)cb.getModel();
////        if(model_old != null) {
////            if(cb.getSelectedItem() != null) {
////                if(!cb.getSelectedItem().toString().equals("")) {
////                    selected_item = cb.getSelectedItem().toString();
////                }
////            }
////        }
        // Placement du nouveau model
        javax.swing.DefaultComboBoxModel model = new DefaultComboBoxModel();
        for(int i=0; i<_env.getListManipulesNoeuds().size(); i++)
            model.addElement(_env.getListManipulesNoeuds().get(i)._etiquettes);
        cb.setModel(model);
        
        // Si ancienne valeur selectionnee, on la replace
////        if(!selected_item.equals("")) {
////            cb.setSelectedItem(selected_item);
////        }
        _combo_changing--;
    }
    public void placerModeleTypesLiens(JComboBox cb) {
        _combo_changing++;
        // Valeur selectionne avant la maj
////        String selected_item = "";
////        DefaultComboBoxModel model_old = (DefaultComboBoxModel)cb.getModel();
////        if(model_old != null) {
////            if(cb.getSelectedItem() != null) {
////                if(!cb.getSelectedItem().toString().equals("")) {
////                    selected_item = cb.getSelectedItem().toString();
////                }
////            }
////        }
        
        // Placement du nouveau model
        javax.swing.DefaultComboBoxModel model = new DefaultComboBoxModel();       
        cb.setModel(model);
        
        // Si ancienne valeur selectionnee, on la replace
////        if(!selected_item.equals("")) {
////            cb.setSelectedItem(selected_item);
////        }
        _combo_changing--;        
        
    }    
    public void placerModeleTypeDx(JComboBox cb) {
        cb.setModel(new DefaultComboBoxModel(new String[] {"dx"}));
    }
    public void placerModeleTypeDy(JComboBox cb) {
        cb.setModel(new DefaultComboBoxModel(new String[] {"dy"}));
    }
    public void placerModeleTypeDz(JComboBox cb) {
        cb.setModel(new DefaultComboBoxModel(new String[] {"dz"}));
    }    
    /**
     * Fonction qui place les noms des reaxels
     * 
     * @param cb La comboBox a remplir
     */
    public void placerModeleNomsNoeuds(JComboBox cb) {
        javax.swing.DefaultComboBoxModel model = new DefaultComboBoxModel();
        for(int i=0; i<_parent.getComponentCount(); i++) {
            WndEditElementDeReaction elt = (WndEditElementDeReaction)_parent.getComponent(i);
            if(elt != null) {
                if(elt.jComboBox_cdt_act.equals(str_est_situe_en_absolu)) {
                    model.addElement(elt.jComboBox_nom0.getSelectedItem().toString());
                }
                if(elt.jComboBox_cdt_act.equals(str_est_situe_en_relatif)) {
                    model.addElement(elt.jComboBox_nom0.getSelectedItem().toString());
                }
            } else {
                i = _parent.getComponentCount(); // sortie de boucle car seuls les noms declares en amont sont pris en compte
            }
        }
        cb.setModel(model);
    }


    public String toSave()
    {
        String ligne_cdt_act = new String("");
        
            if(jComboBox_cdt_act.getSelectedItem() != null)
                ligne_cdt_act+= "\tcdt_act:" + (jComboBox_cdt_act.getSelectedItem().toString() + ":");
            
            if(jComboBox_type0.getSelectedItem() != null)
                ligne_cdt_act+= jComboBox_type0.getSelectedItem().toString();
            else ligne_cdt_act+=" ";
            ligne_cdt_act+=":";
            if(jComboBox_type1.getSelectedItem() != null)
                ligne_cdt_act+= jComboBox_type1.getSelectedItem().toString();
            else ligne_cdt_act+=" ";
            ligne_cdt_act+=":";
            if(jComboBox_type2.getSelectedItem() != null)
                ligne_cdt_act+= jComboBox_type2.getSelectedItem().toString();
            else ligne_cdt_act+=" ";
            ligne_cdt_act+=":";
            if(jComboBox_type3.getSelectedItem() != null)
                ligne_cdt_act+= jComboBox_type3.getSelectedItem().toString();
            else ligne_cdt_act+=" ";
            ligne_cdt_act+=":";
            if(jComboBox_type4.getSelectedItem() != null)
                ligne_cdt_act+= jComboBox_type4.getSelectedItem().toString();
            else ligne_cdt_act+=" ";
            ligne_cdt_act+=":";

            if(jComboBox_nom0.getSelectedItem() != null)
                ligne_cdt_act+= jComboBox_nom0.getSelectedItem().toString();
            else ligne_cdt_act+=" ";
            ligne_cdt_act+=":";
            if(jComboBox_nom1.getSelectedItem() != null)
                ligne_cdt_act+= jComboBox_nom1.getSelectedItem().toString();
            else ligne_cdt_act+=" ";
            ligne_cdt_act+=":";
            if(jComboBox_nom2.getSelectedItem() != null)
                ligne_cdt_act+= jComboBox_nom2.getSelectedItem().toString();
            else ligne_cdt_act+=" ";
            ligne_cdt_act+=":";
            if(jComboBox_nom3.getSelectedItem() != null)
                ligne_cdt_act+= jComboBox_nom3.getSelectedItem().toString();
            else ligne_cdt_act+=" ";
            ligne_cdt_act+=":";
            if(jComboBox_nom4.getSelectedItem() != null)
                ligne_cdt_act+= jComboBox_nom4.getSelectedItem().toString();
            else ligne_cdt_act+=" ";
            ligne_cdt_act+="\n";
            
           return ligne_cdt_act;
    }
    
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jComboBox_type0 = new javax.swing.JComboBox();
        jComboBox_nom0 = new javax.swing.JComboBox();
        jComboBox_cdt_act = new javax.swing.JComboBox();
        jComboBox_type1 = new javax.swing.JComboBox();
        jComboBox_nom1 = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();
        jComboBox_type2 = new javax.swing.JComboBox();
        jComboBox_nom2 = new javax.swing.JComboBox();
        jLabel2 = new javax.swing.JLabel();
        jComboBox_type3 = new javax.swing.JComboBox();
        jComboBox_nom3 = new javax.swing.JComboBox();
        jLabel3 = new javax.swing.JLabel();
        jComboBox_type4 = new javax.swing.JComboBox();
        jComboBox_nom4 = new javax.swing.JComboBox();

        setLayout(null);

        jComboBox_type0.setFont(new java.awt.Font("DejaVu Sans", 0, 9)); // NOI18N
        jComboBox_type0.setMaximumRowCount(16);
        add(jComboBox_type0);
        jComboBox_type0.setBounds(0, 10, 60, 20);

        jComboBox_nom0.setEditable(true);
        jComboBox_nom0.setFont(new java.awt.Font("DejaVu Sans", 0, 9)); // NOI18N
        jComboBox_nom0.setMaximumRowCount(16);
        jComboBox_nom0.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBox_nom0ItemStateChanged(evt);
            }
        });
        add(jComboBox_nom0);
        jComboBox_nom0.setBounds(60, 10, 90, 18);

        jComboBox_cdt_act.setFont(new java.awt.Font("DejaVu Sans", 0, 9)); // NOI18N
        jComboBox_cdt_act.setMaximumRowCount(16);
        jComboBox_cdt_act.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBox_cdt_actItemStateChanged(evt);
            }
        });
        add(jComboBox_cdt_act);
        jComboBox_cdt_act.setBounds(190, 10, 110, 20);

        jComboBox_type1.setFont(new java.awt.Font("DejaVu Sans", 0, 9)); // NOI18N
        jComboBox_type1.setMaximumRowCount(16);
        add(jComboBox_type1);
        jComboBox_type1.setBounds(310, 10, 70, 20);

        jComboBox_nom1.setEditable(true);
        jComboBox_nom1.setFont(new java.awt.Font("DejaVu Sans", 0, 9)); // NOI18N
        jComboBox_nom1.setMaximumRowCount(16);
        add(jComboBox_nom1);
        jComboBox_nom1.setBounds(380, 10, 121, 18);

        jLabel1.setFont(new java.awt.Font("DejaVu Sans", 0, 9)); // NOI18N
        jLabel1.setText("info1");
        add(jLabel1);
        jLabel1.setBounds(500, 10, 24, 12);

        jComboBox_type2.setFont(new java.awt.Font("DejaVu Sans", 0, 9)); // NOI18N
        jComboBox_type2.setMaximumRowCount(16);
        add(jComboBox_type2);
        jComboBox_type2.setBounds(530, 10, 60, 20);

        jComboBox_nom2.setEditable(true);
        jComboBox_nom2.setFont(new java.awt.Font("DejaVu Sans", 0, 9)); // NOI18N
        jComboBox_nom2.setMaximumRowCount(16);
        add(jComboBox_nom2);
        jComboBox_nom2.setBounds(590, 10, 100, 18);

        jLabel2.setFont(new java.awt.Font("DejaVu Sans", 0, 9)); // NOI18N
        jLabel2.setText("info2");
        add(jLabel2);
        jLabel2.setBounds(700, 20, 24, 12);

        jComboBox_type3.setFont(new java.awt.Font("DejaVu Sans", 0, 9)); // NOI18N
        jComboBox_type3.setMaximumRowCount(16);
        add(jComboBox_type3);
        jComboBox_type3.setBounds(740, 10, 70, 20);

        jComboBox_nom3.setEditable(true);
        jComboBox_nom3.setFont(new java.awt.Font("DejaVu Sans", 0, 9)); // NOI18N
        jComboBox_nom3.setMaximumRowCount(16);
        add(jComboBox_nom3);
        jComboBox_nom3.setBounds(810, 10, 90, 18);

        jLabel3.setFont(new java.awt.Font("DejaVu Sans", 0, 9)); // NOI18N
        jLabel3.setText("info3");
        add(jLabel3);
        jLabel3.setBounds(270, 40, 24, 12);

        jComboBox_type4.setFont(new java.awt.Font("DejaVu Sans", 0, 9)); // NOI18N
        jComboBox_type4.setMaximumRowCount(16);
        add(jComboBox_type4);
        jComboBox_type4.setBounds(310, 30, 70, 20);

        jComboBox_nom4.setEditable(true);
        jComboBox_nom4.setFont(new java.awt.Font("DejaVu Sans", 0, 9)); // NOI18N
        jComboBox_nom4.setMaximumRowCount(16);
        add(jComboBox_nom4);
        jComboBox_nom4.setBounds(380, 30, 120, 18);
    }// </editor-fold>//GEN-END:initComponents

    private void jComboBox_cdt_actItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBox_cdt_actItemStateChanged
        // TODO add your handling code here:
        if(_combo_changing > 0) return;
        _combo_changing++;
        String item = jComboBox_cdt_act.getSelectedItem().toString();
        initConditionAction(item);
        _combo_changing--;
    }//GEN-LAST:event_jComboBox_cdt_actItemStateChanged

    private void jComboBox_nom0ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBox_nom0ItemStateChanged
        // TODO add your handling code here:
        //JOptionPane.showMessageDialog(this, "ItemStateChanged.");
        if(_combo_changing > 0) return;
        _combo_changing++;
        String old_value = _nom0;
        String new_value = null;
        if(jComboBox_nom0.getSelectedItem() != null) new_value = jComboBox_nom0.getSelectedItem().toString();
        _nom0 = new_value;

        if(old_value == null) old_value = "";
        if(new_value == null) new_value = "";
        if(old_value.equals(new_value)) {
            _combo_changing--;
            return;
        }

        propagerChangement(jComboBox_nom0, old_value, new_value);
        _combo_changing--;

    }//GEN-LAST:event_jComboBox_nom0ItemStateChanged
    
    public void propagerChangement(javax.swing.JComboBox jComboBox, String old_value, String new_value) {
        // Cas de changement de type :
        // rien à faire !
        if(old_value == null) old_value = "";
        if(new_value == null) new_value = "";
        //JOptionPane.showMessageDialog(this, "Ligne " + (new Integer(positionElement())).toString() + "\nRemplacement de " + old_value + " par " + new_value);
        
        // Cas de changement de nom
        if(jComboBox == jComboBox_nom0 || jComboBox == jComboBox_nom1 ||jComboBox == jComboBox_nom2 ||jComboBox == jComboBox_nom3 ||jComboBox == jComboBox_nom4) {
            // 1- Si le old_value est null ou vide, il faut l'ajouter aux modeles des noms non editable
            boolean ajouter = false, modifier = false, supprimer = false;
            if(old_value.equals("")) ajouter = true;

            if(ajouter == true) {
                int p=positionElement();
                for(int i=p+1; i<_parent.getComponentCount(); i++) {
                    WndEditElementDeReaction elt = (WndEditElementDeReaction)_parent.getComponent(i);
                    if(elt.jComboBox_nom0.isEditable() == false && elt.jComboBox_nom0.isVisible() == true) {
                        //String selectedItem = elt.jComboBox_nom0.getSelectedItem().toString();
                        if(!new_value.equals(""))
                            ((DefaultComboBoxModel)elt.jComboBox_nom0.getModel()).addElement(new_value);
                    }
                }
            } else modifier = true;
            // 2- Si le old_value est non null et non vide, il faut remplacer old_value par new_value dans les modeles des noms non editable
            if(new_value.equals("")) {
                modifier = false;
                supprimer = true;
            }                        
            
            if(modifier == true && !old_value.equals("")) {
                int p=positionElement();
                for(int i=p+1; i<_parent.getComponentCount(); i++) {
                    WndEditElementDeReaction elt = (WndEditElementDeReaction)_parent.getComponent(i);
                    modifierModelNomsCombo(elt, old_value, new_value);
                }
            }
            
            if(supprimer == true) {
                int p=positionElement();
                for(int i=p+1; i<_parent.getComponentCount(); i++) {
                    WndEditElementDeReaction elt = (WndEditElementDeReaction)_parent.getComponent(i);
                    if(elt.jComboBox_nom0.isEditable() == false && elt.jComboBox_nom0.isVisible() == true) {
                        String selectedItem = "";
                        if(elt.jComboBox_nom0.getSelectedItem() != null)
                            selectedItem = elt.jComboBox_nom0.getSelectedItem().toString();
                        DefaultComboBoxModel model = ((DefaultComboBoxModel)elt.jComboBox_nom0.getModel());
                        //int pos_elt = model.getIndexOf(old_value);
                        model.removeElement(old_value);
                        if(old_value.equals(selectedItem)) selectedItem = new_value;
                        //jComboBox.setSelectedItem(selectedItem);
                    }
                }                
                
            }
        }
    }
    
    public void modifierModelNomsCombo(WndEditElementDeReaction elt, String old_value, String new_value) {
        JComboBox[] tab_box = new JComboBox[5];
        tab_box[0] = elt.jComboBox_nom0;
        tab_box[1] = elt.jComboBox_nom1;
        tab_box[2] = elt.jComboBox_nom2;
        tab_box[3] = elt.jComboBox_nom3;
        tab_box[4] = elt.jComboBox_nom4;
        
        for(int i=0; i<tab_box.length; i++) {
            if(tab_box[i].isEditable() == false && tab_box[i].isVisible() == true) {
                String selectedItem = tab_box[i].getSelectedItem().toString();
                DefaultComboBoxModel model = ((DefaultComboBoxModel)tab_box[i].getModel());
                //int pos_elt = model.getIndexOf(old_value);
                model.removeElement(old_value);
                model.addElement(new_value);
                if(old_value.equals(selectedItem)) selectedItem = new_value;
                tab_box[i].setSelectedItem(selectedItem);
            }
        }
    }
    
    public void modifierModelTypesCombo(WndEditElementDeReaction elt, String old_value, String new_value) {
        JComboBox[] tab_box = new JComboBox[5];
        tab_box[0] = elt.jComboBox_type0;
        tab_box[1] = elt.jComboBox_type1;
        tab_box[2] = elt.jComboBox_type2;
        tab_box[3] = elt.jComboBox_type3;
        tab_box[4] = elt.jComboBox_type4;
        
        for(int i=0; i<tab_box.length; i++) {
            if(tab_box[i].isEditable() == false && tab_box[i].isVisible() == true) {
                String selectedItem = tab_box[i].getSelectedItem().toString();
                DefaultComboBoxModel model = ((DefaultComboBoxModel)tab_box[i].getModel());
                //int pos_elt = model.getIndexOf(old_value);
                model.removeElement(old_value);
                model.addElement(new_value);
                if(old_value.equals(selectedItem)) selectedItem = new_value;
                tab_box[i].setSelectedItem(selectedItem);
            }
        }
    }

    public void ActualiserTypeCombo() {
        //JComboBox[] tab_box = new JComboBox[5];
        //tab_box[0] = jComboBox_type0;
        //tab_box[1] = jComboBox_type1;
        //tab_box[2] = jComboBox_type2;
        //tab_box[3] = jComboBox_type3;
        //tab_box[4] = jComboBox_type4;

        // Creation des nouveaux models
        DefaultComboBoxModel model_liens = new DefaultComboBoxModel(); //((DefaultComboBoxModel)tab_box[i].getModel());
        

        DefaultComboBoxModel model_noeuds = new DefaultComboBoxModel(); //((DefaultComboBoxModel)tab_box[i].getModel());
       
        // Placement du nouveau model
        String selectedItem0 = "";
        if(jComboBox_type0.getSelectedItem() != null)
            selectedItem0 = jComboBox_type0.getSelectedItem().toString();

        String selectedItem2 = "";
        if(jComboBox_type2.getSelectedItem() != null)
            selectedItem2 = jComboBox_type2.getSelectedItem().toString();

        // Enfin, on ajoute ces noms aux listes deroulantes des noms de l'elt courant
        // selon le type de la condition/action courante
        if(jComboBox_cdt_act.getSelectedItem().toString().equals(str_est_situe_en_absolu)) {
            jComboBox_type0.setModel(model_noeuds);
            jComboBox_type0.setSelectedItem(selectedItem0);
        }
        if(jComboBox_cdt_act.getSelectedItem().toString().equals(str_est_situe_en_relatif)) {
            jComboBox_type0.setModel(model_noeuds);
            jComboBox_type0.setSelectedItem(selectedItem0);
        }
        if(jComboBox_cdt_act.getSelectedItem().toString().equals(str_est_en_contact_avec)) {
            jComboBox_type0.setModel(model_noeuds);
            jComboBox_type0.setSelectedItem(selectedItem0);
        }
        if(jComboBox_cdt_act.getSelectedItem().toString().equals(str_est_lie_a)) {
            jComboBox_type0.setModel(model_noeuds);
            jComboBox_type2.setModel(model_liens);
            jComboBox_type0.setSelectedItem(selectedItem0);
            jComboBox_type2.setSelectedItem(selectedItem2);
        }
        if(jComboBox_cdt_act.getSelectedItem().toString().equals(str_sera_cree_en)) {
            jComboBox_type0.setModel(model_noeuds);
            jComboBox_type0.setSelectedItem(selectedItem0);
        }
        if(jComboBox_cdt_act.getSelectedItem().toString().equals(str_sera_cree_autour_de)) {
            jComboBox_type0.setModel(model_noeuds);
            jComboBox_type0.setSelectedItem(selectedItem0);
        }
        if(jComboBox_cdt_act.getSelectedItem().toString().equals(str_sera_cree_entre)) {
            jComboBox_type0.setModel(model_liens);
            jComboBox_type0.setSelectedItem(selectedItem0);
        }
        if(jComboBox_cdt_act.getSelectedItem().toString().equals(str_sera_reconnecte_sur)) {
        }
        if(jComboBox_cdt_act.getSelectedItem().toString().equals(str_remplacera_l_entite)) {
            jComboBox_type0.setModel(model_noeuds);
            jComboBox_type0.setSelectedItem(selectedItem0);
        }
        if(jComboBox_cdt_act.getSelectedItem().toString().equals(str_remplacera_le_lien)) {
            jComboBox_type0.setModel(model_liens);
            jComboBox_type0.setSelectedItem(selectedItem0);
        }
        if(jComboBox_cdt_act.getSelectedItem().toString().equals(str_sera_supprime_entite)) {
        }
        if(jComboBox_cdt_act.getSelectedItem().toString().equals(str_sera_supprime_lien)) {
        }        
        
        
    }
    
    
    public int positionElement() {
        for(int i=0; i<_parent.getComponentCount(); i++) {
            WndEditElementDeReaction elt = (WndEditElementDeReaction)_parent.getComponent(i);
            if(elt == this) {
                return i;
            }
        }
        return -1;
    }
    
    static private int _combo_changing = 0;
    public String _cdt_act;
    public String _nom0;
    public String _nom1;
    public String _nom2;
    public String _nom3;
    public String _nom4;
    public String _type0;
    public String _type1;
    public String _type2;
    public String _type3;
    public String _type4;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.JComboBox jComboBox_cdt_act;
    public javax.swing.JComboBox jComboBox_nom0;
    public javax.swing.JComboBox jComboBox_nom1;
    public javax.swing.JComboBox jComboBox_nom2;
    public javax.swing.JComboBox jComboBox_nom3;
    public javax.swing.JComboBox jComboBox_nom4;
    public javax.swing.JComboBox jComboBox_type0;
    public javax.swing.JComboBox jComboBox_type1;
    public javax.swing.JComboBox jComboBox_type2;
    public javax.swing.JComboBox jComboBox_type3;
    public javax.swing.JComboBox jComboBox_type4;
    public javax.swing.JLabel jLabel1;
    public javax.swing.JLabel jLabel2;
    public javax.swing.JLabel jLabel3;
    // End of variables declaration//GEN-END:variables
    
    public Environment _env = null;
    public JPanel _parent = null;

}
