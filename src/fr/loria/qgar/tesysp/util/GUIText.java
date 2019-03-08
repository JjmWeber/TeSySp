package fr.loria.qgar.tesysp.util;


/**
 * This class contains all the text
 * displayed in the GUI
 *
 * @author Jonathan Weber
 */
public class GUIText {

    public static final String MAINFRAMETITLE = Message.SOFTWAREACRONYM;
    public static final String LOADPLANBUTTONTEXT = "Load plan";
    public static final String LOADSYMBOLBUTTONTEXT = "Load symbol";
    public static final String BUILDGTBUTTONTEXT = "Build Ground-Truth";
    public static final String EXPORTREFERENCESBUTTONTEXT = "Export references";
    public static final String LAUNCHOFFLINESPOTTING = "Offline spotting";
    public static final String LAUNCHONLINESPOTTING = "Online spotting";
    public static final String BENCHMARKBUTTONTEXT = "Benchmark method";
    public static final String RESETBUTTONTEXT = "Reset";
    public static final String ABOUTBUTTONTEXT = "About";

    public static final String DOCUMENTLOADINGDIALOGTEXT = "Select the document to load";
    public static final String DOCUMENTLOADINGINFORMATIONTEXT = "Loading document, please wait.";
    public static final String DOCUMENTTOOMUCHDIM = "Document must be monoband, this one has ";

    public static final String SYMBOLLOADINGDIALOGTEXT = "Select the symbol to load";
    public static final String SYMBOLLOADINGINFORMATIONTEXT = "Loading symbol, please wait.";
    public static final String SYMBOLTOOMUCHDIM = "Symbol must be monoband, this one has ";

    public static final String OFFLINESPOTTINGLAUNCHTITLE = "Set Offline Spotting Parameters";

    public static final String ONLINESPOTTINGFRAMETITLE = "Select the spotting threshold";

    public static final String ABOUTFRAMETITLE = "About " + Message.SOFTWAREACRONYM;
    public static final String ABOUTFRAMECONTENT = Message.SOFTWAREFULLNAME + " (" + Message.SOFTWAREACRONYM + ")\nVersion " + Constants.VERSIONNUMBER + " (" + Constants.VERSIONDATE + ")\n\n"
            + "Université de Lorraine - LORIA\n"
            + "Jonathan Weber (jonathan.weber@loria.fr)\n"
            + "Antoine Tabbone (antoine.tabbone@loria.fr)";
}
