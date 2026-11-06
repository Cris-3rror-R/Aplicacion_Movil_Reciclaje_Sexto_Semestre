package com.example.ejemplo_3_colores_p;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {

    TextInputEditText EmailText;
    TextInputEditText PasswordText;
    FirebaseAuth Authe;
    String usuario;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        EmailText = (TextInputEditText) findViewById(R.id.emailText);
        PasswordText = (TextInputEditText) findViewById(R.id.pwdText);
        Authe = FirebaseAuth.getInstance();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void abrirRegistro(View v){
        Intent i = new Intent(this, RegistroActivity.class);
        startActivity(i);
    }

    public void abrirReset(View v){
        Intent i = new Intent(this, ResetActivity.class);
        startActivity(i);
    }

    public void abrirMenu(View v){
        String email = EmailText.getText().toString().trim();
        String pwd = PasswordText.getText().toString().trim();

        if (email.isEmpty() || pwd.isEmpty()) {
            Toast.makeText(this, "Ingresa correo y contraseña", Toast.LENGTH_SHORT).show();
            return;
        }

        Authe.signInWithEmailAndPassword(email, pwd).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                Intent i = new Intent(this, MenuActivity.class);
                i.putExtra("Usuario", email);
                startActivity(i);
                finish();
            } else {
                Toast.makeText(this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void Iniciar(View v) {
        String email = EmailText.getText().toString();
        String password = PasswordText.getText().toString();
        if (correoValido(email) && pwdValido(password)){
            usuario = email;
            //autenticar usuario
            Authe.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(LoginActivity.this,
                            new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (!task.isSuccessful()) {
                                        if (password.length() < 8) {
                                            PasswordText.setError("Contraseña demasiado corta, " +
                                                    "ingrese un mínimo de 8 caracteres");
                                        } else {
                                            Toast.makeText(LoginActivity.this,
                                                    "Falló la autenticación, " +
                                                            "revise su correo electrónico" +
                                                            " y contraseña o regístrese",
                                                    Toast.LENGTH_LONG).show();
                                        }
                                    }
                                    else{
                                        Toast.makeText(LoginActivity.this,
                                                "¡Bienvenido!",
                                                Toast.LENGTH_LONG).show();
                                        abrirMenu(v);
                                    }
                                }
                            });
        }
    }

    private boolean pwdValido(String p) {
        Pattern patron = Pattern.compile
                ("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[_@#$%^&+=?])(?=\\S+$).{8,}$");
        if (!patron.matcher(p).matches()) {
            PasswordText.setError("El password es inválido");
            return false;
        } else {
            PasswordText.setError(null);
        }
        return true;
    }

    private boolean correoValido(String correo) {
        if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            EmailText.setError("El correo electrónico es inválido");
            return false;
        } else {
            EmailText.setError(null);
        }
        return true;
    }

}