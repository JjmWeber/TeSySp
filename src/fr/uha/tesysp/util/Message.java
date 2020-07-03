package fr.uha.tesysp.util;

import java.io.File;

/**
 * This class contains all the message written in the console
 *
 * @author Jonathan Weber
 */
public final class Message {
    public final static String SOFTWAREACRONYM = "TeSySp";
    public final static String SOFTWAREFULLNAME = "Technical Symbol Spotter";
    public final static String LAUNCHMSG = SOFTWAREACRONYM + " launched ";
    public final static String LAUNCHOFFSPOTTING = "Offline Spotting launched.";
    public final static String DONEOFFSPOTTING = "Offline Spotting Done.";
    public final static String SPOTTINGOKSTARTFILTERING = "Spotting done, start points filtering.";
    public final static String FILTERINGOK = "Points filtering done.";
    public final static String OFFSPOTTINGLOGSAVED = "Spotting log saved.";
    public final static String LAUNCHONSPOTTING = "Online Spotting launched.";
    public final static String DONEONSPOTTING = "Online Spotting Done.";
    public final static String SPOTTINGIMAGESAVED = "Spotting image saved";
    public final static String SPOTTEDPOINTSSAVED = "Spotted points saved";
    public final static String QUITMSG = SOFTWAREACRONYM + " terminated";
    public final static String MAINFRAMELOADED = "Main Frame loaded";
    public final static String DOCUMENTLOADED = "Document loaded";
    public final static String SYMBOLLOADED = "Symbol loaded";
    public final static String RESETDATA = "Data have been reseted";
    public final static String CONSOLEMODE = LAUNCHMSG + "in console mode.";
    public final static String GRAPHICALMODE = LAUNCHMSG + "in graphical mode.";
    public final static String CONSOLEARGUMENTS = SOFTWAREACRONYM + " arguments :\n" +
            "\t " + Constants.CONSOLEHELPARGUMENT + "\t\t\t display help\n" +
            "\t " + Constants.CONSOLEVERSIONARGUMENT + "\t\t display version information\n" +
            "\t " + Constants.CONSOLEDOCUMENTARGUMENT + "\t\t document image to process\n" +
            "\t " + Constants.CONSOLESYMBOLARGUMENT + "\t\t symbol image to spot\n" +
            "\t " + Constants.CONSOLENBTHREADARGUMENT + "\t\t number of threads (default: " + Constants.DEFAULTTHREADNUMBER + ")\n" +
            "\t " + Constants.CONSOLEALPHAARGUMENT + "\t\t\t alpha parameter (default: " + Constants.DEFAULTALPHA + ")\n" +
            "\t " + Constants.CONSOLEBETAARGUMENT + "\t\t\t beta parameter (default: " + Constants.DEFAULTBETA + ")\n" +
            "\t " + Constants.CONSOLEFGBGTHRESHARGUMENT + "\t\t foreground/background threshold parameter (default: " + Constants.DEFAULTFGBGTHRESH + ")\n" +
            "\t " + Constants.CONSOLEONLYFGARGUMENT + "\t\t match symbol foreground only\n" +
            "\t " + Constants.CONSOLEONLYBGARGUMENT + "\t\t match symbol background only\n" +
            "\t " + Constants.CONSOLESYMMETRYARGUMENT + "\t\t symbol symmetry processing parameter (default: " + Constants.DEFAULTSYMMETRY + ")\n" +
            "\t " + Constants.CONSOLEORIENTATIONROBUSTNESSANGULARSTEPARGUMENT + "\t symbol orientation step parameter (default: " + Constants.DEFAULTORIENTATIONROBUSTNESSANGULARSTEP + ")\n" +
            "\t " + Constants.CONSOLERESULTPATHARGUMENT + "\t\t result path (default: " + new File("").getAbsolutePath() + File.separator + Constants.DEFAULTMATCHINGFILENAME + ")\n";
}
