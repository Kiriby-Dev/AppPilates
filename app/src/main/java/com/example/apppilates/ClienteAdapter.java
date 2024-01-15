package com.example.apppilates;

import static androidx.core.content.ContentProviderCompat.requireContext;

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
        holder.nombre.setText(arrayList.get(position));
        boolean isChecked = obtenerEstadoCheckBox(arrayList.get(position));
        holder.checkBox.setChecked(isChecked);

        holder.checkBox.setOnCheckedChangeListener((buttonView, isCheckedNew) -> {
            // Actualizar el estado del CheckBox en la base de datos
            actualizarEstadoCheckBox(arrayList.get(position), isCheckedNew);

            listener.onItemCheckedChanged(arrayList.get(position), isCheckedNew);

            if (isCheckedNew) {
                arrayList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, getItemCount());
            }
        });
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

    private void actualizarEstadoCheckBox(String nombreCliente, boolean isChecked) {
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(context, "administracion", null, 1);
        SQLiteDatabase BaseDeDatos = admin.getWritableDatabase();
        ContentValues valores = new ContentValues();
        valores.put("pagado", isChecked ? true : false);

        String whereClause = "nombre_cliente=?";
        String[] whereArgs = {nombreCliente};

        BaseDeDatos.update("pagos", valores, whereClause, whereArgs);
        BaseDeDatos.close();
    }
}
