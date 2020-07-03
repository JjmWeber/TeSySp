package fr.uha.tesysp.engine;

import fr.uha.tesysp.Main;
import fr.uha.tesysp.controllers.MainController;
import fr.uha.tesysp.data.MainData;
import fr.uha.tesysp.data.OfflineSpottingResult;
import fr.uha.tesysp.data.SettingsData;
import fr.uha.tesysp.util.*;
import fr.unistra.pelican.AlgorithmException;
import fr.unistra.pelican.ByteImage;
import fr.unistra.pelican.algorithms.io.ImageLoader;
import javafx.concurrent.Task;
import javafx.scene.control.Accordion;
import javafx.scene.control.Alert;
import javafx.scene.control.TitledPane;
import javafx.stage.FileChooser;

import java.io.File;

public class MainEngineGraphicalMode {

    private MainController controller;

    public MainData mainData;

    private byte version = Constants.VERSION;

    public MainEngineGraphicalMode() {
        mainData = new MainData(Constants.GRAPHICALMODE);

        if (version == Constants.DEMO) {
            Tool.dialogMessage("TeSySp : Trial Version", "This is a trial version.\nYou cannot process image larger than 1000x1000.", Alert.AlertType.WARNING);
        }
    }


    public void loadPlan() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("IMAGE files (*.png, *.jpg)", "*.png", "*.jpg"));
        File planFile = fileChooser.showOpenDialog(Main.PrimaryStage);
        if (planFile != null) {
            Main.PrimaryStage.setTitle("Tesysp  " + planFile.getName());
            controller.labelPlan.setText(planFile.getName());
            File f = new File(planFile.getPath());
            javafx.scene.image.Image image = new javafx.scene.image.Image(f.toURI().toString());
            controller.imgView.setImage(image);

            mainData.setPathLocation(new File(f.getParent()));

            Thread t = new Thread(() -> {
                planByteImage(f, planFile);
            });
            t.start();
        }
    }

    private void loadPlanNoThread() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("IMAGE files (*.png, *.jpg)", "*.png", "*.jpg"));
        File planFile = fileChooser.showOpenDialog(Main.PrimaryStage);
        if (planFile != null) {
            Main.PrimaryStage.setTitle("Tesysp  " + planFile.getName());
            controller.labelPlan.setText(planFile.getName());
            File f = new File(planFile.getPath());
            javafx.scene.image.Image image = new javafx.scene.image.Image(f.toURI().toString());
            controller.imgView.setImage(image);

            mainData.setPathLocation(new File(f.getParent()));
            planByteImage(f, planFile);
        }
    }

    private void loadPlanNoThreadHavePath(String path) {
        File f = new File(path);
        javafx.scene.image.Image image = new javafx.scene.image.Image(f.toURI().toString());
        controller.imgView.setImage(image);

        mainData.setPathLocation(new File(f.getParent()));
        try {
            ByteImage plan = (ByteImage) ImageLoader.exec(f.getAbsolutePath());
            if (version == Constants.DEMO && (Math.log10(plan.getXDim()) > 3 || Math.log10(plan.getYDim()) > 3)) {
                Tool.dialogMessage("Error", "This is a trial version.\nYou cannot process image larger than 1000x1000.", Alert.AlertType.INFORMATION);
            } else if (plan.getBDim() > 1) {
                System.out.println("ERROR : " + GUIText.DOCUMENTTOOMUCHDIM + plan.getBDim());
            } else {
                mainData.setDocument(plan);
                mainData.setDocumentPath(path);
                System.out.println(Message.DOCUMENTLOADED + " : " + path);
            }
        } catch (AlgorithmException ex) {
        }
    }

    public void loadSymbol() {

        FileChooser FileChooser = new FileChooser();
        FileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("IMAGE files (*.png, *.jpg)", "*.png", "*.jpg"));
        File symbol = FileChooser.showOpenDialog(Main.PrimaryStage);
        if (symbol != null) {
            System.out.println(symbol);
            controller.labelSymbols.setText(symbol.getName());
            final String symbolToLoad = symbol.getAbsolutePath();
            mainData.setPathLocation(new File(symbol.getParent()));
            Thread t = new Thread(() -> {
                symboleByteImage(symbolToLoad);
            });
            t.start();
        }
    }

    private void loadSymbolNoThread() {

        FileChooser FileChooser = new FileChooser();
        FileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("IMAGE files (*.png, *.jpg)", "*.png", "*.jpg"));
        File symbol = FileChooser.showOpenDialog(Main.PrimaryStage);
        if (symbol != null) {
            System.out.println(symbol);
            controller.labelSymbols.setText(symbol.getName());
            final String symbolToLoad = symbol.getAbsolutePath();
            mainData.setPathLocation(new File(symbol.getParent()));
            symboleByteImage(symbolToLoad);
        }
    }

    public void loadSymbolNoThreadHavePath(String Path) {

        if (Path != null) {
            System.out.println(Path);
            mainData.setPathLocation(new File(Path));
            symboleByteImage(Path);
        }
    }


    private void planByteImage(File f, File planFile) {
        try {
            ByteImage plan = (ByteImage) ImageLoader.exec(f.getAbsolutePath());
            if (version == Constants.DEMO && (Math.log10(plan.getXDim()) > 3 || Math.log10(plan.getYDim()) > 3)) {
                Tool.dialogMessage("Error", "This is a trial version.\nYou cannot process image larger than 1000x1000.", Alert.AlertType.INFORMATION);
            } else if (plan.getBDim() > 1) {
                System.out.println("ERROR : " + GUIText.DOCUMENTTOOMUCHDIM + plan.getBDim());
                //JOptionPane.showMessageDialog(null, GUIText.DOCUMENTTOOMUCHDIM + plan.getBDim(), "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                mainData.setDocument(plan);
                mainData.setDocumentPath(planFile.getAbsolutePath());
                System.out.println(Message.DOCUMENTLOADED + " : " + planFile.getAbsolutePath());

                //mainFrame.updateButtons();
            }
        } catch (AlgorithmException ex) {
            //JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        //infoFrame.dispose();
    }

    private void symboleByteImage(String symbolToLoad) {
        try {
            ByteImage symbol1 = (ByteImage) ImageLoader.exec(symbolToLoad);
            if (symbol1.getBDim() > 1) {
                Tool.dialogMessage("Error", GUIText.SYMBOLTOOMUCHDIM + symbol1.getBDim(), Alert.AlertType.ERROR);
                //JOptionPane.showMessageDialog(null, GUIText.SYMBOLTOOMUCHDIM + symbol.getBDim(), "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                mainData.setSymbol(symbol1);
                mainData.setSymbolPath(symbolToLoad);
                System.out.println(Message.SYMBOLLOADED + " : " + symbolToLoad);
                //mainFrame.updateButtons();
            }
        } catch (AlgorithmException ex) {
            //JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void reset() {
        mainData.reset();
        //mainFrame.updateButtons();
    }

    public void setControls(MainController controller) {
        this.controller = controller;
        /*if (version == Constants.DEMO)
            mainStage.setTitle(mainStage.getTitle() + " : Trial Version");*/
    }

    public boolean hasPlan() {
        return mainData.hasDocument();
    }

    public boolean hasSymbol() {
        return mainData.hasSymbol();
    }

    public boolean hasOfflineSpottingResult() {
        return mainData.hasOfflineSpottingResult();
    }

    public boolean hasReferences() {
        return mainData.hasReferences();
    }

    public SettingsData getSettingsData() {
        return mainData.getSettingsData();
    }

    public void buildGroundtruth() {
        //GroundTruthBuilderLaunchFrame oslf = new GroundTruthBuilderLaunchFrame(mainData.getSettingsData());
    }

    @Deprecated
    public void buildReferences() {
        //TODO : Solve the tiles problem... don't put this crap in memory.
        mainData.setTiles(new Crop(mainData.getDocument(), 100).getTiles());
        /*OfflineSpottingLaunchFrame oslf = new OfflineSpottingLaunchFrame(mainData.getSettingsData());
        final ArrayList<ByteImage> hmtList = new ArrayList<ByteImage>();
        final ArrayList<OfflineSpottingResult> results = new ArrayList<OfflineSpottingResult>();

        Thread tHMT = new Thread(new Runnable() {
            public void run() {
                for (int i = 0; i < mainData.getTiles().size(); i++) {
                    System.out.println("image " + i);
                    Image tile = mainData.getTiles().get(i);
                    OfflineSpottingResult result = OfflineSpottingEngine.launchOfflineSpotting((ByteImage) tile, mainData.getSymbol(), mainData.getMode(), mainData.getSettingsData(), true, false);
                    hmtList.add(result.getHmt());

                }
                mainData.sethMTs(hmtList);
                mainFrame.updateButtons();
            }
        });
        if (oslf.isValidated()) {
            tHMT.start();
            SelectSymbols select = new SelectSymbols(mainData);
            ArrayList<ArrayList<Rectangle>> selectedAreas = select.getTotalSelectedAreas();
            mainData.setSelectedAreas(selectedAreas);
            mainFrame.updateButtons();
        }*/
    }
        /*
    public void exportReferencesPoints() {
        ArrayList<ByteImage> hmtList = mainData.getHMTs();
        ArrayList<ArrayList<Rectangle>> selectedAreas = mainData.getSelectedAreas();
        ArrayList<ArrayList<Point>> selectedPoints = new FilterPoints(selectedAreas, hmtList).getFilteredPoints();
        String text = Tool.selectedPointsToCSV(selectedPoints);
        JFileChooser jfc = new JFileChooser(mainData.getPathLocation());
        jfc.setFileFilter(new SpottedPointFileFilter());
        int result = jfc.showSaveDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {
            String saveLocation = jfc.getSelectedFile().getAbsolutePath();
            Tool.writeTextFile(saveLocation, text);
        }
    }*/

    public void launchOfflineSpotting(Accordion menuAccordion, TitledPane optionAccordion) {
        Task<OfflineSpottingResult> task = new Task<>() {
            @Override
            public OfflineSpottingResult call() {
                return OfflineSpottingEngine.launchOfflineSpotting(mainData.getDocument(), mainData.getSymbol(), mainData.getMode(),
                        mainData.getSettingsData(), false, mainData.getSettingsData().isAutoFindResult(), controller.progressBar, controller.labelRemainingTime, mainData.getDocumentPath(), mainData.getSymbolPath());
            }
        };

        task.setOnSucceeded(e -> {
            OfflineSpottingResult result = task.getValue();
            mainData.setOfflineSpottingResult(result);
            menuAccordion.setExpandedPane(optionAccordion);
        });

        new Thread(task).start();
    }


    public void launchOnlineSpotting() {
        OnlineSpottingEngine.launchOnlineSpotting(mainData.getDocument(), mainData.getOfflineSpottingResult(), mainData, controller.imgView, controller.sliderThreshold);
    }

    public void exportPoints() throws Exception {
        OnlineSpottingEngine.exportPoints(mainData, mainData.getOfflineSpottingResult(), controller.sliderThreshold);
    }

    public void exportToXML() throws Exception {
        OnlineSpottingEngine.pointsToXML(mainData.getOfflineSpottingResult());
    }

    public void saveAllToXML() throws Exception {
        OnlineSpottingEngine.pointsToXML(mainData.getOfflineSpottingResult());
    }

    public void chargeXml() throws Exception {
        OfflineSpottingResult offlineSpottingResult = OnlineSpottingEngine.xmlToPoints();
        mainData.setOfflineSpottingResult(offlineSpottingResult);
        String documentPath = offlineSpottingResult.getDocumentPath();
        String symbolPath = offlineSpottingResult.getSymbolPath();
        boolean truePath = controller.alertXmlPlan(documentPath, symbolPath);
        if (truePath) {
            loadPlanNoThreadHavePath(documentPath);
            loadSymbolNoThreadHavePath(symbolPath);
            launchOnlineSpotting();
        } else {
            loadPlanNoThread();
            loadSymbolNoThread();
            if (mainData.getDocumentPath() != null) {
                launchOnlineSpotting();
            } else {
                controller.alertNoPlanNoSymbol();
            }
        }
    }

}
