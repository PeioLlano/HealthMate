package com.example.healthmate.pruebaGraficos;

import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.anychart.AnyChartView;
import com.example.healthmate.R;

public class GraficoViewHolder extends RecyclerView.ViewHolder {

    public AnyChartView graficoBase;
    public ProgressBar progressBar;

    public GraficoViewHolder(@NonNull View itemView) {
        super(itemView);

        graficoBase = itemView.findViewById(R.id.graficoBase);
        progressBar = itemView.findViewById(R.id.progressBar);

    }
}
