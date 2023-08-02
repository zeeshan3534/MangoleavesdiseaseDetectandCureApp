package com.ice.mangosurveyour;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

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
        String str = reportGeneratorHelper.getTextview1();
        String str1 = reportGeneratorHelper.getTextview2();
        String str2 = reportGeneratorHelper.getTextview3();
        String[] parts = str.split("=");
        String[] parts1 = str1.split("=");
        String[] parts2 = str2.split("=");

        HashMap<String, Float> data = new HashMap<>();
        ArrayList<String> labels = new ArrayList<>();
        data.put(parts[0], Float.parseFloat(parts[1])*100);
        data.put(parts1[0], Float.parseFloat(parts1[1])*100);
        data.put(parts2[0], Float.parseFloat(parts2[1])*100);
//   Cure
        HashMap<String, String> map = new HashMap<>();
        map.put("Anthracnose","Regular spraying of trees from flowering time onwards with mancozeb (at recommended label rates every 14 days) is useful to reduce the level of infection in the developing fruit. Do not use mancozeb within 14 days of harvest. If anthracnose becomes serious in green immature fruit it would be useful to give a couple of judicious sprays of prochloraz");
        map.put("Apoderus Javanicus","Pesticides: Pesticides can be used to control the beetles on the tree. Sanitation: Proper sanitation of the orchard can help to reduce the population of the beetles. This includes removing and destroying any fallen fruit and debris from the tree, which can serve as a breeding ground for the beetles");
        map.put("Bacterial Canker","Regular inspection of orchards, sanitation, and seedling certification are recommended as preventive measures against the disease. Spray of copper-based fungicides has been found effective in controlling bacterial canker.");
        map.put("Dappula Tertia","Pesticides: Insecticides can be applied to the trunk and branches of the tree to kill larvae and adult beetles. Cultural control: Keeping trees healthy by providing adequate water, fertilizer, and pruning can help to reduce the chances of infestation.");
        map.put("Dialeuropora Decempuncta","Control of Dialeurodes infestation can be done through the use of pesticides and other control measures like using natural enemies of whitefly such as lady beetles, lacewings, and parasitic wasps.");
        map.put("Gall Midge"," Pesticides: Spraying of 0.05% fenitrothion, 0.045% dimethoate at bud burst stage of the inflorescence can be effective in controlling the pest. Foliar application of bifenthrin (70ml/100lit) mixed with water has also given satisfactory results. Sanitation: removing and destroying any heavily infested shoots, leaves, and fruits from the tree.");
        map.put("Black Soothy Mold","The best way to control sooty mold fungi is using preventive method by eliminating their sugary food supply. Controlling sap-feeding insects on the foliage as well as ants that tend and protect them. General-purpose fungicide may be effective on killing fungi but not removing black color.");
        map.put("Icerya Seychellarum","Insecticides such as horticultural oil, and insecticidal soap can be used for prevention. It was found that paraffin oil at 1.25% was the most effective insecticide.");
        map.put("Mictis Longicornis","Control measures include the use of pesticides, pruning and removing infested branches.");
        map.put("Neomelicharia Sparsa","The disease can be controlled by removing infected plant parts, and by applying fungicides. Cultural practices such as proper pruning and irrigation can also help prevent the disease from spreading.");


//        Bar Graph work
        ArrayList<BarEntry> entries = new ArrayList<>();
        for (String key : data.keySet()) {
            entries.add(new BarEntry(data.get(key), data.get(key)));
            labels.add(key);

        }

        XAxis xAxis = holder.barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));


        BarDataSet dataSet = new BarDataSet(entries, "Prediction Overview");
        int[] colors = {Color.parseColor("#9DB288"), Color.parseColor("#B1CBA6"), Color.parseColor("#D9DAA9")};

        // Set some styling for the chart
        dataSet.setColors(Color.GREEN);
        dataSet.setValueTextColor(Color.BLACK);

        dataSet.setBarBorderWidth(10f);
        dataSet.setValueTextSize(14f);

        BarData pieData = new BarData(dataSet);


        holder.img.setImageBitmap(reportGeneratorHelper.getImg());
        holder.first.setText(reportGeneratorHelper.getTextview1());
        holder.second.setText(reportGeneratorHelper.getTextview2());
        holder.third.setText(reportGeneratorHelper.getTextview3());
        holder.medical.setText(map.get(parts[0]));
        holder.barChart.setData(pieData);


    }

    @Override
    public int getItemCount() {
        return reportGeneratorHelpers.size();
    }

    public static class Reporterviewholder extends  RecyclerView.ViewHolder{

        ImageView img;
        TextView first,second,third,medical;
        BarChart barChart;

        public Reporterviewholder(@NonNull View itemView) {
            super(itemView);

            img = itemView.findViewById(R.id.featureimg);
            barChart = itemView.findViewById(R.id.bar_chart);
            first = itemView.findViewById(R.id.second);
            second = itemView.findViewById(R.id.textView10);
            third = itemView.findViewById(R.id.textView12);
            medical = itemView.findViewById(R.id.cure);

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