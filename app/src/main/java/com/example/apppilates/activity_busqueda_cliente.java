package com.example.apppilates;

import static androidx.core.content.ContentProviderCompat.requireContext;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class activity_busqueda_cliente extends AppCompatActivity {

    FirebaseFirestore db;
    private EditText cedula;
    private EditText nombre;
    private EditText apellido;
    private EditText mutualista;
    private EditText emergencia;
    private EditText telefono;
    private EditText domicilio;
    private EditText mail;
    private EditText cuota;
    private EditText genero;
    private EditText patologias;
    private EditText lista;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_busqueda_cliente);

        cedula = findViewById(R.id.cedulaEditTextBuscar);
        nombre = findViewById(R.id.nombreEditTextBuscar);
        apellido = findViewById(R.id.apellidoEditTextBuscar);
        mutualista = findViewById(R.id.mutualistaEditTextBuscar);
        emergencia = findViewById(R.id.emergenciaEditTextBuscar);
        telefono = findViewById(R.id.telefonoEditTextBuscar);
        domicilio = findViewById(R.id.domicilioEditTextBuscar);
        mail = findViewById(R.id.mailEditTextBuscar);
        cuota = findViewById(R.id.cuotaEditTextBuscar);
        genero = findViewById(R.id.generoEditTextBuscar);
        patologias = findViewById(R.id.patologiasEditTextBuscar);
        lista = findViewById(R.id.listaClientesBuscar);

        db = FirebaseFirestore.getInstance();

        Button buscarButton = findViewById(R.id.registrarButton);
        buscarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buscarCliente();
            }
        });

        Button eliminarButton = findViewById(R.id.eliminarButton);
        eliminarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eliminarCliente();
            }
        });

        Button modificarButton = findViewById(R.id.editarButton);
        modificarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { modificarCliente();
            }
        });
    }

    public void buscarCliente(){
        String seleccion = lista.getText().toString();

        db.collection("clientes").document(seleccion).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()) {
                    cedula.setText(documentSnapshot.getString("cedula"));
                    nombre.setText(documentSnapshot.getString("nombre"));
                    apellido.setText(documentSnapshot.getString("apellido"));
                    mutualista.setText(documentSnapshot.getString("mutualista"));
                    emergencia.setText(documentSnapshot.getString("emergencia"));
                    telefono.setText(documentSnapshot.getString("telefono"));
                    domicilio.setText(documentSnapshot.getString("domicilio"));
                    mail.setText(documentSnapshot.getString("mail"));
                    cuota.setText(documentSnapshot.getString("cuota"));
                    genero.setText(documentSnapshot.getString("genero"));
                    patologias.setText(documentSnapshot.getString("patologias"));
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Manejar el caso en que la búsqueda falla
                Toast.makeText(getBaseContext(), "Esa cédula no está registrada", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void eliminarCliente(){
        String seleccion = lista.getText().toString();

        if (!seleccion.isEmpty()) {
            db.collection("clientes").document(seleccion).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(getApplicationContext(), "Cliente eliminado con éxito", Toast.LENGTH_SHORT).show();
                    cedula.setText("");
                    nombre.setText("");
                    apellido.setText("");
                    mutualista.setText("");
                    emergencia.setText("");
                    telefono.setText("");
                    domicilio.setText("");
                    mail.setText("");
                    cuota.setText("");
                    genero.setText("");
                    patologias.setText("");
                }
            });

        } else {
            Toast.makeText(this, "Debe seleccionar un cliente", Toast.LENGTH_SHORT).show();
        }
    }

    public void modificarCliente() {
        String seleccion = lista.getText().toString();

        Map<String, Object> user = new HashMap<>();
        user.put("cedula", cedula.getText().toString());
        user.put("nombre", nombre.getText().toString());
        user.put("apellido", apellido.getText().toString());
        user.put("mutualista", mutualista.getText().toString());
        user.put("emergencia", emergencia.getText().toString());
        user.put("telefono", telefono.getText().toString());
        user.put("domicilio", domicilio.getText().toString());
        user.put("mail", mail.getText().toString());
        user.put("cuota", cuota.getText().toString());
        user.put("genero", genero.getText().toString());
        user.put("patologias", patologias.getText().toString());

        if (!seleccion.isEmpty()) {
            db.collection("clientes").document(seleccion).update(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(getApplicationContext(), "Datos actualizados con éxito", Toast.LENGTH_SHORT).show();
                }
            });

        }
    }
}