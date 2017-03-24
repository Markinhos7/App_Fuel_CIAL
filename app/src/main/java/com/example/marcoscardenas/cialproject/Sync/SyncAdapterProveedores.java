package com.example.marcoscardenas.cialproject.Sync;

/**
 * Created by Marcos on 21-02-17.
 */

import android.content.ContentResolver;

import com.example.marcoscardenas.cialproject.Provider.ContractParaProveedor;
import com.example.marcoscardenas.cialproject.Provider.ContractParaUsuarios;

import com.google.gson.Gson;


/**
 * Maneja la transferencia de datos entre el servidor y el cliente
 */
public class SyncAdapterProveedores {
    private static final String TAG = SyncAdapterUsuarios.class.getSimpleName();

    ContentResolver resolver;
    private Gson gson = new Gson();

    /**
     * Proyección para las consultas
     */
    public static final String[] PROJECTION = new String[]{
            ContractParaProveedor.Columnas.RUT,
            ContractParaProveedor.Columnas.RAZON_SOCIAL,
            ContractParaProveedor.Columnas.USUARIO_PETROLEO,
            ContractParaProveedor.Columnas.ID_REMOTA,

    };

    // Indices para las columnas indicadas en la proyección
    public static final int COLUMNA_RUT = 0;
    public static final int COLUMNA_ID_REMOTA = 3;
    public static final int COLUMNA_RAZON_SOCIAL = 1;
    public static final int COLUMNA_USUARIO_PETROLEO = 2;



}