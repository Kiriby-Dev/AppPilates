package com.example.apppilates;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class AdminSQLiteOpenHelper extends SQLiteOpenHelper{
    public AdminSQLiteOpenHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase BaseDeDatos) {
        BaseDeDatos.execSQL("create table clientes(nombre text primary key, mutualista text, telefono text, cuota real, patologias text)");
        BaseDeDatos.execSQL("create table pagos(id text primary key, nombre text, fecha date, pagado boolean, FOREIGN KEY(nombre) REFERENCES clientes(nombre))");
        BaseDeDatos.execSQL("create table balance_mensual(men int, anio int, balance real, total real, primary key (mes, anio))");
        BaseDeDatos.execSQL("create table ejercicios(nombre text, descripcion text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
