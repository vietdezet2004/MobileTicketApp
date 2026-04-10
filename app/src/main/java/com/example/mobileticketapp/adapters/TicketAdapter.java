package com.example.mobileticketapp.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobileticketapp.R;
import com.example.mobileticketapp.models.Ticket;

import java.util.List;

public class TicketAdapter extends RecyclerView.Adapter<TicketAdapter.TicketViewHolder> {

    private Context context;
    private List<Ticket> ticketList;

    public TicketAdapter(Context context, List<Ticket> ticketList) {
        this.context = context;
        this.ticketList = ticketList;
    }

    @NonNull
    @Override
    public TicketViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_ticket, parent, false);
        return new TicketViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TicketViewHolder holder, int position) {
        Ticket ticket = ticketList.get(position);

        holder.tvMovieTitle.setText(ticket.getMovieTitle());
        holder.tvTheaterName.setText(ticket.getTheaterName());
        holder.tvShowtime.setText(ticket.getStartTime());
        holder.tvPrice.setText("Total: " + ticket.getTotalPrice() + " VND");

        if (ticket.getSeatNumbers() != null) {
            String seats = TextUtils.join(", ", ticket.getSeatNumbers());
            holder.tvSeats.setText("Seats: " + seats);
        } else {
            holder.tvSeats.setText("Seats: N/A");
        }
    }

    @Override
    public int getItemCount() {
        return ticketList != null ? ticketList.size() : 0;
    }

    public static class TicketViewHolder extends RecyclerView.ViewHolder {
        TextView tvMovieTitle, tvTheaterName, tvShowtime, tvSeats, tvPrice;

        public TicketViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMovieTitle = itemView.findViewById(R.id.tvMovieTitle);
            tvTheaterName = itemView.findViewById(R.id.tvTheaterName);
            tvShowtime = itemView.findViewById(R.id.tvShowtime);
            tvSeats = itemView.findViewById(R.id.tvSeats);
            tvPrice = itemView.findViewById(R.id.tvPrice);
        }
    }
}
