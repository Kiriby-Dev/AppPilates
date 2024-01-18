package com.example.apppilates;

import static androidx.core.content.ContentProviderCompat.requireContext;

import static java.lang.Float.parseFloat;

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

import com.example.apppilates.Logica.Cliente;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ClienteAdapter extends RecyclerView.Adapter<ClienteAdapter.ViewHolder> {

    public interface OnCheckedChangeListener {
        void onItemCheckedChanged(String nombreCliente, boolean isChecked);
    }

    View view;
    Context context;
    ArrayList<String> arrayList;
    OnCheckedChangeListener listener;

    public ClienteAdapter(Context context, ArrayList<String> arrayList, OnCheckedChangeListener listener) {
        this.context = context;
        this.arrayList = arrayList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(context).inflate(R.layout.style_lista, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClienteAdapter.ViewHolder holder, int position) {
        if (!arrayList.isEmpty() && position < arrayList.size()) {
            String clienteNombre = arrayList.get(position);

            holder.nombre.setText(clienteNombre);
            boolean isChecked = obtenerEstadoCheckBox(clienteNombre);
            holder.checkBox.setChecked(isChecked);

            holder.checkBox.setOnCheckedChangeListener((buttonView, isCheckedNew) -> {
                // Actualizar el estado del CheckBox en la base de datos
                actualizarEstadoCheckBox(clienteNombre, isCheckedNew);

                listener.onItemCheckedChanged(clienteNombre, isCheckedNew);

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

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        CheckBox checkBox;
        TextView nombre;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            nombre = itemView.findViewById(R.id.textViewNombre);
            checkBox = itemView.findViewById(R.id.checkBoxPago);
        }
    }

    private boolean obtenerEstadoCheckBox(String nombreCliente) {
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(context, "administracion", null, 1);
        SQLiteDatabase BaseDeDatos = admin.getWritableDatabase();
        String[] columns = {"pagado"};
        String selection = "nombre_cliente=?";
        String[] selectionArgs = {nombreCliente};

        Cursor cursor = BaseDeDatos.query("pagos", columns, selection, selectionArgs, null, null, null);

        boolean isChecked = false;
        if (cursor != null && cursor.moveToFirst()) {
            isChecked = cursor.getInt(cursor.getColumnIndex("pagado")) == 1;
            cursor.close();
        }
        BaseDeDatos.close();
        return isChecked;
    }

    private void actualizarEstadoCheckBox(String datos, boolean isChecked) {
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(context, "administracion", null, 1);
        SQLiteDatabase BaseDeDatos = admin.getWritableDatabase();

        float cuota = 0.0f;

        ContentValues valores = new ContentValues();
        ContentValues valores_mensual = new ContentValues();

        String[] partes = datos.split(" - ");
        String nombreCliente = partes[0];
        int month = Integer.parseInt(partes[1].split("/")[0]);
        int year = Integer.parseInt(partes[1].split("/")[1]);

        if(isChecked) {
            valores.put("pagado", 1);
            valores.put("nombre_cliente", nombreCliente);
            valores.put("mes", month);
            valores.put("anio", year);
        }

        BaseDeDatos.update("pagos", valores, "nombre_cliente='" + nombreCliente + "'AND mes='" + month + "'AND anio='" + year +"'", null);

        Cursor cursor = BaseDeDatos.rawQuery("SELECT cuota FROM clientes WHERE nombre = '" + nombreCliente + "'", null);

        if (cursor != null && cursor.moveToFirst()) {
            // Obtener la cuota como un String desde la base de datos
            cuota = cursor.getFloat(cursor.getColumnIndex("cuota"));
            cursor.close();
        }

        Cursor cursor1 = BaseDeDatos.rawQuery("SELECT * FROM balance_mensual WHERE mes = '" + month + "' AND anio = '" + year + "'", null);

        if (cursor1 != null && cursor1.moveToFirst()) {
            valores_mensual.put("mes", month);
            valores_mensual.put("anio", year);
            valores_mensual.put("balance", cursor1.getFloat(cursor1.getColumnIndex("balance")) + cuota);
            valores_mensual.put("total", cursor1.getFloat(cursor1.getColumnIndex("total")));
            cursor1.close();
        }

        BaseDeDatos.update("balance_mensual", valores_mensual, "mes='" + month + "'AND anio='" + year +"'", null);

        BaseDeDatos.close();
    }
}
