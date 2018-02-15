package fr.uha.tesysp.engine;

import fr.uha.tesysp.controllers.GroundTruthController;
import fr.uha.tesysp.data.MainData;
import fr.unistra.pelican.ByteImage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class GroundTruthEngine {

    public void launchGroundTruth(MainData mainData, double thresholdValue) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../views/GroundTruth.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Ground truth");
            stage.setScene(new Scene(root));

            GroundTruthController controller = loader.getController();

            ByteImage symbol = mainData.getSymbol();

            controller.setData(stage, mainData.getDocumentPath(), symbol.getXDim() > symbol.getYDim() ? symbol.getXDim() : symbol.getYDim(),
                    mainData.getOfflineSpottingResult(), thresholdValue);

            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
