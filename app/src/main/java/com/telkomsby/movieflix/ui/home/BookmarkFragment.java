package com.telkomsby.movieflix.ui.home;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.telkomsby.movieflix.R;
import com.telkomsby.movieflix.api.Movie;

import java.util.ArrayList;
import java.util.List;

public class BookmarkFragment extends Fragment {

    private RecyclerView recyclerView;
    private FilmAdapter filmAdapter;
    private List<Movie> bookmarkedMovies;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bookmark, container, false);

        recyclerView = view.findViewById(R.id.recycler_view_bookmarks);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        bookmarkedMovies = new ArrayList<>();
        filmAdapter = new FilmAdapter(bookmarkedMovies, requireContext());
        recyclerView.setAdapter(filmAdapter);

        loadBookmarks();

        return view;
    }

    private void loadBookmarks() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(user.getUid()).collection("bookmarks")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            Movie movie = new Movie();
                            movie.setTitle((String) doc.get("title"));
                            movie.setPosterUrl((String) doc.get("posterUrl"));
                            movie.setReleaseDate((String) doc.get("releaseDate"));
                            movie.setVoteAverage(((Number) doc.get("voteAverage")).floatValue());
                            bookmarkedMovies.add(movie);
                        }
                        filmAdapter.notifyDataSetChanged();
                    }
                });
    }
}