package com.telkomsby.movieflix.api;

import com.telkomsby.movieflix.model.TrailerResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface TmdbApi {

    @GET("search/movie")
    Call<MovieResponse> searchMovies(@Query("query") String query);

    @GET("movie/{movie_id}/videos")
    Call<TrailerResponse> getMovieTrailers(@Path("movie_id") int movieId);

    @GET("discover/movie")
    Call<MovieResponse> getMoviesByGenre(@Query("with_genres") String genreId);
}