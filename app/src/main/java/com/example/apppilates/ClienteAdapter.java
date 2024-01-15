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

    View view;
    Context context;
    ArrayList<String> arrayList;

    public ClienteAdapter(Context context, ArrayList<String> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    public View getView(){
        return view;
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
        String[] columns = {"pago"};
        String selection = "nombre=?";
        String[] selectionArgs = {nombreCliente};

        Cursor cursor = BaseDeDatos.query("clientes", columns, selection, selectionArgs, null, null, null);

        boolean isChecked = false;
        if (cursor != null && cursor.moveToFirst()) {
            isChecked = cursor.getInt(cursor.getColumnIndex("pago")) == 1;
            cursor.close();
        }
        BaseDeDatos.close();
        return isChecked;
    }

    private void actualizarEstadoCheckBox(String nombreCliente, boolean isChecked) {
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(context, "administracion", null, 1);
        SQLiteDatabase BaseDeDatos = admin.getWritableDatabase();
        ContentValues valores = new ContentValues();
        valores.put("pago", isChecked ? 1 : 0);

        String whereClause = "nombre=?";
        String[] whereArgs = {nombreCliente};

        BaseDeDatos.update("clientes", valores, whereClause, whereArgs);
        BaseDeDatos.close();
    }
}
