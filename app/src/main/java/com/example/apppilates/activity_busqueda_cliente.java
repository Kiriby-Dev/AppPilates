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

public class activity_busqueda_cliente extends AppCompatActivity {

    private EditText nombre;
    private EditText mutualista;
    private EditText telefono;
    private EditText cuota;
    private EditText patologias;
    private Spinner lista;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_busqueda_cliente);

        nombre = findViewById(R.id.nombreEditTextBuscar);
        mutualista = findViewById(R.id.mutualistaEditTextBuscar);
        telefono = findViewById(R.id.telefonoEditTextBuscar);
        cuota = findViewById(R.id.cuotaEditTextBuscar);
        patologias = findViewById(R.id.patologiasEditTextBuscar);
        lista = findViewById(R.id.listaClientesBuscar);

        String[] opciones = obtenerListaClientes();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.style_spinner, opciones);
        lista.setAdapter(adapter);

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
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "administracion", null, 1);
        SQLiteDatabase BaseDeDatos = admin.getWritableDatabase();

        String seleccion = lista.getSelectedItem().toString();

        Cursor fila = BaseDeDatos.rawQuery("SELECT nombre, mutualista, telefono, cuota, patologias FROM clientes WHERE nombre = ?", new String[]{seleccion});

        if(fila.moveToFirst()){
            nombre.setText(fila.getString(0));
            mutualista.setText(fila.getString(1));
            telefono.setText(fila.getString(2));
            cuota.setText(fila.getString(3));
            patologias.setText(fila.getString(4));
        }
        BaseDeDatos.close();
    }

    public String[] obtenerListaClientes() {
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "administracion", null, 1);
        SQLiteDatabase BaseDeDatos = admin.getWritableDatabase();

        Cursor fila = BaseDeDatos.rawQuery("SELECT nombre FROM clientes", null);
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

    public void eliminarCliente(){
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "administracion", null, 1);
        SQLiteDatabase BaseDeDatos = admin.getWritableDatabase();

        String seleccion = lista.getSelectedItem().toString();
        if (!seleccion.isEmpty()) {
            int cantidad = BaseDeDatos.delete("clientes", "nombre='" + seleccion + "'", null);
            BaseDeDatos.close();
            nombre.setText("");
            mutualista.setText("");
            telefono.setText("");
            cuota.setText("");
            patologias.setText("");

            Toast.makeText(this, "Cliente eliminado con éxito", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Debe seleccionar un cliente", Toast.LENGTH_SHORT).show();
        }
    }

    public void modificarCliente(){
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "administracion", null, 1);
        SQLiteDatabase BaseDeDatos = admin.getWritableDatabase();

        String nombre_string = nombre.getText().toString();
        String mutualista_string = mutualista.getText().toString();
        String telefono_string = telefono.getText().toString();
        String cuota_string = cuota.getText().toString();
        String patologias_string = patologias.getText().toString();

        String seleccion = lista.getSelectedItem().toString();
        if (!seleccion.isEmpty()) {
            if(!nombre_string.isEmpty() && !mutualista_string.isEmpty() && !telefono_string.isEmpty() && !cuota_string.isEmpty() ){
                ContentValues registro = new ContentValues();

                registro.put("nombre", nombre_string);
                registro.put("mutualista", mutualista_string);
                registro.put("telefono", telefono_string);
                registro.put("cuota", cuota_string);
                registro.put("patologias", patologias_string);

                int cantidad = BaseDeDatos.update("clientes", registro, "nombre='" + seleccion + "'", null);

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