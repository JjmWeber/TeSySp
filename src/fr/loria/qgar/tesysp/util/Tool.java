package fr.loria.qgar.tesysp.util;

import fr.loria.qgar.tesysp.data.MainData;
import fr.loria.qgar.tesysp.data.OfflineSpottingResult;
import fr.loria.qgar.tesysp.data.SpottedPoint;
import fr.unistra.pelican.ByteImage;
import javafx.scene.control.Alert;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

public class Tool {


    public static void dialogMessage(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle("Information Dialog");
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static String getHMSFromMS(long timeMillis) {
        timeMillis = timeMillis / 1000;
        long h = (timeMillis - (timeMillis % 3600)) / 3600;
        timeMillis = timeMillis % 3600;
        long m = (timeMillis - (timeMillis % 60)) / 60;
        timeMillis = timeMillis % 60;
        long s = timeMillis;
        return h + "h " + m + "m " + s + "s";
    }

    public static String spottedPointsToCSV(ArrayList<SpottedPoint> sPoints, boolean header) {
        StringBuilder csv = new StringBuilder();
        if (header)
            csv.append("x;y;orientation;symmetry\n");
        for (SpottedPoint sp : sPoints) {
            csv.append(sp.getX()).append(";").append(sp.getY()).append(";").append(sp.getOrientation()).append(";")
                    .append(sp.isSymmetry()).append(System.getProperty("line.separator"));
        }
        return csv.toString();
    }

    public static void writeTextFile(String location, String content) {
        try {
            FileWriter fr = null;
            fr = new FileWriter(location);
            fr.write(content);
            fr.close();
        } catch (HeadlessException | IOException ignored) {
        }
    }

    public static String offlineSpottingLog(MainData mainData) {
        String log = "";
        Date d = new Date(mainData.getOfflineSpottingResult().getStartTime());
        log += "Launched " + d + "\n\n";


        log += "Document: " + mainData.getDocumentPath() + "\n";
        log += "Symbol: " + mainData.getSymbolPath() + "\n\n";

        log += "Spotting time: " + mainData.getOfflineSpottingResult().getSpottingTime() + "ms\n";
        log += "Filtering time: " + mainData.getOfflineSpottingResult().getFilteringTime() + "ms\n\n";

        log += "Alpha: " + mainData.getSettings().getAlpha() + "\n";
        log += "Beta: " + mainData.getSettings().getBeta() + "\n";
        log += "nbThreads: " + mainData.getSettings().getNbThreads() + "\n";
        log += "FG/BGThresh: " + mainData.getSettings().getFGBGThresh() + "\n";
        log += "OrientationStep: " + mainData.getSettings().getAngularStep() + "\n";
        log += "Symmetry: " + mainData.getSettings().isSymmetry() + "\n";
        log += "FGOnly: " + mainData.getSettings().isOnlyFG() + "\n";
        log += "BGOnly: " + mainData.getSettings().isOnlyBG() + "\n\n";

        log += offlineSpottingCSV(mainData, true);
        return log;
    }

    public static String offlineSpottingCSV(MainData mainData, boolean header) {
        StringBuilder csv = new StringBuilder();
        if (header)
            csv.append("thresh;x;y;orientation;symmetry\n");

        for (int i = 0; i < mainData.getOfflineSpottingResult().getSpottedPoints().size(); i++) {
            ArrayList<SpottedPoint> aSP = mainData.getOfflineSpottingResult().getSpottedPoints().get(i);
            for (SpottedPoint sp : aSP) {
                csv.append(i + 1).append(";").append(sp.getX()).append(";").append(sp.getY()).append(";").append(mainData.getOfflineSpottingResult()
                        .getSymbolOrientationConfiguration().get(sp.getConfiguration())).append(";").append(mainData.getOfflineSpottingResult().getSymbolSymmetryConfiguration().get(sp.getConfiguration())).append("\n");
            }
        }
        return csv.toString();
    }

    public static String selectedPointsToCSV(ArrayList<ArrayList<Point>> tsPoints) {
        String csv = "tile number;x;y" + System.getProperty("line.separator");
        for (int i = 0; i < tsPoints.size(); i++) {
            ArrayList<Point> sPoints = tsPoints.get(i);
            for (Point p : sPoints) {
                csv += i + ";" + p.x + ";" + p.y + System.getProperty("line.separator");

            }
        }

        return csv;
    }

    private static BufferedImage convert(BufferedImage src, int bufImgType) {
        BufferedImage img = new BufferedImage(src.getWidth(), src.getHeight(), bufImgType);
        Graphics2D g2d = img.createGraphics();
        g2d.drawImage(src, 0, 0, null);
        g2d.dispose();
        return img;
    }

    public static BufferedImage[][] SplitImageToBufferedImages(String imagePath, int size, int symbolOffset, OfflineSpottingResult offlineSpottingResult, double sliderValue) throws IOException {

        BufferedImage originalImage = convert(ImageIO.read(new File(imagePath)), BufferedImage.TYPE_INT_RGB);

        Graphics2D graphics2D = originalImage.createGraphics();

        ArrayList<SpottedPoint> currentSpotting = offlineSpottingResult.getSpottedPoints().get(sliderValue > 99 ? 99 : (int) sliderValue);
        ArrayList<ByteImage> symbolConfiguration = offlineSpottingResult.getOrientedSymbols();
        for (SpottedPoint sP : currentSpotting) {
            graphics2D.setStroke(new BasicStroke(2));
            graphics2D.setColor(Color.red);
            graphics2D.drawRect(sP.getX() - symbolConfiguration.get(sP.getConfiguration()).xdim / 2, sP.getY() - symbolConfiguration.get(sP.getConfiguration()).ydim / 2,
                    symbolConfiguration.get(sP.getConfiguration()).xdim, symbolConfiguration.get(sP.getConfiguration()).ydim);
        }


        size = size - symbolOffset;

        int totalGridX = (int) Math.ceil((double) originalImage.getWidth() / size);
        int totalGridY = (int) Math.ceil((double) originalImage.getHeight() / size);

        BufferedImage[][] result = new BufferedImage[totalGridX][totalGridY];

        for (int x = 0; x < totalGridX; x++) {
            for (int y = 0; y < totalGridY; y++) {

                int width = (x + 1) * size + symbolOffset > originalImage.getWidth()
                        ? originalImage.getWidth() - x * size
                        : size + symbolOffset;
                int height = (y + 1) * size + symbolOffset > originalImage.getHeight()
                        ? originalImage.getHeight() - y * size
                        : size + symbolOffset;

                result[x][y] = originalImage.getSubimage(x * size, y * size, width, height);
            }
        }

        return result;
    }

}
