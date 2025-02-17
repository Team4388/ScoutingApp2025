package com.ridgebotics.ridgescout.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ridgebotics.ridgescout.R;

import java.util.List;

public class CustomSpinnerOptionsAdapter extends RecyclerView.Adapter<CustomSpinnerOptionsAdapter.ViewHolder> {
    private List<String> options;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(String option);
    }

    public CustomSpinnerOptionsAdapter(List<String> options, OnItemClickListener listener) {
        this.options = options;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_custom_spinner_option, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String option = options.get(position);
        holder.textView.setText(option);
        holder.itemView.setOnClickListener(v -> listener.onItemClick(option));
    }

    @Override
    public int getItemCount() {
        return options.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        ViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textView);
        }
    }
}