package com.example.ejemplo_3_colores_p;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

public class ResetActivity extends AppCompatActivity {
    TextInputEditText textEmail_text;
    FirebaseAuth auth;
    Button resetButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset);
        textEmail_text = findViewById(R.id.textEmail_text);
        resetButton = findViewById(R.id.loginButton);
        auth = FirebaseAuth.getInstance();



        if (resetButton != null) {
            resetButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    resetPwd(v);
                }
            });
        }
    }

    private boolean CorreoValido(String correo) {
        if (correo.isEmpty()) {
            textEmail_text.setError("El correo no puede estar vacío");
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            textEmail_text.setError("Correo electrónico inválido");
            return false;
        } else {
            textEmail_text.setError(null);
        }
        return true;
    }

    public void resetPwd(View v) {
        String email = textEmail_text.getText().toString().trim();
        if (CorreoValido(email)) {
            auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(ResetActivity.this,
                                "Te hemos enviado instrucciones para restablecer tu contraseña",
                                Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(ResetActivity.this,
                                "Error al enviar correo electrónico para reestablecer contrasena",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}