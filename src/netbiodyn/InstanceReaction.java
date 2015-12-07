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

package netbiodyn;

import netbiodyn.util.UtilPoint3D;
import java.util.ArrayList;

/**
 *
 * @author user
 */
public class InstanceReaction {
    //public Environnement _env;    
    public int _type = 0;
    
    public class cdtAct {
        public int ordre = 0;
    }
    // Classe de mémorisation des cdts
    public class cdtReaxelEn extends cdtAct {
        public String  type_reaxel, nom_reaxel;
        public int dx,dy,dz;
    }
    public class cdtReaxelEnRelatif extends cdtAct  {
        public String  type_reaxel, nom_reaxel, nom_reaxel_origine;
        int dx,dy,dz;
    }    
    public class cdtReaxelTouche extends cdtAct  {
        public String type_reaxel1, nom_reaxel1, type_reaxel2, nom_reaxel2;
    }
    public class cdtReaxelToucheEn extends cdtAct  {
        public String type_reaxel1, nom_reaxel1, type_reaxel2, nom_reaxel2, pos;
    }
    public class cdtReaxelLieAvec extends cdtAct  {
        public String type_reaxel1, nom_reaxel1, type_connexel, nom_connexel, type_reaxel2, nom_reaxel2;
    }
    
    // Classe de mémorisation des acts
    public class actSupprimerReaxel extends cdtAct  {
        public String type_reaxel, nom_reaxel;
    }
    public class actSupprimerConnexel extends cdtAct  {
        public String type_connexel, nom_connexel;
    }
    public class actAjouterReaxelEn extends cdtAct  {
        public String  type_reaxel, nom_reaxel, nom_reaxel_origine;
        int dx,dy,dz;
    }
    public class actAjouterReaxelAutour extends cdtAct  {
        public String type_reaxel1, nom_reaxel1, nom_reaxel2;
    }
    public class actAjouterConnexelSur extends cdtAct  {
        public String  type_connexel, nom_connexel;
        public String type_reaxel1, type_reaxel2;
        public String nom_reaxel1, nom_reaxel2;
    }
    public class actRemplacerReaxel extends cdtAct  {
        public String type_reaxel1, nom_reaxel1, type_reaxel2, nom_reaxel2;
    }
    public class actRemplacerConnexel extends cdtAct  {
        public String type_connexel1, nom_connexel1, type_connexel2, nom_connexel2;
    }
    public class actConnecterConnexel extends cdtAct  {
        String type_connexel, nom_connexel, type_reaxel1, nom_reaxel1, type_reaxel2, nom_reaxel2;
    }
    public class actForce extends cdtAct  {
        String type_reaxel, nom_reaxel;
        double fx, fy, fz;
    }
    
    // Relation entre les noms et les entités ds l'env
    public ArrayList<String>             _reaxels_noms                = new ArrayList<String>();
    public ArrayList<InstanceReaxel>     _reaxels_ident               = new ArrayList<InstanceReaxel>();
    
    // Stockage des cdts & act
    public ArrayList<cdtAct>                _lstToutesCdtAct        = new ArrayList<InstanceReaction.cdtAct>();
    /*
    public ArrayList<cdtReaxelEn>           _lstCdtReaxelEn         = new ArrayList<cdtReaxelEn>();
    public ArrayList<cdtReaxelEnRelatif>    _lstCdtReaxelEnRelatif  = new ArrayList<cdtReaxelEnRelatif>();
    public ArrayList<cdtReaxelTouche>       _lstCdtReaxelTouche     = new ArrayList<cdtReaxelTouche>();
    public ArrayList<cdtReaxelToucheEn>     _lstCdtReaxelToucheEn   = new ArrayList<cdtReaxelToucheEn>();
    public ArrayList<cdtReaxelLieAvec>      _lstCdtReaxelLieAvec    = new ArrayList<cdtReaxelLieAvec>();    
    
    public ArrayList<actSupprimerReaxel>    _lstActSupprimerReaxel  = new ArrayList<actSupprimerReaxel>();
    public ArrayList<actSupprimerConnexel>  _lstActSupprimerConnexel= new ArrayList<actSupprimerConnexel>();
    public ArrayList<actAjouterReaxelEn>    _lstActAjouterReaxelEn  = new ArrayList<actAjouterReaxelEn>();
    public ArrayList<actAjouterReaxelAutour> _lstActAjouterReaxelAutour  = new ArrayList<actAjouterReaxelAutour>();
    public ArrayList<actAjouterConnexelSur> _lstActAjouterConnexelSur= new ArrayList<actAjouterConnexelSur>();
    public ArrayList<actRemplacerReaxel>    _lstActRemplacerReaxel  = new ArrayList<actRemplacerReaxel>();
    public ArrayList<actRemplacerConnexel>  _lstActRemplacerConnexel= new ArrayList<actRemplacerConnexel>();
    public ArrayList<actConnecterConnexel>  _lstActConnecterConnexel= new ArrayList<actConnecterConnexel>();
    public ArrayList<actForce>              _lstActForce            = new ArrayList<actForce>();
    */
    // Conditions
    public int x_centre, y_centre, z_centre;
    public String _nom;

