package com.example.ejemplo_3_colores_p;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.label.ImageLabel;
import com.google.mlkit.vision.label.ImageLabeler;
import com.google.mlkit.vision.label.ImageLabeling;
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class IdentificacionActivity extends AppCompatActivity {

    private ImageView imageView;
    private TextView textLabels, textRClassification;
    private Bitmap ImgBitMap;

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    abrirCamara();
                } else {
                    Toast.makeText(this, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show();
                }
            });

    private final ActivityResultLauncher<Intent> takePictureLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Bundle extras = result.getData().getExtras();
                    if (extras != null) {
                        ImgBitMap = (Bitmap) extras.get("data");
                        if (ImgBitMap != null) {
                            imageView.setImageBitmap(ImgBitMap);
                            processImage(ImgBitMap);
                        }
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_identificacion);

        imageView = findViewById(R.id.imageView);
        textLabels = findViewById(R.id.textLabels);
        textRClassification = findViewById(R.id.textView9);
        Button BtnTomarFoto = findViewById(R.id.btnTomarFoto);

        BtnTomarFoto.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                abrirCamara();
            } else {
                requestPermissionLauncher.launch(Manifest.permission.CAMERA);
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void abrirCamara() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePictureLauncher.launch(intent);
    }

    private void processImage(Bitmap imgBitMap) {
        if (imgBitMap == null) return;

        InputImage image = InputImage.fromBitmap(imgBitMap, 0);
        ImageLabeler labeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS);

        labeler.process(image)
                .addOnSuccessListener(labels -> {
                    StringBuilder labelsText = new StringBuilder();
                    List<String> tagList = new ArrayList<>();
                    for (ImageLabel label : labels) {
                        String text = label.getText();
                        float confidence = label.getConfidence();
                        labelsText.append("- ").append(text).append(" (").append(String.format("%.2f", confidence)).append(")\n");

                        tagList.add(text.toLowerCase());
                    }
                    textLabels.setText(labelsText.toString());
                    classifyLabels(tagList);
                })
                .addOnFailureListener(e -> {
                    textLabels.setText("Error al procesar la imagen");
                    textRClassification.setText("-");
                    e.printStackTrace();
                });
    }

    private void classifyLabels(List<String> tags) {
        Set<String> Reciclable = new HashSet<>(Arrays.asList("plastic", "glass", "paper", "metal", "bottle", "can"));
        Set<String> Organico = new HashSet<>(Arrays.asList("banana", "vegetable", "food", "plant", "apple", "watermelon"));
        Set<String> Eletronico = new HashSet<>(Arrays.asList("computer", "phone", "battery", "tv", "laptop", "mouse", "keyboard"));

        String Clasificacion = "⁉️ Otros";

        for (String tag : tags) {
            if (Reciclable.contains(tag)) {
                Clasificacion = "♻️ Reciclable";
                break;
            } else if (Organico.contains(tag)) {
                Clasificacion = "🌱 Organico";
                break;
            } else if (Eletronico.contains(tag)) {
                Clasificacion = "🤖 Eletronico";
                break;
            }
        }
        textRClassification.setText(Clasificacion);
    }
}