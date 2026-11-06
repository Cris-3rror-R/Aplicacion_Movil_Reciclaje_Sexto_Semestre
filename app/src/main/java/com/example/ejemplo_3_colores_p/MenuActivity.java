package com.example.ejemplo_3_colores_p;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MenuActivity extends AppCompatActivity {

    String Usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_menu);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            Usuario = getIntent().getExtras().getString("Usuario");
            return insets;
        });
    }
    public void abrirPerfil(View V){
        Intent I = new Intent(this,PerfilActivity.class);
        I.putExtra("Usuario", Usuario);
        startActivity(I);

    }
    public void abrirIdentificar(View V){
        Intent I = new Intent(this,IdentificacionActivity.class);
        startActivity(I);

    }

    public void cerrarSesion(View V){
        Toast.makeText(this, "Hasta pronto.........", Toast.LENGTH_SHORT).show();
        finishAffinity();
    }

    public void abrirCentro(View V){
        Intent I = new Intent(this,CentroActivity.class);
        startActivity(I);

    }

    public void abrirGuia(View V){
        Intent I = new Intent(this,GuiaActivity.class);
        startActivity(I);

    }

    public void abrirConsejo(View V){
        Intent I = new Intent(this,ConsejoActivity.class);
        startActivity(I);

    }


}