    public void cdtReaxelEn(String type_reaxel, String nom_reaxel, int dx, int dy, int dz) {
        cdtReaxelEn o = new cdtReaxelEn();
        o.type_reaxel = type_reaxel; o.nom_reaxel = nom_reaxel; o.dx = dx; o.dy = dy; o.dz = dz;
        //_lstCdtReaxelEn.add(o);
        o.ordre = _lstToutesCdtAct.size(); _lstToutesCdtAct.add(o);
    }
    public void cdtReaxelEnRelatif(String type_reaxel, String nom_reaxel, int dx, int dy, int dz, String nom_reaxel_origine) {
        cdtReaxelEnRelatif o = new cdtReaxelEnRelatif();
        o.type_reaxel = type_reaxel;
        o.nom_reaxel = nom_reaxel;
        o.dx = dx;
        o.dy = dy;
        o.dz = dz;
        o.nom_reaxel_origine = nom_reaxel_origine;
        //_lstCdtReaxelEnRelatif.add(o);        
        o.ordre = _lstToutesCdtAct.size(); _lstToutesCdtAct.add(o);
    }
    
    public void cdtReaxelTouche(String type_reaxel1, String nom_reaxel1, String nom_reaxel2) {        
        cdtReaxelTouche o = new cdtReaxelTouche();
        o.type_reaxel1 = type_reaxel1;
        o.nom_reaxel1 = nom_reaxel1;
        //o.type_reaxel2 = type_reaxel2;
        o.nom_reaxel2 = nom_reaxel2;
        //_lstCdtReaxelTouche.add(o);
        o.ordre = _lstToutesCdtAct.size(); _lstToutesCdtAct.add(o);
    }
    /*public void cdtReaxelToucheA(String type_reaxel1, String nom_reaxel1, String type_reaxel2, String nom_reaxel2, String pos) {        
        cdtReaxelToucheEn o = new cdtReaxelToucheEn();
        o.type_reaxel1 = type_reaxel1; o.nom_reaxel1 = nom_reaxel1; o.type_reaxel2 = type_reaxel2; o.nom_reaxel2 = nom_reaxel2; o.pos = pos;
        _lstCdtReaxelToucheEn.add(o);
    }*/
    public void cdtReaxelLieAvec(String type_reaxel1, String nom_reaxel1, String nom_reaxel2, String type_connexel, String nom_connexel) {        
        cdtReaxelLieAvec o = new cdtReaxelLieAvec();
        o.type_reaxel1 = type_reaxel1; o.nom_reaxel1 = nom_reaxel1;
        o.type_connexel = type_connexel; o.nom_connexel = nom_connexel;
        //o.type_reaxel2 = type_reaxel2;
        o.nom_reaxel2 = nom_reaxel2;
        //_lstCdtReaxelLieAvec.add(o);
        o.ordre = _lstToutesCdtAct.size(); _lstToutesCdtAct.add(o);
    }

    // Actions
    public void actSupprimerReaxel(String nom_reaxel) {        
        actSupprimerReaxel o = new actSupprimerReaxel();
        //o.type_reaxel = type_reaxel;
        o.nom_reaxel = nom_reaxel;
        //_lstActSupprimerReaxel.add(o);
        o.ordre = _lstToutesCdtAct.size(); _lstToutesCdtAct.add(o);
    }
    public void actSupprimerConnexel(String nom_connexel) {
        actSupprimerConnexel o = new actSupprimerConnexel();
        //o.type_connexel = type_connexel;
        o.nom_connexel = nom_connexel;
        //_lstActSupprimerConnexel.add(o);        
        o.ordre = _lstToutesCdtAct.size(); _lstToutesCdtAct.add(o);
    }
    public void actAjouterReaxelEn(String type_reaxel, String nom_reaxel, int dx, int dy, int dz, String nom_reaxel_origine) {
        actAjouterReaxelEn o = new actAjouterReaxelEn();
        o.type_reaxel = type_reaxel;
        o.nom_reaxel = nom_reaxel;
        o.dx = dx;
        o.dy = dy;
        o.dz = dz;
        o.nom_reaxel_origine = nom_reaxel_origine;
        //_lstActAjouterReaxelEn.add(o);
        o.ordre = _lstToutesCdtAct.size(); _lstToutesCdtAct.add(o);
    }
    
