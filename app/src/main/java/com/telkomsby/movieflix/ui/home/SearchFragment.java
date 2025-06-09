package com.telkomsby.movieflix.ui.home;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.telkomsby.movieflix.R;
import com.telkomsby.movieflix.api.Movie;
import com.telkomsby.movieflix.api.MovieResponse;
import com.telkomsby.movieflix.repository.TmdbRepository;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchFragment extends Fragment {

    private EditText searchInput;
    private RecyclerView recyclerView;
    private FilmAdapter filmAdapter;
    private List<Movie> movieList;
    private LinearLayout emptyView;
    private ImageView emptyIllustration;

    private TmdbRepository tmdbRepository;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        searchInput = view.findViewById(R.id.search_input);
        recyclerView = view.findViewById(R.id.recycler_view_search);
        emptyView = view.findViewById(R.id.empty_view);
        emptyIllustration = view.findViewById(R.id.iv_empty_illustration);

        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        movieList = new ArrayList<>();
        filmAdapter = new FilmAdapter(movieList, requireContext(), requireActivity());
        recyclerView.setAdapter(filmAdapter);

        tmdbRepository = new TmdbRepository();

        showEmptyView(true);

        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 2) {
                    tmdbRepository.searchMovies(s.toString(), new Callback<MovieResponse>() {
                        @Override
                        public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                movieList.clear();
                                movieList.addAll(response.body().getResults());
                                filmAdapter.notifyDataSetChanged();
                                showEmptyView(movieList.isEmpty());
                            }
                        }

                        @Override
                        public void onFailure(Call<MovieResponse> call, Throwable t) {
                            Log.e("SearchFragment", "Gagal mencari film", t);
                        }
                    });
                } else {
                    movieList.clear();
                    filmAdapter.notifyDataSetChanged();
                    showEmptyView(true);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void afterTextChanged(Editable s) {}
        });

        return view;
    }

    private void showEmptyView(boolean isEmpty) {
        if (isEmpty) {
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }
    }
}