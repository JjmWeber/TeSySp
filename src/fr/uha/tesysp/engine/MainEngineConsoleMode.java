package fr.uha.tesysp.engine;

import fr.uha.tesysp.data.MainData;
import fr.uha.tesysp.util.*;

import java.io.File;


public class MainEngineConsoleMode {

    private MainData mainData;

    private byte version = Constants.VERSION;

    public MainEngineConsoleMode(String[] args) {
        if (version == Constants.DEMO) {
            System.err.println("This is a trial version of TeSySp.\nTrial version of TeSySp does not allow the use of Console mode.\nLaunch TeSySp without arguments to launch graphical mode.");
        } else if (args[0].equals(Constants.CONSOLEHELPARGUMENT)) {
            System.out.println(Message.CONSOLEARGUMENTS);
        } else if (args[0].equals(Constants.CONSOLEVERSIONARGUMENT)) {
            System.out.println(GUIText.ABOUTFRAMECONTENT);
        } else {
            System.out.println(Message.CONSOLEMODE);
            mainData = ConsoleArguments.analyze(args);
            // TODO DO WE NEED CONSOLE MODE ?
            //mainData.setOfflineSpottingResult(OfflineSpottingEngine.launchOfflineSpotting(mainData.getDocument(), mainData.getSymbol(), mainData.getMode(), mainData.getSettings(), false, false));
            Tool.writeTextFile(mainData.getPathLocation() + File.separator + mainData.getCsvOutputFilename(), Tool.offlineSpottingLog(mainData));
            System.out.println(Message.OFFSPOTTINGLOGSAVED);
            System.out.println(Message.QUITMSG);
        }
    }


}
