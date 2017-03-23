package com.example.marcoscardenas.cialproject.DataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.example.marcoscardenas.cialproject.Model.ChoferGetSet;
import com.example.marcoscardenas.cialproject.Model.GetVale;
import com.example.marcoscardenas.cialproject.Model.MesprocesoGetSet;
import com.example.marcoscardenas.cialproject.Model.ObraGetSet;
import com.example.marcoscardenas.cialproject.Model.SurtidorGetSet;
import com.example.marcoscardenas.cialproject.Model.UsuarioGetSet;
import com.example.marcoscardenas.cialproject.Model.VehiculoGetSet;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Marcos on 03-03-17.
 */

public class Conexion extends SQLiteAssetHelper{
    private static final String DATABASE_NAME = "becker.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME_ENCABEZADO = "comb2_vales_encabezado";
    private static final String TABLE_NAME_DETALLE = "comb2_vales_detalle";

    public Conexion(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public ArrayList<MesprocesoGetSet> getMes_proceso() {

        ArrayList<MesprocesoGetSet> Array_mes = new ArrayList<>();
        // obtener mes y año
        Calendar fecha = Calendar.getInstance();
        int año = fecha.get(Calendar.YEAR);
        int mes = fecha.get(Calendar.MONTH) + 1;

        SQLiteDatabase db = getReadableDatabase();
        String selectQuery = "SELECT proceso FROM comb2_mes_y_ano_proceso where cod_empresa = 275 and mes = 0"+ mes +" and ano="+ año +"  " + "and  abierta = 1 and agrupacion = 'Combustibles' ";

        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {

                MesprocesoGetSet new_mes = new MesprocesoGetSet();

                new_mes.setNombre(cursor.getString(cursor.getColumnIndex("proceso")));

                String a = Integer.toString(Array_mes.size());
                Array_mes.add(new_mes);

            } while (cursor.moveToNext());
        }
        db.close();
        return Array_mes;
    }
    public ArrayList<SurtidorGetSet> getSurtidor() {

        ArrayList<SurtidorGetSet> Array_surtidor = new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();
        String selectQuery = "SELECT  surtidores.* \n" +
                "            from surtidores \n" +
                "            inner join \n" +
                "            comb2_categoria_surtidor on \n" +
                "            comb2_categoria_surtidor.Id = surtidores.id_categoria\n" +
                "            where \n" +
                "            comb2_categoria_surtidor.sub_clasificacion = 'Combustibles' and vigente = 1 and surtidores.codigo in" +
                " ( select surtidor from comb_user_ing_guia where usuario = 'marcos.cardenas')";
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {

                SurtidorGetSet surtidor = new SurtidorGetSet();

                surtidor.setCodigo(cursor.getString(cursor.getColumnIndex("codigo")));
                surtidor.setDescripcion(cursor.getString(cursor.getColumnIndex("descripcion")));

                String a = Integer.toString(Array_surtidor.size());

                Array_surtidor.add(surtidor);
            } while (cursor.moveToNext());
        }
        db.close();
        return Array_surtidor;
    }
    public ArrayList<ObraGetSet> getObra() {

        ArrayList<ObraGetSet> Array_obra = new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();
        String selectQuery = "SELECT * FROM  obras where finalizada = 0 and visible_petroleo = 1";
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {

                ObraGetSet obra = new ObraGetSet();

                obra.setCod_obra(cursor.getString(cursor.getColumnIndex("cod_obra")));
                obra.setNombre(cursor.getString(cursor.getColumnIndex("nombre")));

                String a = Integer.toString(Array_obra.size());

                Array_obra.add(obra);
            } while (cursor.moveToNext());
        }
        db.close();
        return Array_obra;
    }

    public ArrayList<ChoferGetSet> getChofer(String usuario) {

        ArrayList<ChoferGetSet> Array_chofer = new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();
        String selectQuery = "select rut, razon_social from proveedores where usuario_petroleo = 'True' and  (razon_social like '%" + usuario + "%' or rut like '% " + usuario +"%');";
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {

                ChoferGetSet chofer = new ChoferGetSet();

                chofer.setRut(cursor.getString(cursor.getColumnIndex("rut")));
                chofer.setRazon_social(cursor.getString(cursor.getColumnIndex("razon_social")));

                String a = Integer.toString(Array_chofer.size());

                Array_chofer.add(chofer);
            } while (cursor.moveToNext());
        }
        db.close();
        return Array_chofer;
    }
    public ArrayList<VehiculoGetSet> getVehiculo(String patente) {

        ArrayList<VehiculoGetSet> Array_vehiculo = new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();
        String selectQuery = "SELECT \n" +
                "            vehiculos.codigo, \n" +
                "            vehiculos.patente,\n" +
                "            vehiculos.cubicaje,\n" +
                "              marca || ' '||  modelo   AS nombre_vehiculo,\n" +
                "            proveedores.razon_social,\n" +
                "            proveedores.rut,\n" +
                "            proveedores.direccion,\n" +
                "            proveedores.fono1\n" +
                "            FROM \n" +
                "                vehiculos \n" +
                "            INNER JOIN proveedores ON\n" +
                "            (\n" +
                "                vehiculos.chofer_x_defecto = proveedores.rut\n" +
                "            )\n" +
                "            WHERE \n" +
                "                vehiculos.patente like '%"+ patente +"%' \n" +
                "            AND vehiculos.fecha_termino_contrato >= date('now') \n" +
                "            AND vehiculos.forma_pago IS NOT NULL \n" +
                "            AND vehiculos.valor_pago IS NOT NULL\n" +
                "            order by patente asc;";
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {

                VehiculoGetSet vehiculo = new VehiculoGetSet();

                vehiculo.setPatente(cursor.getString(cursor.getColumnIndex("patente")));
                vehiculo.setCodigo(cursor.getInt(cursor.getColumnIndex("codigo")));
                vehiculo.setNombre_vehiculo(cursor.getString(cursor.getColumnIndex("razon_social")));

                String a = Integer.toString(Array_vehiculo.size());

                Array_vehiculo.add(vehiculo);
            } while (cursor.moveToNext());
        }
        db.close();
        return Array_vehiculo;
    }
    public Boolean Login(String usuario , String password) {
        boolean salida = false;
        SQLiteDatabase db = getReadableDatabase();
        String selectQuery = "select usuario, password, vigente from usuarios where usuarios.usuario = '"+ usuario +"'" +
                " and password = '"+password+"' and vigente='1'; ";
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.getCount() < 1) {
            cursor.close();
            salida = false;
        }else{
            salida = true;
        }
        db.close();
        return salida;
    }

    public void Insertar(ContentValues vale_encabezado, ContentValues vale_detalle) {

        SQLiteDatabase db = getReadableDatabase();
        db.insert(TABLE_NAME_ENCABEZADO, null, vale_encabezado);

        db.insert(TABLE_NAME_DETALLE, null, vale_detalle);
        db.close();

    }



}
