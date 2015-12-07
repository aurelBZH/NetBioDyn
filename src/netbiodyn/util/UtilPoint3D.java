/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package netbiodyn.util;

import java.util.ArrayList;
import netbiodyn.InstanceReaxel;

/**
 *
 * @author user
 */
public class UtilPoint3D {

    public UtilPoint3D() {
        x = 0;
        y = 0;
        z = 0;
    }

    public UtilPoint3D(int i, int j, int k) {
        x = i;
        y = j;
        z = k;
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }

        if ((obj == null) || (obj.getClass() != this.getClass())) {
            return false;
        }

        UtilPoint3D test = (UtilPoint3D) obj;

        return x == test.x && y == test.y && z == test.z;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + this.x;
        hash = 97 * hash + this.y;
        hash = 97 * hash + this.z;
        return hash;
    }

    public int x = 0, y = 0, z = 0;

    public static ArrayList<UtilPoint3D> BresenhamLigne3D(int x1, int y1, int z1, int x2, int y2, int z2) {
        ArrayList<UtilPoint3D> lst_pts = new ArrayList<>();

        int i, dx, dy, dz, l, m, n, x_inc, y_inc, z_inc,
                err_1, err_2, dx2, dy2, dz2;
        dx = x2 - x1;
        dy = y2 - y1;
        dz = z2 - z1;
        x_inc = (dx < 0) ? -1 : 1;
        l = Math.abs(dx);
        y_inc = (dy < 0) ? -1 : 1;
        m = Math.abs(dy);
        z_inc = (dz < 0) ? -1 : 1;
        n = Math.abs(dz);
        dx2 = l << 1;
        dy2 = m << 1;
        dz2 = n << 1;

        if ((l >= m) && (l >= n)) {
            err_1 = dy2 - l;
            err_2 = dz2 - l;
            for (i = 0; i < l; i++) {
                lst_pts.add(new UtilPoint3D(x1, y1, z1));
                if (err_1 > 0) {
                    y1 += y_inc;
                    err_1 -= dx2;
                }
                if (err_2 > 0) {
                    z1 += z_inc;
                    err_2 -= dx2;
                }
                err_1 += dy2;
                err_2 += dz2;
                x1 += x_inc;
            }
        } else if ((m >= l) && (m >= n)) {
            err_1 = dx2 - m;
            err_2 = dz2 - m;
            for (i = 0; i < m; i++) {
                lst_pts.add(new UtilPoint3D(x1, y1, z1));
                if (err_1 > 0) {
                    x1 += x_inc;
                    err_1 -= dy2;
                }
                if (err_2 > 0) {
                    z1 += z_inc;
                    err_2 -= dy2;
                }
                err_1 += dx2;
                err_2 += dz2;
                y1 += y_inc;
            }
        } else {
            err_1 = dy2 - n;
            err_2 = dx2 - n;
            for (i = 0; i < n; i++) {
                lst_pts.add(new UtilPoint3D(x1, y1, z1));
                if (err_1 > 0) {
                    y1 += y_inc;
                    err_1 -= dz2;
                }
                if (err_2 > 0) {
                    x1 += x_inc;
                    err_2 -= dz2;
                }
                err_1 += dy2;
                err_2 += dx2;
                z1 += z_inc;
            }
        }
        lst_pts.add(new UtilPoint3D(x1, y1, z1));
        return lst_pts;
    }

    public static UtilPoint3D centreDeGravite(ArrayList<InstanceReaxel> lst) {
        UtilPoint3D pt = new UtilPoint3D(0, 0, 0);
        int nb = lst.size();
        for (int i = 0; i < nb; i++) {
            pt.x += lst.get(i).getX();
            pt.y += lst.get(i).getY();
            pt.z += lst.get(i).getZ();
        }
        pt.x = pt.x / nb;
        pt.y = pt.y / nb;
        pt.z = pt.z / nb;
        return pt;
    }

    @Override
    public String toString() {
        return "X=" + this.x + " Y=" + this.y + " Z=" + this.z;
    }
}
