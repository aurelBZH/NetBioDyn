/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package netbiodyn.util;

import java.util.ArrayList;
import java.util.Random;

/**
 * Singleton RandomGen
 *
 * @author riviere
 */
public class RandomGen {

    private static int x;
    private static int y;
    private static int z;
    private static int w;
    private static int c;

    // Stockage de l'unique instance de cette classe.
    private static RandomGen uniqueInstance;

    /**
     * Constructeur en privé (donc inaccessible à l'extérieur de la classe).
     */
    private RandomGen() {
        initSeeds();
    }

    /**
     * Méthode statique qui sert de pseudo-constructeur (utilisation du mot clef
     * "synchronized" pour le multithread).
     *
     * @return un objet UNIQUE de type Journalisation
     */
    public static synchronized RandomGen getInstance() {
        if (uniqueInstance == null) {
            uniqueInstance = new RandomGen();
        }
        return uniqueInstance;
    }

    public int nextInt(int max) {
        int i = Math.abs(getIntKISS() % max);
        if (i >= max) {
            System.err.println("NEXT INT " + i + " >= " + max);
        }
        return i;
    }

    public Double nextDouble() {
        Double d;
        d = getIntKISS() / 4294967296.0 + 0.5;
        if (d >= 1) {
            System.err.println("NEXT DOUBLE " + d + " > 1 ");
        }
        return d;
    }

    private int getIntKISS() {
        int t;
        y ^= y << 5;
        y ^= y >> 7;
        y ^= y << 22;
        t = z + w + c;
        z = w;
        w = t & 2147483647;
        x = x + 1411392427;

        return (x + y + w);
    }

    public ArrayList<Integer> liste_entiers_melanges(int taille) {
        ArrayList<Integer> lst_init = new ArrayList<>();
        ArrayList<Integer> lst_res = new ArrayList<>();

        for (int i = 0; i < taille; i++) {
            lst_init.add(i);
        }

        for (int i = taille - 1; i >= 0; i--) {
            int de = nextInt(lst_init.size());
            lst_res.add(lst_init.get(de));
            lst_init.remove(de);
        }

        return lst_res;
    }

    private void initSeeds() {
        long s = System.currentTimeMillis() * System.nanoTime();
        Random rnd = new Random(0);
        x = (int) (s * rnd.nextInt());
        y = (int) (s * rnd.nextInt());
        z = (int) (s * rnd.nextInt());
        w = (int) (s * rnd.nextInt());
        c = (int) (s * rnd.nextInt());
    }

    public static void main(String[] args) {
        int[] ndigits = new int[10];
        double x;
        int n;

        RandomGen random=RandomGen.getInstance();

        // Initialize the array 
        for (int i = 0; i < 10; i++) {
            ndigits[i] = 0;
        }

        // Test the random number generator a whole lot
        for (long i = 0; i < 100000000; i++) {
            // generate a new random number between 0 and 9
            x = random.nextInt(10);
            n = (int) x;
            //count the digits in the random number
            ndigits[n]++;
        }

        // Print the results
        for (int i = 0; i < 10; i++) {
            System.out.println(i + ": " + ndigits[i]);
        }
    }

}
