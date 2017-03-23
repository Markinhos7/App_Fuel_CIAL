package com.example.marcoscardenas.cialproject.Provider;

import android.content.UriMatcher;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Marcos on 23-03-17.
 */

public class ContractParaSurtidor {

    /**
     * Autoridad del Content ProviderProvider
     */
    public final static String AUTHORITY = "com.example.marcoscardenas.cialproject";
    /**
     * Representación de la tabla a consultar
     */
    public static final String SURTIDOR = "surtidores";

    /**
     * Tipo MIME que retorna la consulta de una sola fila
     */
    public final static String SINGLE_MIME_SURTIDOR =
            "vnd.android.cursor.item/vnd." + AUTHORITY + SURTIDOR;
    /**
     * Tipo MIME que retorna la consulta de CONTENT_URI
     */
    public final static String MULTIPLE_MIME_SURTIDOR =
            "vnd.android.cursor.dir/vnd." + AUTHORITY + SURTIDOR;
    /**
     * URI de contenido principal
     */
    public final static Uri CONTENT_URI_SURTIDOR =
            Uri.parse("content://" + AUTHORITY + "/" + SURTIDOR);
    /**
     * Comparador de URIs de contenido
     */
    public static final UriMatcher uriMatcher;
    /**
     * Código para URIs de multiples registros
     */
    public static final int ALLROWS_SURTIDOR = 5;
    /**
     * Código para URIS de un solo registro
     */
    public static final int SINGLE_ROW_SURTIDOR = 6;


    // Asignación de URIs
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, SURTIDOR, ALLROWS_SURTIDOR);
        uriMatcher.addURI(AUTHORITY, SURTIDOR + "/#", SINGLE_ROW_SURTIDOR);
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
        public final static String CODIGO = "codigo";
        public final static String DESCRIPCION = "descripcion";
        public final static String VIGENTE = "vigente";

        public static final String ESTADO = "estado";
        public static final String ID_REMOTA = "idRemota";
        public final static String PENDIENTE_INSERCION = "pendiente_insercion";

    }
}

