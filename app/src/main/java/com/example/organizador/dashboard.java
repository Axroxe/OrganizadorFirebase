package com.example.organizador;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class dashboard extends AppCompatActivity {

    private TextView titulo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        LinearLayout evento = (LinearLayout) findViewById(R.id.new_evento);
        evento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), evento.class);
                startActivityForResult(intent,0);
            }
        });

        titulo = findViewById(R.id.titulo);

        titulo.setText("Bienvenido " + getIntent().getStringExtra("usuario"));

        Bundle parametros = this.getIntent().getExtras();
        if(parametros !=null){
            String mensaje = parametros.getString("Mensaje");
            Toast.makeText(getApplicationContext(),mensaje, Toast.LENGTH_SHORT).show();
        }
    }
}
