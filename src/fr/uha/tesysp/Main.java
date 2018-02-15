package fr.uha.tesysp;

import fr.uha.tesysp.controllers.MainController;
import fr.uha.tesysp.util.ResourceManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class Main extends Application {

    public static Stage PrimaryStage;

    @Override
    public void start(final Stage primaryStage) throws Exception {
        PrimaryStage = primaryStage;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("views/Main.fxml"));
        Parent root = loader.load();
        primaryStage.setTitle("Tesysp");
        primaryStage.setMinWidth(910);
        primaryStage.setMinHeight(655);
        Scene rootScene = new Scene(root);
        ResourceManager resourceManager = new ResourceManager("/fr/uha/tesysp/resources/");
        rootScene.getStylesheets().add(resourceManager.getCSS("main.css"));
        primaryStage.setScene(rootScene);
        ((MainController) loader.getController()).setStage(primaryStage);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
