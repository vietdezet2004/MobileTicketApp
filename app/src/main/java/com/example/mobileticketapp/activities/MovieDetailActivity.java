package com.example.mobileticketapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mobileticketapp.R;
import com.example.mobileticketapp.adapters.ShowtimeAdapter;
import com.example.mobileticketapp.models.Movie;
import com.example.mobileticketapp.models.Showtime;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MovieDetailActivity extends AppCompatActivity {

    private ImageView ivPoster;
    private TextView tvTitle, tvInfo, tvDescription;
    private RecyclerView rvShowtimes;
    private ShowtimeAdapter showtimeAdapter;
    private List<Showtime> showtimeList;
    private FirebaseFirestore db;
    private Movie currentMovie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        db = FirebaseFirestore.getInstance();

        // Get movie from Intent
        currentMovie = (Movie) getIntent().getSerializableExtra("movie");
        if (currentMovie == null) {
            Toast.makeText(this, "Movie data error", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        displayMovieDetails();
        fetchShowtimes();
    }

    private void initViews() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("");
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        ivPoster = findViewById(R.id.ivPoster);
        tvTitle = findViewById(R.id.tvTitle);
        tvInfo = findViewById(R.id.tvInfo);
        tvDescription = findViewById(R.id.tvDescription);
        rvShowtimes = findViewById(R.id.rvShowtimes);

        showtimeList = new ArrayList<>();
        showtimeAdapter = new ShowtimeAdapter(this, showtimeList, showtime -> {
            Intent intent = new Intent(MovieDetailActivity.this, SeatSelectionActivity.class);
            intent.putExtra("movie", currentMovie);
            intent.putExtra("showtime_id", showtime.getId());
            intent.putExtra("showtime_time", showtime.getStartTime());
            intent.putExtra("showtime_theater", showtime.getTheaterId());
            intent.putExtra("showtime_price", showtime.getPrice());
            startActivity(intent);
        });
        rvShowtimes.setLayoutManager(new LinearLayoutManager(this));
        rvShowtimes.setAdapter(showtimeAdapter);
    }

    private void displayMovieDetails() {
        tvTitle.setText(currentMovie.getTitle());
        tvInfo.setText(currentMovie.getReleaseDate() + " | " + currentMovie.getDuration() + " min | " + currentMovie.getGenre());
        tvDescription.setText(currentMovie.getDescription());

        Glide.with(this)
                .load(currentMovie.getPosterUrl())
                .placeholder(android.R.color.darker_gray)
                .into(ivPoster);
    }

    private void fetchShowtimes() {
        db.collection("showtimes")
                .whereEqualTo("movieId", currentMovie.getId())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
                        showtimeList.clear();
                        for (DocumentSnapshot doc : task.getResult().getDocuments()) {
                            Showtime showtime = doc.toObject(Showtime.class);
                            if (showtime != null) {
                                showtimeList.add(showtime);
                            }
                        }
                        showtimeAdapter.notifyDataSetChanged();
                    } else {
                        // Generate mock showtimes for this movie
                        generateMockShowtimes();
                    }
                });
    }

    private void generateMockShowtimes() {
        List<Showtime> mocks = Arrays.asList(
                new Showtime(currentMovie.getId() + "_s0", currentMovie.getId(), "CGV Vincom",  "11:00 - Today", 80000),
                new Showtime(currentMovie.getId() + "_s1", currentMovie.getId(), "CGV Vincom",  "18:00 - Today", 100000),
                new Showtime(currentMovie.getId() + "_s2", currentMovie.getId(), "Lotte Cinema", "20:30 - Today", 120000),
                new Showtime(currentMovie.getId() + "_s3", currentMovie.getId(), "BHD Star",    "22:00 - Today", 90000)
        );

        for (Showtime s : mocks) {
            db.collection("showtimes").document(s.getId()).set(s);
            showtimeList.add(s);
        }
        showtimeAdapter.notifyDataSetChanged();
    }
}
