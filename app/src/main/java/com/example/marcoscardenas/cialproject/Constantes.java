package com.example.marcoscardenas.cialproject;

/**
 * Constantes
 */
public class Constantes {

    /**
     * Puerto que utilizas para la conexión.
     * Dejalo en blanco si no has configurado esta característica.
     */
    private static final String PUERTO_HOST = ":63343";


    private static final String IP = "http://192.168.0.213:80/serviciosweb";
     // Rutas de los Web Services

    public static final String GET_BY_PATENTE = IP + "/search_vehiculo.php?";
    public static final String GET_BY_CHOFER = IP + "/search_usuario.php?";
    public static final String GET_BY_VALE = IP  +"/Get_vale.php";
    public static final String GET_BY_MES = IP + "/search_mes.php";
    public static final String GET_BY_SURTIDOR = IP + "/search_surtidor.php";
    public static final String POST_VALE = IP + "/insert_Vale.php";

    /**
     * Campos de las respuestas Json
     */
    public static final String ID_GASTO = "id";
    public static final String ESTADO = "estado";
    public static final String GASTOS = "comb2_vales_encabezado";
    public static final String MENSAJE = "mensaje";

    /**
     * Códigos del campo {@link ESTADO}
     */
    public static final String SUCCESS = "1";
    public static final String FAILED = "2";

    /**
     * Tipo de cuenta para la sincronización
     */
    public static final String ACCOUNT_TYPE = "com.example.marcoscardenas.cialproject.account";


}