    public void actAjouterReaxelAutour(String type_reaxel1, String nom_reaxel1, String nom_reaxel_origine) {
        actAjouterReaxelAutour o = new actAjouterReaxelAutour();
        o.type_reaxel1 = type_reaxel1;
        o.nom_reaxel1 = nom_reaxel1;
        o.nom_reaxel2 = nom_reaxel_origine;
        //_lstActAjouterReaxelAutour.add(o);        
        o.ordre = _lstToutesCdtAct.size(); _lstToutesCdtAct.add(o);
    }
    public void actAjouterConnexelSur(String type_connexel, String nom_connexel, String nom_reaxel1,String nom_reaxel2) {
        actAjouterConnexelSur o = new actAjouterConnexelSur();
        o.type_connexel = type_connexel;
        o.nom_connexel = nom_connexel;
        //o.type_reaxel1 = type_reaxel1;
        o.nom_reaxel1 = nom_reaxel1;
        //o.type_reaxel2 = type_reaxel2;
        o.nom_reaxel2 = nom_reaxel2;
        //_lstActAjouterConnexelSur.add(o);        
        o.ordre = _lstToutesCdtAct.size(); _lstToutesCdtAct.add(o);
    }
    public void actRemplacerReaxel(String type_reaxel1, String nom_reaxel1, String nom_reaxel2) {
        actRemplacerReaxel o = new actRemplacerReaxel();
        o.type_reaxel1 = type_reaxel1;
        o.nom_reaxel1 = nom_reaxel1;
        //o.type_reaxel2 = type_reaxel2;
        o.nom_reaxel2 = nom_reaxel2;
        //_lstActRemplacerReaxel.add(o);        
        o.ordre = _lstToutesCdtAct.size(); _lstToutesCdtAct.add(o);
    }
    public void actRemplacerConnexel(String type_connexel1, String nom_connexel1, String nom_connexel2) {
        actRemplacerConnexel o = new actRemplacerConnexel();
        o.type_connexel1 = type_connexel1;
        o.nom_connexel1 = nom_connexel1;
        //o.type_connexel2 = type_connexel2;
        o.nom_connexel2 = nom_connexel2;
        //_lstActRemplacerConnexel.add(o);        
        o.ordre = _lstToutesCdtAct.size(); _lstToutesCdtAct.add(o);
    }
    public void actConnecterConnexel(String nom_connexel, String nom_reaxel1, String nom_reaxel2) {
        actConnecterConnexel o = new actConnecterConnexel();
        //o.type_connexel = type_connexel;
        o.nom_connexel = nom_connexel;
        //o.type_reaxel1 = type_reaxel1;
        o.nom_reaxel1 = nom_reaxel1;
        //o.type_reaxel2 = type_reaxel2;
        o.nom_reaxel2 = nom_reaxel2;
        //_lstActConnecterConnexel.add(o);        
        o.ordre = _lstToutesCdtAct.size(); _lstToutesCdtAct.add(o);
    }
    public void actForce(String type_reaxel, String nom_reaxel, double fx, double fy, double fz) {
        actForce o = new actForce();
        o.type_reaxel = type_reaxel;
        o.nom_reaxel = nom_reaxel;
        o.fx = fx;
        o.fy = fy;
        o.fz = fz;
        //_lstActForce.add(o);        
        o.ordre = _lstToutesCdtAct.size(); _lstToutesCdtAct.add(o);
    }
    
    // Utilitaires => A placer dans l'env
    /*public boolean utilBarycentre(String type_reaxel) {
        return true;
    }*/

    // Listes
    public void effacerCdtsActs() {
        //effacerCdts();
        //effacerActs();
        _lstToutesCdtAct.clear();
    }
    /*private void effacerCdts() {
        _lstCdtReaxelEn.clear();
        _lstCdtReaxelEnRelatif.clear();
        _lstCdtReaxelTouche.clear();
        _lstCdtReaxelToucheEn.clear();
        _lstCdtReaxelLieAvec.clear();   
    }
    private void effacerActs() {
        _lstActSupprimerReaxel.clear();
        _lstActSupprimerConnexel.clear();
        _lstActAjouterReaxelEn.clear();
        _lstActAjouterReaxelAutour.clear();
        _lstActAjouterConnexelSur.clear();
        _lstActRemplacerReaxel.clear();
        _lstActRemplacerConnexel.clear();
        _lstActConnecterConnexel.clear();
        _lstActForce.clear();
    }*/

