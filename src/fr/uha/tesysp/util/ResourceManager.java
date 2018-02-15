package fr.uha.tesysp.util;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.HashMap;

public class ResourceManager {

    private String resourcesPath;
    private HashMap<String, Object> resources;

    public ResourceManager(String resourcesPath) {
        this.resourcesPath = resourcesPath;
        resources = new HashMap<>();
    }

    public ImageView getImage(String imagePath) {
        if (resources.containsKey(imagePath) && resources.get(imagePath) instanceof Image) {
            return new ImageView((Image) resources.get(imagePath));
        } else {
            resources.put(imagePath, new Image(getClass().getResourceAsStream(resourcesPath + "images/" + imagePath)));
            return getImage(imagePath);
        }
    }

    public String getCSS(String cssPath) {
        if (resources.containsKey(cssPath) && resources.get(cssPath) instanceof String) {
            return (String) resources.get(cssPath);
        } else {
            resources.put(cssPath, getClass().getResource(resourcesPath + "styles/" + cssPath).toExternalForm());
            return getCSS(cssPath);
        }
    }


}
