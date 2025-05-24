package com.telkomsby.movieflix.ui.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.telkomsby.movieflix.R;
import com.telkomsby.movieflix.api.Movie;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FilmAdapter extends RecyclerView.Adapter<FilmAdapter.FilmViewHolder> {

    private final List<Movie> movies;
    private final Context context;

    // ✅ Konstruktor sekarang butuh context
    public FilmAdapter(List<Movie> movies, Context context) {
        this.movies = movies;
        this.context = context;
    }

    @NonNull
    @Override
    public FilmViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_film, parent, false);
        return new FilmViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FilmViewHolder holder, int position) {
        Movie movie = movies.get(position);
        holder.bind(movie, context); // Kirim konteks ke bind
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    static class FilmViewHolder extends RecyclerView.ViewHolder {
        private final ImageView posterImageView;
        private final TextView titleTextView;
        private final TextView ratingTextView;
        private final TextView releaseDateTextView;
        private final ImageView bookmarkButton;

        public FilmViewHolder(@NonNull View itemView) {
            super(itemView);
            posterImageView = itemView.findViewById(R.id.poster_image);
            titleTextView = itemView.findViewById(R.id.title_text);
            ratingTextView = itemView.findViewById(R.id.rating_text);
            releaseDateTextView = itemView.findViewById(R.id.release_date_text);
            bookmarkButton = itemView.findViewById(R.id.bookmark_button);
        }

        public void bind(Movie movie, Context context) {
            titleTextView.setText(movie.getTitle());
            ratingTextView.setText(String.valueOf(movie.getVoteAverage()));
            releaseDateTextView.setText(movie.getReleaseDate());

            String posterUrl = movie.getPosterUrl();

            // ✅ Tambahkan Glide untuk load gambar
            if (posterUrl != null && !posterUrl.contains("null")) {
                Glide.with(context)
                        .load(posterUrl)
                        .placeholder(R.drawable.placeholder_poster)
                        .error(R.drawable.error_image)
                        .into(posterImageView);
            } else {
                Glide.with(context)
                        .load(R.drawable.placeholder_poster)
                        .into(posterImageView);
            }

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            if (user != null) {
                DocumentReference ref = db.collection("users")
                        .document(user.getUid())
                        .collection("bookmarks")
                        .document(movie.getTitle().replaceAll("[^a-zA-Z0-9]", ""));

                ref.get().addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        bookmarkButton.setImageResource(R.drawable.ic_bookmark);
                    } else {
                        bookmarkButton.setImageResource(R.drawable.ic_bookmark_outline);
                    }
                });

                bookmarkButton.setOnClickListener(v -> {
                    if (user == null) {
                        Toast.makeText(context, "Silakan login dulu", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    DocumentReference bookmarkRef = db.collection("users")
                            .document(user.getUid())
                            .collection("bookmarks")
                            .document(movie.getTitle().replaceAll("[^a-zA-Z0-9]", ""));

                    bookmarkRef.get().addOnSuccessListener(docSnap -> {
                        if (docSnap.exists()) {
                            bookmarkRef.delete()
                                    .addOnSuccessListener(aVoid -> {
                                        bookmarkButton.setImageResource(R.drawable.ic_bookmark_outline);
                                        Toast.makeText(context, "Dihapus dari bookmark", Toast.LENGTH_SHORT).show();
                                    });
                        } else {
                            Map<String, Object> data = new HashMap<>();
                            data.put("title", movie.getTitle());
                            data.put("posterUrl", movie.getPosterUrl());
                            data.put("releaseDate", movie.getReleaseDate());
                            data.put("voteAverage", movie.getVoteAverage());

                            bookmarkRef.set(data)
                                    .addOnSuccessListener(aVoid -> {
                                        bookmarkButton.setImageResource(R.drawable.ic_bookmark);
                                        Toast.makeText(context, "Disimpan ke bookmark", Toast.LENGTH_SHORT).show();
                                    });
                        }
                    });
                });
            }
        }
    }
}