package com.example.apppilates.Fragments;

import static java.lang.Float.parseFloat;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.apppilates.AdminSQLiteOpenHelper;
import com.example.apppilates.ClienteAdapter;
import com.example.apppilates.HistorialPagosActivity;
import com.example.apppilates.R;

import java.util.ArrayList;
import java.util.Calendar;

public class pagosFragment extends Fragment implements ClienteAdapter.OnCheckedChangeListener{

    RecyclerView lista;
    TextView saldo;
    Button boton;

    public pagosFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pagos, container, false);
        lista = view.findViewById(R.id.listaPagos);
        saldo = view.findViewById(R.id.saldoTextView);
        boton = view.findViewById(R.id.historialPagosButton);

        CrearBalanceMensual();
        saldo.setText("$" + ObtenerSaldo() + "/" + ObtenerTotal());

        ArrayList<String> nombres = obtenerListaClientes();/*
        if(nombres.isEmpty()){
            lista.setVisibility(View.GONE);
            lista.invalidate();
        } else { */
            lista.setVisibility(View.VISIBLE);
            lista.setLayoutManager(new LinearLayoutManager(getContext()));
            lista.setAdapter(new ClienteAdapter(getContext(), nombres, this));
        //}
        boton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lógica para cambiar a la actividad deseada
                Intent intent = new Intent(getActivity(), HistorialPagosActivity.class);
                startActivity(intent);
            }
        });

        //ReinicioPago();

        return view;
    }

    public void onItemCheckedChanged(String nombreCliente, boolean isChecked) {
        // Actualizar el TextView del saldo cada vez que se marca un CheckBox
        saldo.setText("$" + ObtenerSaldo() + "/" + ObtenerTotal());
    }

    public void CrearBalanceMensual(){
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(requireContext(), "administracion", null, 1);
        SQLiteDatabase BaseDeDatos = admin.getWritableDatabase();

        Calendar calendar = Calendar.getInstance();
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);

        Cursor cursor = BaseDeDatos.rawQuery("SELECT * FROM balance_mensual WHERE mes = '" + month + "' AND anio = '" + year + "'", null);

        if (cursor != null && cursor.getCount() == 0){
            ContentValues registro = new ContentValues();

            registro.put("mes", month);
            registro.put("anio", year);
            registro.put("balance", 0.0f);
            registro.put("total", ObtenerTotal());

            BaseDeDatos.insert("balance_mensual", null, registro);

            Cursor cursorClientes = BaseDeDatos.rawQuery("SELECT nombre FROM clientes", null);
            if (cursorClientes != null && cursorClientes.moveToFirst()) {
                do {
                    ContentValues pagos = new ContentValues();
                    String nombre = cursorClientes.getString(cursorClientes.getColumnIndex("nombre"));

                    pagos.put("nombre_cliente", nombre);
                    pagos.put("mes", month);
                    pagos.put("anio", year);
                    pagos.put("fecha", "");

                    BaseDeDatos.insert("pagos", null, pagos);
                } while (cursorClientes.moveToNext());
                cursorClientes.close();
            }
            cursor.close();
        }
        BaseDeDatos.close();
    }

    public ArrayList<String> obtenerListaClientes() {
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(requireContext(), "administracion", null, 1);
        SQLiteDatabase BaseDeDatos = admin.getWritableDatabase();

        Cursor fila = BaseDeDatos.rawQuery("SELECT nombre_cliente FROM pagos WHERE pagado = 0", null);
        ArrayList<String> nombres = new ArrayList<>();

        try {
            if (fila != null && fila.moveToFirst()) {
                do {
                    String nombre = fila.getString(fila.getColumnIndex("nombre_cliente"));
                    nombres.add(nombre);
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

    public String ObtenerSaldo() {
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(requireContext(), "administracion", null, 1);
        SQLiteDatabase BaseDeDatos = admin.getWritableDatabase();
        Float balance = 0.0f;

        Calendar calendar = Calendar.getInstance();
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);

        Cursor cursor = BaseDeDatos.rawQuery("SELECT balance FROM balance_mensual WHERE mes = " + month + " AND anio = " + year, null);

        if (cursor != null && cursor.moveToFirst()) {
            // Obtener la cuota como un String desde la base de datos
            balance = cursor.getFloat(cursor.getColumnIndex("balance"));

            cursor.close();
        }

        BaseDeDatos.close(); // Cerrar la base de datos
        return String.valueOf(balance);
    }

    public String ObtenerTotal() {
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(requireContext(), "administracion", null, 1);
        SQLiteDatabase BaseDeDatos = admin.getWritableDatabase();
        float total = 0.0f;

        Cursor cursor = BaseDeDatos.rawQuery("SELECT cuota FROM clientes", null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                // Obtener la cuota como un String desde la base de datos
                float cuota = cursor.getFloat(cursor.getColumnIndex("cuota"));
                total += cuota;
            } while (cursor.moveToNext());

            cursor.close();
        }

        BaseDeDatos.close(); // Cerrar la base de datos
        return String.valueOf(total);
    }
}