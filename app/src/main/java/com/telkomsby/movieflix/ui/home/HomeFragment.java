package com.telkomsby.movieflix.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.telkomsby.movieflix.R;
import com.telkomsby.movieflix.model.Film;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private FilmAdapter filmAdapter;
    private List<Film> filmList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = view.findViewById(R.id.recycler_view_films);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        filmList = new ArrayList<>();
        filmList.add(new Film("Avengers", "https://image.tmdb.org/t/p/w500/kc3M04QQAuZ9woUvOS3h1Dqt2Kk.jpg ", 8.4f));
        filmList.add(new Film("Inception", "https://image.tmdb.org/t/p/w500/oIkFZZrRTdwi8WAFGAZNpDCMI4Q.jpg ", 8.8f));
        filmList.add(new Film("The Dark Knight", "https://image.tmdb.org/t/p/w500/qJ2tW6WMli7F1sG0EeAY0cGkko8.jpg ", 9.0f));

        filmAdapter = new FilmAdapter(filmList);
        recyclerView.setAdapter(filmAdapter);

        return view;
    }
}