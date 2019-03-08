package fr.loria.qgar.tesysp.controllers;

import fr.loria.qgar.tesysp.data.OfflineSpottingResult;
import fr.loria.qgar.tesysp.data.SpottedPoint;
import fr.loria.qgar.tesysp.util.Tool;
import fr.loria.qgar.tesysp.util.Tuple;
import fr.unistra.pelican.ByteImage;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.*;

public class GroundTruthController implements Initializable {

    private final int tileSize = 750;
    private int symbolOffset;

    @FXML
    public Button previousButton;

    @FXML
    public Button nextButton;

    @FXML
    public ImageView imgView;

    private Stage stage;

    private BufferedImage[][] planTiles;

    private String planPath;

    private int x;
    private int y;

    private ArrayList<Tuple<SpottedPoint, Rectangle>> spottedRectangles;
    private ArrayList<Tuple<SpottedPoint, Rectangle>> listRight = new ArrayList<>();
    private ArrayList<ArrayList<Tuple<SpottedPoint, Rectangle>>> listAllThreshold;
    private ArrayList<Tuple<SpottedPoint, Rectangle>> listL;

    private SpottedPoint point;
    private Rectangle rectangle;

    ArrayList<SpottedPoint> points = new ArrayList<>();
    private ArrayList<Tuple<SpottedPoint, Rectangle>> listRectanglesClean;

    private OfflineSpottingResult offlineSpottingResult;

    private double threshold;

    private double thresholdIdea;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    public void setData(Stage stage, String planPath, int symbolOffset, OfflineSpottingResult offlineSpottingResult, double threshold) {
        this.stage = stage;
        this.symbolOffset = symbolOffset;
        this.planPath = planPath;
        this.offlineSpottingResult = offlineSpottingResult;
        this.threshold = threshold;
        stage.setMinWidth(770.0);
        stage.setMinHeight(835.0);
        stage.setMaxWidth(775.0);
        stage.setMaxHeight(840.0);
        reloadImage(0, 0);

        getRectangles(threshold);
        listRight = spottedRectangles;
        findAllResultsThreshold();
    }

    /**
     * With the SpottedPoint of the given threshold, and with the configuration given by the symbol and the user
     * Will set all the rectangles, of the threshold, on the tile
     *
     * @param idea
     * @return ArrayList of Tuple<SpottedPoint, Rectangle>
     */
    public ArrayList<Tuple<SpottedPoint, Rectangle>> getRectangles(double idea) {

        this.thresholdIdea = idea;

        ArrayList<SpottedPoint> points = offlineSpottingResult.getSpottedPoints().get((int) thresholdIdea);

        ArrayList<ByteImage> symbolConfiguration = offlineSpottingResult.getOrientedSymbols();

        spottedRectangles = new ArrayList<>();

        for (int i = 0; i < points.size(); i++) {

            int symbolWidth = symbolConfiguration.get(points.get(i).getConfiguration()).xdim;
            int symbolHeight = symbolConfiguration.get(points.get(i).getConfiguration()).ydim;

            Rectangle rec = new Rectangle();
            rec.setX(points.get(i).getX() - symbolConfiguration.get(points.get(i).getConfiguration()).xdim / 2);
            rec.setY(points.get(i).getY() - symbolConfiguration.get(points.get(i).getConfiguration()).ydim / 2);
            rec.setWidth(symbolWidth);
            rec.setHeight(symbolHeight);

            spottedRectangles.add(new Tuple<>(points.get(i), rec));
        }

        return spottedRectangles;
    }

    /**
     * Will search and save in a list, all the possible symbols, by searching through all the thresholds.
     *
     * @return ArrayList of Tuple
     */
    private ArrayList<Tuple<SpottedPoint, Rectangle>> findAllResultsThreshold() {

        listAllThreshold = new ArrayList<>();

        for (thresholdIdea = 0; thresholdIdea < 100; thresholdIdea++) {
            getRectangles(thresholdIdea);
            listAllThreshold.add(spottedRectangles);
        }

        listL = new ArrayList<>();

        for (ArrayList<Tuple<SpottedPoint, Rectangle>> list : listAllThreshold) {

            for (Tuple<SpottedPoint, Rectangle> tuple : list) {

                point = tuple.x;
                rectangle = tuple.y;
                listL.add(tuple);
            }
        }

        return listL;
    }

    /**
     * Clean all the symbols' duplicates of the previous symbol's list
     *
     * @param listAllTuple
     * @return clean ArrayList of Tuple
     */
    private ArrayList<Tuple<SpottedPoint, Rectangle>> getAllTuples(ArrayList<Tuple<SpottedPoint, Rectangle>> listAllTuple) {

        listRectanglesClean = new ArrayList<Tuple<SpottedPoint, Rectangle>>();

        for (int i = 0; i < listAllTuple.size(); i++) {

            SpottedPoint p = listAllTuple.get(i).x;
            Rectangle r = listAllTuple.get(i).y;

            boolean exist = false;

            for (int k = i + 1; k < listAllTuple.size(); k++) {

                if (k != i && listAllTuple.get(k).y.getX() == listAllTuple.get(i).y.getX() && listAllTuple.get(k).y.getY() == listAllTuple.get(i).y.getY()) {
                    exist = true;
                }
            }
            if (!exist) {
                listRectanglesClean.add(new Tuple<>(p, r));
            }
        }
        return listRectanglesClean;
    }

