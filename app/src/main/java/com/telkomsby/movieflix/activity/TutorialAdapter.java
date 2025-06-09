package com.telkomsby.movieflix.activity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.telkomsby.movieflix.R;

import java.util.List;

public class TutorialAdapter extends RecyclerView.Adapter<TutorialAdapter.TutorialViewHolder> {

    private final List<TutorialItem> tutorialItems;

    public TutorialAdapter(List<TutorialItem> tutorialItems) {
        this.tutorialItems = tutorialItems;
    }

    @NonNull
    @Override
    public TutorialViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TutorialViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.item_tutorial,
                        parent,
                        false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull TutorialViewHolder holder, int position) {
        holder.bind(tutorialItems.get(position));
    }

    @Override
    public int getItemCount() {
        return tutorialItems.size();
    }

    public static class TutorialViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imageTutorial;

        public TutorialViewHolder(@NonNull View itemView) {
            super(itemView);
            imageTutorial = itemView.findViewById(R.id.imageTutorial);
        }

        void bind(TutorialItem item) {
            if (imageTutorial != null) {
                imageTutorial.setImageResource(item.getImageResId());
            }
        }
    }
}