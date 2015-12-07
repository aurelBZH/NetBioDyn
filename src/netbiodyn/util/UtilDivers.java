/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package netbiodyn.util;

import java.awt.Graphics;

/**
 *
 * @author pascalballet
 */
public class UtilDivers {
    
    // Retourne la chaine originale sans ",102"
    public static String str_originale(String nom) {
        int i = 0;
        while (nom.charAt(i) != ',' && i < nom.length()) {
            i++;
        }
        String res = new String(nom.substring(0, i));
        return res;
    }

    public static double normaliseAngle(double a) {
        while(a<0) a+=Math.PI*2;
        while(a>Math.PI*2) a-=Math.PI*2;
        return a;
    }
    public static String setCharAt(String str, int i, char c) {
        String str2 = "";
        for(int k=0; k<str.length(); k++) {
            if(k != i) str2 += str.charAt(k); else str2 += c;
        }
        return str2;
    }
    public static void drawArrow(Graphics gfx, UtilPointF center, float sizeX, float sizeY, float angle) {
        
        // Right arrow
        UtilPointF p0 = new UtilPointF(center.X-sizeX/2, center.Y, 0); // left
        UtilPointF p1 = new UtilPointF(center.X+sizeX/2, center.Y, 0); // right
        float dx = sizeY/2;
        UtilPointF p2 = new UtilPointF(center.X+sizeX/2-dx, center.Y-dx, 0); // upper semi-arrow
        UtilPointF p3 = new UtilPointF(center.X+sizeX/2-dx, center.Y+dx, 0); // upper semi-arrow
        
        // Rotation
        UtilPointF p0_res = rotatePoint(p0, center, angle);
        UtilPointF p1_res = rotatePoint(p1, center, angle);
        UtilPointF p2_res = rotatePoint(p2, center, angle);
        UtilPointF p3_res = rotatePoint(p3, center, angle);
        
        // Drawing
        gfx.drawLine((int)p0_res.X, (int)p0_res.Y, (int)p1_res.X, (int)p1_res.Y);
        gfx.drawLine((int)p1_res.X, (int)p1_res.Y, (int)p2_res.X, (int)p2_res.Y);
        gfx.drawLine((int)p1_res.X, (int)p1_res.Y, (int)p3_res.X, (int)p3_res.Y);
        
    }
    public static UtilPointF rotatePoint(UtilPointF pt, UtilPointF center, float angle) {
        UtilPointF pt_res = new UtilPointF(0,0,0);
        
        pt_res.X = (float) (center.X + (pt.X - center.X)*Math.cos(-angle) - (pt.Y - center.Y)*Math.sin(-angle));
        pt_res.Y = (float) (center.Y + (pt.X - center.X)*Math.sin(-angle) + (pt.Y - center.Y)*Math.cos(-angle));
        
        return pt_res;
        
    }
    
    public static String fichier(String str) {
        int pos_slash = -1;

        for (int i = str.length() - 1; i >= 0; i--) {
            if (str.charAt(i) == '/' || str.charAt(i) == '\\') {
                pos_slash = i;
                i = -1;
            }
        }
        if (pos_slash >= 0) {
            int deb = pos_slash + 1;
            int fin = str.length();
            return str.substring(deb, fin);
        } else {
            return str;
        }
    }
    
    public static String removeExtension(String str){
        str=str.substring(0, str.indexOf('.'));
        return str;
    }
    
}
