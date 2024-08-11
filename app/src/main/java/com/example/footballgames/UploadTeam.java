package com.example.footballgames;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class UploadTeam extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText editTextName;
    private Button buttonSubmit;
    private ImageView imageViewPlayerPhoto;
    private Uri imageUri;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_team);

        // EditText ve Button bileşenlerini tanımla
        editTextName = findViewById(R.id.editTextName);

        imageViewPlayerPhoto = findViewById(R.id.imageViewTeamPhoto);
        buttonSubmit = findViewById(R.id.buttonSubmit);

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference("teamPhotos");

        imageViewPlayerPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("MainActivity", "Button clicked");
                submitData();
            }
        });
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            imageViewPlayerPhoto.setImageURI(imageUri);
        }
    }

    private void submitData() {
        String name = editTextName.getText().toString().trim();


        if (name.isEmpty() ) {
            Toast.makeText(UploadTeam.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }



        if (imageUri != null) {
            // Fotoğraf yükleme işlemi
            uploadPhotoAndSubmitData(name);
        } else {
            // Fotoğraf seçilmemişse verileri doğrudan gönder
            submitPlayerData(name, null);
        }
    }

    private void uploadPhotoAndSubmitData(String name) {
        if (imageUri != null) {
            StorageReference fileReference = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(imageUri));

            fileReference.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String photoUrl = uri.toString();
                                    submitPlayerData(name, photoUrl);
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(UploadTeam.this, "Photo upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void submitPlayerData(String name, @Nullable String photoUrl) {
        Map<String, Object> playerData = new HashMap<>();
        playerData.put("name", name);

        playerData.put("teamPhoto", photoUrl);

        firebaseFirestore.collection("Teams")
                .add(playerData)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(UploadTeam.this, "Data submitted successfully", Toast.LENGTH_SHORT).show();
                    clearAll();
                })
                .addOnFailureListener(e -> Toast.makeText(UploadTeam.this, "Data submission failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private String getFileExtension(Uri uri) {
        return getContentResolver().getType(uri).split("/")[1];
    }

    private void clearAll() {
        editTextName.setText(null);
        imageUri = null;
    }
}

