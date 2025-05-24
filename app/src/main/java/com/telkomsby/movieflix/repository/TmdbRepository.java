package com.telkomsby.movieflix.repository;

import com.telkomsby.movieflix.api.Movie;
import com.telkomsby.movieflix.api.MovieResponse;
import com.telkomsby.movieflix.api.TmdbApi;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TmdbRepository {

    private static final String BASE_URL = "https://api.themoviedb.org/3/ ";
    private static final String API_KEY = "84cd59f8f5c840f0e713d23bc509e91e";

    private final TmdbApi tmdbApi;

    public TmdbRepository() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        tmdbApi = retrofit.create(TmdbApi.class);
    }

    public void getPopularMovies(Callback<MovieResponse> callback) {
        tmdbApi.getPopularMovies(API_KEY).enqueue(callback);
    }

    // 🔍 Tambahkan method ini
    public void searchMovies(String query, Callback<MovieResponse> callback) {
        tmdbApi.searchMovies(API_KEY, query).enqueue(callback);
    }
}