package com.telkomsby.movieflix.model;

import com.telkomsby.movieflix.api.Movie;

import java.util.List;

public class TmdbResponse {
    private List<Movie> results;

    public List<Movie> getResults() {
        return results;
    }

    public void setResults(List<Movie> results) {
        this.results = results;
    }
}