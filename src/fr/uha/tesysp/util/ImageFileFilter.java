package fr.uha.tesysp.util;

import javax.swing.filechooser.FileFilter;
import java.io.File;

public class ImageFileFilter extends FileFilter {

    @Override
    public boolean accept(File f) {
        if (f.isDirectory())
            return true;
        String s = f.getName();
        int i = s.lastIndexOf('.');
        if (i > 0 && i < s.length() - 1) {
            String ext = s.substring(i + 1);
            return (ext.equalsIgnoreCase("png"));
        }
        return false;
    }

    @Override
    public String getDescription() {
        return "PNG files only";
    }

}
