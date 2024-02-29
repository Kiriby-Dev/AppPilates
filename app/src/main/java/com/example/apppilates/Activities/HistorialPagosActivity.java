package com.example.apppilates.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.apppilates.Adapters.ClienteAdapter;
import com.example.apppilates.AdminSQLiteOpenHelper;
import com.example.apppilates.Adapters.HistorialAdapter;
import com.example.apppilates.Cliente;
import com.example.apppilates.Fragments.pagosFragment;
import com.example.apppilates.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class HistorialPagosActivity extends AppCompatActivity {

    FirebaseFirestore db;
    RecyclerView lista;
    TextView texto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial_pagos);

        lista = findViewById(R.id.listaPagados);
        texto = findViewById(R.id.historialTextView);

        db = FirebaseFirestore.getInstance();

        obtenerListaClientesPagos(new FirestoreCallback() {
            @Override
            public void onCallback(ArrayList<Cliente> clientes) {
                // Se llama a este método cuando se han obtenido los datos de Firestore
                lista.setVisibility(View.VISIBLE);
                lista.setLayoutManager(new LinearLayoutManager(HistorialPagosActivity.this));
                lista.setAdapter(new HistorialAdapter(HistorialPagosActivity.this, clientes));
            }
        });
    }

    public void obtenerListaClientesPagos(FirestoreCallback callback) {
        db.collection("pagos").whereEqualTo("pagado", true).get().addOnSuccessListener(queryDocumentSnapshots -> {
            ArrayList<Cliente> clientes = new ArrayList<>();
            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                String cedula = document.getString("cedula");
                String fecha = document.getString("fecha");
                db.collection("clientes").whereEqualTo("cedula", cedula).get().addOnSuccessListener(clienteQuerySnapshot -> {
                    for (QueryDocumentSnapshot clienteDocument : clienteQuerySnapshot) {
                        String nombre = clienteDocument.getString("nombre");
                        String cuota = clienteDocument.getString("cuota");
                        Cliente cliente = new Cliente(nombre, cedula, cuota, fecha);
                        clientes.add(cliente);
                    }
                    // Ordenar los clientes por fecha
                    Collections.sort(clientes, new Comparator<Cliente>() {
                        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

                        @Override
                        public int compare(Cliente cliente1, Cliente cliente2) {
                            try {
                                Date fechaCliente1 = dateFormat.parse(cliente1.getFecha());
                                Date fechaCliente2 = dateFormat.parse(cliente2.getFecha());
                                return fechaCliente1.compareTo(fechaCliente2);
                            } catch (ParseException e) {
                                e.printStackTrace();
                                return 0;
                            }
                        }
                    });
                    callback.onCallback(clientes);
                });
            }
        });
    }

    interface FirestoreCallback {
        void onCallback(ArrayList<Cliente> clientes);
    }
}