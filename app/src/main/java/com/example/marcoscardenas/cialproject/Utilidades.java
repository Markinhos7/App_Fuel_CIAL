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
    public static final int COLUMNA_ID_REMOTA = 14;
    public static final int COLUMNA_MES_PROCESO = 1;
    public static final int COLUMNA_SURTIDOR = 2;
    public static final int COLUMNA_VEHICULO = 3;
    public static final int COLUMNA_OBRA = 4;
    public static final int COLUMNA_RECIBE = 5;
    public static final int COLUMNA_USUARIO = 6;
    public static final int COLUMNA_FECHA = 7;
    public static final int COLUMNA_VALE = 8;
    public static final int COLUMNA_GUIA = 9;
    public static final int COLUMNA_SELLO = 10;
    public static final int COLUMNA_HOROMETRO = 11;
    public static final int COLUMNA_KILOMETRO = 12;
    public static final int COLUMNA_OBSERVACIONES = 13;
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

     *
     * @param c cursor
     * @return objeto jason
     */
    public static JSONObject deCursorAJSONObject(Cursor c) {

        JSONObject jObject = new JSONObject();

        String fecha;
        String mes_proceso;
        String surtidor ;
        String vehiculo;
        String obra ;
        String recibe ;
        String usuario ;
        String vale ;
        String guia ;
        String sello ;
        String horometro ;
        String kilometro ;
        String observaciones ;

        mes_proceso = c.getString(COLUMNA_MES_PROCESO);
        surtidor    = c.getString(COLUMNA_SURTIDOR);
        vehiculo    = c.getString(COLUMNA_VEHICULO);
        obra        = c.getString(COLUMNA_OBRA);
        recibe      = c.getString(COLUMNA_RECIBE);
        usuario     = c.getString(COLUMNA_USUARIO);
        fecha       = c.getString(COLUMNA_FECHA);
        vale        = c.getString(COLUMNA_VALE);
        guia        = c.getString(COLUMNA_GUIA);
        sello       = c.getString(COLUMNA_SELLO);
        horometro   = c.getString(COLUMNA_HOROMETRO);
        kilometro   = c.getString(COLUMNA_KILOMETRO);
        observaciones = c.getString(COLUMNA_OBSERVACIONES);


        try {

            jObject.put(ContractParaVale.Columnas.MES_PROCESO, mes_proceso);
            jObject.put(ContractParaVale.Columnas.SURTIDOR, surtidor);
            jObject.put(ContractParaVale.Columnas.VEHICULO, vehiculo);
            jObject.put(ContractParaVale.Columnas.OBRA, obra);
            jObject.put(ContractParaVale.Columnas.RECIBE, recibe);
            jObject.put(ContractParaVale.Columnas.USUARIO, usuario);
            jObject.put(ContractParaVale.Columnas.FECHA, fecha);
            jObject.put(ContractParaVale.Columnas.VALE, vale);
            jObject.put(ContractParaVale.Columnas.GUIA, guia);
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
