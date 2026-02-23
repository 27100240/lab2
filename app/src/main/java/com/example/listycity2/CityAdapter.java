package com.example.listycity2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CityAdapter extends RecyclerView.Adapter<CityAdapter.CityVH> {

    public interface OnItemClick {
        void onClick(int position);
    }

    private final ArrayList<String> cities;
    private final OnItemClick onItemClick;
    private int selectedPos = RecyclerView.NO_POSITION;

    public CityAdapter(ArrayList<String> cities, OnItemClick onItemClick) {
        this.cities = cities;
        this.onItemClick = onItemClick;
    }

    public void setSelectedPos(int pos) {
        int old = selectedPos;
        selectedPos = pos;
        if (old != RecyclerView.NO_POSITION) notifyItemChanged(old);
        if (selectedPos != RecyclerView.NO_POSITION) notifyItemChanged(selectedPos);
    }

    public int getSelectedPos() {
        return selectedPos;
    }

    @NonNull
    @Override
    public CityVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_city, parent, false);
        return new CityVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CityVH holder, int position) {
        holder.txt.setText(cities.get(position));

        // highlight selected row (orange like the screenshot)
        holder.itemView.setBackgroundColor(
                position == selectedPos ? 0xFFFFB3C6 : 0x00FFFFFF
        );

        holder.itemView.setOnClickListener(v -> onItemClick.onClick(holder.getAdapterPosition()));
    }

    @Override
    public int getItemCount() {
        return cities.size();
    }

    static class CityVH extends RecyclerView.ViewHolder {
        TextView txt;
        CityVH(@NonNull View itemView) {
            super(itemView);
            txt = itemView.findViewById(R.id.txt_city);
        }
    }
}