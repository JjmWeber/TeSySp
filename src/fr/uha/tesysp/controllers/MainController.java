package fr.uha.tesysp.controllers;

import com.jfoenix.controls.*;
import fr.uha.tesysp.Main;
import fr.uha.tesysp.data.SettingsData;
import fr.uha.tesysp.engine.GroundTruthEngine;
import fr.uha.tesysp.engine.MainEngineGraphicalMode;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML
    public Button buttonGroundTruth;

    @FXML
    public AnchorPane rootPane;

    @FXML
    private SplitPane splitPane;

    @FXML
    private JFXButton buttonImportPlan;

    @FXML
    public JFXButton buttonImportSymbol;

    @FXML
    public JFXListView listSymbols;

    @FXML
    public JFXButton buttonCharge;

    @FXML
    public JFXButton plusImage;

    @FXML
    public JFXButton minusImage;

    @FXML
    public JFXProgressBar progressBar;

    @FXML
    public AnchorPane anchorImg;

    @FXML
    public GridPane gridPaneImg;

    @FXML
    public ImageView imgView;

    @FXML
    public ScrollPane scrollPane;

    @FXML
    public JFXSlider sliderThreshold;

    @FXML
    public Label labelThreshold;

    @FXML
    public Button buttonThreshold;

    @FXML
    public Label labelPlan;

    @FXML
    public Accordion accordion;

    @FXML
    public TitledPane menuAccordion;

    @FXML
    public MenuItem buttonNew;

    @FXML
    public MenuItem buttonDoc;

    @FXML
    public MenuItem buttonAbout;

    @FXML
    public TitledPane optionAccordion;

    @FXML
    public Label labelRemainingTime;

    @FXML
    public Label labelSymbols;

    @FXML
    public JFXTextField fgBgThresholdTextField;

    @FXML
    public JFXCheckBox FTGRACheckBox;

    @FXML
    public JFXTextField alphaTextField;

    @FXML
    public JFXTextField betaTextField;

    @FXML
    public RadioButton FGRadioButton;

    @FXML
    public RadioButton BGRadioButton;

    @FXML
    public JFXTextField AngulartextField;

    @FXML
    public JFXCheckBox symmetryCheckBox;

    @FXML
    public JFXTextField threadsTextField;

    private MainEngineGraphicalMode mainEngineGraphicalMode;

    private GroundTruthEngine groundTruthEngine;

    private SettingsData settingsData;

    private Stage primaryStage;

    //needed variable for image View action
    private double width;
    private double height;
    private static final int MIN_PIXELS = 10;
    private ObjectProperty<Point2D> mouseDown = new SimpleObjectProperty<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        accordion.setExpandedPane(menuAccordion);
        mainEngineGraphicalMode = new MainEngineGraphicalMode();
        groundTruthEngine = new GroundTruthEngine();
        settingsData = new SettingsData();
        getParameterAnalysis();
        mainEngineGraphicalMode.setControls(this);
        sliderThreshold.valueProperty().addListener((ov, old_val, new_val) -> {
            labelThreshold.setText(Integer.toString(new_val.intValue()));
            mainEngineGraphicalMode.launchOnlineSpotting();
        });

    }

    public void setStage(Stage stage) {
        primaryStage = stage;
    }

    private void getParameterAnalysis() {
        FTGRACheckBox.setSelected(settingsData.isAutoFindResult());
        fgBgThresholdTextField.setText(Integer.toString(settingsData.getFGBGThresh()));
        alphaTextField.setText(String.valueOf(settingsData.getAlpha()));
        betaTextField.setText(String.valueOf(settingsData.getBeta()));
        AngulartextField.setText(String.valueOf(settingsData.getAngularStep()));
        symmetryCheckBox.setSelected(settingsData.isSymmetry());
        threadsTextField.setText(Integer.toString(settingsData.getNbThreads()));
    }

    private void setParameterAnalysis() {
        settingsData.setAutoFindResult(FTGRACheckBox.isSelected());
        settingsData.setFGBGThresh(Integer.parseInt(fgBgThresholdTextField.getText()));
        settingsData.setAlpha(Double.parseDouble(alphaTextField.getText()));
        settingsData.setBeta(Double.parseDouble(betaTextField.getText()));
        if (FGRadioButton.isSelected()) {
            settingsData.setOnlyFG(true);
            settingsData.setOnlyBG(false);
        } else if (BGRadioButton.isSelected()) {
            settingsData.setOnlyBG(true);
            settingsData.setOnlyFG(false);
        }
        settingsData.setAngularStep(Double.parseDouble(AngulartextField.getText()));
        settingsData.setSymmetry(symmetryCheckBox.isSelected());
        settingsData.setNbThreads(Integer.parseInt(threadsTextField.getText()));
    }

    @FXML
    private void ImportPlan() {
        mainEngineGraphicalMode.loadPlan();
        //set some parameter for more smooth beginning in imageView action
        width = imgView.getImage().getWidth();
        height = imgView.getImage().getHeight();
        imgView.setPreserveRatio(true);
        reset(imgView, width, height);
        imgView.fitWidthProperty().bind(anchorImg.widthProperty());
        imgView.fitHeightProperty().bind(anchorImg.heightProperty());
    }

    @FXML
    private void ImportSymbol() {
        mainEngineGraphicalMode.loadSymbol();
    }

    @FXML
    private void ExecuteOffline() {
        setParameterAnalysis();
        mainEngineGraphicalMode.launchOfflineSpotting(accordion, optionAccordion);

    }

    @FXML
    private void plusImage() {
        reset(imgView, width * 1.5, height * 1.5);
    }

    @FXML
    private void minisImage() {
        reset(imgView, width / 1.5, height / 1.5);
    }

    @FXML
    private void onlineMapping() {
        mainEngineGraphicalMode.launchOnlineSpotting();
    }

    @FXML
    private void newProcess() {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Dialog");
        alert.setHeaderText("Confirmation close the project without save and open a new one");
        alert.setContentText("Are you ok with this ?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            primaryStage.close();
            Platform.runLater(() -> {
                try {
                    new Main().start(new Stage());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } else {
            // ... user chose CANCEL or closed the dialog
        }
    }

    public boolean alertXmlPlan(String documentPath, String symbolPath) {
        boolean x;
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Charge Plan and Symbol");
        alert.setHeaderText("We go try to charge the plan and the symbol in this location, Are you ok with this ? (Cancel for launch the fileChooser)");
        alert.setContentText("This is the path for the plan : " + documentPath + " and the symbol : " + symbolPath);
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            x = true;
        } else {
            // ... user chose CANCEL or closed the dialog
            x = false;
        }
        return x;
    }

    public void alertNoPlanNoSymbol() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Charge Plan and Symbol");
        alert.setHeaderText("ERROR");
        alert.setContentText("You don't select any plan or symbol !! End of procedure.");
        Optional<ButtonType> result = alert.showAndWait();
    }

    @FXML
    private void aboutProcess() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.initStyle(StageStyle.UTILITY);
        alert.setTitle("About");
        alert.setHeaderText("Projet : Tesysp");
        alert.setContentText("Créateur: Jonathan WEBER\n" +
                "Développeurs: CARETTE Antoine\n" +
                "                        FREY Alvin\n" +
                "                        FRITZ Valentin\n" +
                "                        DEVERRE Loïc\n" +
                "                        TAHON Valentin\n");


        alert.showAndWait();
    }

    @FXML
    private void docProcess() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information Dialog");
        alert.setHeaderText("Look, an Information Dialog");
        alert.setContentText("I have a great message for you!");
        alert.showAndWait();
    }

    @FXML
    private void exportToCSV() throws Exception {
        mainEngineGraphicalMode.exportPoints();
    }

    @FXML
    private void exportToXML() throws Exception {
        mainEngineGraphicalMode.exportToXML();
    }

    @FXML
    private void saveAllToXML() throws Exception {
        mainEngineGraphicalMode.saveAllToXML();
    }

    @FXML
    private void chargeXml() throws Exception {
        mainEngineGraphicalMode.chargeXml();
        width = imgView.getImage().getWidth();
        height = imgView.getImage().getHeight();
        imgView.setPreserveRatio(true);
        reset(imgView, width, height);
        imgView.fitWidthProperty().bind(anchorImg.widthProperty());
        imgView.fitHeightProperty().bind(anchorImg.heightProperty());
        accordion.setExpandedPane(optionAccordion);
    }

    @FXML
    private void groundTruth(ActionEvent actionEvent) {
        groundTruthEngine.launchGroundTruth(mainEngineGraphicalMode.mainData, sliderThreshold.getValue());
    }
    //Action on Image View : ScrollZoom , use mouse dragged action for move in img

    @FXML
    private void imgViewPressedMouse(MouseEvent me) {
        Point2D mousePress = imageViewToImage(imgView, new Point2D(me.getX(), me.getY()));
        mouseDown.set(mousePress);
    }

    @FXML
    private void imgViewDraggedMouse(MouseEvent me) {
        Point2D dragPoint = imageViewToImage(imgView, new Point2D(me.getX(), me.getY()));
        shift(imgView, dragPoint.subtract(mouseDown.get()));
        mouseDown.set(imageViewToImage(imgView, new Point2D(me.getX(), me.getY())));
    }

    @FXML
    private void imgViewOnScroll(ScrollEvent me) {
        double delta = -me.getDeltaY();
        Rectangle2D viewport = imgView.getViewport();

        double scale = clamp(Math.pow(1.005, delta),

                // don't scale so we're zoomed in to fewer than MIN_PIXELS in any direction:
                Math.min(MIN_PIXELS / viewport.getWidth(), MIN_PIXELS / viewport.getHeight()),

                // don't scale so that we're bigger than image dimensions:
                Math.max(width / viewport.getWidth(), height / viewport.getHeight())

        );

        if (scale != 1.0) {
            Point2D mouse = imageViewToImage(imgView, new Point2D(me.getX(), me.getY()));

            double newWidth = viewport.getWidth();
            double newHeight = viewport.getHeight();
            double imageViewRatio = (imgView.getFitWidth() / imgView.getFitHeight());
            double viewportRatio = (newWidth / newHeight);
            if (viewportRatio < imageViewRatio) {
                // adjust width to be proportional with height
                newHeight = newHeight * scale;
                newWidth = newHeight * imageViewRatio;
                if (newWidth > width) {
                    newWidth = width;
                }
            } else {
                // adjust height to be proportional with width
                newWidth = newWidth * scale;
                newHeight = newWidth / imageViewRatio;
                if (newHeight > height) {
                    newHeight = height;
                }
            }

            // To keep the visual point under the mouse from moving, we need
            // (x - newViewportMinX) / (x - currentViewportMinX) = scale
            // where x is the mouse X coordinate in the image
            // solving this for newViewportMinX gives
            // newViewportMinX = x - (x - currentViewportMinX) * scale
            // we then clamp this value so the image never scrolls out
            // of the imageview:
            double newMinX = 0;
            if (newWidth < width) {
                newMinX = clamp(mouse.getX() - (mouse.getX() - viewport.getMinX()) * scale,
                        0, width - newWidth);
            }
            double newMinY = 0;
            if (newHeight < height) {
                newMinY = clamp(mouse.getY() - (mouse.getY() - viewport.getMinY()) * scale,
                        0, height - newHeight);
            }

            imgView.setViewport(new Rectangle2D(newMinX, newMinY, newWidth, newHeight));
        }
    }

    @FXML
    private void imgViewClickedMouse(MouseEvent me) {
        if (me.getClickCount() == 2) {
            reset(imgView, width, height);
        }
    }

    private Point2D imageViewToImage(ImageView imageView, Point2D imageViewCoordinates) {
        double xProportion = imageViewCoordinates.getX() / imageView.getBoundsInLocal().getWidth();
        double yProportion = imageViewCoordinates.getY() / imageView.getBoundsInLocal().getHeight();

        Rectangle2D viewport = imageView.getViewport();
        return new Point2D(
                viewport.getMinX() + xProportion * viewport.getWidth(),
                viewport.getMinY() + yProportion * viewport.getHeight());
    }

    private void shift(ImageView imageView, Point2D delta) {
        Rectangle2D viewport = imageView.getViewport();

        double width = imageView.getImage().getWidth();
        double height = imageView.getImage().getHeight();

        double maxX = width - viewport.getWidth();
        double maxY = height - viewport.getHeight();

        double minX = clamp(viewport.getMinX() - delta.getX(), 0, maxX);
        double minY = clamp(viewport.getMinY() - delta.getY(), 0, maxY);

        if (minX < 0) {
            minX = 0;
        }
        if (minY < 0) {
            minY = 0;
        }

        imageView.setViewport(new Rectangle2D(minX, minY, viewport.getWidth(), viewport.getHeight()));
    }

    private double clamp(double value, double min, double max) {

        if (value < min)
            return min;
        if (value > max)
            return max;
        return value;
    }

    private void reset(ImageView imageView, double width, double height) {
        imageView.setViewport(new Rectangle2D(0, 0, width, height));
    }
}
