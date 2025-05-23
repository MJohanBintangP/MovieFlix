package com.telkomsby.movieflix.ui.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.telkomsby.movieflix.R;
import com.telkomsby.movieflix.model.Film;

import java.util.List;

public class FilmAdapter extends RecyclerView.Adapter<FilmAdapter.FilmViewHolder> {

    private final List<Film> filmList;

    public FilmAdapter(List<Film> filmList) {
        this.filmList = filmList;
    }

    @NonNull
    @Override
    public FilmViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_film, parent, false);
        return new FilmViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FilmViewHolder holder, int position) {
        Film film = filmList.get(position);
        holder.title.setText(film.getTitle());
        holder.rating.setText(String.valueOf(film.getRating()));

        // Gunakan Picasso atau Glide untuk load gambar dari URL
        Picasso.get().load(film.getPosterUrl()).into(holder.poster);
    }

    @Override
    public int getItemCount() {
        return filmList.size();
    }

    static class FilmViewHolder extends RecyclerView.ViewHolder {
        ImageView poster;
        TextView title, rating;

        public FilmViewHolder(@NonNull View itemView) {
            super(itemView);
            poster = itemView.findViewById(R.id.poster);
            title = itemView.findViewById(R.id.title);
            rating = itemView.findViewById(R.id.rating);
        }
    }
}