    public InstanceReaction cloner() {
        InstanceReaction clone = new InstanceReaction();
        clone._nom = _nom;
        clone._type = _type;
        /*clone._lstCdtReaxelEn           = _lstCdtReaxelEn;
        clone._lstCdtReaxelEnRelatif    = _lstCdtReaxelEnRelatif;
        clone._lstCdtReaxelTouche       = _lstCdtReaxelTouche;
        clone._lstCdtReaxelToucheEn     = _lstCdtReaxelToucheEn;
        clone._lstCdtReaxelLieAvec      = _lstCdtReaxelLieAvec;
        clone._lstActSupprimerReaxel    = _lstActSupprimerReaxel;
        clone._lstActSupprimerConnexel  = _lstActSupprimerConnexel;
        clone._lstActAjouterReaxelEn    = _lstActAjouterReaxelEn;
        clone._lstActAjouterReaxelAutour= _lstActAjouterReaxelAutour;
        clone._lstActAjouterConnexelSur = _lstActAjouterConnexelSur;
        clone._lstActRemplacerReaxel    = _lstActRemplacerReaxel;
        clone._lstActRemplacerConnexel  = _lstActRemplacerConnexel;
        clone._lstActConnecterConnexel  = _lstActConnecterConnexel;
        clone._lstActForce              = _lstActForce;
        */
        clone._lstToutesCdtAct          = _lstToutesCdtAct;
        
        return clone;
    }


    // Pour la compatibilité avec les réactions situées et semi-situées
    public ArrayList<String> _reactifs_noms = new ArrayList<String>();
    public ArrayList<UtilPoint3D> _reactifs_pos = new ArrayList<UtilPoint3D>();

    public ArrayList<String> _produits_noms = new ArrayList<String>();
    public ArrayList<UtilPoint3D> _produits_pos = new ArrayList<UtilPoint3D>();
}
/*
// Exemple 1 (membrane : disloque)
// *********
reaction.cdtReaxelEn("A","A0", 0,0,0);
reaction.cdtReaxelLieAvec("A", "A0","L", "L0", "A", "A1");
reaction.actSupprimerReaxel("A", "A0");
reaction.actSupprimerReaxel("A", "A1");

Exemple 2 (membrane : reduction)
// *********
reaction.cdtReaxelEn("A","A0", 0,0,0);

reaction.cdtReaxelLieAvec("A", "A0","L", "L0", "A", "A1");
reaction.cdtReaxelLieAvec("A", "A1","L", "L1", "A", "A2");

reaction.actSupprimerReaxel("A", "A1");

reaction.actConnecterConnexel("L", "L0", "A", "A0", "A", "A2"); 

Exemple 3 (fleurs)
// ******
 * pousse
reaction.cdtReaxelEn("sol", "sol1", 0, 0, 0);
reaction.cdtReaxelLieAvec("sol", "sol1", "L0", "L00", "F", "F1");
reaction.actRemplacerConnexel("L0", "L00", "L1", "L10");
* diminue
reaction.cdtReaxelEn("sol", "sol1", 0, 0, 0);
reaction.cdtReaxelLieAvec("sol", "sol1", "L1", "L11", "F", "F1");
reaction.actRemplacerConnexel("L0", "L11", "L0", "L00");

Exemple 4 (vers)
// ********
 * pousse
reaction.cdtReaxelEn("sol", "sol1", 0, 0, 0);
reaction.cdtReaxelLieAvec("sol", "sol1", "L0", "L00", "F", "F1");
reaction.actRemplacerConnexel("L0", "L00", "L1", "L10");
reaction.actRemplacerReaxel("sol", "sol1", "F", "F2");
reaction.actRemplacerReaxel("F", "F1", "sol", "sol2");

 * diminu
reaction.cdtReaxelEn("sol", "sol1", 0, 0, 0);
reaction.cdtReaxelLieAvec("sol", "sol1", "L1", "L11", "F", "F1");
reaction.actRemplacerConnexel("L1", "L11", "L0", "L00");
reaction.actRemplacerReaxel("sol", "sol1", "F", "F2");
reaction.actRemplacerReaxel("F", "F1", "sol", "sol2");

 * supprime sur ctc
reaction.cdtReaxelEn("sol", "sol1", 0, 0, 0);
reaction.cdtReaxelTouche("sol", "sol1", "F", "F1");
reaction.actSupprimerReaxel("sol", "sol1");
reaction.actSupprimerReaxel("F", "F1");

 * supp extra
reaction.cdtReaxelEn("sol", "sol1", 0, 0, 0);
reaction.cdtReaxelTouche("sol", "sol1", "F", "F1");
reaction.cdtReaxelTouche("sol", "sol1", "F", "F2");
reaction.actSupprimerReaxel("sol", "sol1");
reaction.actSupprimerReaxel("F", "F1");
reaction.actSupprimerReaxel("F", "F2");

 * actForce
reaction.cdtReaxelEn("F", "F1", 0, 0, 0);
reaction.actForce("F", "F1", 10, 0, 0);

 */ 

