package com.example.apppilates.Fragments;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.apppilates.AdminSQLiteOpenHelper;
import com.example.apppilates.Logica.Interfaces.IControladorClientes;
import com.example.apppilates.R;


public class AgregarEjercicioFragment extends Fragment {

    private IControladorClientes icc;
    private EditText nombre;
    private EditText descripcion;

    public AgregarEjercicioFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_agregar_ejercicio, container, false);

        nombre = view.findViewById(R.id.nombreEditText);
        descripcion = view.findViewById(R.id.descripcionEditText);

        Button crearButton = view.findViewById(R.id.crearButton);
        crearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                crearEjercicio(v);
            }
        });

        return view;
    }

    public void crearEjercicio(View view) {
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(requireContext(), "administracion", null, 1);
        SQLiteDatabase BaseDeDatos = admin.getWritableDatabase();

        String nombre_string = nombre.getText().toString();
        String descripcion_string = descripcion.getText().toString();

        if(!nombre_string.isEmpty() && !descripcion_string.isEmpty()){
            ContentValues registro = new ContentValues();

            registro.put("nombre", nombre_string);
            registro.put("descripcion", descripcion_string);

            BaseDeDatos.insert("ejercicios", null, registro);

            BaseDeDatos.close();
            nombre.setText("");
            descripcion.setText("");

            Toast.makeText(requireContext(), "Ejercicio creado con éxito", Toast.LENGTH_SHORT).show();
        } else{
            Toast.makeText(requireContext(), "Debe llenar todos los campos", Toast.LENGTH_SHORT).show();
        }
    }
}