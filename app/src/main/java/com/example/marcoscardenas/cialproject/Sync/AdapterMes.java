package com.example.marcoscardenas.cialproject.Sync;

/**
 * Created by Marcos on 21-02-17.
 */

import com.example.marcoscardenas.cialproject.Provider.ContractParaMes;

/**
 * Maneja la transferencia de datos entre el servidor y el cliente
 */
public class AdapterMes {

    /**
     * Proyección para las consultas
     */
    public static final String[] PROJECTION = new String[]{
            ContractParaMes.Columnas.ID,
            ContractParaMes.Columnas.PROCESO,
            ContractParaMes.Columnas.ABIERTA,
            ContractParaMes.Columnas.MES,
            ContractParaMes.Columnas.ANO,
            ContractParaMes.Columnas.ID_REMOTA,

    };

    // Indices para las columnas indicadas en la proyección
    public static final int COLUMNA_ID = 0;
    public static final int COLUMNA_ID_REMOTA = 5;
    public static final int COLUMNA_PROCESO = 1;
    public static final int COLUMNA_ABIERTA = 2;
    public static final int COLUMNA_MES = 3;
    public static final int COLUMNA_ANO = 4;



}