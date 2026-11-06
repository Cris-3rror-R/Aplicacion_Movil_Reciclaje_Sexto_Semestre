package com.example.ejemplo_3_colores_p;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

public class CentroActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_centro);
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        RecyclerView recycler = findViewById(R.id.recyclerCentros);
        if (recycler != null) {
            recycler.setLayoutManager(new LinearLayoutManager(this));
            recycler.setAdapter(new CentroAdapter(obtenerCentros(), this::mostrarDetalle));
        }
    }

    private void mostrarDetalle(CentroAdapter.Centro centro) {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View sheetView = LayoutInflater.from(this).inflate(android.R.layout.simple_list_item_2, null);
        sheetView.setBackgroundResource(R.drawable.recuadro2);
        sheetView.setPadding(40, 40, 40, 40);

        TextView tv1 = sheetView.findViewById(android.R.id.text1);
        TextView tv2 = sheetView.findViewById(android.R.id.text2);

        tv1.setText(centro.nombre);
        tv1.setTextSize(20);
        tv1.setTextColor(getResources().getColor(R.color.MD_Theme_On_PrimaryContainer));

        tv2.setText(centro.direccion + "\n\n🕐 " + centro.horario + "\n♻ " + centro.tipos + "\n\n(Toca para abrir en Google Maps)");
        tv2.setTextColor(getResources().getColor(R.color.MD_Theme_PrimaryContainer));
        
        sheetView.setOnClickListener(v -> {
            dialog.dismiss();
            abrirMaps(centro.direccion);
        });

        dialog.setContentView(sheetView);
        dialog.show();
    }

    private void abrirMaps(String direccion) {
        Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + Uri.encode(direccion));
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapIntent);
        } else {
            Uri webUri = Uri.parse("https://maps.google.com/?q=" + Uri.encode(direccion));
            startActivity(new Intent(Intent.ACTION_VIEW, webUri));
        }
    }

    private List<CentroAdapter.Centro> obtenerCentros() {
        List<CentroAdapter.Centro> lista = new ArrayList<>();
        lista.add(new CentroAdapter.Centro(
                "Comercializadora y Recicladora La Rivera SA de CV",
                "C. La Paz 16, Torrecillas, 47899 Ocotlán, Jal.",
                "Lun–Sáb 7:30–16:30",
                "Chatarra, Aluminio, Cobre"));
        lista.add(new CentroAdapter.Centro(
                "RECICLABLES HERNANDEZ",
                "PAULINO NAVARRO 27, Paulino Navarro 40, 47829 Ocotlán, Jal.",
                "Lun–Sáb 7:30–19:30",
                "Chatarra, Aluminio, Cobre"));
        lista.add(new CentroAdapter.Centro(
                "Punto Limpio Ocotlán",
                "Parque Metropolitano, Ocotlán, Jalisco",
                "Abierto 24h",
                "Residuos domésticos separados"));
        return lista;
    }

    public void abrirMenu(View V){
        Intent I = new Intent(this, MenuActivity.class);
        startActivity(I);
    }

    public static class CentroAdapter extends RecyclerView.Adapter<CentroAdapter.ViewHolder> {
        public static class Centro {
            public String nombre, direccion, horario, tipos;
            public Centro(String nombre, String direccion, String horario, String tipos) {
                this.nombre = nombre;
                this.direccion = direccion;
                this.horario = horario;
                this.tipos = tipos;
            }
        }
        public interface OnCentroClickListener { void onClick(Centro centro); }
        private final List<Centro> centros;
        private final OnCentroClickListener listener;
        public CentroAdapter(List<Centro> centros, OnCentroClickListener listener) {
            this.centros = centros;
            this.listener = listener;
        }
        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_2, parent, false);
            return new ViewHolder(view);
        }
        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Centro c = centros.get(position);
            holder.text1.setText(c.nombre);
            holder.text2.setText(c.direccion);
            holder.itemView.setOnClickListener(v -> listener.onClick(c));
        }
        @Override
        public int getItemCount() { return centros.size(); }
        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView text1, text2;
            ViewHolder(View itemView) {
                super(itemView);
                text1 = itemView.findViewById(android.R.id.text1);
                text2 = itemView.findViewById(android.R.id.text2);
                text1.setTextColor(itemView.getResources().getColor(R.color.MD_Theme_On_Secundary));
                text2.setTextColor(itemView.getResources().getColor(R.color.MD_Theme_On_PrimaryContainer));
            }
        }
    }
}