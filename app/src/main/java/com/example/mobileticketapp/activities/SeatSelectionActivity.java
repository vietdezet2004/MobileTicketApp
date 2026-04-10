package com.example.mobileticketapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.example.mobileticketapp.R;
import com.example.mobileticketapp.models.Movie;
import com.example.mobileticketapp.models.Showtime;
import com.example.mobileticketapp.models.Ticket;
import com.example.mobileticketapp.utils.NotificationReceiver;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SeatSelectionActivity extends AppCompatActivity {

    private GridLayout gridLayoutSeats;
    private TextView tvTotalPrice;
    private MaterialButton btnBook;
    private ProgressBar progressBar;

    private Movie currentMovie;
    private Showtime currentShowtime;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    private List<String> selectedSeats = new ArrayList<>();
    private List<String> bookedSeats = new ArrayList<>();
    private int totalPrice = 0;

    // Dữ liệu nhận từ Intent
    private String showtimeTime;
    private String showtimeTheater;
    private int showtimePrice = 100000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seat_selection);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // Nhận dữ liệu từ MovieDetailActivity
        currentMovie = (Movie) getIntent().getSerializableExtra("movie");
        showtimeTime    = getIntent().getStringExtra("showtime_time");
        showtimeTheater = getIntent().getStringExtra("showtime_theater");
        showtimePrice   = getIntent().getIntExtra("showtime_price", 100000);

        initViews();
        fetchBookedSeats();
    }

    private void initViews() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Select Seats");
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        gridLayoutSeats = findViewById(R.id.gridLayoutSeats);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        btnBook = findViewById(R.id.btnBook);
        progressBar = findViewById(R.id.progressBar);

        btnBook.setOnClickListener(v -> bookTickets());
    }

    private void fetchBookedSeats() {
        // Mock booked seats
        bookedSeats.add("A3");
        bookedSeats.add("B4");
        bookedSeats.add("C1");
        
        generateSeatGrid();
    }

    private void generateSeatGrid() {
        int rows = 6;
        int cols = 6;
        char rowChar = 'A';

        for (int i = 0; i < rows; i++) {
            for (int j = 1; j <= cols; j++) {
                String seatId = rowChar + String.valueOf(j);
                
                TextView seatView = new TextView(this);
                seatView.setText(seatId);
                seatView.setGravity(Gravity.CENTER);
                seatView.setTextColor(ContextCompat.getColor(this, R.color.seatText));
                seatView.setTextSize(12f);
                
                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.width = 100;
                params.height = 100;
                params.setMargins(8, 8, 8, 8);
                seatView.setLayoutParams(params);

                if (bookedSeats.contains(seatId)) {
                    seatView.setBackgroundResource(R.color.seatBooked);
                    seatView.setEnabled(false);
                } else {
                    seatView.setBackgroundResource(R.color.seatAvailable);
                    seatView.setOnClickListener(v -> toggleSeatSelection(seatView, seatId));
                }

                gridLayoutSeats.addView(seatView);
            }
            rowChar++;
        }
    }

    private void toggleSeatSelection(TextView seatView, String seatId) {
        if (selectedSeats.contains(seatId)) {
            selectedSeats.remove(seatId);
            seatView.setBackgroundResource(R.color.seatAvailable);
            totalPrice -= showtimePrice;
        } else {
            selectedSeats.add(seatId);
            seatView.setBackgroundResource(R.color.seatSelected);
            totalPrice += showtimePrice;
        }
        tvTotalPrice.setText(totalPrice + " VND");
    }

    private void bookTickets() {
        if (selectedSeats.isEmpty()) {
            Toast.makeText(this, "Please select at least one seat", Toast.LENGTH_SHORT).show();
            return;
        }
        if (mAuth.getCurrentUser() == null) return;

        progressBar.setVisibility(View.VISIBLE);
        btnBook.setEnabled(false);

        String movieTitle   = (currentMovie != null) ? currentMovie.getTitle() : "Movie";
        String theater      = (showtimeTheater != null) ? showtimeTheater : "CGV Vincom";
        String timeLabel    = (showtimeTime != null) ? showtimeTime : "18:00 - Today";
        // Lấy giờ thuần ("11:00 - Today" → "11:00")
        String timeOnly = timeLabel.contains(" ") ? timeLabel.split(" ")[0] : timeLabel;

        String ticketId = UUID.randomUUID().toString();
        Ticket ticket = new Ticket(
                ticketId,
                mAuth.getCurrentUser().getUid(),
                getIntent().getStringExtra("showtime_id") != null
                        ? getIntent().getStringExtra("showtime_id") : "dummy_showtime",
                selectedSeats,
                totalPrice,
                System.currentTimeMillis(),
                movieTitle,
                timeLabel,
                theater
        );

        db.collection("tickets").document(ticketId).set(ticket)
                .addOnSuccessListener(aVoid -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "🎬 Booking Successful! Bạn sẽ nhận thông báo trước giờ chiếu.", Toast.LENGTH_LONG).show();

                    long showtimeMillis = getShowtimeMillis(timeOnly);
                    NotificationReceiver.scheduleNotification(
                            SeatSelectionActivity.this,
                            movieTitle,
                            theater,
                            timeLabel,
                            showtimeMillis
                    );

                    Intent intent = new Intent(SeatSelectionActivity.this, MyTicketsActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    btnBook.setEnabled(true);
                    Toast.makeText(this, "Booking failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    /**
     * Parse "HH:mm" thành milliseconds của ngày hôm nay
     * Nếu đã qua hoặc không parse được thì trả về 5 giây từ bây giờ (để demo notification)
     */
    private long getShowtimeMillis(String timeStr) {
        try {
            String[] parts = timeStr.split(":");
            int hour = Integer.parseInt(parts[0].trim());
            int minute = Integer.parseInt(parts[1].trim());

            java.util.Calendar cal = java.util.Calendar.getInstance();
            cal.set(java.util.Calendar.HOUR_OF_DAY, hour);
            cal.set(java.util.Calendar.MINUTE, minute);
            cal.set(java.util.Calendar.SECOND, 0);
            cal.set(java.util.Calendar.MILLISECOND, 0);
            return cal.getTimeInMillis();
        } catch (Exception e) {
            return System.currentTimeMillis(); // fallback: fire in ~5s (handled in scheduleNotification)
        }
    }
}
