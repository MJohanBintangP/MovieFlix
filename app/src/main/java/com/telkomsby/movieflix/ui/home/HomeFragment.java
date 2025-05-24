package com.telkomsby.movieflix.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.telkomsby.movieflix.R;
import com.telkomsby.movieflix.api.Movie;
import com.telkomsby.movieflix.api.MovieResponse;
import com.telkomsby.movieflix.repository.TmdbRepository;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private FilmAdapter filmAdapter;
    private List<Movie> movieList;
    private TmdbRepository tmdbRepository;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = view.findViewById(R.id.recycler_view_films);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        movieList = new ArrayList<>();
        filmAdapter = new FilmAdapter(movieList, requireContext());
        recyclerView.setAdapter(filmAdapter);

        tmdbRepository = new TmdbRepository();
        loadPopularMovies();

        return view;
    }

    private void loadPopularMovies() {
        tmdbRepository.getPopularMovies(new Callback<MovieResponse>() {
            @Override
            public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("HomeFragment", "Jumlah film diterima: " + response.body().getResults().size());

                    movieList.clear();
                    movieList.addAll(response.body().getResults());
                    filmAdapter.notifyDataSetChanged();

                    if (!movieList.isEmpty()) {
                        Movie firstMovie = movieList.get(0);
                        Log.d("HomeFragment", "Film pertama: " + firstMovie.getTitle());
                        Log.d("HomeFragment", "Poster Path: " + firstMovie.getPosterUrl());
                        Log.d("HomeFragment", "Rating: " + firstMovie.getVoteAverage());
                        Log.d("HomeFragment", "Tanggal Rilis: " + firstMovie.getReleaseDate());
                    }

                } else {
                    Log.e("HomeFragment", "Response gagal: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<MovieResponse> call, Throwable t) {
                Log.e("HomeFragment", "Gagal mengambil data: " + t.getMessage());
            }
        });
    }
}