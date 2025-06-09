package com.telkomsby.movieflix.ui.home;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import com.telkomsby.movieflix.R;
import com.telkomsby.movieflix.api.Movie;

import java.util.ArrayList;
import java.util.List;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class BookmarkFragment extends Fragment {

    private RecyclerView recyclerView;
    private FilmAdapter filmAdapter;
    private List<Movie> bookmarkedMovies;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayout emptyView;
    private ImageView emptyIllustration;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bookmark, container, false);

        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        recyclerView = view.findViewById(R.id.recycler_view_bookmarks);
        emptyView = view.findViewById(R.id.empty_view);
        emptyIllustration = view.findViewById(R.id.iv_empty_illustration);

        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        bookmarkedMovies = new ArrayList<>();
        filmAdapter = new FilmAdapter(bookmarkedMovies, requireContext(), requireActivity());
        recyclerView.setAdapter(filmAdapter);

        swipeRefreshLayout.setOnRefreshListener(() -> {
            loadBookmarks();
        });

        showEmptyView(false);

        loadBookmarks();

        return view;
    }

    private void loadBookmarks() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(requireContext(), "Silakan login dulu", Toast.LENGTH_SHORT).show();
            swipeRefreshLayout.setRefreshing(false);
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users")
                .document(user.getUid())
                .collection("bookmarks")
                .get()
                .addOnCompleteListener(task -> {
                    swipeRefreshLayout.setRefreshing(false);
                    if (task.isSuccessful()) {
                        bookmarkedMovies.clear();
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            Movie movie = new Movie();
                            movie.setTitle((String) doc.get("title"));
                            movie.setPosterUrl((String) doc.get("posterUrl"));
                            movie.setReleaseDate((String) doc.get("releaseDate"));
                            movie.setVoteAverage(((Number) doc.get("voteAverage")).floatValue());
                            bookmarkedMovies.add(movie);
                        }

                        filmAdapter.notifyDataSetChanged();
                        showEmptyView(bookmarkedMovies.isEmpty());
                    } else {
                        Toast.makeText(requireContext(), "Gagal memuat bookmark", Toast.LENGTH_SHORT).show();
                    }
                });
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