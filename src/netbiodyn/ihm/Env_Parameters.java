/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package netbiodyn.ihm;

import java.awt.image.BufferedImage;
import netbiodyn.util.Lang;

/**
 *
 * @author riviere
 */
public class Env_Parameters {
    
    private int X;
    private int Y;
    private int Z;
    private String description;
    private BufferedImage image;
    private String lang;
    private String _str_image_deco = "";
    
    public Env_Parameters(){
        X=100;
        Y=100;
        Z=2;
        description="Default Environment";
        image=null;
        lang=Lang.getInstance().getLang();
        _str_image_deco ="";
    }
    
    public Env_Parameters(String lang, int X, int Y, int Z, String description, BufferedImage image, String _str_image_deco){
        this.X=X;
        this.Y=Y;
        this.Z=Z;
        this.image=image;
        this.description=description;
        this.lang=lang;
        this._str_image_deco=_str_image_deco;
    }

    public int getX() {
        return X;
    }

    public void setX(int X) {
        this.X = X;
    }

    public int getY() {
        return Y;
    }

    public void setY(int Y) {
        this.Y = Y;
    }

    public int getZ() {
        return Z;
    }

    public void setZ(int Z) {
        this.Z = Z;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BufferedImage getImage() {
        return image;
    }

    public void setImage(BufferedImage image) {
        this.image = image;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }    

    public String getStr_image_deco() {
        return _str_image_deco;
    }

    public void setStr_image_deco(String _str_image_deco) {
        this._str_image_deco = _str_image_deco;
    }
    
    @Override
    public String toString(){
        return this.getX()+" x "+this.getY()+" x "+this.getZ()+" - "+getDescription()+" - "+getLang();
    }
}
