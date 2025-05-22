package com.telkomsby.movieflix;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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
                LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tutorial, parent, false)
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

    static class TutorialViewHolder extends RecyclerView.ViewHolder {
        private final TextView textTitle, textDescription;
        private final ImageView imageTutorial;

        public TutorialViewHolder(@NonNull View itemView) {
            super(itemView);
            textTitle = itemView.findViewById(R.id.textTitle);
            textDescription = itemView.findViewById(R.id.textDescription);
            imageTutorial = itemView.findViewById(R.id.imageTutorial);
        }

        void bind(TutorialItem item) {
            textTitle.setText(item.getTitle());
            textDescription.setText(item.getDescription());
            imageTutorial.setImageResource(item.getImageResId());
        }
    }
}

