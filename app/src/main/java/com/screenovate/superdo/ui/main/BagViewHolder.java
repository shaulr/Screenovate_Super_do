package com.screenovate.superdo.ui.main;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.screenovate.superdo.R;

public class BagViewHolder extends RecyclerView.ViewHolder {
    public ImageView color;
    public TextView weight;
    public TextView name;
    public BagViewHolder(@NonNull View itemView) {
        super(itemView);
        color = itemView.findViewById(R.id.color);
        weight = itemView.findViewById(R.id.weight);
        name = itemView.findViewById(R.id.name);
    }

}
