package com.example.marcoscardenas.cialproject.Provider;

import android.content.UriMatcher;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Marcos on 23-03-17.
 */

public class ContractParaProveedor {

    /**
     * Autoridad del Content ProviderProvider
     */
    public final static String AUTHORITY = "com.example.marcoscardenas.cialproject";
    /**
     * Representación de la tabla a consultar
     */
    public static final String PROVEEDOR = "proveedores";

    /**
     * Tipo MIME que retorna la consulta de una sola fila
     */
    public final static String SINGLE_MIME_PROVEEDOR =
            "vnd.android.cursor.item/vnd." + AUTHORITY + PROVEEDOR;
    /**
     * Tipo MIME que retorna la consulta de CONTENT_URI
     */
    public final static String MULTIPLE_MIME_PROVEEDOR =
            "vnd.android.cursor.dir/vnd." + AUTHORITY + PROVEEDOR;
    /**
     * URI de contenido principal
     */
    public final static Uri CONTENT_URI_PROVEEDOR =
            Uri.parse("content://" + AUTHORITY + "/" + PROVEEDOR);
    /**
     * Comparador de URIs de contenido
     */
    public static final UriMatcher uriMatcher;
    /**
     * Código para URIs de multiples registros
     */
    public static final int ALLROWS_PROVEEDOR = 13;
    /**
     * Código para URIS de un solo registro
     */
    public static final int SINGLE_ROW_PROVEEDOR = 14;


    // Asignación de URIs
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, PROVEEDOR, ALLROWS_PROVEEDOR);
        uriMatcher.addURI(AUTHORITY, PROVEEDOR + "/#", SINGLE_ROW_PROVEEDOR);
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
        public final static String RUT = "rut";
        public final static String RAZON_SOCIAL = "razon_social";
        public final static String USUARIO_PETROLEO = "usuario_petroleo";

        public static final String ESTADO = "estado";
        public static final String ID_REMOTA = "idRemota";
        public final static String PENDIENTE_INSERCION = "pendiente_insercion";

    }
}

