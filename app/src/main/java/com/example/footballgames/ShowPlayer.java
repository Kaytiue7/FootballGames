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

public class ShowPlayer extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private FirebaseFirestore firebaseFirestore;
    private RecyclerView recyclerView;
    private PlayerAdapter playerAdapter;
    private List<PlayerPost> playerList;
    private SearchView searchView;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_player);

        firebaseFirestore = FirebaseFirestore.getInstance();

        searchView = findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(this);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        playerList = new ArrayList<>();
        playerAdapter = new PlayerAdapter(playerList, this);
        recyclerView.setAdapter(playerAdapter);

        getData(); // İlk veri çekme işlemi

        findViewById(R.id.teamsButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playerList.clear();
                playerAdapter.notifyDataSetChanged();
                Intent intent = new Intent(ShowPlayer.this, ShowTeam.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.ekleButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playerList.clear();
                playerAdapter.notifyDataSetChanged();
                Intent intent = new Intent(ShowPlayer.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    // Tüm oyuncuları getirir
    public void getData() {
        firebaseFirestore.collection("Players")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Toast.makeText(ShowPlayer.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (value != null) {
                            playerList.clear(); // Yeni verileri eklemeden önce listeyi temizle
                            for (DocumentSnapshot snapshot : value.getDocuments()) {
                                Map<String, Object> data = snapshot.getData();
                                if (data != null) {
                                    String id = snapshot.getId();
                                    String isim = (String) data.get("name");
                                    String takim = (String) data.get("team");
                                    String reyting = data.get("reyting") != null ? String.valueOf(((Long) data.get("reyting")).intValue()) : "";
                                    String playerPhoto = (String) data.get("playerPhoto");

                                    PlayerPost playerPost = new PlayerPost(id, isim, takim, reyting, playerPhoto);
                                    playerList.add(playerPost);
                                }
                            }
                            playerAdapter.notifyDataSetChanged();
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
            playerList.clear();
            playerAdapter.notifyDataSetChanged();
            getData(); // Eğer arama kutusu boşsa tüm veriyi getir
        } else {
            searchPlayers(searchQuery);
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


    private void searchPlayers(String searchQuery) {
        String normalizedQuery = normalizeText(searchQuery); // Arama sorgusunu normalize et

        firebaseFirestore.collection("Players")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    playerList.clear(); // Yeni verileri eklemeden önce listeyi temizle
                    for (DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments()) {
                        Map<String, Object> data = snapshot.getData();
                        if (data != null) {
                            String id = snapshot.getId();
                            String isim = (String) data.get("name");

                            if (isim != null && normalizeText(isim).contains(normalizedQuery)) { // İsmi normalize et ve arama yap
                                String takim = (String) data.get("team");
                                String reyting = data.get("reyting") != null ? String.valueOf(((Long) data.get("reyting")).intValue()) : "";
                                String playerPhoto = (String) data.get("playerPhoto");

                                PlayerPost playerPost = new PlayerPost(id, isim, takim, reyting, playerPhoto);
                                playerList.add(playerPost);
                            }
                        }
                    }
                    playerAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Toast.makeText(ShowPlayer.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show());
    }

}

