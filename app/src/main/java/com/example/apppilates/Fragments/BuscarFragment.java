package com.example.apppilates.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.apppilates.R;
import com.example.apppilates.activity_busqueda_cliente;

public class BuscarFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_buscar, container, false);

        Button buscarClienteButton = view.findViewById(R.id.buscarClienteButton);
        buscarClienteButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Lógica para cambiar a la actividad deseada
                Intent intent = new Intent(getActivity(), activity_busqueda_cliente.class);
                startActivity(intent);
            }
        });
        return view;
    }
}