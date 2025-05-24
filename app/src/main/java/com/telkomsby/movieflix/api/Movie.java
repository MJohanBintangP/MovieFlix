package com.telkomsby.movieflix.api;

import android.util.Log;

import com.google.gson.annotations.SerializedName;

public class Movie {

    @SerializedName("title")
    private String title;

    @SerializedName("poster_path")
    private String posterPath;

    @SerializedName("vote_average")
    private float voteAverage;

    @SerializedName("release_date")
    private String releaseDate;

    // Getter
    public String getTitle() {
        return title;
    }

    public String getPosterUrl() {
        if (posterPath == null || posterPath.isEmpty()) {
            return null;
        }

        String cleanPath = posterPath.trim();

        String url = "https://image.tmdb.org/t/p/w500" + cleanPath;

        Log.d("Movie", "Generated Poster URL: " + url);

        return url;
    }

    public float getVoteAverage() {
        return voteAverage;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    // setter
    public void setTitle(String title) {
        this.title = title;
    }

    public void setPosterUrl(String posterPath) {
        this.posterPath = posterPath;
    }

    public void setVoteAverage(float voteAverage) {
        this.voteAverage = voteAverage;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }
}