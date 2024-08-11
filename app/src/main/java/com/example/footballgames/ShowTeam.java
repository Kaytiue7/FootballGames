package com.example.footballgames;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ShowTeam extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private FirebaseFirestore firebaseFirestore;
    private RecyclerView recyclerView;
    private TeamAdapter teamAdapter;
    private List<TeamPost> teamList;
    private SearchView searchView;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_team);

        firebaseFirestore = FirebaseFirestore.getInstance();

        searchView = findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(this);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        teamList = new ArrayList<>();
        teamAdapter = new TeamAdapter(teamList, this);
        recyclerView.setAdapter(teamAdapter);

        getData(); // İlk veri çekme işlemi

        findViewById(R.id.playersButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                teamList.clear();
                teamAdapter.notifyDataSetChanged();
                Intent intent = new Intent(ShowTeam.this, ShowPlayer.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.ekleButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                teamList.clear();
                teamAdapter.notifyDataSetChanged();
                Intent intent = new Intent(ShowTeam.this, UploadTeam.class);
                startActivity(intent);
            }
        });
    }

    // Tüm oyuncuları getirir
    public void getData() {
        firebaseFirestore.collection("Teams")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Toast.makeText(ShowTeam.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (value != null) {
                            teamList.clear(); // Yeni verileri eklemeden önce listeyi temizle
                            for (DocumentSnapshot snapshot : value.getDocuments()) {
                                Map<String, Object> data = snapshot.getData();
                                if (data != null) {
                                    String id = snapshot.getId();
                                    String takim_isim = (String) data.get("name");


                                    String teamPhoto = (String) data.get("teamPhoto");

                                    TeamPost teamPost = new TeamPost(id,takim_isim,teamPhoto);
                                    teamList.add(teamPost);
                                }
                            }
                            teamAdapter.notifyDataSetChanged();
                        }
                    }
                });
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        String searchQuery = normalizeText(newText.trim()); // Küçük harfe çevir ve normalize et

        if (searchQuery.isEmpty()) {
            teamList.clear();
            teamAdapter.notifyDataSetChanged();
            getData(); // Eğer arama kutusu boşsa tüm veriyi getir
        } else {
            searchTeams(searchQuery);
        }
        return false;
    }


    private String normalizeText(String text) {
        return text.toLowerCase()
                .replace("ç", "c")
                .replace("ş", "s")
                .replace("ı", "i")
                .replace("ğ", "g")
                .replace("ü", "u")
                .replace("ö", "o");
    }


    private void searchTeams(String searchQuery) {
        String normalizedQuery = normalizeText(searchQuery); // Arama sorgusunu normalize et

        firebaseFirestore.collection("Teams")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    teamList.clear(); // Yeni verileri eklemeden önce listeyi temizle
                    for (DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments()) {
                        Map<String, Object> data = snapshot.getData();
                        if (data != null) {
                            String id = snapshot.getId();
                            String takim_isim = (String) data.get("name");

                            if (takim_isim != null && normalizeText(takim_isim).contains(normalizedQuery)) { // İsmi normalize et ve arama yap
                                String teamPhoto = (String) data.get("teamPhoto");

                                TeamPost teamPost = new TeamPost(id, takim_isim, teamPhoto);
                                teamList.add(teamPost);
                            }
                        }
                    }
                    teamAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Toast.makeText(ShowTeam.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show());
    }

}

