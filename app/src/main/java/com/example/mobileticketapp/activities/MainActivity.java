package com.example.mobileticketapp.activities;

import android.Manifest;
import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobileticketapp.R;
import com.example.mobileticketapp.adapters.MovieAdapter;
import com.example.mobileticketapp.models.Movie;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView rvMovies;
    private MovieAdapter movieAdapter;
    private List<Movie> movieList;
    private ProgressBar progressBar;
    private ImageView ivLogout, ivMyTickets;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    // Launcher xin quyền thông báo (Android 13+)
    private final ActivityResultLauncher<String> notifPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    Toast.makeText(this, "✅ Thông báo đã được bật!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "⚠️ Bạn cần bật thông báo để nhận nhắc giờ chiếu.", Toast.LENGTH_LONG).show();
                }
                checkExactAlarmPermission();
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        if (mAuth.getCurrentUser() == null) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
            return;
        }

        initViews();
        setupRecyclerView();
        setupListeners();
        fetchMovies();

        // Xin quyền thông báo sau khi UI đã load
        requestNotificationPermission();
    }

    /** Xin quyền POST_NOTIFICATIONS trên Android 13+ */
    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                notifPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            } else {
                checkExactAlarmPermission();
            }
        } else {
            checkExactAlarmPermission();
        }
    }

    /** Kiểm tra + hướng dẫn bật quyền đặt báo thức chính xác (Android 12+) */
    private void checkExactAlarmPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            if (!am.canScheduleExactAlarms()) {
                new AlertDialog.Builder(this)
                        .setTitle("Cấp quyền Báo thức")
                        .setMessage("App cần quyền đặt báo thức chính xác để nhắc giờ chiếu phim.\nBấm OK để vào Cài đặt bật lên nhé!")
                        .setPositiveButton("Vào Cài đặt", (d, w) -> {
                            Intent intent = new Intent(
                                    Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM,
                                    Uri.parse("package:" + getPackageName()));
                            startActivity(intent);
                        })
                        .setNegativeButton("Bỏ qua", null)
                        .show();
            }
        }
    }

    private void initViews() {
        rvMovies = findViewById(R.id.rvMovies);
        progressBar = findViewById(R.id.progressBar);
        ivLogout = findViewById(R.id.ivLogout);
        ivMyTickets = findViewById(R.id.ivMyTickets);
    }

    private void setupRecyclerView() {
        movieList = new ArrayList<>();
        movieAdapter = new MovieAdapter(this, movieList, movie -> {
            Intent intent = new Intent(MainActivity.this, MovieDetailActivity.class);
            intent.putExtra("movie", movie);
            startActivity(intent);
        });
        rvMovies.setLayoutManager(new GridLayoutManager(this, 2));
        rvMovies.setAdapter(movieAdapter);
    }

    private void setupListeners() {
        ivLogout.setOnClickListener(v -> {
            mAuth.signOut();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        });

        ivMyTickets.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, MyTicketsActivity.class));
        });
    }

    private void fetchMovies() {
        progressBar.setVisibility(View.VISIBLE);
        db.collection("movies")
                .whereEqualTo("showing", true)
                .get()
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null && !querySnapshot.isEmpty()) {
                            movieList.clear();
                            for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                                Movie movie = doc.toObject(Movie.class);
                                if (movie != null) {
                                    movieList.add(movie);
                                }
                            }
                            movieAdapter.notifyDataSetChanged();
                        } else {
                            generateMockData();
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "Error fetching movies", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void generateMockData() {
        List<Movie> mocks = Arrays.asList(
                new Movie("m1", "Avengers: Endgame", "After the devastating events of Infinity War...", "https://image.tmdb.org/t/p/w500/or06FN3Dka5tukK1e9sl16pB3iy.jpg", "", 181, "2019-04-26", "Action, Sci-Fi", true),
                new Movie("m2", "Dune: Part Two", "Paul Atreides unites with Chani and the Fremen while on a warpath of revenge...", "https://image.tmdb.org/t/p/w500/1pdfLvkbY9ohJlCjQH2TG52X4P3.jpg", "", 166, "2024-03-01", "Sci-Fi, Adventure", true),
                new Movie("m3", "Spider-Man: No Way Home", "Peter Parker is unmasked...", "https://image.tmdb.org/t/p/w500/1g0dhYtq4irTY1R80vE1IGCVuJE.jpg", "", 148, "2021-12-17", "Action, Fantasy", true),
                new Movie("m4", "Oppenheimer", "The story of American scientist J. Robert Oppenheimer...", "https://image.tmdb.org/t/p/w500/8Gxv8gSFCU0XGDykEGv7zR1n2ua.jpg", "", 180, "2023-07-21", "Drama, History", true)
        );

        for (Movie m : mocks) {
            db.collection("movies").document(m.getId()).set(m);
            movieList.add(m);
        }
        movieAdapter.notifyDataSetChanged();
        Toast.makeText(this, "Generated Mock Data", Toast.LENGTH_SHORT).show();
    }
}