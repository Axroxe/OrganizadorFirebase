package com.example.organizador;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.renderscript.ScriptGroup;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.example.organizador.models.persona;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private List<persona> lista = new ArrayList<persona>();
    ArrayAdapter<persona> personaArrayAdapter;

    private Button ingresar, modificar, eliminar, login, registro_usuario;
    private TextView registrar;
    private ListView lista_personas;

    private FirebaseAuth mAuth;

    EditText usuario, contraseña;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    persona personaSeleccionada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initControl();
    }

    private void initControl() {
        ingresar = ( Button ) findViewById(R.id.ingresar);
        modificar = ( Button ) findViewById(R.id.modificar);
        registrar = ( TextView ) findViewById(R.id.registrar);
        eliminar = ( Button ) findViewById(R.id.eliminar);
        login = ( Button ) findViewById(R.id.login);
        registro_usuario = ( Button ) findViewById(R.id.registro);

        usuario = findViewById(R.id.usuario);
        contraseña = findViewById(R.id.contraseña2);
        lista_personas = findViewById(R.id.lista);

        ingresar.setOnClickListener(this);
        modificar.setOnClickListener(this);
        registrar.setOnClickListener(this);
        eliminar.setOnClickListener(this);
        login.setOnClickListener(this);
        registro_usuario.setOnClickListener(this);

        inicializarFirebase();
        listarDatos();

        mAuth = FirebaseAuth.getInstance();

        lista_personas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                personaSeleccionada = (persona) adapterView.getItemAtPosition(i);
                usuario.setText(personaSeleccionada.getUsuario());
                contraseña.setText(personaSeleccionada.getContraseña());
            }
        });
    }

    private void listarDatos() {
        databaseReference.child("Persona").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                lista.clear();
                for (DataSnapshot objSnapshot : dataSnapshot.getChildren()){
                    persona p = objSnapshot.getValue(persona.class);
                    lista.add(p);

                    personaArrayAdapter = new ArrayAdapter<persona>(MainActivity.this, android.R.layout.simple_list_item_1, lista);
                    lista_personas.setAdapter(personaArrayAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void inicializarFirebase() {
        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
//        firebaseDatabase.setPersistenceEnabled(true);
        databaseReference = firebaseDatabase.getReference();

    }

    public void onClick(View v){

        String user = usuario.getText().toString();
        String pass = contraseña.getText().toString();
        switch (v.getId()) {
            case R.id.ingresar:
                if(user.equals("") || pass.equals("")){

                    validate();

                }else{
                    persona p = new persona();
                    p.setId(UUID.randomUUID().toString());
                    p.setUsuario(user);
                    p.setContraseña(pass);
                    databaseReference.child("Persona").child(p.getId()).setValue(p);
                    clear();
                    Intent intent = new Intent(v.getContext(), dashboard.class);
                    startActivityForResult(intent,0);
                }

                break;
            case R.id.registrar:
                Intent intent2 = new Intent(v.getContext(), registro.class);
                startActivityForResult(intent2,0);
                break;
            case R.id.modificar:
                persona p = new persona();
                p.setId(personaSeleccionada.getId());
                p.setUsuario(user);
                p.setContraseña(pass);
                databaseReference.child("Persona").child(p.getId()).setValue(p);
                Toast.makeText(this, "Actualizado", Toast.LENGTH_LONG).show();
                clear();
                break;
            case R.id.eliminar:
                persona per = new persona();
                per.setId(personaSeleccionada.getId());
                databaseReference.child("Persona").child(per.getId()).removeValue();
                Toast.makeText(this, "Eliminado", Toast.LENGTH_LONG).show();
                clear();
                break;
            case R.id.registro:

                register();
                break;
            case R.id.login:
                auth();
                break;
        }
    }

    private void auth() {
        final String user = usuario.getText().toString();
        String pass = contraseña.getText().toString();

        mAuth.signInWithEmailAndPassword(user, pass).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    int pos = user.indexOf("@");
                    String user_name = user.substring(0, pos);
                    Toast.makeText(getApplicationContext(), "Bienvenido", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(getApplication(), dashboard.class);
                    intent.putExtra("usuario", user_name);
                    startActivity(intent);
                    clear();
                } else {
                    Toast.makeText(getApplicationContext(), "Credenciales incorrectas", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void register() {

        String user = usuario.getText().toString();
        String pass = contraseña.getText().toString();

        mAuth.createUserWithEmailAndPassword(user, pass).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
//                    FirebaseUser user = mAuth.getCurrentUser();
                    Toast.makeText(getApplicationContext(), "Usuario registrado", Toast.LENGTH_LONG).show();
                    clear();
                } else {
                    if(task.getException() instanceof FirebaseAuthUserCollisionException){
                        Toast.makeText(getApplicationContext(), "Este usuario ya existe", Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(getApplicationContext(), "No se creo el usuario", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    private void clear() {
        usuario.setText("");
        contraseña.setText("");
    }

    private void validate() {
        String user = usuario.getText().toString();
        String pass = contraseña.getText().toString();
        if(user.equals("")){
            usuario.setError("Requerido");
        }else if(pass.equals("")){
            contraseña.setError("Requerido");
        }
    }
}
