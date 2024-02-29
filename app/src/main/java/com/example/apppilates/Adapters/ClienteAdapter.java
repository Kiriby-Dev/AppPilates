package com.example.apppilates.Adapters;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.apppilates.AdminSQLiteOpenHelper;
import com.example.apppilates.Cliente;
import com.example.apppilates.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Calendar;

public class ClienteAdapter extends RecyclerView.Adapter<ClienteAdapter.ViewHolder> {

    public interface OnCheckedChangeListener {
        void onItemCheckedChanged(Cliente cliente, boolean isChecked);
    }

    FirebaseFirestore db;
    View view;
    Context context;
    ArrayList<Cliente> arrayList;
    OnCheckedChangeListener listener;

    public ClienteAdapter(Context context, ArrayList<Cliente> arrayList, OnCheckedChangeListener listener) {
        this.context = context;
        this.arrayList = arrayList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(context).inflate(R.layout.style_lista, parent, false);
        db = FirebaseFirestore.getInstance();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClienteAdapter.ViewHolder holder, int position) {
        if (!arrayList.isEmpty() && position < arrayList.size()) {
            Cliente cliente = arrayList.get(position);

            holder.nombre.setText(cliente.getNombre());
            holder.cedula.setText(cliente.getCedula());
            holder.cuotaYfecha.setText("$" + cliente.getCuota() + " - " + cliente.getFecha());
            obtenerEstadosCheckBox(arrayList, new CheckBoxStateCallback() {
                @Override
                public void onCheckBoxStateChanged(String cedula, boolean isChecked) {
                    // Verificar si el cliente actual coincide con la cédula obtenida y actualizar el estado del CheckBox
                    if (cliente.getCedula().equals(cedula)) {
                        holder.checkBox.setChecked(isChecked);
                    }
                }
            });

            holder.checkBox.setOnCheckedChangeListener((buttonView, isCheckedNew) -> {
                // Actualizar el estado del CheckBox en la base de datos
                actualizarEstadoCheckBox(cliente, isCheckedNew);

                listener.onItemCheckedChanged(cliente, isCheckedNew);

                if (isCheckedNew) {
                    // Eliminar el elemento de la lista
                    int adapterPosition = holder.getAdapterPosition();
                    if (adapterPosition != RecyclerView.NO_POSITION) {
                        arrayList.remove(adapterPosition);
                        notifyItemRemoved(adapterPosition);
                    }
                }
            });
        }
    }

    public interface CheckBoxStateCallback {
        void onCheckBoxStateChanged(String cedula, boolean isChecked);
    }

    public void obtenerEstadosCheckBox(ArrayList<Cliente> clientes, CheckBoxStateCallback callback) {
        ArrayList<String> cedulas = new ArrayList<>();
        for (Cliente cliente : clientes) {
            cedulas.add(cliente.getCedula());
        }
        db.collection("pagos").whereIn("cedula", cedulas).get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                String cedula = document.getString("cedula");
                boolean pagado = document.getBoolean("pagado");
                // Actualizar el estado del CheckBox en función de la cédula y el estado del pago
                callback.onCheckBoxStateChanged(cedula, pagado);
            }
        });
    }

    private void actualizarEstadoCheckBox(Cliente cliente, boolean isChecked) {

        String cedula = cliente.getCedula();
        String fecha = cliente.getFecha();

        String[] partes = fecha.split("/");
        int month = Integer.parseInt(partes[0]);
        int year = Integer.parseInt(partes[1]);

        if (isChecked) {
            // Actualizar el estado del pago en la colección "pagos" de Firestore
            db.collection("pagos").whereEqualTo("cedula", cedula).whereEqualTo("mes", month).whereEqualTo("año", year).get().addOnSuccessListener(queryDocumentSnapshots -> {
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    document.getReference().update("pagado", true);

                    Calendar fechaActual = Calendar.getInstance();
                    int añoActual = fechaActual.get(Calendar.YEAR);
                    int mesActual = fechaActual.get(Calendar.MONTH) + 1;
                    int diaActual = fechaActual.get(Calendar.DAY_OF_MONTH);
                    String anioActualString = String.valueOf(añoActual);
                    String mesActualString = String.valueOf(mesActual);
                    String diaActualString = String.valueOf(diaActual);

                    String fechaPago = diaActualString + "/" + mesActualString + "/" + anioActualString;
                    document.getReference().update("fecha", fechaPago);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        CheckBox checkBox;
        TextView nombre;
        TextView cedula;
        TextView cuotaYfecha;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            nombre = itemView.findViewById(R.id.textViewNombre);
            cedula = itemView.findViewById(R.id.textViewCedula);
            cuotaYfecha = itemView.findViewById(R.id.textViewCuotayFecha);
            checkBox = itemView.findViewById(R.id.checkBoxPago);
        }
    }
}
