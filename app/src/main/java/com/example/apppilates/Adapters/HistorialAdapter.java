package com.example.apppilates.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.apppilates.Cliente;
import com.example.apppilates.R;

import java.util.ArrayList;

public class HistorialAdapter extends RecyclerView.Adapter<HistorialAdapter.ViewHolder>{

    View view;
    Context context;
    ArrayList<Cliente> arrayList;

    public HistorialAdapter(Context context, ArrayList<Cliente> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(context).inflate(R.layout.style_historial, parent, false);
        return new HistorialAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistorialAdapter.ViewHolder holder, int position) {
        Cliente cliente = arrayList.get(position);

        holder.nombre.setText(cliente.getNombre());
        holder.cedula.setText(cliente.getCedula());
        holder.cuotaYfecha.setText("$" + cliente.getCuota() + " - " + cliente.getFecha());
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView nombre;
        TextView cedula;
        TextView cuotaYfecha;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            nombre = itemView.findViewById(R.id.historialNombre);
            cedula = itemView.findViewById(R.id.historialCedula);
            cuotaYfecha = itemView.findViewById(R.id.historialCuotayFecha);
        }
    }
}
