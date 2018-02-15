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
import java.util.ArrayList;
import java.util.ResourceBundle;

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

    private OfflineSpottingResult offlineSpottingResult;

    private double threshold;

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


        ArrayList<SpottedPoint> points = offlineSpottingResult.getSpottedPoints().get((int) threshold);

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

    }

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
                        spottedRectangles.remove(ii);
                        reloadImage(x, y);

                    });
                    contextMenu.getItems().add(deleteItem);

                    contextMenu.show(imgView, event.getScreenX(), event.getScreenY());

                    break;
                }
            }


        }


    }
}
