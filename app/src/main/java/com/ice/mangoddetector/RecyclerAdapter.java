package com.ice.mangoddetector;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    private final ArrayList<Uri> uriArrayList;
    CountOfImagesWhenRemoved countOfImagesWhenRemoved;

    private final itemClickListner itemClickListner;

    public RecyclerAdapter(ArrayList<Uri> uriArrayList,CountOfImagesWhenRemoved countOfImagesWhenRemoved,itemClickListner itemClickListner) {
        this.uriArrayList = uriArrayList;
        this.countOfImagesWhenRemoved =countOfImagesWhenRemoved;
        this.itemClickListner = itemClickListner;
    }

    @NonNull
    @Override
    public RecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.custom_single_image,parent,false);


        return new ViewHolder(view,countOfImagesWhenRemoved,itemClickListner);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerAdapter.ViewHolder holder, int position) {
        holder.imageView.setImageURI(uriArrayList.get(position));
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uriArrayList.remove(uriArrayList.get(position));
                notifyItemRemoved(position);
                notifyItemChanged(position,getItemCount());
                countOfImagesWhenRemoved.clicked(uriArrayList.size());
            }
        });
    }

    @Override
    public int getItemCount() {

        return uriArrayList.size();
    }



    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView imageView;
        ImageView delete;
        CountOfImagesWhenRemoved countOfImagesWhenRemoved;
        itemClickListner itemClickListner;



        public ViewHolder(@NonNull View itemView,CountOfImagesWhenRemoved countOfImagesWhenRemoved, itemClickListner itemClickListner) {
            super(itemView);
            this.countOfImagesWhenRemoved = countOfImagesWhenRemoved;
            imageView= itemView.findViewById(R.id.image);
            delete = itemView.findViewById(R.id.delete);

            this.itemClickListner = itemClickListner;

            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            if (itemClickListner != null){
                itemClickListner.itemClick(getAdapterPosition());
            }

        }
    }
    public interface CountOfImagesWhenRemoved{
        void clicked(int getsize);
    }

    public interface itemClickListner{
        void itemClick(int position);
    }
}
