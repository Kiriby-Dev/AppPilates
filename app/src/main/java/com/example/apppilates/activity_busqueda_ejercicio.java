package com.example.apppilates;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class activity_busqueda_ejercicio extends AppCompatActivity {
    private EditText nombre;
    private Spinner lista;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_busqueda_ejercicio);

        nombre = findViewById(R.id.nombreEditTextBuscar);
        lista = findViewById(R.id.listaEjerciciosBuscar);

        String[] opciones = obtenerListaEjercicios();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.style_spinner, opciones);
        lista.setAdapter(adapter);

        Button buscarButton = findViewById(R.id.buscarButton);
        buscarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buscarEjercicio();
            }
        });

        Button eliminarButton = findViewById(R.id.eliminarButton);
        eliminarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eliminarEjercicio();
            }
        });

        Button modificarButton = findViewById(R.id.editarButton);
        modificarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { modificarEjercicio();
            }
        });
    }

    public void buscarEjercicio(){
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "administracion", null, 1);
        SQLiteDatabase BaseDeDatos = admin.getWritableDatabase();

        String seleccion = lista.getSelectedItem().toString();

        Cursor fila = BaseDeDatos.rawQuery("SELECT nombre FROM ejercicios WHERE nombre = ?", new String[]{seleccion});

        if(fila.moveToFirst()){
            nombre.setText(fila.getString(0));
        }
        BaseDeDatos.close();
    }

    public String[] obtenerListaEjercicios() {
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "administracion", null, 1);
        SQLiteDatabase BaseDeDatos = admin.getWritableDatabase();

        Cursor fila = BaseDeDatos.rawQuery("SELECT nombre FROM ejercicios", null);
        String[] nombres;
        if (fila != null && fila.moveToFirst()) {
            nombres = new String[fila.getCount() + 1]; // Agregar espacio para el elemento vacío
            nombres[0] = ""; // Establecer el primer elemento como cadena vacía
            int index = 1; // Comenzar desde el segundo elemento

            do {
                String nombre = fila.getString(fila.getColumnIndex("nombre"));
                nombres[index++] = nombre;
            } while (fila.moveToNext());

            fila.close();
        } else {
            // No se encontraron datos
            nombres = new String[]{""}; // Devolver una lista con un elemento vacío
        }

        BaseDeDatos.close(); // Cerrar la base de datos

        return nombres;
    }

    public void eliminarEjercicio(){
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "administracion", null, 1);
        SQLiteDatabase BaseDeDatos = admin.getWritableDatabase();

        String seleccion = lista.getSelectedItem().toString();
        if (!seleccion.isEmpty()) {
            int cantidad = BaseDeDatos.delete("ejercicios", "nombre='" + seleccion + "'", null);
            BaseDeDatos.close();
            nombre.setText("");

            Toast.makeText(this, "Cliente eliminado con éxito", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Debe seleccionar un cliente", Toast.LENGTH_SHORT).show();
        }
    }

    public void modificarEjercicio(){
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "administracion", null, 1);
        SQLiteDatabase BaseDeDatos = admin.getWritableDatabase();

        String nombre_string = nombre.getText().toString();

        String seleccion = lista.getSelectedItem().toString();
        if (!seleccion.isEmpty()) {
            if(!nombre_string.isEmpty()){
                ContentValues registro = new ContentValues();

                registro.put("nombre", nombre_string);

                int cantidad = BaseDeDatos.update("ejercicios", registro, "nombre='" + seleccion + "'", null);

                BaseDeDatos.close();
                Toast.makeText(this, "Cambios guardados", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this, "Debes llenar todos los campos", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Debe seleccionar un cliente", Toast.LENGTH_SHORT).show();
        }
    }
}