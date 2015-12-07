/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package netbiodyn.util;

/**
 *
 * @author riviere
 */
public class Lang {

    private String lang;

    private static Lang uniqueInstance;

    private Lang() {
        lang = "EN";
    }

    public static synchronized Lang getInstance() {
        if (uniqueInstance == null) {
            uniqueInstance = new Lang();
        }
        return uniqueInstance;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String l) {
        lang = l;
    }

    public void changeLang() {
        if (lang.equals("FR")) {
            lang = "EN";
        } else {
            lang = "FR";
        }
    }

}
