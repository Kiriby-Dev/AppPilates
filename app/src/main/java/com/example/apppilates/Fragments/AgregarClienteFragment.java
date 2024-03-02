package com.example.apppilates.Fragments;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.apppilates.AdminSQLiteOpenHelper;
import com.example.apppilates.R;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AgregarClienteFragment extends Fragment {
    FirebaseFirestore db;
    private EditText cedula;
    private EditText nombre;
    private EditText apellido;
    private EditText mutualista;
    private EditText emergencia;
    private EditText telefono;
    private EditText domicilio;
    private EditText mail;
    private Spinner cuota;
    private RadioGroup genero;
    private EditText patologias;

    public AgregarClienteFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_agregar_cliente, container, false);

        cedula = view.findViewById(R.id.cedulaEditText);
        nombre = view.findViewById(R.id.nombreEditText);
        apellido = view.findViewById(R.id.apellidoEditText);
        mutualista = view.findViewById(R.id.mutualistaEditText);
        emergencia = view.findViewById(R.id.emergenciaEditText);
        telefono = view.findViewById(R.id.telefonoEditText);
        domicilio = view.findViewById(R.id.domicilioEditText);
        mail = view.findViewById(R.id.mailEditText);
        cuota = view.findViewById(R.id.spinnerCuota);
        genero = view.findViewById(R.id.radioGroup);
        patologias = view.findViewById(R.id.patologiasEditText);

        db = FirebaseFirestore.getInstance();

        String[] opciones = {"","Opcion 1", "Opcion 2", "Opcion 3", "Opcion 4"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), R.layout.style_spinner, opciones);
        cuota.setAdapter(adapter);

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

        String cedula_string = cedula.getText().toString();
        String nombre_string = nombre.getText().toString();
        String apellido_string = apellido.getText().toString();
        String mutualista_string = mutualista.getText().toString();
        String emergencia_string = emergencia.getText().toString();
        String telefono_string = telefono.getText().toString();
        String domicilio_string = domicilio.getText().toString();
        String mail_string = mail.getText().toString();
        String patologias_string = patologias.getText().toString();

        String cuota_string = "";
        if (cuota.getSelectedItem() != null) {
            cuota_string = cuota.getSelectedItem().toString();
        }

        String cuota_string_final = "";

        if ("Opcion 1".equals(cuota_string)) {
            cuota_string_final = "1";
        } else if ("Opcion 2".equals(cuota_string)) {
            cuota_string_final = "2";
        } else if ("Opcion 3".equals(cuota_string)) {
            cuota_string_final = "3";
        } else if ("Opcion 4".equals(cuota_string)) {
            cuota_string_final = "4";
        }

        int genero_int = genero.getCheckedRadioButtonId();
        String genero_string = "";
        if (genero_int == R.id.radioButton1) {
            genero_string = "Masculino";
        } else if (genero_int == R.id.radioButton2) {
            genero_string = "Femenino";
        } else if (genero_int == R.id.radioButton3) {
            genero_string = "Otro";
        }

        if (!cedula_string.isEmpty() && !nombre_string.isEmpty() && !apellido_string.isEmpty()) {
            if (validarCedulaUruguaya(cedula_string)) {

                Calendar calendar = Calendar.getInstance();
                int month = calendar.get(Calendar.MONTH) + 1;
                int year = calendar.get(Calendar.YEAR);
                String mesString = String.valueOf(month);
                String anioString = String.valueOf(year);

                Map<String, Object> user = new HashMap<>();
                user.put("cedula", cedula_string);
                user.put("nombre", nombre_string);
                user.put("apellido", apellido_string);
                user.put("mutualista", mutualista_string);
                user.put("emergencia", emergencia_string);
                user.put("telefono", telefono_string);
                user.put("domicilio", domicilio_string);
                user.put("mail", mail_string);
                user.put("cuota", cuota_string_final);
                user.put("genero", genero_string);
                user.put("patologias", patologias_string);
                user.put("mesAlta", month);
                user.put("añoAlta", year);

                db.collection("clientes").document(cedula_string).set(user);

                Map<String, Object> pago = new HashMap<>();
                pago.put("cedula", cedula_string);
                pago.put("mes", month);
                pago.put("año", year);
                pago.put("fecha", "");
                pago.put("pagado", false);

                String identificadorPago = cedula_string + mesString + anioString;
                db.collection("pagos").document(identificadorPago).set(pago);

                cedula.setText("");
                nombre.setText("");
                apellido.setText("");
                mutualista.setText("");
                emergencia.setText("");
                telefono.setText("");
                domicilio.setText("");
                mail.setText("");
                cuota.setSelection(0);
                patologias.setText("");

                Toast.makeText(requireContext(), "Cliente registrado con éxito", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(requireContext(), "La cédula es incorrecta", Toast.LENGTH_SHORT).show();
            }
        } else{
            Toast.makeText(requireContext(), "Debe llenar todos los campos", Toast.LENGTH_SHORT).show();
        }
    }

    public static boolean validarCedulaUruguaya(String cedula) {
        if (cedula == null || cedula.length() != 8 || !cedula.matches("\\d+")) {
            return false;
        }

        int[] coeficientes = {9, 8, 7, 6, 5, 4, 3, 2};
        int suma = 0;
        int digitoVerificador = Integer.parseInt(cedula.substring(7));

        for (int i = 0; i < coeficientes.length; i++) {
            int num = Integer.parseInt(cedula.substring(i, i + 1));
            suma += num * coeficientes[i];
        }

        int residuo = suma % 10;
        int resultado = (residuo == 0) ? 0 : (10 - residuo);

        return resultado == digitoVerificador;
    }
}