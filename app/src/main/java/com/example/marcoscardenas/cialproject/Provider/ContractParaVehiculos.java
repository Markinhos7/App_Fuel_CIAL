package com.example.marcoscardenas.cialproject.Provider;

import android.content.UriMatcher;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Marcos on 23-03-17.
 */

public class ContractParaVehiculos {

    /**
     * Autoridad del Content ProviderProvider
     */
    public final static String AUTHORITY = "com.example.marcoscardenas.cialproject";
    /**
     * Representación de la tabla a consultar
     */
    public static final String VEHICULO = "vehiculos";

    /**
     * Tipo MIME que retorna la consulta de una sola fila
     */
    public final static String SINGLE_MIME_VEHICULO =
            "vnd.android.cursor.item/vnd." + AUTHORITY + VEHICULO;
    /**
     * Tipo MIME que retorna la consulta de CONTENT_URI
     */
    public final static String MULTIPLE_MIME_VEHICULO =
            "vnd.android.cursor.dir/vnd." + AUTHORITY + VEHICULO;
    /**
     * URI de contenido principal
     */
    public final static Uri CONTENT_URI_VEHICULO =
            Uri.parse("content://" + AUTHORITY + "/" + VEHICULO);
    /**
     * Comparador de URIs de contenido
     */
    public static final UriMatcher uriMatcher;
    /**
     * Código para URIs de multiples registros
     */
    public static final int ALLROWS_VEHICULO = 7;
    /**
     * Código para URIS de un solo registro
     */
    public static final int SINGLE_ROW_VEHICULO = 8;


    // Asignación de URIs
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, VEHICULO, ALLROWS_VEHICULO);
        uriMatcher.addURI(AUTHORITY, VEHICULO + "/#", SINGLE_ROW_VEHICULO);
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
        public final static String PATENTE = "patente";
        public final static String FECHA_TERMINO_CONTRATO = "fecha_termino_contrato";
        public final static String VALOR_PAGO = "valor_pago";
        public final static String FORMA_PAGO = "forma_pago";


        public static final String ESTADO = "estado";
        public static final String ID_REMOTA = "idRemota";
        public final static String PENDIENTE_INSERCION = "pendiente_insercion";

    }
}

