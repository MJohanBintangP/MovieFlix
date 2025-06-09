package com.telkomsby.movieflix.ui.home;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import com.telkomsby.movieflix.R;
import com.telkomsby.movieflix.api.Movie;
import com.telkomsby.movieflix.model.TrailerResponse;
import com.telkomsby.movieflix.repository.TmdbRepository;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FilmAdapter extends RecyclerView.Adapter<FilmAdapter.FilmViewHolder> {
    private final List<Movie> movies;
    private final Context context;
    private final LifecycleOwner lifecycleOwner;

    public FilmAdapter(List<Movie> movies, Context context, LifecycleOwner lifecycleOwner) {
        this.movies = movies;
        this.context = context;
        this.lifecycleOwner = lifecycleOwner;
    }

    @NonNull
    @Override
    public FilmViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_film, parent, false);
        return new FilmViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FilmViewHolder holder, int position) {
        Movie movie = movies.get(position);
        holder.bind(movie, context, lifecycleOwner);
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    public void updateMovies(List<Movie> newMovieList) {
        movies.clear();
        movies.addAll(newMovieList);
        notifyDataSetChanged();
    }

    static class FilmViewHolder extends RecyclerView.ViewHolder {
        private final ImageView posterImageView;
        private final TextView titleTextView, ratingTextView, releaseDateTextView;
        private final ImageButton bookmarkButton;

        public FilmViewHolder(@NonNull View itemView) {
            super(itemView);
            posterImageView = itemView.findViewById(R.id.poster_image);
            titleTextView = itemView.findViewById(R.id.title_text);
            ratingTextView = itemView.findViewById(R.id.rating_text);
            releaseDateTextView = itemView.findViewById(R.id.release_date_text);
            bookmarkButton = itemView.findViewById(R.id.bookmark_button);
        }

        public void bind(Movie movie, Context context, LifecycleOwner lifecycleOwner) {
            String posterUrl = movie.getPosterUrl();
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

            titleTextView.setText(movie.getTitle());
            ratingTextView.setText(String.format("%.1f/10", movie.getVoteAverage()));
            releaseDateTextView.setText(movie.getReleaseDate());

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            if (user != null) {
                String safeTitle = movie.getTitle().replaceAll("[^a-zA-Z0-9]", "");
                DocumentReference bookmarkRef = db.collection("users")
                        .document(user.getUid())
                        .collection("bookmarks")
                        .document(safeTitle);

                bookmarkRef.get().addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        bookmarkButton.setImageResource(R.drawable.ic_bookmark_outline);
                        bookmarkButton.setTag(true);
                    } else {
                        bookmarkButton.setImageResource(R.drawable.ic_bookmark);
                        bookmarkButton.setTag(false);
                    }
                });

                bookmarkButton.setOnClickListener(v -> {
                    boolean isBookmarked = (boolean) bookmarkButton.getTag();

                    if (isBookmarked) {
                        bookmarkRef.delete()
                                .addOnSuccessListener(aVoid -> {
                                    bookmarkButton.setImageResource(R.drawable.ic_bookmark_outline);
                                    bookmarkButton.setTag(false);
                                    Toast.makeText(context, "Dihapus dari bookmark", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> Toast.makeText(context, "Gagal menghapus", Toast.LENGTH_SHORT).show());
                    } else {
                        Map<String, Object> data = new HashMap<>();
                        data.put("title", movie.getTitle());
                        data.put("posterUrl", movie.getPosterUrl());
                        data.put("releaseDate", movie.getReleaseDate());
                        data.put("voteAverage", movie.getVoteAverage());

                        bookmarkRef.set(data)
                                .addOnSuccessListener(aVoid -> {
                                    bookmarkButton.setImageResource(R.drawable.ic_bookmark);
                                    bookmarkButton.setTag(true);
                                    Toast.makeText(context, "Disimpan ke bookmark", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> Toast.makeText(context, "Gagal menyimpan", Toast.LENGTH_SHORT).show());
                    }
                });
            }

            posterImageView.setOnClickListener(v -> {
                if (movie.getTrailerKey() != null && !movie.getTrailerKey().isEmpty()) {
                    showTrailerDialog(movie.getTrailerKey(), context, lifecycleOwner);
                } else {
                    fetchTrailerFromApi(movie.getId(), context, lifecycleOwner, movie);
                }
            });
        }

        private void showTrailerDialog(String videoId, Context context, LifecycleOwner lifecycleOwner) {
            Dialog dialog = new Dialog(context);
            dialog.setContentView(R.layout.dialog_trailer);

            YouTubePlayerView youTubePlayerView = dialog.findViewById(R.id.youtube_player_view);
            youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
                @Override
                public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                    youTubePlayer.loadVideo(videoId, 0);
                }
            });

            lifecycleOwner.getLifecycle().addObserver(youTubePlayerView);
            dialog.show();
        }

        private void fetchTrailerFromApi(int movieId, Context context, LifecycleOwner lifecycleOwner, Movie movie) {
            TmdbRepository repository = new TmdbRepository();
            repository.getMovieTrailers(movieId, new Callback<TrailerResponse>() {
                @Override
                public void onResponse(Call<TrailerResponse> call, Response<TrailerResponse> response) {
                    if (response.isSuccessful() && response.body() != null && !response.body().getResults().isEmpty()) {
                        for (TrailerResponse.Trailer trailer : response.body().getResults()) {
                            if ("YouTube".equalsIgnoreCase(trailer.getSite()) &&
                                    "Trailer".equalsIgnoreCase(trailer.getType())) {
                                String trailerKey = trailer.getKey();
                                movie.setTrailerKey(trailerKey);
                                showTrailerDialog(trailerKey, context, lifecycleOwner);
                                break;
                            }
                        }
                    } else {
                        Toast.makeText(context, "Trailer tidak tersedia", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<TrailerResponse> call, Throwable t) {
                    Toast.makeText(context, "Gagal memuat trailer", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}