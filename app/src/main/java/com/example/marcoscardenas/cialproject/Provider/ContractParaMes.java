package com.example.marcoscardenas.cialproject.Provider;

import android.content.UriMatcher;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Marcos on 23-03-17.
 */

public class ContractParaMes {

    /**
     * Autoridad del Content ProviderProvider
     */
    public final static String AUTHORITY = "com.example.marcoscardenas.cialproject";
    /**
     * Representación de la tabla a consultar
     */
    public static final String MES = "comb2_mes_y_ano_proceso";

    /**
     * Tipo MIME que retorna la consulta de una sola fila
     */
    public final static String SINGLE_MIME_MES =
            "vnd.android.cursor.item/vnd." + AUTHORITY + MES;
    /**
     * Tipo MIME que retorna la consulta de CONTENT_URI
     */
    public final static String MULTIPLE_MIME_MES =
            "vnd.android.cursor.dir/vnd." + AUTHORITY + MES;
    /**
     * URI de contenido principal
     */
    public final static Uri CONTENT_URI_MES =
            Uri.parse("content://" + AUTHORITY + "/" + MES);
    /**
     * Comparador de URIs de contenido
     */
    public static final UriMatcher uriMatcher;
    /**
     * Código para URIs de multiples registros
     */
    public static final int ALLROWS_MES = 11;
    /**
     * Código para URIS de un solo registro
     */
    public static final int SINGLE_ROW_MES = 12;


    // Asignación de URIs
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, MES, ALLROWS_MES);
        uriMatcher.addURI(AUTHORITY, MES + "/#", SINGLE_ROW_MES);
    }

    // Valores para la columna ESTADO
    public static final int ESTADO_OK = 0;
    public static final int ESTADO_SYNC = 1;


    /**
     * Estructura de la tabla
     */
    public static class Columnas implements BaseColumns {

        private Columnas() {

        }
        public final static String ID = "id";
        public final static String PROCESO = "proceso";
        public final static String ABIERTA = "abierta";
        public final static String MES = "mes";
        public final static String ANO = "ano";

        public static final String ESTADO = "estado";
        public static final String ID_REMOTA = "idRemota";
        public final static String PENDIENTE_INSERCION = "pendiente_insercion";

    }
}

