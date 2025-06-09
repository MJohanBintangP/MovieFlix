package com.telkomsby.movieflix.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.telkomsby.movieflix.R;
import com.telkomsby.movieflix.api.MovieResponse;
import com.telkomsby.movieflix.repository.TmdbRepository;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerViewAdventure, recyclerViewHorror;
    private FilmAdapter adventureAdapter, horrorAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerViewAdventure = view.findViewById(R.id.recycler_view_adventure);
        recyclerViewAdventure.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        adventureAdapter = new FilmAdapter(new ArrayList<>(), requireContext(), requireActivity());
        recyclerViewAdventure.setAdapter(adventureAdapter);
        recyclerViewHorror = view.findViewById(R.id.recycler_view_horror);
        recyclerViewHorror.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        horrorAdapter = new FilmAdapter(new ArrayList<>(), requireContext(), requireActivity());
        recyclerViewHorror.setAdapter(horrorAdapter);

        TmdbRepository repository = new TmdbRepository();

        repository.getMoviesByGenre("12", new Callback<MovieResponse>() {
            @Override
            public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    adventureAdapter.updateMovies(response.body().getResults());
                }
            }

            @Override
            public void onFailure(Call<MovieResponse> call, Throwable t) {
                Toast.makeText(requireContext(), "Gagal memuat film Adventure", Toast.LENGTH_SHORT).show();
            }
        });

        repository.getMoviesByGenre("27", new Callback<MovieResponse>() {
            @Override
            public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    horrorAdapter.updateMovies(response.body().getResults());
                }
            }

            @Override
            public void onFailure(Call<MovieResponse> call, Throwable t) {
                Toast.makeText(requireContext(), "Gagal memuat film Horror", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}