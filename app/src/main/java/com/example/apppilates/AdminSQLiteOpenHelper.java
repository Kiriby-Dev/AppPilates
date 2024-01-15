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
        BaseDeDatos.execSQL("create table balance_mensual(men int, anio int, balance real, total real, primary key (mes, anio))");
        BaseDeDatos.execSQL("create table ejercicios(nombre text primary key, descripcion text)");
        BaseDeDatos.execSQL("create table pagos(nombre_cliente text, mes int, anio int, fecha date, pagado boolean, primary key(nombre, mes, anio))");
        BaseDeDatos.execSQL("create table recibos_pagos(nombre_cliente text, mes int, anio int, recibo text, primary key(nombre, mes, anio))");
        BaseDeDatos.execSQL("create table hace(nombre_cliente text, ejercicio text, primary key(nombre_cliente, ejercicio))");
        BaseDeDatos.execSQL("create table no_puede(nombre_cliente text, ejercicio text, primary key(nombre_cliente, ejercicio))");



    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
