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
 * Reaxel.java
 * 
 * Created on 23 oct. 2007, 10:22:08
 * 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package netbiodyn;

import java.awt.Color;
import java.awt.Image;

/**
 *
 * @author ballet
 */
public class InstanceReaxel extends InstanceSimplexel implements Cloneable{

    private int _x, _y, _z;
    private Color _couleur = Color.BLUE;
    private boolean _vidable = true;
    private double _taille = 1;
    private int _forme = 0;
    private double _demie_vie = 0;

    private boolean _selectionne = false;

    private String _nom;
    private Image _image = null;
    
    public InstanceReaxel(){
        _x=-1;
        _y=-1;
        _z=-1;
    }

    @Override
    public InstanceReaxel clone() {
        InstanceReaxel clone = new InstanceReaxel();
        clone._couleur = _couleur;
        clone._taille = _taille;
        clone._demie_vie = _demie_vie;
        clone._forme = _forme;
        clone._nom = _nom;
        clone._image = _image;
        clone._vidable = _vidable;
        clone._x=_x;
        clone._y=_y;
        clone._z=_z;
        return clone;
    }
    
    public static InstanceReaxel CreerReaxel(Entity cli) {
        InstanceReaxel r = new InstanceReaxel();
        r._couleur = cli.Couleur;
        r._taille = cli._taille;
        r._forme = cli._forme;
        r._demie_vie = cli.DemieVie;
        r._nom = cli.getEtiquettes();
        r._image = cli.BackgroundImage;
        r._vidable = cli.Vidable;
        return r;
    }
    
    @Override
    public boolean equals(Object o){
        if(o instanceof InstanceReaxel){
            InstanceReaxel r=(InstanceReaxel)o;
            return ((r.getX() == getX())&&(r.getY()==getY())&&(r.getZ()==getZ())&&(r.getNom().equals(getNom())));
        }
        return false;
    }

    public int getX() {
        return _x;
    }

    public void setX(int _x) {
        this._x = _x;
    }

    public int getY() {
        return _y;
    }

    public void setY(int _y) {
        this._y = _y;
    }

    public int getZ() {
        return _z;
    }

    public void setZ(int _z) {
        this._z = _z;
    }

    public Color getCouleur() {
        return _couleur;
    }

    public void setCouleur(Color _couleur) {
        this._couleur = _couleur;
    }

    public boolean isVidable() {
        return _vidable;
    }

    public void setVidable(boolean _vidable) {
        this._vidable = _vidable;
    }

    public double getTaille() {
        return _taille;
    }

    public void setTaille(double _taille) {
        this._taille = _taille;
    }

    public int getForme() {
        return _forme;
    }

    public void setForme(int _forme) {
        this._forme = _forme;
    }

    public double getDemie_vie() {
        return _demie_vie;
    }

    public void setDemie_vie(double _demie_vie) {
        this._demie_vie = _demie_vie;
    }

    public boolean isSelectionne() {
        return _selectionne;
    }

    public void setSelectionne(boolean _selectionne) {
        this._selectionne = _selectionne;
    }

    public String getNom() {
        return _nom;
    }

    public void setNom(String _nom) {
        this._nom = _nom;
    }

    public Image getImage() {
        return _image;
    }

    public void setImage(Image _image) {
        this._image = _image;
    }
    
    
    
}
