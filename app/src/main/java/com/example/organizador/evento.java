package com.example.organizador;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TimePicker;

import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;

public class evento extends AppCompatActivity implements View.OnClickListener{

    private static final String CERO = "0";
    private static final String BARRA = "/";
    private static final String DOS_PUNTOS = ":";

    public final Calendar c = Calendar.getInstance();

    final int mes = c.get(Calendar.MONTH);
    final int dia = c.get(Calendar.DAY_OF_MONTH);
    final int anio = c.get(Calendar.YEAR);

    final int hora = c.get(Calendar.HOUR_OF_DAY);
    final int minuto = c.get(Calendar.MINUTE);

    TextInputEditText fecha, hora_evento;
    AutoCompleteTextView menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evento);

        fecha = (TextInputEditText) findViewById(R.id.fecha);
        fecha.setOnClickListener(this);

        hora_evento = (TextInputEditText) findViewById(R.id.hora_evento);
        hora_evento.setOnClickListener(this);

        Button registrar = ( Button ) findViewById(R.id.registrar);
        registrar.setOnClickListener(this);

        String[] COUNTRIES = new String[] {"Item 1", "Item 2", "Item 3", "Item 4"};

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        this,
                        R.layout.item,
                        COUNTRIES);

        AutoCompleteTextView editTextFilledExposedDropdown = findViewById(R.id.filled_exposed_dropdown);
        editTextFilledExposedDropdown.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fecha:
                obtenerFecha();
                break;
            case R.id.hora_evento:
                obtenerHora();
                break;
            case R.id.registrar:
                Intent intent2 = new Intent(v.getContext(), dashboard.class);
                intent2.putExtra("Mensaje", "Evento creado con exito.");
                startActivityForResult(intent2,0);
                break;
        }
    }

    private void obtenerFecha(){
        DatePickerDialog recogerFecha = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                final int mesActual = month + 1;
                String diaFormateado = (dayOfMonth < 10)? CERO + String.valueOf(dayOfMonth):String.valueOf(dayOfMonth);
                String mesFormateado = (mesActual < 10)? CERO + String.valueOf(mesActual):String.valueOf(mesActual);
                fecha.setText(diaFormateado + BARRA + mesFormateado + BARRA + year);


            }
            //Estos valores deben ir en ese orden, de lo contrario no mostrara la fecha actual
            /**
             *También puede cargar los valores que usted desee
             */
        },anio, mes, dia);
        recogerFecha.show();
    }

    private void obtenerHora(){
        TimePickerDialog recogerHora = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                String horaFormateada =  (hourOfDay < 10)? String.valueOf(CERO + hourOfDay) : String.valueOf(hourOfDay);
                String minutoFormateado = (minute < 10)? String.valueOf(CERO + minute):String.valueOf(minute);
                String AM_PM;
                if(hourOfDay < 12) {
                    AM_PM = "a.m.";
                } else {
                    AM_PM = "p.m.";
                }
                hora_evento.setText(horaFormateada + DOS_PUNTOS + minutoFormateado + " " + AM_PM);
            }
            //Estos valores deben ir en ese orden
            //Al colocar en false se muestra en formato 12 horas y true en formato 24 horas
            //Pero el sistema devuelve la hora en formato 24 horas
        }, hora, minuto, false);
        recogerHora.show();
    }
}
