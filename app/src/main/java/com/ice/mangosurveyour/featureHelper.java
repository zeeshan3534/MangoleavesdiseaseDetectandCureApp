package com.ice.mangosurveyour;

public class featureHelper {

    int image;
    String title,description;

    public featureHelper(int image, String title, String description) {
        this.image = image;
        this.title = title;
        this.description = description;
    }

    public int getImage() {
        return image;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }


}