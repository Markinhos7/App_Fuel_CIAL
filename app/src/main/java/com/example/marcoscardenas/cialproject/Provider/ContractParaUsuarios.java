package com.example.marcoscardenas.cialproject.Provider;

import android.content.UriMatcher;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Marcos on 23-03-17.
 */

public class ContractParaUsuarios {

    /**
     * Autoridad del Content ProviderProvider
     */
    public final static String AUTHORITY = "com.example.marcoscardenas.cialproject";
    /**
     * Representación de la tabla a consultar
     */
    public static final String USUARIO = "usuarios";

    /**
     * Tipo MIME que retorna la consulta de una sola fila
     */
    public final static String SINGLE_MIME_USUARIO =
            "vnd.android.cursor.item/vnd." + AUTHORITY + USUARIO;
    /**
     * Tipo MIME que retorna la consulta de CONTENT_URI
     */
    public final static String MULTIPLE_MIME_USUARIO =
            "vnd.android.cursor.dir/vnd." + AUTHORITY + USUARIO;
    /**
     * URI de contenido principal
     */
    public final static Uri CONTENT_URI_USUARIO =
            Uri.parse("content://" + AUTHORITY + "/" + USUARIO);
    /**
     * Comparador de URIs de contenido
     */
    public static final UriMatcher uriMatcher;
    /**
     * Código para URIs de multiples registros
     */
    public static final int ALLROWS_USUARIO = 9;
    /**
     * Código para URIS de un solo registro
     */
    public static final int SINGLE_ROW_USUARIO = 10;


    // Asignación de URIs
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, USUARIO, ALLROWS_USUARIO);
        uriMatcher.addURI(AUTHORITY, USUARIO + "/#", SINGLE_ROW_USUARIO);
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
        public final static String INDICE = "indice";
        public final static String USUARIO = "usuario";
        public final static String PASSWORD = "password";
        public final static String VIGENTE = "vigente";

        public static final String ESTADO = "estado";
        public static final String ID_REMOTA = "idRemota";
        public final static String PENDIENTE_INSERCION = "pendiente_insercion";

    }
}

