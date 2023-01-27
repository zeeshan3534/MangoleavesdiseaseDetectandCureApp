package com.ice.mangoddetector;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ReportGeneratorAdapter extends RecyclerView.Adapter<ReportGeneratorAdapter.Reporterviewholder> {
    ArrayList<ReportGeneratorHelper> reportGeneratorHelpers;

    public ReportGeneratorAdapter(ArrayList<ReportGeneratorHelper> reportGeneratorHelpers) {
        this.reportGeneratorHelpers = reportGeneratorHelpers;
    }

    @NonNull
    @Override
    public Reporterviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.reportcard,parent,false );
        Reporterviewholder reporterviewholder = new Reporterviewholder(view);
//        ReportGeneratorAdapter reportGeneratorViewHolder = new ReportGeneratorAdapter.Reporterviewholder(view);
        return reporterviewholder;
    }

    @Override
    public void onBindViewHolder(@NonNull Reporterviewholder holder, int position) {

        ReportGeneratorHelper reportGeneratorHelper = reportGeneratorHelpers.get(position);
        holder.img.setImageBitmap(reportGeneratorHelper.getImg());
        holder.first.setText(reportGeneratorHelper.getTextview1());
        holder.second.setText(reportGeneratorHelper.getTextview2());
        holder.third.setText(reportGeneratorHelper.getTextview3());


    }

    @Override
    public int getItemCount() {
        return reportGeneratorHelpers.size();
    }

    public static class Reporterviewholder extends  RecyclerView.ViewHolder{

        ImageView img;
        TextView first,second,third;

        public Reporterviewholder(@NonNull View itemView) {
            super(itemView);

            img = itemView.findViewById(R.id.featureimg);
            first = itemView.findViewById(R.id.second);
            second = itemView.findViewById(R.id.textView10);
            third = itemView.findViewById(R.id.textView12);

        }
    }
}

//
//
//    @NonNull
//    @Override
//    public ReportGeneratorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.reportcard,parent,false );
//        ReportGeneratorAdapter reportGeneratorViewHolder = new ReportGeneratorAdapter.ReportGeneratorViewHolder(view);
//        return reportGeneratorViewHolder;
//    }
//
//
//    ;
//    @Override
//    public void onBindViewHolder(@NonNull ReportGeneratorViewHolder holder, int position) {
//        ReportGeneratorHelper reportGeneratorHelper=  reportGeneratorHelpers.get(position);
//        holder.img.setImageBitmap(reportGeneratorHelper.getImg());
//        holder.first.setText(reportGeneratorHelper.getTextview1());
//        holder.second.setText(reportGeneratorHelper.getTextview2());
//        holder.third.setText(reportGeneratorHelper.getTextview2());
//
//    }
//
//    @Override
//    public int getItemCount() {
//        return reportGeneratorHelpers.size();
//    }
//
//    public interface ReportGeneratorViewHolder {
//
//        ImageView img;
//        TextView first,second,third;
//
//        public ReportGeneratorViewHolder(@NonNull View itemView) {
//            super(itemView);
//
//            img = itemView.findViewById(R.id.featureimg);
//            first = itemView.findViewById(R.id.second);
//            second = itemView.findViewById(R.id.textView10);
//            third = itemView.findViewById(R.id.textView12);
//
//
//
//        }
//
//    }
//}
