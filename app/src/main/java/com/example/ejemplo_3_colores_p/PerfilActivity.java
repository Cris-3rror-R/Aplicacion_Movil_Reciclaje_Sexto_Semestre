package com.example.ejemplo_3_colores_p;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class PerfilActivity extends AppCompatActivity {

    LinearLayout cardsContainer;
    private ShapeableImageView bCamara, iFoto;
    Bitmap imgBitmap;
    BottomNavigationView bottomNavigationView;
    TextView textViewName;
    private TextInputEditText nameText;
    private TextInputEditText surnamesText;
    private RadioGroup rgGenero;
    private RadioButton rbFemenino;
    private RadioButton rbMasculino;
    private TextInputEditText phoneText;
    private TextInputEditText birthdateText;
    private CheckBox boletinCB;
    private CheckBox notifyCB;
    private CheckBox personalCB;
    String varGenero, varBoletin, varNotify, varPersonal;
    FirebaseAuth auth;
    FirebaseDatabase database;
    DatabaseReference userRef;
    String usuario;
    String urlImagen;
    Perfil profile;
    String nombre;
    String apellidos;
    String telefono;
    String nacimiento;
    String foto;
    String genero;
    String boletin;
    String notificacion;
    String recomendacion;
    private int requestCode;
    private int resultCode;
    private Intent data;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);
        textViewName = findViewById(R.id.textView7);
        cardsContainer = findViewById(R.id.cardContainer);

        // Inflar diferentes tipos de cards
        //addExpandableCard(R.layout.cardpersonal);
        //addExpandableCard(R.layout.cardfavorite);
        addExpandableCard(1);
        addExpandableCard(2);

        bCamara = findViewById(R.id.iCamara);
        iFoto = findViewById(R.id.iFoto);

        //Verificar permisos para abrir la camara
        bCamara.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA}, 101);
            } else {
                abrirCamara();
            }
        });

        //Funcionalidad de bottomNavigation
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                finish();
                return true;
            } else if (itemId == R.id.nav_save) {
                guardarPerfil();
                return true;
            } else if (itemId == R.id.nav_cancel) {
                cargarDatosAnteriores();
                return true;
            } else if (itemId == R.id.nav_returned) {
                //finish();
                onBackPressed();
                return true;
            }
            return false;
        });

        //Toma el usuario (email) con el que se accedio en login
        if (getIntent().getExtras() != null) {
            usuario = getIntent().getExtras().getString("usuario");
            if (usuario == null) usuario = getIntent().getExtras().getString("Usuario");
        }

        //Firebase
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        
        if (auth.getCurrentUser() != null) {
            userRef = database.getReference("users")
                    .child(auth.getCurrentUser().getUid());

            //listener para cualquier cambio en la bd
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        cargarImagenDesdeFirestore();
                        profile = snapshot.getValue(Perfil.class);
                        if (profile != null) {
                            // Actualizar las vistas con la información del perfil
                            nameText.setText(profile.Nombre);
                            textViewName.setText(profile.Nombre);
                            surnamesText.setText(profile.Apellidos);

                            rbFemenino.setChecked("Femenino".equals(profile.Genero));
                            rbMasculino.setChecked("Masculino".equals(profile.Genero));
                            varGenero = profile.Genero;

                            phoneText.setText(profile.Telefono);
                            birthdateText.setText(profile.Nacimiento);

                            boletinCB.setChecked("Si".equals(profile.Boletin));
                            varBoletin = profile.Boletin;
                            
                            notifyCB.setChecked("Si".equals(profile.Notificacion));
                            varNotify = profile.Notificacion;
                            
                            personalCB.setChecked("Si".equals(profile.Recomendaciones));
                            varPersonal = profile.Recomendaciones;

                            urlImagen = profile.Foto;

                            //guardar datos en variables por si se cancela
                            nombre = profile.Nombre;
                            apellidos = profile.Apellidos;
                            telefono = profile.Telefono;
                            nacimiento = profile.Nacimiento;
                            foto = profile.Foto;
                            genero = profile.Genero;
                            boletin = profile.Boletin;
                            notificacion = profile.Notificacion;
                            recomendacion = profile.Recomendaciones;
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    //Manejo de errores

                }
            });
        }
    }//onCreate

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            Bundle extras = data.getExtras();
            if (extras != null) {
                imgBitmap = (Bitmap) extras.get("data");
                iFoto.setImageBitmap(imgBitmap);
            }
        }
    }


    public void cargarDatosAnteriores(){
        nameText.setText(nombre);
        textViewName.setText(nombre);
        surnamesText.setText(apellidos);

        if (genero != null){
            rbFemenino.setChecked("Femenino".equals(genero));
            rbMasculino.setChecked("Masculino".equals(genero));
        }
        else{
            rbFemenino.setChecked(false);
            rbMasculino.setChecked(false);
        }

        phoneText.setText(telefono);
        birthdateText.setText(nacimiento);

        if (boletin != null){
            boletinCB.setChecked("Si".equals(boletin));
        }
        else{
            boletinCB.setChecked(false);
        }

        if (notificacion != null) {
            notifyCB.setChecked("Si".equals(notificacion));
        }else{
            notifyCB.setChecked(false);
        }

        if (recomendacion != null) {
            personalCB.setChecked("Si".equals(recomendacion));
        }else{
            personalCB.setChecked(false);
        }

        if (foto != null) {
            cargarImagenDesdeFirestore();
        }
        else{
            iFoto.setImageResource(R.drawable.profile);
        }
    }

    public void guardarPerfil(){
        // Subir imagen a Firestore solo si se tomó una
        if (imgBitmap != null) {
            subirImagenFirestore(imgBitmap);
        }

        //Tomar valores de los componentes
        nombre = nameText.getText().toString();
        apellidos = surnamesText.getText().toString();
        telefono = phoneText.getText().toString();
        nacimiento = birthdateText.getText().toString();
        foto = urlImagen;

        //validar datos
        boolean a = nombreValido(nombre,nameText,30);
        boolean b = nombreValido(apellidos,surnamesText,30);

        //si son válidos almacenar en firebase
        //se ejecuta también addListenerForSingleValueEvent
        if (a && b){
            Perfil profile = new Perfil(nombre, apellidos, varGenero,
                    telefono, nacimiento, varBoletin, varNotify, varPersonal, usuario, foto);
            
            if (userRef != null) {
                userRef.setValue(profile).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Datos guardados exitosamente
                        textViewName.setText(nombre);
                        Toast.makeText(this, "Datos guardados", Toast.LENGTH_SHORT)
                                .show();
                    } else {
                        // Manejo de errores
                        Toast.makeText(this, "Error al guardar datos", Toast.LENGTH_SHORT)
                                .show();
                    }
                });
            }
        }
    }

    private void addExpandableCard(int i) {
        View cardView = null;
        //cardpersonal
        if (i == 1) {
            cardView = LayoutInflater.from(this).inflate(R.layout.cardpersonal,
                    cardsContainer, false);
            nameText = cardView.findViewById(R.id.nameText);
            surnamesText = cardView.findViewById(R.id.surnamesText);
            rgGenero = cardView.findViewById(R.id.rgGender);
            rbMasculino = cardView.findViewById(R.id.rMale);
            rbFemenino = cardView.findViewById(R.id.rFemenino);
            phoneText = cardView.findViewById(R.id.phoneText);
            birthdateText = cardView.findViewById(R.id.birthdateText);

            //formato y validación de tipo telefono para phoneText
            phoneText.addTextChangedListener(new TextWatcher() {
                private boolean isEditing = false;
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}

                @Override
                public void afterTextChanged(Editable s) {
                    if (isEditing) return;
                    isEditing = true;

                    String digits = s.toString().replaceAll("[^\\d]", ""); // Elimina caracteres no numéricos
                    String formatted = formatPhoneNumber(digits);
                    s.replace(0, s.length(), formatted);
                    isEditing = false;
                }

                private String formatPhoneNumber(String digits) {
                    int length = digits.length();
                    if (length > 10) digits = digits.substring(0, 10); // Máximo 10 dígitos
                    StringBuilder formatted = new StringBuilder();
                    if (length > 0) formatted.append("(").append(digits.substring(0, Math.min(3, length)));
                    if (length > 3) formatted.append(") ").append(digits.substring(3, Math.min(6, length)));
                    if (length > 6) formatted.append("-").append(digits.substring(6, Math.min(10, length)));
                    return formatted.toString();
                }
            });

            //formato y validación de tipo fecha para birthdateText
            birthdateText.addTextChangedListener(new TextWatcher() {
                private boolean isEditing = false;
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}

                @Override
                public void afterTextChanged(Editable s) {
                    if (isEditing) return;
                    isEditing = true;
                    String digits = s.toString().replaceAll("[^\\d]", ""); // Elimina caracteres no numéricos
                    String formatted = formatDate(digits);
                    s.replace(0, s.length(), formatted);
                    isEditing = false;
                }

                private String formatDate(String digits) {
                    int length = digits.length();
                    if (length > 8) digits = digits.substring(0, 8); // Máximo 8 dígitos
                    StringBuilder formatted = new StringBuilder();
                    if (length > 0) formatted.append(digits.substring(0, Math.min(2, length))); // Día
                    if (length > 2) formatted.append("/").append(digits.substring(2, Math.min(4, length))); // Mes
                    if (length > 4) formatted.append("/").append(digits.substring(4, Math.min(8, length))); // Año
                    return formatted.toString();
                }
            });

            //funcionalidad radiogroup
            rgGenero.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    if (rbMasculino.isChecked()){
                        varGenero = "Masculino";
                    }
                    if (rbFemenino.isChecked()){
                        varGenero = "Femenino";
                    }
                }
            });
        }
        //cardfavorite
        else if (i == 2) {
            cardView = LayoutInflater.from(this).inflate(R.layout.cardfavorite, cardsContainer, false);
            boletinCB = cardView.findViewById(R.id.boletinCB);
            notifyCB = cardView.findViewById(R.id.notifyCB);
            personalCB = cardView.findViewById(R.id.personalCB);

            //funcionalidad checkBox
            boletinCB.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    varBoletin = "Si";
                } else {
                    varBoletin = "No";
                }
            });

            notifyCB.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    varNotify = "Si";
                } else {
                    varNotify = "No";
                }
            });

            personalCB.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    varPersonal = "Si";
                } else {
                    varPersonal = "No";
                }
            });
        }

        //Elementos comunes en ambas tarjetas
        LinearLayout headerLayout = cardView.findViewById(R.id.headerLayout);
        LinearLayout expandableContent = cardView.findViewById(R.id.expandableContent);
        ImageView arrowIcon = cardView.findViewById(R.id.arrowIcon);

        headerLayout.setOnClickListener(v -> {
            if (expandableContent.getVisibility() == View.GONE) {
                // Expandir
                expandableContent.setVisibility(View.VISIBLE);
                expandableContent.setAlpha(0f);
                expandableContent.animate().alpha(1f).setDuration(300).start();
                rotateArrow(arrowIcon, 0f, 180f);
            } else {
                // Contraer
                expandableContent.animate().alpha(0f).setDuration(200).withEndAction(() -> {
                    expandableContent.setVisibility(View.GONE);
                }).start();
                rotateArrow(arrowIcon, 180f, 0f);
            }
        });
        cardsContainer.addView(cardView);
    }

    private boolean nombreValido(String nombre, TextView v, Integer l) {
        Pattern patron = Pattern.compile("^[a-zA-Z.-áéíóúÁÉÍÓÚñÑ ]+$");
        if (nombre == null || nombre.isEmpty() || !patron.matcher(nombre).matches() || nombre.length() > l) {
            v.setError("Nombre inválido");
            return false;
        } else {
            v.setError(null);
        }
        return true;
    }

    private void rotateArrow(ImageView arrow, float fromDegrees, float toDegrees) {
        RotateAnimation rotate = new RotateAnimation(
                fromDegrees, toDegrees,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(300);
        rotate.setFillAfter(true);
        arrow.startAnimation(rotate);
    }

    private void abrirCamara() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getApplicationContext().getPackageManager()) != null) {
            startActivityForResult(intent, 1);
        }
    }

    private void subirImagenFirestore(Bitmap bitmap) {
        if (bitmap == null) return;
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        // ID del usuario autenticado
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Convertir Bitmap a Base64
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String imagenBase64 = Base64.encodeToString(imageBytes, Base64.DEFAULT);

        // Guardar imagen en Firestore
        Map<String, Object> datosImagen = new HashMap<>();
        datosImagen.put("fotoPerfil", imagenBase64);

        db.collection("users").document(userId)
                .set(datosImagen, SetOptions.merge())
                .addOnSuccessListener(aVoid -> Log.d("FIREBASE", "Imagen guardada en Firestore"))
                .addOnFailureListener(e -> Log.e("FIREBASE", "Error al guardar imagen en Firestore", e));

        // URL lógica para recuperación de la foto
        urlImagen = "firestore://users/" + userId + "/fotoPerfil";
    }

    private void cargarImagenDesdeFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        db.collection("users").document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String imagenBase64 = documentSnapshot.getString("fotoPerfil");
                        if (imagenBase64 != null) {
                            byte[] decodedBytes = Base64.decode(imagenBase64, Base64.DEFAULT);
                            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                            iFoto.setImageBitmap(bitmap);
                        }
                    }
                })
                .addOnFailureListener(e -> Log.e("FIREBASE", "Error al recuperar la imagen", e));
    }
}