package com.example.marcoscardenas.cialproject;

import android.database.Cursor;
import android.os.Build;
import android.util.Log;

import com.example.marcoscardenas.cialproject.Provider.ContractParaVale;


import org.json.JSONException;
import org.json.JSONObject;

public class Utilidades {
    // Indices para las columnas indicadas en la proyección
    public static final int COLUMNA_ID = 0;
    public static final int COLUMNA_ID_REMOTA = 1;
    public static final int COLUMNA_MES_PROCESO = 2;
    public static final int COLUMNA_SURTIDOR = 3;
    public static final int COLUMNA_VEHICULO = 4;
    public static final int COLUMNA_OBRA = 5;
    public static final int COLUMNA_RECIBE = 6;
    public static final int COLUMNA_USUARIO = 7;
    public static final int COLUMNA_FECHA = 8;
    public static final int COLUMNA_VALE = 9;
    public static final int COLUMNA_GUIA = 10;
    public static final int COLUMNA_SELLO = 11;
    public static final int COLUMNA_HOROMETRO = 12;
    public static final int COLUMNA_KILOMETRO = 13;
    public static final int COLUMNA_OBSERVACIONES = 14;
    /**
     * Determina si la aplicación corre en versiones superiores o iguales
     * a Android LOLLIPOP
     *
     * @return booleano de confirmación
     */
    public static boolean materialDesign() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    /**
     * Copia los datos de un gasto almacenados en un cursor hacia un
     * JSONObject
     *
     * @param c cursor
     * @return objeto jason
     */
    public static JSONObject deCursorAJSONObject(Cursor c) {
        JSONObject jObject = new JSONObject();
        String monto;
        String etiqueta;
        String fecha;
        String descripcion;

        int mes_proceso;
        int surtidor ;
        int vehiculo;
        int obra ;
        String recibe ;
        String usuario ;
        int vale ;
        int guia ;
        int sello ;
        int horometro ;
        int kilometro ;
        String observaciones ;

        mes_proceso = c.getInt(COLUMNA_MES_PROCESO);
        surtidor    = c.getInt(COLUMNA_SURTIDOR);
        obra        = c.getInt(COLUMNA_OBRA);
        vehiculo    = c.getInt(COLUMNA_VEHICULO);
        recibe      = c.getString(COLUMNA_RECIBE);
        usuario     = c.getString(COLUMNA_USUARIO);
        fecha       = c.getString(COLUMNA_FECHA);
        vale        = c.getInt(COLUMNA_VALE);
        guia        = c.getInt(COLUMNA_GUIA);
        sello       = c.getInt(COLUMNA_SELLO);
        horometro   = c.getInt(COLUMNA_HOROMETRO);
        kilometro   = c.getInt(COLUMNA_KILOMETRO);
        observaciones = c.getString(COLUMNA_OBSERVACIONES);


        try {
            jObject.put(ContractParaVale.Columnas.OBRA, obra);
            jObject.put(ContractParaVale.Columnas.SURTIDOR, surtidor);
            jObject.put(ContractParaVale.Columnas.FECHA, fecha);
            jObject.put(ContractParaVale.Columnas.VEHICULO, vehiculo);
            jObject.put(ContractParaVale.Columnas.MES_PROCESO, mes_proceso);
            jObject.put(ContractParaVale.Columnas.USUARIO, usuario);
            jObject.put(ContractParaVale.Columnas.VALE, vale);
            jObject.put(ContractParaVale.Columnas.GUIA, guia);
            jObject.put(ContractParaVale.Columnas.RECIBE, recibe);
            jObject.put(ContractParaVale.Columnas.SELLO, sello);
            jObject.put(ContractParaVale.Columnas.HOROMETRO, horometro);
            jObject.put(ContractParaVale.Columnas.KILOMETRO, kilometro);
            jObject.put(ContractParaVale.Columnas.OBSERVACIONES, observaciones);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.i("Cursor a JSONObject", String.valueOf(jObject));

        return jObject;
    }
}
