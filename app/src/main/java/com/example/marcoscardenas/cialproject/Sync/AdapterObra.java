package com.example.marcoscardenas.cialproject.Sync;

/**
 * Created by Marcos on 21-02-17.
 */

import com.example.marcoscardenas.cialproject.Provider.ContractParaObras;

/**
 * Maneja la transferencia de datos entre el servidor y el cliente
 */
public class AdapterObra {

    /**
     * Proyección para las consultas
     */
    private static final String[] PROJECTION = new String[]{
            ContractParaObras.Columnas.COD_OBRA,
            ContractParaObras.Columnas.LOCALIDAD,
            ContractParaObras.Columnas.NOMBRE,
            ContractParaObras.Columnas.FINALIZADA,
            ContractParaObras.Columnas.VISIBLE_PETROLEO,
            ContractParaObras.Columnas.ID_REMOTA,

    };

    // Indices para las columnas indicadas en la proyección
    public static final int COLUMNA_COD_OBRA = 0;
    public static final int COLUMNA_ID_REMOTA = 5;
    public static final int COLUMNA_LOCALIDAD = 1;
    public static final int COLUMNA_NOMBRE = 2;
    public static final int COLUMNA_FINALIZADA = 3;
    public static final int COLUMNA_VISIBLE_PETROLEO = 4;



}