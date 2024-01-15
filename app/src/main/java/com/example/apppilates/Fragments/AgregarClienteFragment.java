package com.example.apppilates.Fragments;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.apppilates.AdminSQLiteOpenHelper;
import com.example.apppilates.Logica.Fabrica;
import com.example.apppilates.Logica.Interfaces.IControladorClientes;
import com.example.apppilates.R;

public class AgregarClienteFragment extends Fragment {

    private IControladorClientes icc;
    private EditText nombre;
    private EditText mutualista;
    private EditText telefono;
    private EditText cuota;
    private EditText patologias;

    public AgregarClienteFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_agregar_cliente, container, false);

        nombre = view.findViewById(R.id.nombreEditText);
        mutualista = view.findViewById(R.id.mutualistaEditText);
        telefono = view.findViewById(R.id.telefonoEditText);
        cuota = view.findViewById(R.id.cuotaEditText);
        patologias = view.findViewById(R.id.patologiasEditText);

        Button buscarButton = view.findViewById(R.id.registrarButton);
        buscarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ingresarCliente(v);
            }
        });

        return view;
    }

    public void ingresarCliente(View view) {
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(requireContext(), "administracion", null, 1);
        SQLiteDatabase BaseDeDatos = admin.getWritableDatabase();

        String nombre_string = nombre.getText().toString();
        String mutualista_string = mutualista.getText().toString();
        String telefono_string = telefono.getText().toString();
        String cuota_string = cuota.getText().toString();
        String patologias_string = patologias.getText().toString();

        if(!nombre_string.isEmpty() && !mutualista_string.isEmpty() && !telefono_string.isEmpty() && !patologias_string.isEmpty() && !cuota_string.isEmpty() ){
            ContentValues registro = new ContentValues();

            registro.put("nombre", nombre_string);
            registro.put("mutualista", mutualista_string);
            registro.put("telefono", telefono_string);
            registro.put("cuota", cuota_string);
            registro.put("patologias", patologias_string);

            BaseDeDatos.insert("clientes", null, registro);

            BaseDeDatos.close();
            nombre.setText("");
            mutualista.setText("");
            telefono.setText("");
            cuota.setText("");
            patologias.setText("");

            Toast.makeText(requireContext(), "Cliente registrado con éxito", Toast.LENGTH_SHORT).show();
        } else{
            Toast.makeText(requireContext(), "Debe llenar todos los campos", Toast.LENGTH_SHORT).show();
        }
    }
}