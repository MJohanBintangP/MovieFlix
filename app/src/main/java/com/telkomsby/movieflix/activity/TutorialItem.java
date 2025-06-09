package com.telkomsby.movieflix.activity;

public class TutorialItem {
    private final String title;
    private final String description;
    private final int imageResId;

    public TutorialItem(String title, String description, int imageResId) {
        this.title = title;
        this.description = description;
        this.imageResId = imageResId;
    }

    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public int getImageResId() { return imageResId; }
}
