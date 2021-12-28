package com.example.newsapp;

import java.io.Serializable;
import java.net.URL;

public class NewsUtils implements Serializable {

    private String title;
    private String description;
    private String imgURL;
    public String URL;



    public NewsUtils(String title , String description, String Image_URL , String HeadLinesURL){
        this.title = title;
        this.description = description;
        imgURL = Image_URL;
        URL = HeadLinesURL;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }


    public String getURL() {
        return URL;
    }

    public String getImgURL() {
        return imgURL;
    }

}
