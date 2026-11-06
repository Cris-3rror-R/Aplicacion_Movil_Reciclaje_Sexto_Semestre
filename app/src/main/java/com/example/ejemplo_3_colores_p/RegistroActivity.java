package com.example.ejemplo_3_colores_p;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Pattern;

public class RegistroActivity extends AppCompatActivity {

    private TextInputEditText textPwd_text;
    private TextInputEditText textPwd_text2;
    private TextInputEditText textEmail_text;
    TextInputLayout textPwd_Cont, textPwd_Cont2;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);
        textEmail_text = (TextInputEditText) findViewById(R.id.textEmail_text);
        textPwd_text = (TextInputEditText) findViewById(R.id.textPwd_text);
        textPwd_text2 = (TextInputEditText) findViewById(R.id.textPwd_textR);
        textPwd_Cont = findViewById(R.id.textPwd_Cont);
        textPwd_Cont2 = findViewById(R.id.textPwd_ContR);

        auth = FirebaseAuth.getInstance();

        // visibilidadIcono(textPwd_Cont,textPwd_text);
        // visibilidadIcono(textPwd_Cont2,textPwd_text2);
        validacionPassword(textPwd_text);
        validacionPassword(textPwd_text2);
    }

    public void registrarUsuario1(View v){
        Toast.makeText(this, " Se ha registrado al usuario ", Toast.LENGTH_LONG).show();
    }


    private void visibilidadIcono(TextInputLayout tpc, TextInputEditText tpt){
        tpc.setStartIconOnClickListener(v -> {
            if (tpt.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
                // Mostrar la contraseña
                tpt.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                // Cambia icono a "ver"
                tpc.setStartIconDrawable(R.drawable.retorn);
            } else {
                // Ocultar la contraseña
                tpt.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                // Cambia icono a "ocultar"
                tpc.setStartIconDrawable(R.drawable.paloma);
            }
            // Mueve el cursor al final del texto después de cambiar el tipo de entrada
            tpt.setSelection(tpt.getText().length());
        });
    }

    private void validacionPassword(TextInputEditText t) {
        t.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) { // Solo valida cuando el usuario deja el campo
                String password = t.getText().toString().trim();
                if (password.isEmpty()) {
                    t.setError("La contraseña no puede estar vacía");
                } else if (password.length() < 8) {
                    t.setError("Debe tener al menos 8 caracteres");
                } else {
                    pwdValido(t, password);
                }
            }
        });
    }

    private boolean pwdValido(TextInputEditText t,String p) {
        Pattern patron = Pattern.compile
                ("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[_@#$%^&+=?])(?=\\S+$).{8,}$");
        if (!patron.matcher(p).matches()) {
            t.setError("Debe tener mayus, minus, números y algún símbolo:_@#$%^&+=?");
            return false;
        } else {
            t.setError(null); // Borra el error si es válido
        }
        return true;
    }

    private boolean pwdIgual(String p,String q) {
        if (!p.equals(q)) {
            textPwd_text2.setError("Las contraseñas no coinciden");
            return false;
        } else {
            textPwd_text2.setError(null);
        }
        return true;
    }

    private boolean correoValido(String correo) {
        if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            textEmail_text.setError("El correo electrónico es inválido");
            return false;
        } else {
            textEmail_text.setError(null);
        }
        return true;
    }

    public void registrarUsuario(View v) {
        // Obtenemos los textos y usamos trim() para limpiar espacios accidentales
        String correo = textEmail_text.getText().toString().trim();
        String pwd = textPwd_text.getText().toString().trim();
        String pwd2 = textPwd_text2.getText().toString().trim();

        // Validamos cada campo individualmente para que se muestren todos los errores
        boolean esCorreoOk = correoValido(correo);
        boolean sonPwdsIguales = pwdIgual(pwd, pwd2);
        boolean esPwdOk = pwdValido(textPwd_text, pwd);

        if (esCorreoOk && sonPwdsIguales && esPwdOk) {
            // Intentar crear usuario en Firebase
            auth.createUserWithEmailAndPassword(correo, pwd).addOnCompleteListener(
                    RegistroActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(RegistroActivity.this, "Se ha registrado al usuario " + correo, Toast.LENGTH_LONG).show();
                                startActivity(new Intent(RegistroActivity.this, LoginActivity.class));
                                finish();
                            } else {
                                // Mostramos el error específico de Firebase (ej: correo ya registrado, sin internet, etc.)
                                String mensajeError = task.getException() != null ? task.getException().getMessage() : "Error desconocido";
                                Toast.makeText(RegistroActivity.this, "Fallo en Firebase: " + mensajeError, Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        } else {
            // Si las validaciones locales fallan, damos un aviso
            Toast.makeText(this, "Por favor, corrige los errores en el formulario", Toast.LENGTH_SHORT).show();
        }
    }
}