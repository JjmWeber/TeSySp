package fr.uha.tesysp.engine;

import com.jfoenix.controls.JFXSlider;
import com.thoughtworks.xstream.XStream;
import fr.uha.tesysp.Main;
import fr.uha.tesysp.data.MainData;
import fr.uha.tesysp.data.OfflineSpottingResult;
import fr.uha.tesysp.data.SpottedPoint;
import fr.uha.tesysp.util.Message;
import fr.uha.tesysp.util.Tool;
import fr.unistra.pelican.ByteImage;
import fr.unistra.pelican.Image;
import fr.unistra.pelican.algorithms.draw.DrawRectangle;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

import javax.media.jai.RasterFactory;
import java.awt.*;
import java.awt.image.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;


public class OnlineSpottingEngine {

    protected static final void launchOnlineSpotting(ByteImage plan, OfflineSpottingResult offSpottingResult, MainData mainData, ImageView imgView, JFXSlider slider) {

        ByteImage planByte;

        BufferedImage bimg = null;

        System.out.println(Message.LAUNCHONSPOTTING);

        planByte = plan.newByteImage(plan.xdim, plan.ydim, 1, 1, 3);
        planByte.setImage4D(plan, 0, Image.B);
        planByte.setImage4D(plan, 1, Image.B);
        planByte.setImage4D(plan, 2, Image.B);

        bimg = new BufferedImage(planByte.xdim, planByte.ydim, BufferedImage.TYPE_3BYTE_BGR);
        bimg.setData(drawRaster(planByte, offSpottingResult, slider));

        javafx.scene.image.Image image = SwingFXUtils.toFXImage(bimg, null);
        imgView.setImage(image);

        /**
         * Version pas intelligente (À commenter)
         */

        /*OnlineSpottingFrame osf = new OnlineSpottingFrame(plan, offSpottingResult, mainData);
        osf.setVisible(true);*/

        /**
         * Mettre ici version intelligente :
         * 1/ Récupérer tous les résultats de valeur 50 (valeur à rediscuter) ou plus
         * 2/ Créer et stocker toutes les vignettes correspondantes à ces résultats, éventuellement en corrigant rotation, symétrie, etc selon les infos (pour plus tard)
         * 3/ Faire interface pour présenter à l'utilisateur les X meilleurs résultats (selon score), les X plus mauvais  et Y exemples pris aléatoirement et on demande à l'utilisateur si c'est ce qu'il cherche ou non. Et on stocke réponses
         * 4/ Entrainer réseau de neurones pour ça (réseau à définir)
         * 5/ Envoyez données de test dans le réseau
         * 6/ Afficher résultats à l'utilisateur
         */
    }

    private static Raster drawRaster(ByteImage plan, OfflineSpottingResult offSpottingResult, JFXSlider slider) {
        ByteImage spotting = makeSpottingByteImage(plan, offSpottingResult, slider);
        DataBufferByte dbb;
        SampleModel s;
        Raster r;
        int[] bandOffsets = {0, 1, 2};
        int imageDim = plan.xdim * plan.ydim * 3;
        byte[] byteVal = new byte[imageDim];
        for (int i = 0; i < imageDim; i++) {
            byteVal[i] = (byte) spotting.getPixelByte(i);
        }
        dbb = new DataBufferByte(byteVal, byteVal.length);
        s = RasterFactory.createPixelInterleavedSampleModel(
                DataBuffer.TYPE_BYTE, plan.xdim, plan.ydim, 3, 3 * plan.xdim, bandOffsets);
        r = RasterFactory.createWritableRaster(s, dbb, new Point(0, 0));
        return r;
    }

    private static ByteImage makeSpottingByteImage(ByteImage plan, OfflineSpottingResult offSpottingResult, JFXSlider slider) {
        ByteImage spotting = plan.copyImage(true);
        ArrayList<SpottedPoint> currentSpotting = offSpottingResult.getSpottedPoints().get(slider.getValue() > 99 ? 99 : (int) slider.getValue());
        ArrayList<ByteImage> symbolConfiguration = offSpottingResult.getOrientedSymbols();
        for (SpottedPoint sP : currentSpotting) {
            spotting = (ByteImage) DrawRectangle.exec(spotting, new Point(sP.getX() - symbolConfiguration.get(sP.getConfiguration()).xdim / 2, sP.getY() - symbolConfiguration.get(sP.getConfiguration()).ydim / 2), new Point(sP.getX() + symbolConfiguration.get(sP.getConfiguration()).xdim / 2, sP.getY() + symbolConfiguration.get(sP.getConfiguration()).ydim / 2), 2, Color.red, true);
        }
        return spotting;
    }

    protected static File exportPoints(MainData mainData, OfflineSpottingResult offSpottingResult, JFXSlider slider) throws IOException {

        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv");
        fileChooser.getExtensionFilters().add(extFilter);

        //Show save file dialog
        File file = fileChooser.showSaveDialog(Main.PrimaryStage);

        mainData.setPathLocation(file);

        Tool.writeTextFile(file.getAbsolutePath(), Tool.spottedPointsToCSV(offSpottingResult.getSpottedPoints().get(slider.getValue() > 99 ? 99 : (int) slider.getValue()), true));

        return file;

    }

    public static void pointsToXML(OfflineSpottingResult offlineSpottingResult) throws Exception {


        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("XML files (*.xml)", "*.xml");
        fileChooser.getExtensionFilters().add(extFilter);

        File file = fileChooser.showSaveDialog(Main.PrimaryStage);

        XStream xstream = new XStream();
        xstream.alias("offlineSpottingResult", OfflineSpottingResult.class);
        xstream.alias("spottedPoint", SpottedPoint.class);
        xstream.alias("byteImage", ByteImage.class);

        try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
            xstream.toXML(offlineSpottingResult, fileOutputStream);
        }

        System.out.println("XML File created");
    }

    public static OfflineSpottingResult xmlToPoints() throws Exception {

        OfflineSpottingResult offlineSpottingResult;

        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("XML files (*.xml)", "*.xml");
        fileChooser.getExtensionFilters().add(extFilter);

        File file = fileChooser.showOpenDialog(Main.PrimaryStage);

        XStream xstream = new XStream();
        xstream.alias("offlineSpottingResult", OfflineSpottingResult.class);
        xstream.alias("spottedPoint", SpottedPoint.class);
        xstream.alias("byteImage", ByteImage.class);

        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            offlineSpottingResult = (OfflineSpottingResult) xstream.fromXML(fileInputStream);
        }

        return offlineSpottingResult;
    }
}