    /**
     * Reload the image in the actual tile
     *
     * @param x , y
     * @return void
     */
    private void reloadImage(int x, int y) {
        try {
            planTiles = Tool.SplitImageToBufferedImages(planPath, tileSize, symbolOffset, offlineSpottingResult, threshold);
            this.x = x;
            this.y = y;
            setImage();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setImage() {
        imgView.setFitWidth(planTiles[x][y].getWidth());
        imgView.setFitHeight(planTiles[x][y].getHeight());
        imgView.setImage(SwingFXUtils.toFXImage(planTiles[x][y], null));
        stage.setTitle("Ground truth print x:" + x + " y:" + y);
    }

    public void previous(ActionEvent actionEvent) {

        x--;
        if (x < 0) {
            if (y > 0) {
                x = planTiles.length - 1;
                y--;
            } else {
                x = 0;
            }
        }
        setImage();
    }

    public void next(ActionEvent actionEvent) {
        x++;
        if (x >= planTiles.length) {
            if (y < planTiles[0].length - 1) {
                x = 0;
                y++;
            } else {
                x--;
            }
        }
        setImage();
    }

    private ContextMenu contextMenu;

    public void rightClick(MouseEvent event) {

        if (contextMenu != null) {
            contextMenu.hide();
        }
        this.threshold = threshold;
        getRectangles(threshold);
        if (event.getButton() == MouseButton.SECONDARY) {

            int xOnImage = x * tileSize + (int) event.getX() - (x * symbolOffset);
            int yOnImage = y * tileSize + (int) event.getY() - (y * symbolOffset);

            for (int i = 0; i < spottedRectangles.size(); i++) {

                final int ii = i;

                if (spottedRectangles.get(i).y.intersects(xOnImage, yOnImage, 1, 1)) {

                    contextMenu = new ContextMenu();
                    MenuItem deleteItem = new MenuItem("Delete");
                    deleteItem.setOnAction((ActionEvent e) -> {

                        offlineSpottingResult.getSpottedPoints().get((int) threshold).remove(spottedRectangles.get(ii).x);
                        listRight.remove(ii);
                        reloadImage(x, y);

                    });
                    contextMenu.getItems().add(deleteItem);

                    contextMenu.show(imgView, event.getScreenX(), event.getScreenY());

                    break;
                }
            }

        }
    }


    /**
     * Find in a ArrayList every symbol possible that intersect with the coordinates of the click.
     * Find with the center of the rectangle and the coordinates of the click, the symbol that should appear and add it to the final symbol list.
     *
     * @param event
     */

    public void leftClick(MouseEvent event) {

        listRectanglesClean = getAllTuples(listL);

        ArrayList<Tuple<SpottedPoint, Rectangle>> list = new ArrayList<>();

        if (event.getButton() == MouseButton.PRIMARY) {

            int distanceMin = tileSize;
            int distanceMinY = tileSize;
            int xOnImage = x * tileSize + (int) event.getX() - (x * symbolOffset);
            int yOnImage = y * tileSize + (int) event.getY() - (y * symbolOffset);
            Tuple<SpottedPoint, Rectangle> tupleMin = null;

            //find all possible symbols that can be found on x and y position, save them in a list
            for (int c = 0; c < listRectanglesClean.size(); c++) {

                if (listRectanglesClean.get(c).y.intersects(xOnImage, yOnImage, 1, 1)) {

                    Tuple<SpottedPoint, Rectangle> possibleTuple = listRectanglesClean.get(c);
                    if (!list.contains(listRectanglesClean.get(c))) {
                        list.add(possibleTuple);
                    }
                }
            }
            //find the right symbol by comparing the distance of the center point
            for (Tuple<SpottedPoint, Rectangle> possible : list) {

                double pointCX = possible.y.getWidth() / 2;
                double pointCY = possible.y.getHeight() / 2;
                int cdistance = Math.abs((int) pointCX - xOnImage);
                int cdistanceY = Math.abs((int) pointCY - yOnImage);

                if (cdistance < distanceMin && cdistanceY < distanceMinY) {

                    distanceMin = cdistance;
                    distanceMinY = cdistanceY;
                } else {

                    distanceMin = cdistance;
                    distanceMinY = cdistanceY;
                    tupleMin = new Tuple<>(possible.x, possible.y);
                }
            }
            if (tupleMin != null) {
                offlineSpottingResult.getSpottedPoints().get((int) threshold).add(tupleMin.x);
                listRight.add(tupleMin);
                reloadImage(x, y);
            }
        }
        list.clear();

    }
}