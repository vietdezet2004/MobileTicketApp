package com.example.mobileticketapp.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobileticketapp.R;
import com.example.mobileticketapp.adapters.TicketAdapter;
import com.example.mobileticketapp.models.Ticket;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MyTicketsActivity extends AppCompatActivity {

    private RecyclerView rvMyTickets;
    private TextView tvNoTickets;
    private ProgressBar progressBar;
    private TicketAdapter ticketAdapter;
    private List<Ticket> ticketList;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_tickets);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        initViews();
        fetchTickets();
    }

    private void initViews() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("My Tickets");
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        rvMyTickets = findViewById(R.id.rvMyTickets);
        tvNoTickets = findViewById(R.id.tvNoTickets);
        progressBar = findViewById(R.id.progressBar);

        ticketList = new ArrayList<>();
        ticketAdapter = new TicketAdapter(this, ticketList);
        rvMyTickets.setLayoutManager(new LinearLayoutManager(this));
        rvMyTickets.setAdapter(ticketAdapter);
    }

    private void fetchTickets() {
        if (mAuth.getCurrentUser() == null) return;
        
        progressBar.setVisibility(View.VISIBLE);
        String uid = mAuth.getCurrentUser().getUid();

        db.collection("tickets")
                .whereEqualTo("uid", uid)
                .get()
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null && !querySnapshot.isEmpty()) {
                            ticketList.clear();
                            for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                                Ticket ticket = doc.toObject(Ticket.class);
                                if (ticket != null) {
                                    ticketList.add(ticket);
                                }
                            }
                            ticketAdapter.notifyDataSetChanged();
                            tvNoTickets.setVisibility(View.GONE);
                        } else {
                            tvNoTickets.setVisibility(View.VISIBLE);
                        }
                    } else {
                        Toast.makeText(MyTicketsActivity.this, "Failed to load tickets", Toast.LENGTH_SHORT).show();
                        tvNoTickets.setVisibility(View.VISIBLE);
                    }
                });
    }
}
