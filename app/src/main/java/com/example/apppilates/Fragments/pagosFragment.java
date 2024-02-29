package com.example.apppilates.Fragments;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.apppilates.AdminSQLiteOpenHelper;
import com.example.apppilates.Adapters.ClienteAdapter;
import com.example.apppilates.Activities.HistorialPagosActivity;
import com.example.apppilates.Cliente;
import com.example.apppilates.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class pagosFragment extends Fragment implements ClienteAdapter.OnCheckedChangeListener{

    interface FirestoreCallback {
        void onCallback(ArrayList<Cliente> clientes);
    }

    interface FirestoreCallbackAtrasados {
        void onCallback(ArrayList<Cliente> clientesAtrasados);
    }

    FirebaseFirestore db;
    RecyclerView lista;
    RecyclerView listaAtrasados;
    TextView saldoTextView;
    TextView totalTextView;
    Button boton;

    public pagosFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pagos, container, false);

        lista = view.findViewById(R.id.listaPagos);
        listaAtrasados = view.findViewById(R.id.listaPagosAtrasados);
        saldoTextView = view.findViewById(R.id.saldoTextView);
        totalTextView = view.findViewById(R.id.totalTextView);
        boton = view.findViewById(R.id.historialPagosButton);

        db = FirebaseFirestore.getInstance();

        crearInstanciasPagos(new CrearInstanciasPagosCallback() {
            @Override
            public void onInstanciasPagosCreadas() {

            }
        });

        obtenerListaClientes(new FirestoreCallback() {
            @Override
            public void onCallback(ArrayList<Cliente> clientes) {
                // Se llama a este método cuando se han obtenido los datos de Firestore
                lista.setVisibility(View.VISIBLE);
                lista.setLayoutManager(new LinearLayoutManager(getContext()));
                lista.setAdapter(new ClienteAdapter(getContext(), clientes, pagosFragment.this));
            }
        });

        obtenerListaClientesAtrasados(new FirestoreCallbackAtrasados() {
            @Override
            public void onCallback(ArrayList<Cliente> clientesAtrasados) {
                listaAtrasados.setVisibility(View.VISIBLE);
                listaAtrasados.setLayoutManager(new LinearLayoutManager(getContext()));
                listaAtrasados.setAdapter(new ClienteAdapter(getContext(), clientesAtrasados, pagosFragment.this));
            }
        });

        obtenerTotal(total -> {
            // Actualizar el TextView del saldo con el total obtenido
            totalTextView.setText(String.valueOf(total));
        });

        obtenerSaldo(saldo -> {
            // Actualizar el TextView del saldo con el saldo obtenido
            saldoTextView.setText("$" + saldo);
        });

        boton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lógica para cambiar a la actividad deseada
                Intent intent = new Intent(getActivity(), HistorialPagosActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }

    @Override
    public void onItemCheckedChanged(Cliente cliente, boolean isChecked) {
        // Obtener el saldo actual
        float saldoActual = Float.parseFloat(saldoTextView.getText().toString().replace("$", ""));

        // Obtener la cuota del cliente
        float cuotaCliente = Float.parseFloat(cliente.getCuota());

        // Calcular el nuevo saldo
        float nuevoSaldo = isChecked ? saldoActual + cuotaCliente : saldoActual + cuotaCliente;

        // Actualizar el TextView del saldo con el nuevo saldo calculado
        saldoTextView.setText("$" + nuevoSaldo);
    }

    public void obtenerListaClientes(FirestoreCallback firestoreCallback) {
        ArrayList<Cliente> clientes = new ArrayList<>();
        AtomicInteger count = new AtomicInteger(0); // Contador para rastrear el número de consultas completadas

        Calendar calendar = Calendar.getInstance();
        int month = calendar.get(Calendar.MONTH) + 1;
        int year = calendar.get(Calendar.YEAR);

        db.collection("pagos").whereEqualTo("pagado", false).whereEqualTo("mes", month).whereEqualTo("año", year).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                int totalDocuments = queryDocumentSnapshots.size(); // Obtener el número total de documentos en la consulta
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    String cedula = document.getString("cedula");
                    Long mesLong = document.getLong("mes");
                    Long anioLong = document.getLong("año");
                    String mes = String.valueOf(mesLong);
                    String anio = String.valueOf(anioLong);
                    db.collection("clientes").whereEqualTo("cedula", cedula).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            for (QueryDocumentSnapshot personaDocument : queryDocumentSnapshots) {
                                String nombre = personaDocument.getString("nombre");
                                String cuota = personaDocument.getString("cuota");
                                String fecha = mes + "/" + anio;
                                Cliente cliente = new Cliente(nombre, cedula, cuota, fecha);
                                clientes.add(cliente);
                            }

                            // Incrementar el contador después de completar la consulta adicional
                            int completedQueries = count.incrementAndGet();
                            // Verificar si todas las consultas adicionales se han completado
                            if (completedQueries == totalDocuments) {
                                // Llamar a firestoreCallback.onCallback solo cuando todas las consultas se hayan completado
                                firestoreCallback.onCallback(clientes);
                            }
                        }
                    });
                }
            }
        });
    }

    public void obtenerListaClientesAtrasados(FirestoreCallbackAtrasados firestoreCallback) {
        ArrayList<Cliente> clientesAtrasados = new ArrayList<>();
        AtomicInteger count = new AtomicInteger(0); // Contador para rastrear el número de consultas completadas

        Calendar calendar = Calendar.getInstance();
        int currentMonth = calendar.get(Calendar.MONTH) + 1; // Mes actual
        int currentYear = calendar.get(Calendar.YEAR); // Año actual

        db.collection("pagos").whereEqualTo("pagado", false).get().addOnSuccessListener(queryDocumentSnapshots -> {
            int totalDocuments = queryDocumentSnapshots.size(); // Número total de documentos en la consulta
            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                int mesPago = document.getLong("mes").intValue(); // Obtener el mes del documento
                int añoPago = document.getLong("año").intValue(); // Obtener el año del documento
                if (mesPago != currentMonth || añoPago != currentYear) {
                    // Si el pago no es del mes y año actual, considerarlo como atrasado
                    String cedula = document.getString("cedula");
                    Long mesLong = document.getLong("mes");
                    Long anioLong = document.getLong("año");
                    String mes = String.valueOf(mesLong);
                    String anio = String.valueOf(anioLong);
                    db.collection("clientes").whereEqualTo("cedula", cedula).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            for (QueryDocumentSnapshot personaDocument : queryDocumentSnapshots) {
                                String nombre = personaDocument.getString("nombre");
                                String cuota = personaDocument.getString("cuota");
                                String fecha = mes + "/" + anio;
                                Cliente cliente = new Cliente(nombre, cedula, cuota, fecha);
                                clientesAtrasados.add(cliente); // Agregar el cliente a la lista de clientes atrasados
                            }
                            int completedQueries = count.incrementAndGet();
                            // Verificar si todas las consultas adicionales se han completado
                            if (completedQueries == totalDocuments) {
                                firestoreCallback.onCallback(clientesAtrasados);
                            }
                        }
                    });
                } else {
                    // Incrementar el contador aunque no se agregue el cliente
                    count.incrementAndGet();
                }
            }

            // Si no hay pagos atrasados, llamar al callback directamente
            if (queryDocumentSnapshots.isEmpty()) {
                firestoreCallback.onCallback(clientesAtrasados);
            }
        });
    }

    public void crearInstanciasPagos(CrearInstanciasPagosCallback callback) {
        // Obtener todos los usuarios de la base de datos
        db.collection("clientes").get().addOnSuccessListener(queryDocumentSnapshots -> {
            int totalClientes = queryDocumentSnapshots.size();
            final AtomicInteger clientesProcesados = new AtomicInteger(0);
            // Iterar sobre todos los usuarios
            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                Calendar fechaActual = Calendar.getInstance();
                int añoActual = fechaActual.get(Calendar.YEAR);
                int mesActual = fechaActual.get(Calendar.MONTH) + 1; // Sumar 1 porque los meses se indexan desde 0

                Long mesAlta = document.getLong("mesAlta");
                Long anioAlta = document.getLong("añoAlta");
                String cedula = document.getString("cedula");

                // Calcular los meses transcurridos desde la fecha de alta hasta el mes actual
                Long mesesTranscurridos = (añoActual - anioAlta) * 12 + (mesActual - mesAlta) + 1;

                // Iterar sobre los meses transcurridos y crear instancias de pago para cada mes
                for (int i = 0; i < mesesTranscurridos; i++) {
                    final Long mesPago = mesAlta + i;
                    Long anioPago = anioAlta + (mesPago - 1) / 12; // Si el mes es mayor que 12, aumenta el año
                    final Long mesPagoFinal = (mesPago - 1) % 12 + 1; // Ajustar el mes si es mayor que 12

                    String mesString = String.valueOf(mesPago);
                    String anioString = String.valueOf(anioPago);

                    String identificadorPago = cedula + mesString + anioString;

                    // Verificar si ya existe un pago para el cliente en el mes y año actual
                    db.collection("pagos").document(identificadorPago).get().addOnCompleteListener(pagoExistenteTask -> {
                        if (pagoExistenteTask.isSuccessful()) {
                            DocumentSnapshot pagoExistenteDocument = pagoExistenteTask.getResult();
                            if (!pagoExistenteDocument.exists()) {
                                // No existe un pago para este cliente en el mes y año actual, crear uno nuevo
                                Map<String, Object> pago = new HashMap<>();
                                pago.put("cedula", cedula);
                                pago.put("mes", mesPagoFinal);
                                pago.put("año", anioPago);
                                pago.put("fecha", "");
                                pago.put("pagado", false);

                                db.collection("pagos").document(identificadorPago).set(pago).addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        int processedCount = clientesProcesados.incrementAndGet();
                                        if (processedCount == totalClientes) {
                                            callback.onInstanciasPagosCreadas();
                                        }
                                    }
                                });
                            } else {
                                // Ya existe un pago para este cliente en el mes y año actual, omitir la creación
                                int processedCount = clientesProcesados.incrementAndGet();
                                if (processedCount == totalClientes) {
                                    callback.onInstanciasPagosCreadas();
                                }
                            }
                        }
                    });
                }
            }
        });
    }

    public interface CrearInstanciasPagosCallback {
        void onInstanciasPagosCreadas();
    }

    public void obtenerTotal(TotalCallback callback) {
        final float[] total = {0.0f}; // Declarar una variable final o efectivamente final

        db.collection("clientes").get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                // Obtener la cuota como un Float desde Firestore
                String cuota_string = document.getString("cuota");
                float cuota = Float.parseFloat(cuota_string);
                total[0] += cuota; // Actualizar el valor de total dentro del lambda
            }
            // Llamar al callback con el total calculado
            callback.onTotalReceived(total[0]);
        });
    }

    // Interfaz para manejar el total recibido
    public interface TotalCallback {
        void onTotalReceived(float total);
    }

    public void obtenerSaldo(SaldoCallback callback) {
        Calendar calendar = Calendar.getInstance();
        int month = calendar.get(Calendar.MONTH) + 1; // Ajustar el índice del mes
        int year = calendar.get(Calendar.YEAR);

        db.collection("pagos").whereEqualTo("pagado", true).get().addOnSuccessListener(queryDocumentSnapshots -> {
            final float[] saldo = {0.0f};
            int totalDocumentos = queryDocumentSnapshots.size();
            AtomicInteger documentosProcesados = new AtomicInteger(0);
            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                Long mes = document.getLong("mes");
                Long anio = document.getLong("año");
                if (mes == month && anio == year) {
                    String cedula = document.getString("cedula");
                    db.collection("clientes").whereEqualTo("cedula", cedula).get().addOnSuccessListener(clienteQuerySnapshot -> {
                        for (QueryDocumentSnapshot clienteDocument : clienteQuerySnapshot) {
                            // Obtener la cuota como un Float desde Firestore
                            String cuota_string = clienteDocument.getString("cuota");
                            float cuota = Float.parseFloat(cuota_string);
                            saldo[0] += cuota;
                        }
                        int documentosCompletados = documentosProcesados.incrementAndGet();
                        // Verificar si todas las consultas de clientes se han completado
                        if (documentosCompletados == totalDocumentos) {
                            // Llamar al callback con el saldo calculado
                            callback.onSaldoReceived(String.valueOf(saldo[0]));
                        }
                    });
                }
            }
        });
    }

    public interface SaldoCallback {
        void onSaldoReceived(String saldo);
    }
}