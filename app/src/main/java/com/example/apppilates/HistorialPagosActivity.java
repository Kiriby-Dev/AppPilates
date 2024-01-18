package com.example.apppilates;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

public class HistorialPagosActivity extends AppCompatActivity {

    RecyclerView lista;
    TextView texto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial_pagos);

        lista = findViewById(R.id.listaPagados);
        texto = findViewById(R.id.historialTextView);

        ArrayList<String> nombres = obtenerListaClientesPagos();

        lista.setVisibility(View.VISIBLE);
        lista.setLayoutManager(new LinearLayoutManager(this));
        lista.setAdapter(new HistorialAdapter(this, nombres));
    }

    public ArrayList<String> obtenerListaClientesPagos() {
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "administracion", null, 1);
        SQLiteDatabase BaseDeDatos = admin.getWritableDatabase();

        Cursor fila = BaseDeDatos.rawQuery("SELECT * FROM pagos WHERE pagado = 1 ORDER BY anio DESC, mes DESC", null);
        ArrayList<String> nombres = new ArrayList<>();

        try {
            if (fila != null && fila.moveToFirst()) {
                do {
                    String nombre = fila.getString(fila.getColumnIndex("nombre_cliente"));
                    String fecha = fila.getString(fila.getColumnIndex("fecha"));
                    Cursor datosCliente = BaseDeDatos.rawQuery("SELECT cuota FROM clientes WHERE nombre = '" + nombre + "'", null);
                    if (datosCliente != null && datosCliente.moveToFirst()) {
                        String cuota = datosCliente.getString(datosCliente.getColumnIndex("cuota"));
                        nombres.add(nombre + " - $" + cuota + " - " + fecha);
                    }
                    datosCliente.close();
                } while (fila.moveToNext());
            }
        } finally {
            if (fila != null) {
                fila.close();
            }
            BaseDeDatos.close(); // Cerrar la base de datos
        }

        return nombres;
    }
}