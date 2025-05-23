package com.telkomsby.movieflix.model;

public class Film {
    private String title;
    private String posterUrl;
    private float rating;

    public Film(String title, String posterUrl, float rating) {
        this.title = title;
        this.posterUrl = posterUrl;
        this.rating = rating;
    }

    public String getTitle() {
        return title;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public float getRating() {
        return rating;
    }
}
