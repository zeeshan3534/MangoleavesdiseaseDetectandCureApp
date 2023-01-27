package com.ice.mangoddetector;

import android.graphics.Bitmap;

public class ReportGeneratorHelper {


    String textview1,textview2,textview3;
    Bitmap img;

    public ReportGeneratorHelper(String textview1, String textview2, String textview3, Bitmap img) {
        this.textview1 = textview1;
        this.textview2 = textview2;
        this.textview3 = textview3;
        this.img = img;

    }

    public String getTextview1() {
        return textview1;
    }

    public String getTextview2() {
        return textview2;
    }

    public String getTextview3() {
        return textview3;
    }

    public Bitmap getImg() {
        return img;
    }
}
