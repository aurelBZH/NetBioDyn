/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package netbiodyn.util;

import netbiodyn.ihm.Environment;
import netbiodyn.ihm.WndEditReaction;
import netbiodyn.ihm.WndEditElementDeReaction;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import netbiodyn.ihm.Env_Parameters;
import netbiodyn.InstanceReaxel;
import netbiodyn.Behavior;
import netbiodyn.ProtoBioDyn;
import netbiodyn.Entity;
import netbiodyn.ihm.Controller;

/**
 *
 * @author riviere
 */
public class FileSaverLoader extends SaverLoader {

    private String path;
    private String path_parent;
    private Environment ihm;

    public FileSaverLoader(Environment ihm, String path) {
        this.ihm = ihm;
        this.path = path;
    }

    @Override
    public void save(Serialized toSave) {
        boolean erreur = false;

        FileWriter testSave = null;
        try {
            testSave = new FileWriter(path);
        } catch (Exception e) {
            erreur = true;
        }

        BufferedWriter out = new BufferedWriter(testSave);
        ArrayList<ProtoBioDyn> list_BioDyn = new ArrayList<>();

        try {
            out.write("version:3d-1.0\n");
        } catch (Exception e) {
            erreur = true;
        }

        for (int i = 0; i < toSave.getListManipulesNoeuds().size(); i++) {
            list_BioDyn.add(toSave.getListManipulesNoeuds().get(i));
        }
        for (int i = 0; i < toSave.getListManipulesReactions().size(); i++) {
            list_BioDyn.add(toSave.getListManipulesReactions().get(i));
        }

        for (ProtoBioDyn list_BioDyn1 : list_BioDyn) {
            ArrayList<String> toString = list_BioDyn1.toSave();
            try {
                for (String toString1 : toString) {
                    out.write(toString1);
                }
                // Save image related to a Entity
                if (list_BioDyn1 instanceof Entity) {
                    Entity reaxel = (Entity) list_BioDyn1;
                    if (reaxel.BackgroundImage != null) {
                        File F = new File(path_parent + "/" + reaxel._str_image_deco);
                        String str_image_modifie = reaxel._str_image_deco.replace('.', ';');
                        String[] tab_str = str_image_modifie.split(";");
                        String ext = tab_str[1];
                        if (F.exists()) {
                            F.delete();
                        }
                        ImageIO.write((RenderedImage) reaxel.BackgroundImage, ext, F);
                    }
                }
                out.write("Fin\n");
                out.write("\n");
            } catch (Exception e) {
                erreur = true;
            }
        }

        //Sauvegarde de l'environnement
        try {

            Env_Parameters parameters = toSave.getParameters();
            out.write("netbiodyn.Environnement\n");

            out.write("\ttailleX:" + Integer.toString(parameters.getX()) + "\n");
            out.write("\ttailleY:" + Integer.toString(parameters.getY()) + "\n");
            out.write("\ttailleZ:" + Integer.toString(parameters.getZ()) + "\n");

            if (parameters.getImage() != null) {
                out.write("\tImage:" + parameters.getStr_image_deco() + "\n");
                //BackgroundImage.save(_str_image_deco);                    
                File F = new File(path_parent + "/" + parameters.getStr_image_deco());
                String str_image_modifie = parameters.getStr_image_deco().replace('.', ';');
                String[] tab_str = str_image_modifie.split(";");
                String ext = tab_str[1];
                if (F.exists()) {
                    F.delete();
                }
                try {
                    ImageIO.write((RenderedImage) parameters.getImage(), ext, F);
                } catch (Exception e) {
                    erreur = true;
                }
            }

            out.write("Fin\n");
            out.write("\n");
        } catch (Exception e) {
            erreur = true;
        }

        // Sauvegarde des positions des reaxels // ======================================
        try {
            out.write("Reaxels" + "\n");
            for (int i = 0; i < toSave.getInstances().getSize(); i++) {
                InstanceReaxel r = toSave.getInstances().getInList(i);
                Integer x0, y0, z0;
                x0 = r.getX();
                y0 = r.getY();
                z0 = r.getZ();
                out.write("\tReaxel:" + r.getNom() + ":" + x0.toString() + ":" + y0.toString() + ":" + z0.toString() + "\n");
            }

            out.write("Fin\n");
            out.write("\n");
            out.close();
        } catch (Exception e) {
            System.err.println("5");
            erreur = true;
        }

        if (!erreur) {
            if (Lang.getInstance().getLang().equals("FR")) {
                JOptionPane.showMessageDialog(ihm, "Enregistrement effectue", "Information", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(ihm, "Saved", "Information", JOptionPane.INFORMATION_MESSAGE);
            }
        } else {
            if (Lang.getInstance().getLang().equals("FR")) {
                JOptionPane.showMessageDialog(ihm, "L'enregistrement a echoue", "Attention", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(ihm, "Saving failed", "Warning", JOptionPane.INFORMATION_MESSAGE);
            }
        }

    }

    @Override
    public Serialized load() {
        String chemin = null;
        try {
            chemin = chemin(path);
        } catch (Exception e) {
            if (Lang.getInstance().getLang().equals("FR")) {
                JOptionPane.showMessageDialog(ihm, "Impossible d'ouvrir le fichier " + path + "\nse trouvant dans le dossier " + chemin);
            } else {
                JOptionPane.showMessageDialog(ihm, "Can't open file " + path + "\nse at " + chemin);
            }
            return null;
        }

        java.net.URLConnection cnx;
        FileReader testLoad;
        BufferedReader in = null;
        try {
            testLoad = new FileReader(path);
            in = new BufferedReader(testLoad);
        } catch (Exception e) {
            try {
                cnx = (new URL(path)).openConnection();
                InputStream instr = cnx.getInputStream();
                in = new BufferedReader(new InputStreamReader(instr));
            } catch (Exception e2) {
                JOptionPane.showMessageDialog(ihm, e2);
            }
        }

        String version = "2d";

        Serialized saved = new Serialized();

        try {
            while (in.ready()) {
                // Lecture du mot
                String ligne = in.readLine();
                String[] lst_mots = decoupeLigne(ligne);
                // Chargement Entite
                if (lst_mots[0].equals("version")) {
                    version = lst_mots[1];
                }
                if (lst_mots[0].equals("biodyn_net.Clinamon") || lst_mots[0].equals("biodyn_net.Noeud") || lst_mots[0].equals("biodyn_net.ProtoReaxel") || lst_mots[0].equals("class netbiodyn.ProtoReaxel")
                        || lst_mots[0].equals("class netbiodyn.Entity")) {
                    saved = ChargerNoeud(chemin, in, saved);
                }
                // Chargement Comportements
                if (lst_mots[0].equals("biodyn_net.CliMoteurDeReaction3") || lst_mots[0].equals("biodyn_net.MoteurReaction") || lst_mots[0].equals("class netbiodyn.MoteurReaction")
                        || lst_mots[0].equals("class netbiodyn.Behavior")) {
                    saved = ChargerReaction(in, saved);
                }
                if (lst_mots[0].equals("biodyn_net.CliEnvironnement") || lst_mots[0].equals("biodyn_net.Environnement") || lst_mots[0].equals("netbiodyn.Environnement")) {
                    saved = ChargerEnvironnement(chemin, in, saved);
                    if (version.equals("3d-1.0")) {
                        saved.initMatriceAndList();
                    }
                }
            }
            in.close();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(ihm, "while principal : " + e);
        }

        if (version.equals("2d")) {
            try {
                remplirControlReaxels(ImageIO.read(new URL(path + ".bmp")), saved);
            } catch (Exception e2) {
                JOptionPane.showMessageDialog(ihm, e2);
            }
        }

        try {
            testLoad = new FileReader(path);
            in = new BufferedReader(testLoad);
        } catch (Exception e) {
            //JOptionPane.showMessageDialog(this, e.getMessage());
            try {
                cnx = (new URL(path)).openConnection();
                ////JOptionPane.showMessageDialog(this, "connexion ré-établie !");
                InputStream instr = cnx.getInputStream();
                in = new BufferedReader(new InputStreamReader(instr));
                ////JOptionPane.showMessageDialog(this, "BufferedReader ré-initialisé !");
            } catch (Exception e2) {
                JOptionPane.showMessageDialog(ihm, e2);
            }
        }

        try {

            while (in.ready() == true) {
                // Lecture du mot
                String ligne = in.readLine();
                String[] lst_mots = decoupeLigne(ligne);
                if (lst_mots[0].equals("Reaxels")) {
                    ChargerReaxels(in, saved);
                }
            }
            in.close();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(ihm, e);
        }
        return saved;
    }

    public static void saveAsText(String path, String toSave) {
        try {
            FileWriter file = new FileWriter(path);
            BufferedWriter out = new BufferedWriter(file);
            out.write(toSave);
            out.close();
            
        } catch (IOException ex) {
            Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private String[] decoupeLigne(String str) {
        String tab_separe = ":";
        str = str.replaceAll("\t", ":");
        String[] res = str.split(tab_separe);
        return res;
    }

    public static void exportCurves(String path, ArrayList<String> toExport) {
        boolean error = false;
        try {
            FileWriter testSave = new FileWriter(path);
            BufferedWriter out = new BufferedWriter(testSave);

            for (String s : toExport) {
                out.write(s + "\n");
            }
            out.close();

        } catch (Exception e) {
            System.err.println(e.toString());
            error = true;
        }
        if (!error) {
            if (Lang.getInstance().getLang().equals("FR")) {
                JOptionPane.showMessageDialog(null, "Enregistrement effectue", "Information", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "Saved", "Information", JOptionPane.INFORMATION_MESSAGE);
            }
        } else {
            if (Lang.getInstance().getLang().equals("FR")) {
                JOptionPane.showMessageDialog(null, "L'enregistrement a echoue", "Attention", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "Saving failed", "Warning", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }
    
    public static void appendCurves(String path, ArrayList<String> toExport) {
        try {
            ArrayList<String> existing=new ArrayList<>();                                  
            BufferedReader in=new BufferedReader(new FileReader(path));

            while (in.ready() == true) {
                // Lecture du mot
                existing.add(in.readLine());                
            }
            in.close();
            
            System.out.println(existing.toString());
            
            FileWriter testSave = new FileWriter(path);  
            BufferedWriter out = new BufferedWriter(testSave);
            for(int i=0;i<existing.size();i++){
                String s=existing.get(i);
                if(toExport.size()>i)s=s.concat(toExport.get(i));
                out.write(s + "\n");
            }
            out.close();

        } catch (Exception e) {
            System.err.println(e.toString());
        }
    }
    

    public static File chooseFileToSave(String nameSaved, String description, String[] extensions) {
        JFileChooser saveFileDialog1 = new JFileChooser();
        UtilFileFilter filter = new UtilFileFilter(description, extensions);
        saveFileDialog1.setFileFilter(filter);
        saveFileDialog1.setAcceptAllFileFilterUsed(false);
        if (Lang.getInstance().getLang().equals("FR")) {
            saveFileDialog1.setDialogTitle("Sauvegarder");
        } else {
            saveFileDialog1.setDialogTitle("Save");
        }

        saveFileDialog1.setSelectedFile(new File(nameSaved));
        int returnval = saveFileDialog1.showSaveDialog(null);

        if (returnval == JFileChooser.APPROVE_OPTION) {
            File f = saveFileDialog1.getSelectedFile();
            String path_name = saveFileDialog1.getSelectedFile().getPath();
            boolean accept = false;
            for (String ex : extensions) {
                if (path_name.endsWith(ex)) {
                    accept = true;
                }
            }
            if (!accept) {
                path_name = path_name.concat("." + extensions[0]);
            }

            nameSaved = UtilDivers.fichier(path_name);

            boolean confirmer = true;
            File toReturn = new File(path_name);
            if (toReturn.exists()) {
                int retval;

                if (Lang.getInstance().getLang().equals("FR")) {
                    retval = JOptionPane.showConfirmDialog(null, "L'enregistrement " + path_name + " existe deja. " + "Voulez-vous l'ecraser ?", "Attention", JOptionPane.WARNING_MESSAGE);
                } else {
                    retval = JOptionPane.showConfirmDialog(null, "The recording " + path_name + " already exists. " + "Erase?", "Warning", JOptionPane.WARNING_MESSAGE);
                }

                if (retval == JOptionPane.NO_OPTION || retval == JOptionPane.CANCEL_OPTION) {
                    confirmer = false;
                }
            }
            if (!nameSaved.equals("") && confirmer == true) {
                return toReturn;
            }
        }
        return null;
    }

    /**
     *
     * @param nameSaved
     * @param filter
     * @return
     */
    public static File chooseFileToLoad(String nameSaved, UtilFileFilter filter) {
        File toReturn = null;

        String nomFichier = nameSaved;
        //String nomFichier_simple = new String("");
        String path_name;
        //String path = null;
        JFileChooser loadFileDialog1 = new JFileChooser();
        loadFileDialog1.setFileFilter(filter);
        if (Lang.getInstance().getLang().equals("FR")) {
            loadFileDialog1.setDialogTitle("Charger une simulation");
        } else {
            loadFileDialog1.setDialogTitle("Load a simulation");
        }

        loadFileDialog1.setSelectedFile(new File(nomFichier));
        int returnval = loadFileDialog1.showOpenDialog(null);

        if (returnval == JFileChooser.APPROVE_OPTION) {
            toReturn = loadFileDialog1.getSelectedFile();
            path_name = loadFileDialog1.getSelectedFile().getPath();
            nomFichier = path_name;
            nomFichier = nomFichier.replace('\\', '/');
        }
        return toReturn;
//
//        if (!nomFichier.equals("") && returnval == JFileChooser.APPROVE_OPTION) {
//
//            if (!nomFichier.startsWith("http") && !nomFichier.startsWith("file")) {
//                if (nomFichier.startsWith("/")) {
//                    nomFichier = "file://" + nomFichier; // Sous Linux, il y a le '/' de debut...
//                } else {
//                    nomFichier = "file:///" + nomFichier; // mais pas sous windows...
//                }
//            }
//
//        }
    }

    public static ArrayList<String> importCurves(String path) {
        ArrayList<String> lines = new ArrayList<>();
        try {
            FileReader testLoad = new FileReader(path);
            BufferedReader in = new BufferedReader(testLoad);

            while (in.ready()) {
                String l = in.readLine();
                lines.add(l);
            }
            in.close();
        } catch (Exception e) {
            System.err.println(e.toString());
        }

        return lines;
    }

    private static String chemin(String str) {
        int pos_slash = -1;

        for (int i = str.length() - 1; i >= 0; i--) {
            if (str.charAt(i) == '/' || str.charAt(i) == '\\') {
                pos_slash = i;
                i = -1;
            }
        }
        if (pos_slash >= 0) {
            return str.substring(0, pos_slash);
        } else {
            return str;
        }
    }

    private Serialized ChargerNoeud(String abs_path, BufferedReader testLoad, Serialized saved) {
        // Chargement Entite
        Entity cli = new Entity();

        boolean fin_clinamon = false;
        while (fin_clinamon == false) {
            String ligne = null;
            try {
                ligne = testLoad.readLine();
            } catch (Exception e) {
                fin_clinamon = true;
                JOptionPane.showMessageDialog(ihm, e);
            }
            String[] lst_mots = decoupeLigne(ligne);
            if (lst_mots[0].equals("Fin")) {
                fin_clinamon = true;
            } else {
                if (lst_mots.length == 2) { // Cas ou la valeur est ""
                    String[] tmp = new String[3];
                    tmp[0] = lst_mots[0];
                    tmp[1] = lst_mots[1];
                    tmp[2] = "";
                    lst_mots = tmp;
                }
                if (lst_mots[1].equals("Etiquettes")) {
                    // Etiquettes
                    cli.setEtiquettes(lst_mots[2]);
                    // Chargement de la description
                    String nom_fichier_description = path + "_Description_" + cli.getEtiquettes() + ".txt";

                    if (fichierExiste("", nom_fichier_description) == true) {
                        cli.getDescription().setText(chargerTexte(nom_fichier_description));
                    }
                }
                if (lst_mots[1].equals("X")) {
                    cli.setLocation(Integer.parseInt(lst_mots[2]), cli.getLocation().y);
                }
                if (lst_mots[1].equals("Y")) {
                    cli.setLocation(cli.getLocation().x, Integer.parseInt(lst_mots[2]));
                }
                if (lst_mots[1].equals("couleur")) {
                    cli.Couleur = Color.decode((lst_mots[2]));
                }
                if (lst_mots[1].equals("forme")) {
                    cli._forme = Integer.parseInt(lst_mots[2]);
                }
                if (lst_mots[1].equals("taille")) {
                    cli._taille = Integer.parseInt(lst_mots[2]);
                }
                if (lst_mots[1].equals("tailleFenetre")) {
                    cli.setTailleFenetre(Integer.parseInt(lst_mots[2]));
                }
                if (lst_mots[1].equals("demie_vie")) {
                    cli.DemieVie = Double.parseDouble(lst_mots[2].replaceAll(",", "."));
                }
                if (lst_mots[1].equals("visibleDansPanel")) {
                    cli._visibleDansPanel = Boolean.parseBoolean(lst_mots[2]);
                }
                if (lst_mots[1].equals("vidable")) {
                    cli.Vidable = Boolean.parseBoolean(lst_mots[2]);
                }
                if (lst_mots[1].equals("Image")) {
                    cli._str_image_deco = lst_mots[2];
                    // Chargement de l'image                        
                    BufferedImage monImage = null;
                    try {
                        monImage = ImageIO.read(new URL(abs_path + "/" + cli._str_image_deco));
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(ihm, e);
                    }
                    cli.BackgroundImage = monImage;
                }

            }
        }
        saved.addProtoReaxel(cli);
        return saved;
    }

    private Serialized ChargerReaction(BufferedReader testLoad, Serialized saved) {
        Behavior react3 = new Behavior();
        react3._positions = new ArrayList<>();

        boolean fin = false;
        WndEditReaction tmp_wnd_react_cplx = new WndEditReaction(saved.getListManipulesNoeuds(), saved.getListManipulesReactions());
        while (fin == false) {
            String ligne = null;
            try {
                ligne = testLoad.readLine();
            } catch (Exception e) {
                fin = true;
                JOptionPane.showMessageDialog(ihm, e);
            }
            String[] lst_mots = decoupeLigne(ligne);
            if (lst_mots[0].equals("Fin")) {
                fin = true;
            } else {
                if (lst_mots.length == 2) { // Cas ou la valeur est ""
                    String[] tmp = new String[3];
                    tmp[0] = lst_mots[0];
                    tmp[1] = lst_mots[1];
                    tmp[2] = "";
                    lst_mots = tmp;
                }
                if (lst_mots[1].equals("Etiquettes")) {
                    react3.setEtiquettes(lst_mots[2]);
                    // Chargement de la description
                    String nom_fichier_description = path + "_Description_" + react3.getEtiquettes() + ".txt";
                    if (fichierExiste("", nom_fichier_description) == true) {
                        react3._description.setText(chargerTexte(nom_fichier_description));
                    }
                }
                if (lst_mots[1].equals("Description")) {
                    react3._description.setText(lst_mots[2].replace('§', '\n'));
                }
                if (lst_mots[1].equals("reactif")) {
                    react3._reactifs.add(lst_mots[2]);
                }
                if (lst_mots[1].equals("produit")) {
                    react3._produits.add(lst_mots[2]);
                }
                if (lst_mots[1].equals("pos")) {
                    react3._positions.add(lst_mots[2]);
                }

                if (lst_mots[1].equals("visibleDansPanel")) {
                    String mot = lst_mots[2];
                    react3._visibleDansPanel = Boolean.parseBoolean(mot);
                }
                if (lst_mots[1].equals("k")) {
                    String mot = lst_mots[2].replaceAll(",", ".");
                    react3.set_k(Double.parseDouble(mot));
                }

                if (lst_mots[1].equals("cdt_act")) {
                    WndEditElementDeReaction elt = null;
                    if (tmp_wnd_react_cplx.jPanel_cplx.getComponentCount() == 0) {
                        elt = new WndEditElementDeReaction(); // Le panel est encore vide (pas encore de "FIN" cree automatiquement
                    } else // sinon, le "FIN" a deja ete cree automatiquement par l'insertion precedente et c'est lui qu'on utilise
                    {
                        elt = (WndEditElementDeReaction) tmp_wnd_react_cplx.jPanel_cplx.getComponent(tmp_wnd_react_cplx.jPanel_cplx.getComponentCount() - 1);
                    }

                    elt._env = ihm;
                    elt._parent = tmp_wnd_react_cplx.jPanel_cplx;
                    react3._ListElementsReactions.add(elt);
                    elt._parent.add(elt);
                    elt.WndEditElementDeReaction_Load();// elt.setLocation(0, 0);
                    elt.jComboBox_cdt_act.setSelectedItem(lst_mots[2]);
                    elt.jComboBox_type0.setSelectedItem(lst_mots[3]);
                    elt.jComboBox_type1.setSelectedItem(lst_mots[4]);
                    elt.jComboBox_type2.setSelectedItem(lst_mots[5]);
                    elt.jComboBox_type3.setSelectedItem(lst_mots[6]);
                    elt.jComboBox_type4.setSelectedItem(lst_mots[7]);
                    elt.jComboBox_nom0.setSelectedItem(lst_mots[8]);
                    elt.jComboBox_nom1.setSelectedItem(lst_mots[9]);
                    elt.jComboBox_nom2.setSelectedItem(lst_mots[10]);
                    elt.jComboBox_nom3.setSelectedItem(lst_mots[11]);
                    elt.jComboBox_nom4.setSelectedItem(lst_mots[12]);

                    String code_g = tmp_wnd_react_cplx.genererCode();
                    react3._code.setText(code_g);
                }
            }
        }
        if (react3._positions.isEmpty()) {
            // Init des positions
            react3._positions.add("122222222");
            for (int i = 1; i < 9; i++) {
                react3._positions.add("212121212");
            }
        }
        saved.addMoteurReaction(react3);
        return saved;
    }

    private Serialized ChargerReaxels(BufferedReader testLoad, Serialized saved) {
        boolean fin = false;
        while (fin == false) {
            String ligne = null;
            try {
                ligne = testLoad.readLine();
            } catch (Exception e) {
                fin = true;
                JOptionPane.showMessageDialog(ihm, e);
            }

            String[] lst_mots = decoupeLigne(ligne);
            if (lst_mots[0].equals("Fin")) {
                fin = true;
            } else {
                if (lst_mots[1].equals("Reaxel")) {
                    String nom_reaxel = lst_mots[2];
                    int x0 = Integer.parseInt(lst_mots[3]);
                    int y0 = Integer.parseInt(lst_mots[4]);
                    int z0 = Integer.parseInt(lst_mots[5]);
                    saved.AjouterReaxel(x0, y0, z0, nom_reaxel);
                }
            }
        }
        return saved;
    }

    private Serialized ChargerEnvironnement(String abs_path, BufferedReader testLoad, Serialized saved) {
        boolean fin = false;
        Env_Parameters p = saved.getParameters();

        while (fin == false) {
            String ligne = null;
            try {
                ligne = testLoad.readLine();
            } catch (Exception e) {
                fin = true;
            }

            String[] lst_mots = decoupeLigne(ligne);
            if (lst_mots[0].equals("Fin")) {
                fin = true;
            } else {
                if (lst_mots.length == 2) { // Cas ou la valeur est ""
                    String[] tmp = new String[3];
                    tmp[0] = lst_mots[0];
                    tmp[1] = lst_mots[1];
                    tmp[2] = "";
                    lst_mots = tmp;
                }

                if (lst_mots[1].equals("Image")) {
                    p.setStr_image_deco(lst_mots[2]);
                    // Chargement de l'image                        
                    BufferedImage monImage = null;
                    try {
                        monImage = ImageIO.read(new URL(abs_path + "/" + lst_mots[2]));
                        p.setImage(monImage);
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(ihm, e);
                    }
                } else {
                    saved.setTaille(lst_mots[1], lst_mots[2]);
                }
            }
        }

        // Chargement de la description
        String nom_fichier_description = path + "_Description_Simulation.txt";
        if (fichierExiste("", nom_fichier_description) == true) {
            p.setDescription(chargerTexte(nom_fichier_description));
        }
        saved.setParameters(p);
        return saved;
    }

    public boolean fichierExiste(String path, String nomFichier) {
        try {
            if (new File(path, nomFichier).exists()) {
                return true;
            } else {
                return URLexiste(nomFichier);
            }
        } catch (Exception e) {
            return URLexiste(nomFichier);
        }
    }

    public boolean URLexiste(String URLName) {
        if (!URLName.startsWith("http") && !URLName.startsWith("file")) {
            if (URLName.startsWith("/")) {
                URLName = "file://" + URLName; // Fichier Linux
            } else {
                URLName = "file:///" + URLName; // Fichier Windows
            }
        }
        try {
            java.net.URLConnection cnx = null;
            cnx = (new URL(URLName)).openConnection();
            int L = cnx.getContentLength();

            return L > 0;
        } catch (Exception e) {
            return false;
        }
    }

    public String chargerTexte(String nomFichier) {
        try {
            URL f = new URL(nomFichier);
            int nb = f.openConnection().getContentLength();
            byte[] contenu = new byte[nb];
            f.openStream().read(contenu);
            String str = new String(contenu);
            return str;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(ihm, e);
            return new String("");
        }
    }

    private Serialized remplirControlReaxels(BufferedImage Image_CliEnv, Serialized saved) {
        ArrayList<Entity> lst_cli = new ArrayList<Entity>();
        HashMap<Integer, Entity> dic_color_cli = new HashMap<Integer, Entity>();
        if (Image_CliEnv != null) {
            saved.setTaille("tailleX", "" + Image_CliEnv.getWidth());
            saved.setTaille("tailleY", "" + Image_CliEnv.getHeight());

            saved.initMatriceAndList();

            ArrayList<Entity> noeuds = saved.getListManipulesNoeuds();

            for (int i = 0; i < noeuds.size(); i++) {
                lst_cli.add(noeuds.get(i));
                int rgb = (noeuds.get(i)).Couleur.getRGB();
                dic_color_cli.put(rgb, noeuds.get(i));
            }

            for (int i = 0; i < Image_CliEnv.getWidth(); i++) {
                for (int j = 0; j < Image_CliEnv.getHeight(); j++) {
                    BufferedImage bmp = Image_CliEnv;
                    int c = bmp.getRGB(i, j);
                    if (dic_color_cli.containsKey(c) == true) {
                        Entity cli = dic_color_cli.get(c);
                        saved.AjouterReaxel(i, j, 0, cli);
                    }
                }
            }
        } else {
            saved.initMatriceAndList();
        }
        return saved;
    }

    public void setEnvironment(Environment ihm) {
        this.ihm = ihm;
    }

    public void setFileName(String f) {
        path = f;
    }

    public void setParentPath(String f) {
        path_parent = f;
    }

}
