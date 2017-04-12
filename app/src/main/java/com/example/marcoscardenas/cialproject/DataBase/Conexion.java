package com.example.marcoscardenas.cialproject.DataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.marcoscardenas.cialproject.Model.ProveedorGetSet;
import com.example.marcoscardenas.cialproject.Model.MesprocesoGetSet;
import com.example.marcoscardenas.cialproject.Model.ObraGetSet;
import com.example.marcoscardenas.cialproject.Model.SurtidorGetSet;
import com.example.marcoscardenas.cialproject.Model.VehiculoGetSet;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

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
        String selectQuery = "SELECT proceso FROM comb2_mes_y_ano_proceso where  mes = 0"+ mes +" and ano = "+ año +" and  abierta = 1";

        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {

                MesprocesoGetSet new_mes = new MesprocesoGetSet();

                new_mes.setProceso(cursor.getString(cursor.getColumnIndex("proceso")));

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

    public ArrayList<ProveedorGetSet> getChofer(String usuario) {

        ArrayList<ProveedorGetSet> Array_chofer = new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();
        String selectQuery = "select rut, razon_social from proveedores where usuario_petroleo = 'True' and  (razon_social like '%" + usuario + "%' or rut like '% " + usuario +"%');";
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {

                ProveedorGetSet chofer = new ProveedorGetSet();

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
                "            codigo, \n" +
                "            patente\n" +
                "            FROM \n" +
                "                vehiculos \n" +
                "            WHERE \n" +
                "            patente like '%"+ patente +"%' \n" +
                "            AND fecha_termino_contrato >= date() \n" +
                "            AND forma_pago IS NOT NULL \n" +
                "            AND valor_pago IS NOT NULL;";
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {

                VehiculoGetSet vehiculo = new VehiculoGetSet();

                vehiculo.setPatente(cursor.getString(cursor.getColumnIndex("patente")));
                vehiculo.setCodigo(cursor.getString(cursor.getColumnIndex("codigo")));

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

    public void InsertVale(ContentValues vale_encabezado, ContentValues vale_detalle) {

        SQLiteDatabase db = getReadableDatabase();
        db.insert(TABLE_NAME_ENCABEZADO, null, vale_encabezado);

        db.insert(TABLE_NAME_DETALLE, null, vale_detalle);
        db.close();

    }
    public String SearchVehiculo(String patente){

        String id_vehiculo = "";
        SQLiteDatabase db = getReadableDatabase();
        String selectQuery = "SELECT \n" +
                "            idRemota, \n" +
                "            patente\n" +
                "            FROM \n" +
                "                vehiculos \n" +
                "            WHERE \n" +
                "            patente like '%"+ patente +"%' \n" +
                "            AND fecha_termino_contrato >= date() \n" +
                "            AND forma_pago IS NOT NULL \n" +
                "            AND valor_pago IS NOT NULL;";
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {


                id_vehiculo = cursor.getString(cursor.getColumnIndex("idRemota"));


            } while (cursor.moveToNext());
        }
        db.close();
        return id_vehiculo;

    }
    public String SearchObra(String obra){

        String id_obra = "";
        SQLiteDatabase db = getReadableDatabase();
        String selectQuery = "Select idRemota from obras where nombre = '"+obra + "'";
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {


                id_obra = cursor.getString(cursor.getColumnIndex("idRemota"));


            } while (cursor.moveToNext());
        }
        db.close();
        return id_obra;

    }
    public String SearchSurtidor(String surtidor){

            String id_surtidor = "";
            SQLiteDatabase db = getReadableDatabase();
            String selectQuery = "Select idRemota from surtidores where `descripcion` = '"+surtidor+"'";
            Cursor cursor = db.rawQuery(selectQuery, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {


                    id_surtidor = cursor.getString(cursor.getColumnIndex("idRemota"));


                } while (cursor.moveToNext());
            }
            db.close();
            return id_surtidor;

        }

    public String SearchMes(String mes){

        String id_mes = "";
        SQLiteDatabase db = getReadableDatabase();
        String selectQuery = "Select idRemota from comb2_mes_y_ano_proceso where `proceso` = '"+mes+"'";
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {


                id_mes = cursor.getString(cursor.getColumnIndex("idRemota"));


            } while (cursor.moveToNext());
        }
        db.close();
        return id_mes;

    }

}
