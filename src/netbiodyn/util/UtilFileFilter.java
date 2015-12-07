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


package netbiodyn.util;

/**
 *
 * @author ballet
 */
import java.io.File;
import java.util.ArrayList;
import javax.swing.filechooser.FileFilter;
 
/**
 * This class models a simple file filter with a description for accepted files
 * and their extensions.
 **/
public class UtilFileFilter extends FileFilter
{
    
    private String description;
    private ArrayList<String> acceptedExtensions = new ArrayList<String>();
    
    /**
     * Creates a new UtilFileFilter object with the specified description for 
     * accepted files and their extensions.
     * @param description the description of the accepted files.
     * @param extensions the extensions of the accepted files.
     **/
    public UtilFileFilter(String description, String ... extensions)
    {
        if(description == null || extensions.length == 0)
            throw new NullPointerException();
        
        this.description = description; 
        for(String extension : extensions)
            acceptedExtensions.add(extension);
    } 
    
    /**
     * Adds an extensions to the accepted extensions of the file filter.
     * @param extension the extension to ad.
     **/
    public void addExtension(String extension)
    {
        acceptedExtensions.add(extension);
    }
    
    /**
     * Removes an extension from the accepted extensions of the file filter.
     * @param extension the extension to remove.
     **/
    public void removeExtension(String extension)
    {
       acceptedExtensions.remove(extension);
    }
    
    /**
     * Returns true if the given file is accepted by the file filter, returns
     * false otherwise.
     * @return true if the given file is accepted by the file filter, returns
     * false otherwise.
     **/
    @Override
    public boolean accept(File file)
    {
        String nomFichier = file.getName().toLowerCase();
        if(file.isDirectory())
            return true;
        else
            for(int i = 0; i < acceptedExtensions.size();i++)
                if(nomFichier.endsWith(acceptedExtensions.get(i)))
                    return true;
        return false;
        
    } 
    
    /** 
     * Returns the description of the accepted files.
     * @return the description of the accepted files.
     */ 
    @Override
    public String getDescription()
    {
        String rep = description + " ( ." + acceptedExtensions.get(0);
        for(int i = 1; i < acceptedExtensions.size(); i++)
            rep += ", ."+ acceptedExtensions.get(i);
        return rep + " )";
    }
    
}
 