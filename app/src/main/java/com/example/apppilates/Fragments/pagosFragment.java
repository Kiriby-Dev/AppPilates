package com.example.apppilates.Fragments;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.apppilates.AdminSQLiteOpenHelper;
import com.example.apppilates.ClienteAdapter;
import com.example.apppilates.R;

import java.util.ArrayList;
import java.util.Calendar;

public class pagosFragment extends Fragment {

    RecyclerView lista;
    TextView saldo;

    public pagosFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pagos, container, false);
        lista = view.findViewById(R.id.listaPagos);
        saldo = view.findViewById(R.id.saldoTextView);

        saldo.setText("$" + ObtenerSaldo());

        ArrayList<String> nombres = obtenerListaClientes();

        lista.setLayoutManager(new LinearLayoutManager(getContext()));
        lista.setAdapter(new ClienteAdapter(getContext(), nombres));

        ReinicioPago();

        return view;
    }


    public ArrayList<String> obtenerListaClientes() {
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(requireContext(), "administracion", null, 1);
        SQLiteDatabase BaseDeDatos = admin.getWritableDatabase();

        Cursor fila = BaseDeDatos.rawQuery("SELECT nombre FROM clientes", null);
        ArrayList<String> nombres = new ArrayList<>();

        if (fila != null && fila.moveToFirst()) {
            do {
                String nombre = fila.getString(fila.getColumnIndex("nombre"));
                nombres.add(nombre);
            } while (fila.moveToNext());

            fila.close();
        } else {
            // No se encontraron datos, agregar cadena vacía si se necesita
            nombres.add("");
        }

        BaseDeDatos.close(); // Cerrar la base de datos

        return nombres;
    }

    public String ObtenerSaldo() {
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(requireContext(), "administracion", null, 1);
        SQLiteDatabase BaseDeDatos = admin.getWritableDatabase();
        float valorSaldo = 0.0f; // Inicializar el saldo en 0

        Cursor cursor = BaseDeDatos.rawQuery("SELECT cuota FROM clientes WHERE pago=1", null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                // Obtener la cuota como un String desde la base de datos
                String cuotaString = cursor.getString(cursor.getColumnIndex("cuota"));

                // Convertir el valor de la cuota a un número flotante
                float cuota = Float.parseFloat(cuotaString);

                // Sumar la cuota al saldo
                valorSaldo += cuota;
            } while (cursor.moveToNext());

            cursor.close();
        }

        BaseDeDatos.close(); // Cerrar la base de datos
        return String.valueOf(valorSaldo);
    }

    public void ReinicioPago() {
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        if (day == 1) {
            AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(requireContext(), "administracion", null, 1);
            SQLiteDatabase BaseDeDatos = admin.getWritableDatabase();

            ContentValues valores = new ContentValues();
            valores.put("pago", 0);

            BaseDeDatos.update("clientes", valores, null, null);
        }
    }

}