package com.ice.mangosurveyour;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class FeatureAdapter extends RecyclerView.Adapter<FeatureAdapter.FeaturedViewHolder> {
    ArrayList<featureHelper> featureHelpers;

    public FeatureAdapter(ArrayList<featureHelper> featureHelpers) {
        this.featureHelpers = featureHelpers;
    }

    @NonNull
    @Override
    public FeaturedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card,parent,false );
        FeaturedViewHolder featuredViewHolder = new FeaturedViewHolder(view);
        return featuredViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull FeaturedViewHolder holder, int position) {
        featureHelper featureHelper=  featureHelpers.get(position);
        holder.image.setImageResource(featureHelper.getImage());
        holder.title.setText(featureHelper.getTitle());
        holder.desc.setText(featureHelper.getDescription());
    }

    @Override
    public int getItemCount() {
        return featureHelpers.size();
    }

    public static class FeaturedViewHolder extends RecyclerAdapter.ViewHolder{
        ImageView image;
        TextView title,desc;

        public FeaturedViewHolder(@NonNull View itemView) {
            super(itemView,null,null);

            image = itemView.findViewById(R.id.featureimg);
            title = itemView.findViewById(R.id.featuretitle);
            desc = itemView.findViewById(R.id.featuretext);



        }

    }

}

