package com.example.marcoscardenas.cialproject;

/**
 * Created by Marcos on 23-03-17.
 */

public class Constantes {

    /**
     * Puerto que utilizas para la conexión.
     */

    private static final String IP = "http://192.168.0.213:80/serviciosweb";
     // Rutas de los Web Services

    public static final String GET_BY_PATENTE = IP + "/search_vehiculo.php";
    public static final String GET_BY_PROVEEDORES = IP + "/search_proveedor.php";
    public static final String GET_BY_VALE = IP  +"/Get_vale.php";
    public static final String GET_BY_MES = IP + "/search_mes.php";
    public static final String GET_BY_OBRA = IP + "/search_obra.php";
    public static final String GET_BY_SURTIDOR = IP + "/search_surtidor.php";
    public static final String GET_BY_USUARIO = IP + "/Search_usuarios.php";
    public static final String POST_VALE = IP + "/insert_Vale.php";

    /**
     * Campos de las respuestas Json
     */
    public static final String ID_GASTO = "Id";
    public static final String ID_OBRA = "cod_obra";
    public static final String ESTADO = "estado";
    public static final String GASTOS = "comb2_vales_encabezado";
    public static final String OBRA = "obras";
    public static final String SURTIDOR = "surtidores";
    public static final String VEHICULO = "vehiculos";
    public static final String USUARIO = "usuarios";
    public static final String MES = "comb2_mes_y_ano_proceso";
    public static final String PROVEEDORES = "proveedores";

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
