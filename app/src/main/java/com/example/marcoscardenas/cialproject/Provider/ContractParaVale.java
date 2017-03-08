package com.example.marcoscardenas.cialproject.Provider;
import android.content.UriMatcher;
import android.net.Uri;
import android.provider.BaseColumns;
/**
 * Created by Marcos on 21-02-17.
 */

public class ContractParaVale {

        /**
         * Autoridad del Content ProviderProvider
         */
        public final static String AUTHORITY
                = "com.example.marcoscardenas.cialproject";
        /**
         * Representación de la tabla a consultar
         */
        public static final String VALE_ENCABEZADO = "comb2_vales_encabezado";
        public static final String VALE_DETALLE = "comb2_vales_encabezado";
        private static final String RUTA_SURTIDOR = "cabeceras_pedidos";
        private static final String RUTA_OBRA = "detalles_pedido";
        private static final String RUTA_ = "productos";
        private static final String RUTA_CLIENTES = "clientes";
        private static final String RUTA_FORMAS_PAGO = "formas_pago";
        /**
         * Tipo MIME que retorna la consulta de una sola fila
         */
        public final static String SINGLE_MIME =
                "vnd.android.cursor.item/vnd." + AUTHORITY + VALE_ENCABEZADO;
        /**
         * Tipo MIME que retorna la consulta de CONTENT_URI
         */
        public final static String MULTIPLE_MIME =
                "vnd.android.cursor.dir/vnd." + AUTHORITY + VALE_ENCABEZADO;
        /**
         * URI de contenido principal
         */
        public final static Uri CONTENT_URI =
                Uri.parse("content://" + AUTHORITY + "/" + VALE_ENCABEZADO);
        /**
         * Comparador de URIs de contenido
         */
        public static final UriMatcher uriMatcher;
        /**
         * Código para URIs de multiples registros
         */
        public static final int ALLROWS = 1;
        /**
         * Código para URIS de un solo registro
         */
        public static final int SINGLE_ROW = 2;


        // Asignación de URIs
        static {
            uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
            uriMatcher.addURI(AUTHORITY, VALE_ENCABEZADO, ALLROWS);
            uriMatcher.addURI(AUTHORITY, VALE_ENCABEZADO + "/#", SINGLE_ROW);
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

            public final static String MES_PROCESO = "id_mes_proceso";
            public final static String SURTIDOR = "id_surtidor";
            public final static String VEHICULO = "id_vehiculo";
            public final static String OBRA = "id_obra";
            public final static String RECIBE = "rut_recibe";
            public final static String USUARIO = "usuario_crea";
            public final static String FECHA = "fecha_crea";
            public final static String VALE = "numero_vale";
            public final static String GUIA = "numero_guia_proveedor";
            public final static String SELLO = "numero_sello";
            public final static String HOROMETRO = "contador_hr";
            public final static String KILOMETRO = "contador_km";
            public final static String OBSERVACIONES = "observaciones";

            public final static String CANTIDAD = "cantidad";
            public final static String PRODUCTO = "id_producto";
            public final static String VALE_ENC = "id_vale_enc";





            public static final String ESTADO = "estado";
            public static final String ID_REMOTA = "idRemota";
            public final static String PENDIENTE_INSERCION = "pendiente_insercion";

        }
    }

