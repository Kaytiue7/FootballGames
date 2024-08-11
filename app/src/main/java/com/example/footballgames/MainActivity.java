package com.example.footballgames;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText editTextName, editTextAge, editTextPosition, editTextTeam, editTextReyting,
            editTextPace, editTextShoot, editTextHeadKick, editTextPas, editTextDribling,
            editTextPhysical, editTextDurability, editTextDefence;
    private Button buttonSubmit;
    private ImageView imageViewPlayerPhoto;
    private Uri imageUri;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // EditText ve Button bileşenlerini tanımla
        editTextName = findViewById(R.id.editTextName);
        editTextAge = findViewById(R.id.editTextAge);
        editTextPosition = findViewById(R.id.editTextPosition);
        editTextTeam = findViewById(R.id.editTextTeam);
        editTextReyting = findViewById(R.id.editTextReyting);
        editTextPace = findViewById(R.id.editTextPace);
        editTextShoot = findViewById(R.id.editTextShoot);
        editTextHeadKick = findViewById(R.id.editTextHeadKick);
        editTextPas = findViewById(R.id.editTextPas);
        editTextDribling = findViewById(R.id.editTextDribling);
        editTextPhysical = findViewById(R.id.editTextPhysical);
        editTextDurability = findViewById(R.id.editTextDurability);
        editTextDefence = findViewById(R.id.editTextDefence);
        imageViewPlayerPhoto = findViewById(R.id.imageViewPlayerPhoto);
        buttonSubmit = findViewById(R.id.buttonSubmit);

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference("playerPhotos");

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
        String age = editTextAge.getText().toString().trim();
        String position = editTextPosition.getText().toString().trim();
        String team = editTextTeam.getText().toString().trim();
        String reytingStr = editTextReyting.getText().toString().trim();
        String paceStr = editTextPace.getText().toString().trim();
        String shootStr = editTextShoot.getText().toString().trim();
        String headKickStr = editTextHeadKick.getText().toString().trim();
        String pasStr = editTextPas.getText().toString().trim();
        String driblingStr = editTextDribling.getText().toString().trim();
        String physicalStr = editTextPhysical.getText().toString().trim();
        String durabilityStr = editTextDurability.getText().toString().trim();
        String defenceStr = editTextDefence.getText().toString().trim();

        if (name.isEmpty() || age.isEmpty() || position.isEmpty() || team.isEmpty() || reytingStr.isEmpty() || paceStr.isEmpty() || shootStr.isEmpty() || headKickStr.isEmpty() || pasStr.isEmpty() || driblingStr.isEmpty() || physicalStr.isEmpty() || durabilityStr.isEmpty() || defenceStr.isEmpty()) {
            Toast.makeText(MainActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        int reyting = Integer.parseInt(reytingStr);
        int pace = Integer.parseInt(paceStr);
        int shoot = Integer.parseInt(shootStr);
        int headKick = Integer.parseInt(headKickStr);
        int pas = Integer.parseInt(pasStr);
        int dribling = Integer.parseInt(driblingStr);
        int physical = Integer.parseInt(physicalStr);
        int durability = Integer.parseInt(durabilityStr);
        int defence = Integer.parseInt(defenceStr);

        if (imageUri != null) {
            // Fotoğraf yükleme işlemi
            uploadPhotoAndSubmitData(name, age, position, team, reyting, pace, shoot, headKick, pas, dribling, physical, durability, defence);
        } else {
            // Fotoğraf seçilmemişse verileri doğrudan gönder
            submitPlayerData(name, age, position, team, reyting, pace, shoot, headKick, pas, dribling, physical, durability, defence, null);
        }
    }

    private void uploadPhotoAndSubmitData(String name, String age, String position, String team, int reyting, int pace, int shoot, int headKick, int pas, int dribling, int physical, int durability, int defence) {
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
                                    submitPlayerData(name, age, position, team, reyting, pace, shoot, headKick, pas, dribling, physical, durability, defence, photoUrl);
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MainActivity.this, "Photo upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void submitPlayerData(String name, String age, String position, String team, int reyting, int pace, int shoot, int headKick, int pas, int dribling, int physical, int durability, int defence, @Nullable String photoUrl) {
        Map<String, Object> playerData = new HashMap<>();
        playerData.put("name", name);
        playerData.put("age", age);
        playerData.put("position", position);
        playerData.put("team", team);
        playerData.put("reyting", reyting);
        playerData.put("pace", pace);
        playerData.put("shoot", shoot);
        playerData.put("headKick", headKick);
        playerData.put("pas", pas);
        playerData.put("dribling", dribling);
        playerData.put("physical", physical);
        playerData.put("durability", durability);
        playerData.put("defence", defence);
        playerData.put("playerPhoto", photoUrl);

        firebaseFirestore.collection("Players")
                .add(playerData)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(MainActivity.this, "Data submitted successfully", Toast.LENGTH_SHORT).show();
                    clearAll();
                })
                .addOnFailureListener(e -> Toast.makeText(MainActivity.this, "Data submission failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private String getFileExtension(Uri uri) {
        return getContentResolver().getType(uri).split("/")[1];
    }

    private void clearAll() {
        editTextName.setText(null);
        editTextAge.setText(null);
        editTextPosition.setText(null);
        editTextTeam.setText(null);
        editTextReyting.setText(null);
        editTextPace.setText(null);
        editTextShoot.setText(null);
        editTextHeadKick.setText(null);
        editTextPas.setText(null);
        editTextDribling.setText(null);
        editTextPhysical.setText(null);
        editTextDurability.setText(null);
        editTextDefence.setText(null);
        imageViewPlayerPhoto.setImageURI(null);
        imageUri = null;
    }
}

