package com.example.marcoscardenas.cialproject.Provider;
import android.content.UriMatcher;
import android.net.Uri;
import android.provider.BaseColumns;
/**
 * Created by Marcos on 21-02-17.
 */

public class ContractParaObras {

    /**
     * Autoridad del Content ProviderProvider
     */
    public final static String AUTHORITY = "com.example.marcoscardenas.cialproject";
    /**
     * Representación de la tabla a consultar
     */
    public static final String OBRA = "obras";

    /**
     * Tipo MIME que retorna la consulta de una sola fila
     */
    public final static String SINGLE_MIME_OBRA =
            "vnd.android.cursor.item/vnd." + AUTHORITY + OBRA;
    /**
     * Tipo MIME que retorna la consulta de CONTENT_URI
     */
    public final static String MULTIPLE_MIME_OBRA =
            "vnd.android.cursor.dir/vnd." + AUTHORITY + OBRA;
    /**
     * URI de contenido principal
     */
    public final static Uri CONTENT_URI_OBRA =
            Uri.parse("content://" + AUTHORITY + "/" + OBRA);
    /**
     * Comparador de URIs de contenido
     */
    public static final UriMatcher uriMatcher;
    /**
     * Código para URIs de multiples registros
     */
    public static final int ALLROWS_OBRA = 3;
    /**
     * Código para URIS de un solo registro
     */
    public static final int SINGLE_ROW_OBRA = 4;


    // Asignación de URIs
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, OBRA, ALLROWS_OBRA);
        uriMatcher.addURI(AUTHORITY, OBRA + "/#", SINGLE_ROW_OBRA);
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
        public final static String COD_OBRA = "cod_obra";
        public final static String LOCALIDAD = "localidad";
        public final static String NOMBRE = "nombre";
        public final static String FINALIZADA = "finalizada";
        public final static String VISIBLE_PETROLEO = "visible_petroleo";

        public static final String ESTADO = "estado";
        public static final String ID_REMOTA = "idRemota";
        public final static String PENDIENTE_INSERCION = "pendiente_insercion";

    }
}

