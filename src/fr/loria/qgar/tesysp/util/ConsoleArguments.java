package fr.loria.qgar.tesysp.util;

import fr.loria.qgar.tesysp.data.MainData;
import fr.unistra.pelican.ByteImage;
import fr.unistra.pelican.algorithms.io.ImageLoader;

import java.io.File;

public class ConsoleArguments {

    public final static MainData analyze(String[] args) {
        MainData mainData = new MainData(Constants.CONSOLEMODE);
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals(Constants.CONSOLEDOCUMENTARGUMENT)) {
                i++;
                ByteImage document = (ByteImage) ImageLoader.exec(args[i]);
                if (document.getBDim() > 1) {
                    System.err.println(GUIText.DOCUMENTTOOMUCHDIM);
                    System.exit(0);
                }
                mainData.setDocument(document);
                mainData.setDocumentPath(args[i]);
                System.out.println(Message.DOCUMENTLOADED);
            } else if (args[i].equals(Constants.CONSOLESYMBOLARGUMENT)) {
                i++;
                ByteImage symbol = (ByteImage) ImageLoader.exec(args[i]);
                if (symbol.getBDim() > 1) {
                    System.err.println(GUIText.SYMBOLTOOMUCHDIM);
                    System.exit(0);
                }
                mainData.setSymbol(symbol);
                mainData.setSymbolPath(args[i]);
                System.out.println(Message.SYMBOLLOADED);
            } else if (args[i].equals(Constants.CONSOLENBTHREADARGUMENT)) {
                i++;
                int nbThreads = Integer.parseInt(args[i]);
                if (nbThreads < 1) {
                    System.err.println("The number of threads must be >= 1 !");
                    System.exit(0);
                }
                mainData.getSettingsData().setNbThreads(nbThreads);
            } else if (args[i].equals(Constants.CONSOLEALPHAARGUMENT)) {
                i++;
                double alpha = Double.parseDouble(args[i]);
                if (alpha <= 0) {
                    System.err.println("Alpha must be > 0 !");
                    System.exit(0);
                }
                mainData.getSettingsData().setAlpha(alpha);
            } else if (args[i].equals(Constants.CONSOLEBETAARGUMENT)) {
                i++;
                double beta = Double.parseDouble(args[i]);
                if (beta <= 0) {
                    System.err.println("Beta must be > 0 !");
                    System.exit(0);
                }
                mainData.getSettingsData().setBeta(beta);
            } else if (args[i].equals(Constants.CONSOLEFGBGTHRESHARGUMENT)) {
                i++;
                int thresh = Integer.parseInt(args[i]);
                if (thresh < 0 || thresh > 255) {
                    System.err.println("FG/BG threshold must be between 0 and 255 !");
                    System.exit(0);
                }
                mainData.getSettingsData().setFGBGThresh(thresh);
            } else if (args[i].equals(Constants.CONSOLEONLYFGARGUMENT)) {
                mainData.getSettingsData().setOnlyFG(true);
            } else if (args[i].equals(Constants.CONSOLEONLYBGARGUMENT)) {
                mainData.getSettingsData().setOnlyBG(true);
            } else if (args[i].equals(Constants.CONSOLESYMMETRYARGUMENT)) {
                i++;
                boolean symmetry = Boolean.parseBoolean(args[i]);
                mainData.getSettingsData().setSymmetry(symmetry);
            } else if (args[i].equals(Constants.CONSOLEORIENTATIONROBUSTNESSANGULARSTEPARGUMENT)) {
                i++;
                int step = Integer.parseInt(args[i]);
                if (step < 2 || step > 360) {
                    System.err.println("Orientation step must be between 2 and 360 !");
                    System.exit(0);
                }
                mainData.getSettingsData().setAngularStep(step);
            } else if (args[i].equals(Constants.CONSOLERESULTPATHARGUMENT)) {
                i++;
                File resPath = new File(args[i]);
                String filename = resPath.getName();
                String directory = resPath.getAbsolutePath().substring(0, resPath.getAbsolutePath().length() - filename.length());
                mainData.setPathLocation(new File(directory));
                mainData.setCsvOutputFilename(filename);
            } else {
                System.err.println("unknow argument : " + args[i] + "\n" + Message.CONSOLEARGUMENTS);
                System.exit(0);

            }
        }
        if (!mainData.hasDocument() || !mainData.hasSymbol()) {
            System.err.println("User must at least indicates a document and a symbol\n" + Message.CONSOLEARGUMENTS);
            System.exit(0);
        }
        return mainData;
    }


}
