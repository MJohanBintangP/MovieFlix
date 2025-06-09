package com.telkomsby.movieflix.repository;

import com.telkomsby.movieflix.api.MovieResponse;
import com.telkomsby.movieflix.api.TmdbApi;
import com.telkomsby.movieflix.model.TrailerResponse;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TmdbRepository {

    private static final String BASE_URL = "https://api.themoviedb.org/3/";
    private static final String V4_TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiI4NGNkNTlmOGY1Yzg0MGYwZTcxM2QyM2JjNTA5ZTkxZSIsIm5iZiI6MTc0ODExOTU0Ny41OTMsInN1YiI6IjY4MzIyZmZiZjAyM2FhNWNmZmViMmFiOCIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.iqhtYaCjgJz-d8x7rdy759xTrWkoHpegvTQ2qvYIBoY";

    private final TmdbApi tmdbApi;

    public TmdbRepository() {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    Request newRequest = chain.request().newBuilder()
                            .addHeader("Authorization", "Bearer " + V4_TOKEN)
                            .addHeader("Accept", "application/json")
                            .build();
                    return chain.proceed(newRequest);
                })
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        tmdbApi = retrofit.create(TmdbApi.class);
    }

    public void searchMovies(String query, Callback<MovieResponse> callback) {
        tmdbApi.searchMovies(query).enqueue(callback);
    }

    public void getMoviesByGenre(String genreId, Callback<MovieResponse> callback) {
        tmdbApi.getMoviesByGenre(genreId).enqueue(callback);
    }

    public void getMovieTrailers(int movieId, Callback<TrailerResponse> callback) {
        tmdbApi.getMovieTrailers(movieId).enqueue(callback);
    }
}