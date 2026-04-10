package com.example.mobileticketapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobileticketapp.R;
import com.example.mobileticketapp.models.Showtime;

import java.util.List;

public class ShowtimeAdapter extends RecyclerView.Adapter<ShowtimeAdapter.ShowtimeViewHolder> {

    private Context context;
    private List<Showtime> showtimeList;
    private OnShowtimeClickListener clickListener;

    public interface OnShowtimeClickListener {
        void onShowtimeClick(Showtime showtime);
    }

    public ShowtimeAdapter(Context context, List<Showtime> showtimeList, OnShowtimeClickListener clickListener) {
        this.context = context;
        this.showtimeList = showtimeList;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public ShowtimeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_showtime, parent, false);
        return new ShowtimeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShowtimeViewHolder holder, int position) {
        Showtime showtime = showtimeList.get(position);
        
        holder.tvTheaterName.setText("Theater: " + showtime.getTheaterId()); // We would map this to actual Theater name in a real app
        holder.tvTime.setText(showtime.getStartTime());
        holder.tvPrice.setText(showtime.getPrice() + " VND");

        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onShowtimeClick(showtime);
            }
        });
    }

    @Override
    public int getItemCount() {
        return showtimeList != null ? showtimeList.size() : 0;
    }

    public static class ShowtimeViewHolder extends RecyclerView.ViewHolder {
        TextView tvTheaterName, tvTime, tvPrice;

        public ShowtimeViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTheaterName = itemView.findViewById(R.id.tvTheaterName);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvPrice = itemView.findViewById(R.id.tvPrice);
        }
    }
}
