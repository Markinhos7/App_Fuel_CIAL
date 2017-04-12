package com.example.marcoscardenas.cialproject.Sync;

/**
 * Created by Marcos on 21-02-17.
 */
import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.content.SyncResult;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.marcoscardenas.cialproject.Model.GetVale;
import com.example.marcoscardenas.cialproject.Model.MesprocesoGetSet;
import com.example.marcoscardenas.cialproject.Model.ObraGetSet;
import com.example.marcoscardenas.cialproject.Model.ProveedorGetSet;
import com.example.marcoscardenas.cialproject.Model.SurtidorGetSet;
import com.example.marcoscardenas.cialproject.Model.UsuarioGetSet;
import com.example.marcoscardenas.cialproject.Model.VehiculoGetSet;
import com.example.marcoscardenas.cialproject.Provider.ContractParaMes;
import com.example.marcoscardenas.cialproject.Provider.ContractParaObras;
import com.example.marcoscardenas.cialproject.Provider.ContractParaProveedor;
import com.example.marcoscardenas.cialproject.Provider.ContractParaSurtidor;
import com.example.marcoscardenas.cialproject.Provider.ContractParaUsuarios;
import com.example.marcoscardenas.cialproject.Provider.ContractParaVale;
import com.example.marcoscardenas.cialproject.Provider.ContractParaVehiculos;
import com.example.marcoscardenas.cialproject.R;
import com.google.gson.Gson;
import com.example.marcoscardenas.cialproject.Utilidades;
import com.example.marcoscardenas.cialproject.Constantes;
import com.example.marcoscardenas.cialproject.VolleySingleton;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Maneja los datos entre el servidor y el cliente
 */
public class SyncAdapter extends AbstractThreadedSyncAdapter {
    private static final String TAG = SyncAdapter.class.getSimpleName();

    ContentResolver resolver;
    private Gson gson = new Gson();

       /**
     * Proyección para las consultas Obra
     *
     *
     */
    private static final String[] PROJECTION_OBRA = new String[]{
            ContractParaObras.Columnas.COD_OBRA,
            ContractParaObras.Columnas.LOCALIDAD,
            ContractParaObras.Columnas.NOMBRE,
            ContractParaObras.Columnas.FINALIZADA,
            ContractParaObras.Columnas.VISIBLE_PETROLEO,
            ContractParaObras.Columnas.ID_REMOTA,

    };

    /**
     * Proyección para las consultas Vales
     *
     *
     */
    private static final String[] PROJECTION = new String[]{
            ContractParaVale.Columnas.ID,
            ContractParaVale.Columnas.MES_PROCESO,
            ContractParaVale.Columnas.SURTIDOR,
            ContractParaVale.Columnas.VEHICULO,
            ContractParaVale.Columnas.OBRA,
            ContractParaVale.Columnas.RECIBE,
            ContractParaVale.Columnas.USUARIO,
            ContractParaVale.Columnas.FECHA,
            ContractParaVale.Columnas.VALE,
            ContractParaVale.Columnas.GUIA,
            ContractParaVale.Columnas.SELLO,
            ContractParaVale.Columnas.HOROMETRO,
            ContractParaVale.Columnas.KILOMETRO,
            ContractParaVale.Columnas.OBSERVACIONES,
            ContractParaVale.Columnas.ID_REMOTA
    };

    // Indices para las columnas indicadas en la proyección
    public static final int COLUMNA_ID = 0;
    public static final int COLUMNA_ID_REMOTA = 14;
    public static final int COLUMNA_MES_PROCESO = 1;
    public static final int COLUMNA_SURTIDOR = 2;
    public static final int COLUMNA_VEHICULO = 3;
    public static final int COLUMNA_OBRA = 4;
    public static final int COLUMNA_RECIBE = 5;
    public static final int COLUMNA_USUARIO = 6;
    public static final int COLUMNA_FECHA = 7;
    public static final int COLUMNA_VALE = 8;
    public static final int COLUMNA_GUIA = 9;
    public static final int COLUMNA_SELLO = 10;
    public static final int COLUMNA_HOROMETRO = 11;
    public static final int COLUMNA_KILOMETRO = 12;
    public static final int COLUMNA_OBSERVACIONES = 13;


    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        resolver = context.getContentResolver();
    }

    /**
     * Constructor para mantener compatibilidad en versiones inferiores a 3.0
     */
    public SyncAdapter(
            Context context,
            boolean autoInitialize,
            boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        resolver = context.getContentResolver();
    }

    public static void inicializarSyncAdapter(Context context) {
        obtenerCuentaASincronizar(context);
    }

    @Override
    public void onPerformSync(Account account,
                              Bundle extras,
                              String authority,
                              ContentProviderClient provider,
                              final SyncResult syncResult) {

        Log.i(TAG, "onPerformSync()...");

        boolean soloSubida = extras.getBoolean(ContentResolver.SYNC_EXTRAS_UPLOAD, false);

        if (!soloSubida) {
            realizarSincronizacionLocal(syncResult);
            realizarSincronizacionLocalObra(syncResult);
            realizarSincronizacionLocalSurtidores(syncResult);
            realizarSincronizacionLocalVehiculos(syncResult);
            realizarSincronizacionLocalUsuarios(syncResult);
            realizarSincronizacionLocalMes(syncResult);
            realizarSincronizacionLocalProveedores(syncResult);
            //realizarSincronizacionRemota();

        } else {
            realizarSincronizacionRemota();
        }
    }

    /**
     * Realiza Sincronizacion local de datos
     * @param syncResult
     */
    private void realizarSincronizacionLocal(final SyncResult syncResult) {
        Log.i(TAG, "Actualizando el cliente.");
        VolleySingleton.getInstance(getContext()).addToRequestQueue(
                new JsonObjectRequest(
                Request.Method.GET,
                Constantes.GET_BY_VALE,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        procesarRespuestaGet(response, syncResult);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, " Error de sincronizacion servidor local" +error.getMessage());
                        Toast toast1 =
                                Toast.makeText(getContext(),
                                        "Error de Internet", Toast.LENGTH_SHORT);

                        toast1.show();
                    }
                }
        )

        );
    }
    /**
     * Realiza Sincronizacion local de datos
     * @param syncResult
     */
    private void realizarSincronizacionLocalObra(final SyncResult syncResult) {
        Log.i(TAG, "Actualizando el cliente.");
        VolleySingleton.getInstance(getContext()).addToRequestQueue(
                new JsonObjectRequest(
                        Request.Method.GET,
                        Constantes.GET_BY_OBRA,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                procesarRespuestaGetObra(response, syncResult);
                                Log.i("Procesa respuesta Obra", "Actualizando el cliente.");
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d(TAG, " Error de sincronizacion servidor local" +error.getMessage());
                            }
                        }
                )

        );
    }
    /**
     * Realiza Sincronizacion local de datos
     * @param syncResult
     */
    private void realizarSincronizacionLocalSurtidores(final SyncResult syncResult) {
        Log.i(TAG, "Actualizando el cliente.");
        VolleySingleton.getInstance(getContext()).addToRequestQueue(
                new JsonObjectRequest(
                        Request.Method.GET,
                        Constantes.GET_BY_SURTIDOR,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                procesarRespuestaGetSurtidor(response, syncResult);
                                Log.i("Procesa  Surtidor", "Actualizando el cliente.");
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d(TAG, " Error de sincronizacion servidor local" +error.getMessage());
                            }
                        }
                )

        );
    }
    /**
     * Realiza Sincronizacion local de datos
     * @param syncResult
     */
    private void realizarSincronizacionLocalVehiculos(final SyncResult syncResult) {
        Log.i(TAG, "Actualizando el cliente.");
        VolleySingleton.getInstance(getContext()).addToRequestQueue(
                new JsonObjectRequest(
                        Request.Method.GET,
                        Constantes.GET_BY_PATENTE,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                procesarRespuestaGetVehiculo(response, syncResult);
                                Log.i("Procesa  Vehiculo", "Actualizando el cliente.");
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d(TAG, " Error de sincronizacion servidor local" +error.getMessage());
                            }
                        }
                )

        );
    }
    /**
     * Realiza Sincronizacion local de datos
     * @param syncResult
     */
    private void realizarSincronizacionLocalUsuarios(final SyncResult syncResult) {
        Log.i(TAG, "Actualizando el cliente.");
        VolleySingleton.getInstance(getContext()).addToRequestQueue(
                new JsonObjectRequest(
                        Request.Method.GET,
                        Constantes.GET_BY_USUARIO,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                procesarRespuestaGetUsuarios(response, syncResult);
                                Log.i("Procesa Usuarios", "Actualizando el cliente.");
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d(TAG, " Error de sincronizacion servidor local" +error.getMessage());
                            }
                        }
                )

        );
    }
    /**
     * Realiza Sincronizacion local de datos
     * @param syncResult
     */
    private void realizarSincronizacionLocalMes(final SyncResult syncResult) {
        Log.i(TAG, "Actualizando el cliente.");
        VolleySingleton.getInstance(getContext()).addToRequestQueue(
                new JsonObjectRequest(
                        Request.Method.GET,
                        Constantes.GET_BY_MES,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                procesarRespuestaGetMes(response, syncResult);
                                Log.i("Procesa  Vehiculo", "Actualizando el cliente.");
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d(TAG, " Error de sincronizacion servidor local" +error.getMessage());
                            }
                        }
                )

        );
    }
    /**
     * Realiza Sincronizacion local de datos
     * @param syncResult
     */
    private void realizarSincronizacionLocalProveedores(final SyncResult syncResult) {
        Log.i(TAG, "Actualizando el cliente.");
        VolleySingleton.getInstance(getContext()).addToRequestQueue(
                new JsonObjectRequest(
                        Request.Method.GET,
                        Constantes.GET_BY_PROVEEDORES,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                procesarRespuestaGetProveedores(response, syncResult);
                                Log.i("Procesa  Proveedores", "Actualizando el cliente.");
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d(TAG, " Error de sincronizacion servidor local" +error.getMessage());
                            }
                        }
                )

        );
    }
    /**
     * Procesa la respuesta del servidor al pedir que se retornen todos los gastos.
     *
     * @param response   Respuesta en formato Json
     * @param syncResult Registro de resultados de sincronización
     */
    private void procesarRespuestaGet(JSONObject response, SyncResult syncResult) {
        try {
            // Obtener atributo "estado"
            String estado = response.getString(Constantes.ESTADO);
            Log.d("Procesar Respuesta:",estado);
            switch (estado) {
                case Constantes.SUCCESS: // EXITO
                    actualizarDatosLocales(response, syncResult);
                    break;
                case Constantes.FAILED: // FALLIDO
                    String mensaje = response.getString(Constantes.MENSAJE);
                    Log.i(TAG, mensaje);
                    break;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    /**
     * Procesa la respuesta del servidor al pedir que se retornen todos los gastos.
     *
     * @param response   Respuesta en formato Json
     * @param syncResult Registro de resultados de sincronización
     */
    private void procesarRespuestaGetObra(JSONObject response, SyncResult syncResult) {
        try {
            // Obtener atributo "estado"
            String estado = response.getString(Constantes.ESTADO);
            Log.d("Procesar Respuesta:",estado);
            switch (estado) {
                case Constantes.SUCCESS: // EXITO
                    actualizarDatosLocalesObra(response, syncResult);
                    break;
                case Constantes.FAILED: // FALLIDO
                    String mensaje = response.getString(Constantes.MENSAJE);
                    Log.i(TAG, mensaje);
                    break;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    /**
     * Procesa la respuesta del servidor al pedir que se retornen todos los gastos.
     *
     * @param response   Respuesta en formato Json
     * @param syncResult Registro de resultados de sincronización
     */
    private void procesarRespuestaGetSurtidor(JSONObject response, SyncResult syncResult) {
        try {
            // Obtener atributo "estado"
            String estado = response.getString(Constantes.ESTADO);
            Log.d("Procesar Respuesta:",estado);
            switch (estado) {
                case Constantes.SUCCESS: // EXITO
                    actualizarDatosLocalesSurtidor(response, syncResult);
                    break;
                case Constantes.FAILED: // FALLIDO
                    String mensaje = response.getString(Constantes.MENSAJE);
                    Log.i(TAG, mensaje);
                    break;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    /**
     * Procesa la respuesta del servidor al pedir que se retornen todos los gastos.
     *
     * @param response   Respuesta en formato Json
     * @param syncResult Registro de resultados de sincronización
     */
    private void procesarRespuestaGetVehiculo(JSONObject response, SyncResult syncResult) {
        try {
            // Obtener atributo "estado"
            String estado = response.getString(Constantes.ESTADO);
            Log.d("Procesar Respuesta:",estado);
            switch (estado) {
                case Constantes.SUCCESS: // EXITO
                    actualizarDatosLocalesVehiculos(response, syncResult);
                    break;
                case Constantes.FAILED: // FALLIDO
                    String mensaje = response.getString(Constantes.MENSAJE);
                    Log.i(TAG, mensaje);
                    break;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    /**
     * Procesa la respuesta del servidor al pedir que se retornen todos los gastos.
     *
     * @param response   Respuesta en formato Json
     * @param syncResult Registro de resultados de sincronización
     */
    private void procesarRespuestaGetUsuarios(JSONObject response, SyncResult syncResult) {
        try {
            // Obtener atributo "estado"
            String estado = response.getString(Constantes.ESTADO);
            Log.d("Procesar Respuesta:",estado);
            switch (estado) {
                case Constantes.SUCCESS: // EXITO
                    actualizarDatosLocalesUsuarios(response, syncResult);
                    break;
                case Constantes.FAILED: // FALLIDO
                    String mensaje = response.getString(Constantes.MENSAJE);
                    Log.i(TAG, mensaje);
                    break;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    /**
     * Procesa la respuesta del servidor al pedir que se retornen todos los gastos.
     *
     * @param response   Respuesta en formato Json
     * @param syncResult Registro de resultados de sincronización
     */
    private void procesarRespuestaGetMes(JSONObject response, SyncResult syncResult) {
        try {
            // Obtener atributo "estado"
            String estado = response.getString(Constantes.ESTADO);
            Log.d("Procesar Respuesta:",estado);
            switch (estado) {
                case Constantes.SUCCESS: // EXITO
                    actualizarDatosLocalesMes(response, syncResult);
                    break;
                case Constantes.FAILED: // FALLIDO
                    String mensaje = response.getString(Constantes.MENSAJE);
                    Log.i(TAG, mensaje);
                    break;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    /**
     * Procesa la respuesta del servidor al pedir que se retornen todos los gastos.
     *
     * @param response   Respuesta en formato Json
     * @param syncResult Registro de resultados de sincronización
     */
    private void procesarRespuestaGetProveedores(JSONObject response, SyncResult syncResult) {
        try {
            // Obtener atributo "estado"
            String estado = response.getString(Constantes.ESTADO);
            Log.d("Procesar Respuesta:",estado);
            switch (estado) {
                case Constantes.SUCCESS: // EXITO
                    actualizarDatosLocalesProveedores(response, syncResult);
                    break;
                case Constantes.FAILED: // FALLIDO
                    String mensaje = response.getString(Constantes.MENSAJE);
                    Log.i(TAG, mensaje);
                    break;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void realizarSincronizacionRemota() {
        Log.i(TAG, "Actualizando el servidor...");

        iniciarActualizacion();
        JsonObjectRequest jsonObjectRequests;
        Cursor c = obtenerRegistrosSucios();

        Log.i(TAG, "Se encontraron " + c.getCount() + " registros sucios.");

        if (c.getCount() > 0) {
            while (c.moveToNext()) {
                final int idLocal = c.getInt(COLUMNA_ID);
                System.out.println("ID :"+idLocal);

                VolleySingleton.getInstance(getContext()).addToRequestQueue(
                        jsonObjectRequests = new JsonObjectRequest(
                                Request.Method.POST,
                                Constantes.POST_VALE,
                                Utilidades.deCursorAJSONObject(c),
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        procesarRespuestaInsert(response, idLocal);
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Log.d(TAG, "Error Volley adapter: " + error.getMessage());
                                    }
                                }

                        )

                        {
                            @Override
                            public Map<String, String> getHeaders() {
                                Map<String, String> headers = new HashMap<String, String>();
                                headers.put("Content-Type", "application/json; charset=utf-8");
                                headers.put("Accept", "application/json");
                                return headers;
                            }

                            @Override
                            public String getBodyContentType() {
                                return "application/json; charset=utf-8" + getParamsEncoding();
                            }
                        }
                );
                jsonObjectRequests.setRetryPolicy(new DefaultRetryPolicy(500000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            }

        } else {
            Log.i(TAG, "No se requiere sincronización");
        }
        c.close();
    }
    /**
     * Obtiene el registro que se acaba de marcar como "pendiente por sincronizar" y
     * con "estado de sincronización"
     *
     * @return Cursor con el registro.
     */
    private Cursor obtenerRegistrosSucios() {
        Uri uri = ContractParaVale.CONTENT_URI;
        String selection = ContractParaVale.Columnas.PENDIENTE_INSERCION + "=? AND "
                + ContractParaVale.Columnas.ESTADO + "=?";
        String[] selectionArgs = new String[]{"1", ContractParaVale.ESTADO_SYNC + ""};

        return resolver.query(uri, PROJECTION, selection, selectionArgs, null);
    }
    /**
     * Cambia a estado "de sincronización" el registro que se acaba de insertar localmente
     */
    private void iniciarActualizacion() {
        Uri uri = ContractParaVale.CONTENT_URI;
        String selection = ContractParaVale.Columnas.PENDIENTE_INSERCION + "=? AND "
                + ContractParaVale.Columnas.ESTADO + "=?";
        String[] selectionArgs = new String[]{"1", ContractParaVale.ESTADO_OK + ""};

        ContentValues v = new ContentValues();
        v.put(ContractParaVale.Columnas.ESTADO, ContractParaVale.ESTADO_SYNC);

        int results = resolver.update(uri, v, selection, selectionArgs);
        Log.i(TAG, "Registros puestos en cola de inserción:" + results);
    }
    /**
     * Limpia el registro que se sincronizó y le asigna la nueva id remota proveida
     * por el servidor
     *
     * @param idRemota id remota
     */
    private void finalizarActualizacion(String idRemota, int idLocal) {
        Uri uri = ContractParaVale.CONTENT_URI;
        String selection = ContractParaVale.Columnas.ID + "=?";
        String[] selectionArgs = new String[]{String.valueOf(idLocal)};

        ContentValues v = new ContentValues();
        v.put(ContractParaVale.Columnas.PENDIENTE_INSERCION, "0");
        v.put(ContractParaVale.Columnas.ESTADO, ContractParaVale.ESTADO_OK);
        v.put(ContractParaVale.Columnas.ID_REMOTA, idRemota);

        resolver.update(uri, v, selection, selectionArgs);

    }
    /**
     * Procesa los diferentes tipos de respuesta obtenidos del servidor
     *
     * @param response Respuesta en formato Json
     */
    public void procesarRespuestaInsert(JSONObject response, int idLocal) {

        try {
            // Obtener estado
            String estado = response.getString(Constantes.ESTADO);
            // Obtener mensaje
            String mensaje = response.getString(Constantes.MENSAJE);
            // Obtener identificador del nuevo registro creado en el servidor
            String idRemota = response.getString(Constantes.ID_GASTO);

            switch (estado) {
                case Constantes.SUCCESS:
                    Log.i(TAG + "SUCCESS", mensaje + idRemota);
                    finalizarActualizacion(idRemota, idLocal);
                    break;

                case Constantes.FAILED:
                    Log.i(TAG +"Failed", mensaje);
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    /**
     * Actualiza los registros locales a través de una comparación con los datos
     * del servidor
     *
     * @param response   Respuesta en formato Json obtenida del servidor
     * @param syncResult Registros de la sincronización
     */
    private void actualizarDatosLocalesObra(JSONObject response, SyncResult syncResult) {

        JSONArray gastos = null;

        try {
            // Obtener array "gastos"
            gastos = response.getJSONArray(Constantes.OBRA);
            Log.i("datos locales","actualizar datos locales");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // Parsear con Gson
        ObraGetSet[] res = gson.fromJson(gastos != null ? gastos.toString() : null, ObraGetSet[].class);
        List<ObraGetSet> data = Arrays.asList(res);

        // Lista para recolección de operaciones pendientes
        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();

        // Tabla hash para recibir las entradas entrantes
        HashMap<String, ObraGetSet> expenseMap = new HashMap<String, ObraGetSet>();
        for (ObraGetSet e : data) {
            expenseMap.put(e.getCod_obra(), e);
        }
        // Consultar registros remotos actuales
        Log.i("consulta registros Obra","consulta registros remotos");
        Uri uri = ContractParaObras.CONTENT_URI_OBRA;
        String select = ContractParaObras.Columnas.ID_REMOTA + " IS NOT NULL";
        Cursor c = resolver.query(uri, PROJECTION_OBRA, select, null, null);
        assert c != null;

        Log.i(TAG, "Se encontraron " + c.getCount() + " registros locales.");

        // Encontrar datos obsoletos
        String cod_obra;
        String nombre;
        String localidad;
        int visible_petroleo ;
        int finalizada;


        while (c.moveToNext()) {
            syncResult.stats.numEntries++;

            cod_obra         = c.getString(AdapterObra.COLUMNA_ID_REMOTA);
            nombre           = c.getString(AdapterObra.COLUMNA_NOMBRE);
            localidad        = c.getString(AdapterObra.COLUMNA_LOCALIDAD);
            visible_petroleo = c.getInt(AdapterObra.COLUMNA_VISIBLE_PETROLEO);
            finalizada       = c.getInt(AdapterObra.COLUMNA_FINALIZADA);

            ObraGetSet match = expenseMap.get(cod_obra);

            if (match != null) {
                // Esta entrada existe, por lo que se remueve del mapeado
                expenseMap.remove(cod_obra);

                Uri existingUri = ContractParaObras.CONTENT_URI_OBRA.buildUpon()
                        .appendPath(cod_obra).build();

                // Comprobar si el gasto necesita ser actualizado

                boolean b = match.getCod_obra() != cod_obra && !match.getCod_obra().equals(cod_obra);
                boolean b1 = match.getNombre() != nombre;
                boolean b2 = match.getLocalidad() != localidad;
                boolean b3 = match.getFinalizada() != finalizada;
                boolean b4 = match.getVisible_petroleo() != visible_petroleo;


                if (b || b1 || b2 || b3 || b4 ) {

                    Log.i(TAG, "Programando actualización de: " + existingUri);

                    ops.add(ContentProviderOperation.newUpdate(existingUri)
                            .withValue(ContractParaObras.Columnas.COD_OBRA, match.getCod_obra())
                            .withValue(ContractParaObras.Columnas.NOMBRE, match.getNombre())
                            .withValue(ContractParaObras.Columnas.LOCALIDAD, match.getLocalidad())
                            .withValue(ContractParaObras.Columnas.FINALIZADA, match.getFinalizada())
                            .withValue(ContractParaObras.Columnas.VISIBLE_PETROLEO, match.getVisible_petroleo())

                            .build());
                    syncResult.stats.numUpdates++;
                } else {
                    Log.i(TAG, "No hay acciones para este registro: " + existingUri);
                }
            } else {
                // Debido a que la entrada no existe, es removida de la base de datos
                Uri deleteUri = ContractParaObras.CONTENT_URI_OBRA.buildUpon()
                        .appendPath(cod_obra).build();
                Log.i(TAG, "Programando eliminación de: " + deleteUri);
                ops.add(ContentProviderOperation.newDelete(deleteUri).build());
                syncResult.stats.numDeletes++;
            }
        }
        c.close();

        // Insertar items resultantes
        for (ObraGetSet e : expenseMap.values()) {
            Log.i(TAG, "Programando inserción de: " + e.getCod_obra());
            ops.add(ContentProviderOperation.newInsert(ContractParaObras.CONTENT_URI_OBRA)
                    .withValue(ContractParaObras.Columnas.COD_OBRA, e.getCod_obra())
                    .withValue(ContractParaObras.Columnas.NOMBRE, e.getNombre())
                    .withValue(ContractParaObras.Columnas.LOCALIDAD, e.getLocalidad())
                    .withValue(ContractParaObras.Columnas.FINALIZADA, e.getFinalizada())
                    .withValue(ContractParaObras.Columnas.VISIBLE_PETROLEO, e.getVisible_petroleo())
                    .withValue(ContractParaObras.Columnas.ID_REMOTA, e.getCod_obra())
                    .build());
            syncResult.stats.numInserts++;
        }

        if (syncResult.stats.numInserts > 0 ||
                syncResult.stats.numUpdates > 0 ||
                syncResult.stats.numDeletes > 0) {
            Log.i(TAG, "Aplicando operaciones...");
            try {
                resolver.applyBatch(ContractParaVale.AUTHORITY, ops);
            } catch (RemoteException | OperationApplicationException e) {
                e.printStackTrace();
            }
            resolver.notifyChange(
                    ContractParaObras.CONTENT_URI_OBRA,
                    null,
                    false);
            Log.i(TAG, "Sincronización finalizada.");
            Toast toast1 =
                    Toast.makeText(getContext(),
                            "Sincronización finalizada.", Toast.LENGTH_SHORT);

            toast1.show();

        } else {
            Log.i(TAG, "No se requiere sincronización");
        }

    }
    /**
     * Actualiza los registros locales a través de una comparación con los datos
     * del servidor
     *
     * @param response   Respuesta en formato Json obtenida del servidor
     * @param syncResult Registros de la sincronización
     */
    public void actualizarDatosLocalesSurtidor(JSONObject response, SyncResult syncResult) {

        JSONArray surtidor = null;

        try {
            // Obtener array "surtidor"
            surtidor = response.getJSONArray(Constantes.SURTIDOR);
            Log.i("datos locales","actualizar datos locales");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // Parsear con Gson
        SurtidorGetSet[] res = gson.fromJson(surtidor != null ? surtidor.toString() : null, SurtidorGetSet[].class);
        List<SurtidorGetSet> data = Arrays.asList(res);

        // Lista para recolección de operaciones pendientes
        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();

        // Tabla hash para recibir las entradas entrantes
        HashMap<String, SurtidorGetSet> expenseMap = new HashMap<String, SurtidorGetSet>();
        for (SurtidorGetSet e : data) {
            expenseMap.put(e.getCodigo(), e);
        }
        // Consultar registros remotos actuales
        Log.i("consulta  Surtidor","consulta registros remotos");
        Uri uri = ContractParaSurtidor.CONTENT_URI_SURTIDOR;
        String select = ContractParaSurtidor.Columnas.ID_REMOTA + " IS NOT NULL";
        Cursor c = resolver.query(uri, SyncAdapterSurtidor.PROJECTION, select, null, null);
        assert c != null;

        Log.i(TAG, "Se encontraron " + c.getCount() + " registros locales.");

        // Encontrar datos obsoletos
        String codigo;
        int vigente;
        String descripcion;
        int id_categoria;

        while (c.moveToNext()) {
            syncResult.stats.numEntries++;

            codigo         = c.getString(SyncAdapterSurtidor.COLUMNA_CODIGO);
            descripcion    = c.getString(SyncAdapterSurtidor.COLUMNA_DESCRIPCION);
            vigente        = c.getInt(SyncAdapterSurtidor.COLUMNA_VIGENTE);
            id_categoria   = c.getInt(SyncAdapterSurtidor.COLUMNA_ID_CATEGORIA);

            SurtidorGetSet match = expenseMap.get(codigo);

            if (match != null) {
                // Esta entrada existe, por lo que se remueve del mapeado
                expenseMap.remove(codigo);

                Uri existingUri = ContractParaSurtidor.CONTENT_URI_SURTIDOR.buildUpon()
                        .appendPath(codigo).build();

                // Comprobar si el gasto necesita ser actualizado

                boolean b = match.getCodigo() != codigo && !match.getCodigo().equals(codigo);
                boolean b1 = match.getDescripcion() != descripcion;
                boolean b2 = match.getVigente() != vigente;
                boolean b3 = match.getId_categoria() != id_categoria;



                if (b || b1 || b2 || b3) {

                    Log.i(TAG, "Programando actualización de: " + existingUri);

                    ops.add(ContentProviderOperation.newUpdate(existingUri)
                            .withValue(ContractParaSurtidor.Columnas.CODIGO, match.getCodigo())
                            .withValue(ContractParaSurtidor.Columnas.DESCRIPCION, match.getDescripcion())
                            .withValue(ContractParaSurtidor.Columnas.VIGENTE, match.getVigente())
                            .withValue(ContractParaSurtidor.Columnas.ID_CATEGORIA, match.getId_categoria())
                            .build());
                    syncResult.stats.numUpdates++;
                } else {
                    Log.i(TAG, "No hay acciones para este registro: " + existingUri);
                }
            } else {
                // Debido a que la entrada no existe, es removida de la base de datos
                Uri deleteUri = ContractParaSurtidor.CONTENT_URI_SURTIDOR.buildUpon()
                        .appendPath(codigo).build();
                Log.i(TAG, "Programando eliminación de: " + deleteUri);
                ops.add(ContentProviderOperation.newDelete(deleteUri).build());
                syncResult.stats.numDeletes++;
            }
        }
        c.close();

        // Insertar items resultantes
        for (SurtidorGetSet e : expenseMap.values()) {
            Log.i(TAG, "Programando inserción de: " + e.getCodigo());
            ops.add(ContentProviderOperation.newInsert(ContractParaSurtidor.CONTENT_URI_SURTIDOR)
                    .withValue(ContractParaSurtidor.Columnas.CODIGO, e.getCodigo())
                    .withValue(ContractParaSurtidor.Columnas.DESCRIPCION, e.getDescripcion())
                    .withValue(ContractParaSurtidor.Columnas.VIGENTE, e.getVigente())
                    .withValue(ContractParaSurtidor.Columnas.ID_CATEGORIA, e.getId_categoria())
                    .withValue(ContractParaSurtidor.Columnas.ID_REMOTA, e.getCodigo())
                    .build());
            syncResult.stats.numInserts++;
        }

        if (syncResult.stats.numInserts > 0 ||
                syncResult.stats.numUpdates > 0 ||
                syncResult.stats.numDeletes > 0) {
            Log.i(TAG, "Aplicando operaciones...");
            try {
                resolver.applyBatch(ContractParaSurtidor.AUTHORITY, ops);
            } catch (RemoteException | OperationApplicationException e) {
                e.printStackTrace();
            }
            resolver.notifyChange(
                    ContractParaSurtidor.CONTENT_URI_SURTIDOR,
                    null,
                    false);
            Log.i(TAG, "Sincronización finalizada.");
            Toast toast1 =
                    Toast.makeText(getContext(),
                            "Sincronización finalizada.", Toast.LENGTH_SHORT);

            toast1.show();

        } else {
            Log.i(TAG, "No se requiere sincronización");
        }

    }
    /**
     * Actualiza los registros locales a través de una comparación con los datos
     * del servidor
     *
     * @param response   Respuesta en formato Json obtenida del servidor
     * @param syncResult Registros de la sincronización
     */
    public void actualizarDatosLocalesVehiculos(JSONObject response, SyncResult syncResult) {

        JSONArray vehiculo = null;

        try {
            // Obtener array "vehiculo"
            vehiculo = response.getJSONArray(Constantes.VEHICULO);
            Log.i("datos locales","actualizar datos locales");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // Parsear con Gson
        VehiculoGetSet[] res = gson.fromJson(vehiculo != null ? vehiculo.toString() : null, VehiculoGetSet[].class);
        List<VehiculoGetSet> data = Arrays.asList(res);

        // Lista para recolección de operaciones pendientes
        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();

        // Tabla hash para recibir las entradas entrantes
        HashMap<String, VehiculoGetSet> expenseMap = new HashMap<String, VehiculoGetSet>();
        for (VehiculoGetSet e : data) {
            expenseMap.put(e.getCodigo(), e);
        }
        // Consultar registros remotos actuales
        Log.i("consulta  vehiculo","consulta registros remotos");
        Uri uri = ContractParaVehiculos.CONTENT_URI_VEHICULO;
        String select = ContractParaVehiculos.Columnas.ID_REMOTA + " IS NOT NULL";
        Cursor c = resolver.query(uri, SyncAdapterVehiculos.PROJECTION, select, null, null);
        assert c != null;

        Log.i(TAG, "Se encontraron " + c.getCount() + " registros locales.");

        // Encontrar datos obsoletos
        String codigo;
        String patente;
        String termino_contrato;
        String forma_pago;
        String valor_pago;

        while (c.moveToNext()) {
            syncResult.stats.numEntries++;

            codigo           = c.getString(SyncAdapterVehiculos.COLUMNA_CODIGO);
            patente          = c.getString(SyncAdapterVehiculos.COLUMNA_PATENTE);
            termino_contrato = c.getString(SyncAdapterVehiculos.COLUMNA_FECHA_TERMINO_CONTRATO);
            forma_pago       = c.getString(SyncAdapterVehiculos.COLUMNA_FORMA_PAGO);
            valor_pago       = c.getString(SyncAdapterVehiculos.COLUMNA_VALOR_PAGO);

            VehiculoGetSet match = expenseMap.get(codigo);

            if (match != null) {
                // Esta entrada existe, por lo que se remueve del mapeado
                expenseMap.remove(codigo);

                Uri existingUri = ContractParaVehiculos.CONTENT_URI_VEHICULO.buildUpon()
                        .appendPath(codigo).build();

                // Comprobar si el gasto necesita ser actualizado

                boolean b = match.getCodigo() != codigo && !match.getCodigo().equals(codigo);
                boolean b1 = match.getPatente() != patente && !match.getPatente().equals(patente);
                boolean b2 = match.getfecha_termino_contrato() != termino_contrato && !match.getfecha_termino_contrato().equals(termino_contrato);
                boolean b3 = match.getForma_pago() != forma_pago && !match.getForma_pago().equals(forma_pago);
                boolean b4 = match.getValor_pago() != valor_pago && !match.getValor_pago().equals(valor_pago);


                if (b || b1 || b2 || b3 || b4) {

                    Log.i(TAG, "Programando actualización de: " + existingUri);

                    ops.add(ContentProviderOperation.newUpdate(existingUri)
                            .withValue(ContractParaVehiculos.Columnas.CODIGO, match.getCodigo())
                            .withValue(ContractParaVehiculos.Columnas.PATENTE, match.getPatente())
                            .withValue(ContractParaVehiculos.Columnas.FECHA_TERMINO_CONTRATO, match.getfecha_termino_contrato())
                            .withValue(ContractParaVehiculos.Columnas.FORMA_PAGO, match.getForma_pago())
                            .withValue(ContractParaVehiculos.Columnas.VALOR_PAGO, match.getValor_pago())
                            .build());
                    syncResult.stats.numUpdates++;
                } else {
                    Log.i(TAG, "No hay acciones para este registro: " + existingUri);
                }
            } else {
                // Debido a que la entrada no existe, es removida de la base de datos
                Uri deleteUri = ContractParaVehiculos.CONTENT_URI_VEHICULO.buildUpon()
                        .appendPath(codigo).build();
                Log.i(TAG, "Programando eliminación de: " + deleteUri);
                ops.add(ContentProviderOperation.newDelete(deleteUri).build());
                syncResult.stats.numDeletes++;
            }
        }
        c.close();

        // Insertar items resultantes
        for (VehiculoGetSet e : expenseMap.values()) {
            Log.i(TAG, "Programando inserción de: " + e.getCodigo()+" -" +e.getfecha_termino_contrato());
            ops.add(ContentProviderOperation.newInsert(ContractParaVehiculos.CONTENT_URI_VEHICULO)
                    .withValue(ContractParaVehiculos.Columnas.CODIGO, e.getCodigo())
                    .withValue(ContractParaVehiculos.Columnas.PATENTE, e.getPatente())
                    .withValue(ContractParaVehiculos.Columnas.FECHA_TERMINO_CONTRATO, e.getfecha_termino_contrato())
                    .withValue(ContractParaVehiculos.Columnas.FORMA_PAGO, e.getForma_pago())
                    .withValue(ContractParaVehiculos.Columnas.VALOR_PAGO, e.getValor_pago())
                    .withValue(ContractParaVehiculos.Columnas.ID_REMOTA, e.getCodigo())
                    .build());
            syncResult.stats.numInserts++;
        }

        if (syncResult.stats.numInserts > 0 ||
                syncResult.stats.numUpdates > 0 ||
                syncResult.stats.numDeletes > 0) {
            Log.i(TAG, "Aplicando operaciones...");
            try {
                resolver.applyBatch(ContractParaVehiculos.AUTHORITY, ops);
            } catch (RemoteException | OperationApplicationException e) {
                e.printStackTrace();
            }
            resolver.notifyChange(
                    ContractParaVehiculos.CONTENT_URI_VEHICULO,
                    null,
                    false);
            Log.i(TAG, "Sincronización finalizada.");
            Toast toast1 =
                    Toast.makeText(getContext(),
                            "Sincronización finalizada.", Toast.LENGTH_SHORT);

            toast1.show();

        } else {
            Log.i(TAG, "No se requiere sincronización");
        }

    }
    /**
     * Actualiza los registros locales a través de una comparación con los datos
     * del servidor
     *
     * @param response   Respuesta en formato Json obtenida del servidor
     * @param syncResult Registros de la sincronización
     */
    public void actualizarDatosLocalesUsuarios(JSONObject response, SyncResult syncResult) {

        JSONArray usuarios = null;

        try {
            // Obtener array "usuarios"
            usuarios = response.getJSONArray(Constantes.USUARIO);
            Log.i("datos locales","actualizar datos locales");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // Parsear con Gson
        UsuarioGetSet[] res = gson.fromJson(usuarios != null ? usuarios.toString() : null, UsuarioGetSet[].class);
        List<UsuarioGetSet> data = Arrays.asList(res);

        // Lista para recolección de operaciones pendientes
        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();

        // Tabla hash para recibir las entradas entrantes
        HashMap<String, UsuarioGetSet> expenseMap = new HashMap<String, UsuarioGetSet>();
        for (UsuarioGetSet e : data) {
            expenseMap.put(e.getId(), e);
        }
        // Consultar registros remotos actuales
        Log.i("consulta  usuarios","consulta registros remotos");
        Uri uri = ContractParaUsuarios.CONTENT_URI_USUARIO;
        String select = ContractParaUsuarios.Columnas.ID_REMOTA + " IS NOT NULL";
        Cursor c = resolver.query(uri, SyncAdapterUsuarios.PROJECTION, select, null, null);
        assert c != null;

        Log.i(TAG, "Se encontraron " + c.getCount() + " registros locales.");

        // Encontrar datos obsoletos
        String indice;
        String usuario;
        String password;
        int vigente;

        while (c.moveToNext()) {
            syncResult.stats.numEntries++;

            indice     = c.getString(SyncAdapterUsuarios.COLUMNA_INDICE);
            usuario    = c.getString(SyncAdapterUsuarios.COLUMNA_USUARIO);
            password   = c.getString(SyncAdapterUsuarios.COLUMNA_PASSWORD);
            vigente    = c.getInt(SyncAdapterUsuarios.COLUMNA_VIGENTE);

            UsuarioGetSet match = expenseMap.get(indice);

            if (match != null) {
                // Esta entrada existe, por lo que se remueve del mapeado
                expenseMap.remove(indice);

                Uri existingUri = ContractParaUsuarios.CONTENT_URI_USUARIO.buildUpon()
                        .appendPath(indice).build();

                // Comprobar si el usuario necesita ser actualizado

                boolean b = match.getId() != indice && !match.getId().equals(indice);
                boolean b1 = match.getUsuario() != usuario && !match.getUsuario().equals(usuario);
                boolean b2 = match.getPassword() != password && !match.getPassword().equals(password);
                boolean b3 = match.getVigente() != vigente ;

                if (b || b1 || b2 || b3) {

                    Log.i(TAG, "Programando actualización de: " + existingUri);

                    ops.add(ContentProviderOperation.newUpdate(existingUri)
                            .withValue(ContractParaUsuarios.Columnas.INDICE, match.getId())
                            .withValue(ContractParaUsuarios.Columnas.USUARIO, match.getUsuario())
                            .withValue(ContractParaUsuarios.Columnas.PASSWORD, match.getPassword())
                            .withValue(ContractParaUsuarios.Columnas.VIGENTE, match.getVigente())
                            .build());
                    syncResult.stats.numUpdates++;
                } else {
                    Log.i(TAG, "No hay acciones para este registro: " + existingUri);
                }
            } else {
                // Debido a que la entrada no existe, es removida de la base de datos
                Uri deleteUri = ContractParaUsuarios.CONTENT_URI_USUARIO.buildUpon()
                        .appendPath(indice).build();
                Log.i(TAG, "Programando eliminación de: " + deleteUri);
                ops.add(ContentProviderOperation.newDelete(deleteUri).build());
                syncResult.stats.numDeletes++;
            }
        }
        c.close();
        // Insertar items resultantes
        for (UsuarioGetSet e : expenseMap.values()) {
            Log.i(TAG, "Programando inserción de: " + e.getId());
            ops.add(ContentProviderOperation.newInsert(ContractParaUsuarios.CONTENT_URI_USUARIO)
                    .withValue(ContractParaUsuarios.Columnas.INDICE, e.getId())
                    .withValue(ContractParaUsuarios.Columnas.USUARIO, e.getUsuario())
                    .withValue(ContractParaUsuarios.Columnas.PASSWORD, e.getPassword())
                    .withValue(ContractParaUsuarios.Columnas.VIGENTE, e.getVigente())
                    .withValue(ContractParaUsuarios.Columnas.ID_REMOTA, e.getId())
                    .build());
            syncResult.stats.numInserts++;
        }

        if (syncResult.stats.numInserts > 0 ||
                syncResult.stats.numUpdates > 0 ||
                syncResult.stats.numDeletes > 0) {
            Log.i(TAG, "Aplicando operaciones...");
            try {
                resolver.applyBatch(ContractParaUsuarios.AUTHORITY, ops);
            } catch (RemoteException | OperationApplicationException e) {
                e.printStackTrace();
            }
            resolver.notifyChange(
                    ContractParaUsuarios.CONTENT_URI_USUARIO,
                    null,
                    false);
            Log.i(TAG, "Sincronización finalizada.");
            Toast toast1 =
                    Toast.makeText(getContext(),
                            "Sincronización finalizada.", Toast.LENGTH_SHORT);

            toast1.show();

        } else {
            Log.i(TAG, "No se requiere sincronización");
        }

    }
    /**
     * Actualiza los registros locales a través de una comparación con los datos
     * del servidor
     *
     * @param response   Respuesta en formato Json obtenida del servidor
     * @param syncResult Registros de la sincronización
     */
    public void actualizarDatosLocalesMes(JSONObject response, SyncResult syncResult) {

        JSONArray mes_proceso = null;

        try {
            // Obtener array "mes"
            mes_proceso = response.getJSONArray(Constantes.MES);
            Log.i("datos locales","actualizar datos locales");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // Parsear con Gson
        MesprocesoGetSet[] res = gson.fromJson(mes_proceso != null ? mes_proceso.toString() : null, MesprocesoGetSet[].class);
        List<MesprocesoGetSet> data = Arrays.asList(res);

        // Lista para recolección de operaciones pendientes
        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();

        // Tabla hash para recibir las entradas entrantes
        HashMap<String, MesprocesoGetSet> expenseMap = new HashMap<String, MesprocesoGetSet>();
        for (MesprocesoGetSet e : data) {
            expenseMap.put(e.getId(), e);
        }
        // Consultar registros remotos actuales
        Log.i("consulta  vehiculo","consulta registros remotos");
        Uri uri = ContractParaMes.CONTENT_URI_MES;
        String select = ContractParaMes.Columnas.ID_REMOTA + " IS NOT NULL";
        Cursor c = resolver.query(uri, AdapterMes.PROJECTION, select, null, null);
        assert c != null;

        Log.i(TAG, "Se encontraron " + c.getCount() + " registros locales.");

        // Encontrar datos obsoletos
        String id;
        String  proceso;
        int abierta;
        int mes;
        int ano;

        while (c.moveToNext()) {
            syncResult.stats.numEntries++;

            id        = c.getString(AdapterMes.COLUMNA_ID);
            proceso   = c.getString(AdapterMes.COLUMNA_PROCESO);
            abierta   = c.getInt(AdapterMes.COLUMNA_ABIERTA);
            mes       = c.getInt(AdapterMes.COLUMNA_MES);
            ano       = c.getInt(AdapterMes.COLUMNA_ANO);

            MesprocesoGetSet match = expenseMap.get(id);

            if (match != null) {
                // Esta entrada existe, por lo que se remueve del mapeado
                expenseMap.remove(id);

                Uri existingUri = ContractParaMes.CONTENT_URI_MES.buildUpon()
                        .appendPath(id).build();

                // Comprobar si el gasto necesita ser actualizado

                boolean b = match.getId() != id && !match.getId().equals(id);
                boolean b1 = match.getProceso() != proceso && !match.getProceso().equals(proceso);
                boolean b2 = match.getAbierta() != abierta ;
                boolean b3 = match.getMes() != mes ;
                boolean b4 = match.getAno() != ano ;


                if (b || b1 || b2 || b3 || b4) {

                    Log.i(TAG, "Programando actualización de: " + existingUri);

                    ops.add(ContentProviderOperation.newUpdate(existingUri)
                            .withValue(ContractParaMes.Columnas.ID, match.getId())
                            .withValue(ContractParaMes.Columnas.PROCESO, match.getProceso())
                            .withValue(ContractParaMes.Columnas.ABIERTA, match.getAbierta())
                            .withValue(ContractParaMes.Columnas.MES, match.getMes())
                            .withValue(ContractParaMes.Columnas.ANO, match.getAno())
                            .build());
                    syncResult.stats.numUpdates++;
                } else {
                    Log.i(TAG, "No hay acciones para este registro: " + existingUri);
                }
            } else {
                // Debido a que la entrada no existe, es removida de la base de datos
                Uri deleteUri = ContractParaMes.CONTENT_URI_MES.buildUpon()
                        .appendPath(id).build();
                Log.i(TAG, "Programando eliminación de: " + deleteUri);
                ops.add(ContentProviderOperation.newDelete(deleteUri).build());
                syncResult.stats.numDeletes++;
            }
        }
        c.close();

        // Insertar items resultantes
        for (MesprocesoGetSet e : expenseMap.values()) {
            Log.i(TAG, "Programando inserción de: " + e.getId());
            ops.add(ContentProviderOperation.newInsert(ContractParaMes.CONTENT_URI_MES)
                    .withValue(ContractParaMes.Columnas.ID, e.getId())
                    .withValue(ContractParaMes.Columnas.PROCESO, e.getProceso())
                    .withValue(ContractParaMes.Columnas.ABIERTA, e.getAbierta())
                    .withValue(ContractParaMes.Columnas.MES, e.getMes())
                    .withValue(ContractParaMes.Columnas.ANO, e.getAno())
                    .withValue(ContractParaMes.Columnas.ID_REMOTA, e.getId())
                    .build());
            syncResult.stats.numInserts++;
        }

        if (syncResult.stats.numInserts > 0 ||
                syncResult.stats.numUpdates > 0 ||
                syncResult.stats.numDeletes > 0) {
            Log.i(TAG, "Aplicando operaciones...");
            try {
                resolver.applyBatch(ContractParaMes.AUTHORITY, ops);
            } catch (RemoteException | OperationApplicationException e) {
                e.printStackTrace();
            }
            resolver.notifyChange(
                    ContractParaMes.CONTENT_URI_MES,
                    null,
                    false);
            Log.i(TAG, "Sincronización finalizada.");
            Toast toast1 =
                    Toast.makeText(getContext(),
                            "Sincronización finalizada.", Toast.LENGTH_SHORT);

            toast1.show();

        } else {
            Log.i(TAG, "No se requiere sincronización");
        }

    }
    /**
     * Actualiza los registros locales a través de una comparación con los datos
     * del servidor
     *
     * @param response   Respuesta en formato Json obtenida del servidor
     * @param syncResult Registros de la sincronización
     */
    public void actualizarDatosLocalesProveedores(JSONObject response, SyncResult syncResult) {

        JSONArray proveedor = null;

        try {
            // Obtener array "proveedor"
            proveedor = response.getJSONArray(Constantes.PROVEEDORES);
            Log.i("datos locales","actualizar datos locales");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // Parsear con Gson
        ProveedorGetSet[] res = gson.fromJson(proveedor != null ? proveedor.toString() : null, ProveedorGetSet[].class);
        List<ProveedorGetSet> data = Arrays.asList(res);

        // Lista para recolección de operaciones pendientes
        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();

        // Tabla hash para recibir las entradas entrantes
        HashMap<String, ProveedorGetSet> expenseMap = new HashMap<String, ProveedorGetSet>();
        for (ProveedorGetSet e : data) {
            expenseMap.put(e.getRut(), e);
        }
        // Consultar registros remotos actuales
        Log.i("consulta  proveedor","consulta registros remotos");
        Uri uri = ContractParaProveedor.CONTENT_URI_PROVEEDOR;
        String select = ContractParaProveedor.Columnas.ID_REMOTA + " IS NOT NULL";
        Cursor c = resolver.query(uri, SyncAdapterProveedores.PROJECTION, select, null, null);
        assert c != null;

        Log.i(TAG, "Se encontraron " + c.getCount() + " registros locales.");

        // Encontrar datos obsoletos
        String rut;
        String razon_social;
        String usuario_petroleo;


        while (c.moveToNext()) {
            syncResult.stats.numEntries++;

            rut              = c.getString(SyncAdapterProveedores.COLUMNA_RUT);
            razon_social     = c.getString(SyncAdapterProveedores.COLUMNA_RAZON_SOCIAL);
            usuario_petroleo = c.getString(SyncAdapterProveedores.COLUMNA_USUARIO_PETROLEO);

            ProveedorGetSet match = expenseMap.get(rut);

            if (match != null) {
                // Esta entrada existe, por lo que se remueve del mapeado
                expenseMap.remove(rut);

                Uri existingUri = ContractParaProveedor.CONTENT_URI_PROVEEDOR.buildUpon()
                        .appendPath(rut).build();

                // Comprobar si el gasto necesita ser actualizado

                boolean b = match.getRut() != rut && !match.getRut().equals(rut);
                boolean b1 = match.getRazon_social() != razon_social && !match.getRazon_social().equals(razon_social);
                boolean b2 = match.getUsuario_petroleo() != usuario_petroleo && !match.getUsuario_petroleo().equals(usuario_petroleo);


                if (b || b1 || b2 ) {

                    Log.i(TAG, "Programando actualización de: " + existingUri);

                    ops.add(ContentProviderOperation.newUpdate(existingUri)
                            .withValue(ContractParaProveedor.Columnas.RUT, match.getRut())
                            .withValue(ContractParaProveedor.Columnas.RAZON_SOCIAL, match.getRazon_social())
                            .withValue(ContractParaProveedor.Columnas.USUARIO_PETROLEO, match.getUsuario_petroleo())
                            .build());
                    syncResult.stats.numUpdates++;
                } else {
                    Log.i(TAG, "No hay acciones para este registro: " + existingUri);
                }
            } else {
                // Debido a que la entrada no existe, es removida de la base de datos
                Uri deleteUri = ContractParaProveedor.CONTENT_URI_PROVEEDOR.buildUpon()
                        .appendPath(rut).build();
                Log.i(TAG, "Programando eliminación de: " + deleteUri);
                ops.add(ContentProviderOperation.newDelete(deleteUri).build());
                syncResult.stats.numDeletes++;
            }
        }
        c.close();

        // Insertar items resultantes
        for (ProveedorGetSet e : expenseMap.values()) {
            Log.i(TAG, "Programando inserción de: " + e.getRut());
            ops.add(ContentProviderOperation.newInsert(ContractParaProveedor.CONTENT_URI_PROVEEDOR)
                    .withValue(ContractParaProveedor.Columnas.RUT, e.getRut())
                    .withValue(ContractParaProveedor.Columnas.RAZON_SOCIAL, e.getRazon_social())
                    .withValue(ContractParaProveedor.Columnas.USUARIO_PETROLEO, e.getUsuario_petroleo())
                    .withValue(ContractParaProveedor.Columnas.ID_REMOTA, e.getRut())
                    .build());
            syncResult.stats.numInserts++;
        }

        if (syncResult.stats.numInserts > 0 ||
                syncResult.stats.numUpdates > 0 ||
                syncResult.stats.numDeletes > 0) {
            Log.i(TAG, "Aplicando operaciones...");
            try {
                resolver.applyBatch(ContractParaProveedor.AUTHORITY, ops);
            } catch (RemoteException | OperationApplicationException e) {
                e.printStackTrace();
            }
            resolver.notifyChange(
                    ContractParaProveedor.CONTENT_URI_PROVEEDOR,
                    null,
                    false);
            Log.i(TAG, "Sincronización finalizada.");
            Toast toast1 =
                    Toast.makeText(getContext(),
                            "Sincronización finalizada.", Toast.LENGTH_SHORT);

            toast1.show();

        } else {
            Log.i(TAG, "No se requiere sincronización");
        }

    }
    /**
     * Actualiza los registros locales a través de una comparación con los datos
     * del servidor
     *
     * @param response   Respuesta en formato Json obtenida del servidor
     * @param syncResult Registros de la sincronización
     */
    private void actualizarDatosLocales(JSONObject response, SyncResult syncResult) {

        JSONArray gastos = null;

        try {
            // Obtener array "gastos"
            gastos = response.getJSONArray(Constantes.GASTOS);
            Log.i("datos locales","actualizar datos locales");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // Parsear con Gson
        GetVale[] res = gson.fromJson(gastos != null ? gastos.toString() : null, GetVale[].class);
        List<GetVale> data = Arrays.asList(res);

        // Lista para recolección de operaciones pendientes
        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();

        // Tabla hash para recibir las entradas entrantes
        HashMap<String, GetVale> expenseMap = new HashMap<String, GetVale>();
        for (GetVale e : data) {
            expenseMap.put(e.Id, e);
        }
        // Consultar registros remotos actuales
        Log.i("consulta registros","consulta registros remotos");
        Uri uri = ContractParaVale.CONTENT_URI;
        String select = ContractParaVale.Columnas.ID_REMOTA + " IS NOT NULL";
        Cursor c = resolver.query(uri, PROJECTION, select, null, null);
        assert c != null;

        Log.i(TAG, "Se encontraron " + c.getCount() + " registros locales.");

        // Encontrar datos obsoletos
         String id;
         String fecha;
         int mes_proceso;
         int surtidor ;
         int vehiculo;
         int obra ;
         String recibe ;
         String usuario ;
         int vale ;
         int guia ;
         int sello ;
         int horometro ;
         int kilometro ;
         String observaciones ;

        while (c.moveToNext()) {
            syncResult.stats.numEntries++;

            id          = c.getString(COLUMNA_ID_REMOTA);
            mes_proceso = c.getInt(COLUMNA_MES_PROCESO);
            surtidor    = c.getInt(COLUMNA_SURTIDOR);
            obra        = c.getInt(COLUMNA_OBRA);
            vehiculo    = c.getInt(COLUMNA_VEHICULO);
            recibe      = c.getString(COLUMNA_RECIBE);
            usuario     = c.getString(COLUMNA_USUARIO);
            fecha       = c.getString(COLUMNA_FECHA);
            vale        = c.getInt(COLUMNA_VALE);
            guia        = c.getInt(COLUMNA_GUIA);
            sello       = c.getInt(COLUMNA_SELLO);
            horometro   = c.getInt(COLUMNA_HOROMETRO);
            kilometro   = c.getInt(COLUMNA_KILOMETRO);
            observaciones = c.getString(COLUMNA_OBSERVACIONES);




            GetVale match = expenseMap.get(id);

            if (match != null) {
                // Esta entrada existe, por lo que se remueve del mapeado
                expenseMap.remove(id);

                Uri existingUri = ContractParaVale.CONTENT_URI.buildUpon()
                        .appendPath(id).build();

                // Comprobar si el gasto necesita ser actualizado

                boolean b = match.id_mes_proceso != mes_proceso;
                boolean b1 = match.id_obra != obra;
                boolean b2 = match.id_surtidor != surtidor;
                boolean b3 = match.id_vehiculo != vehiculo;
                boolean b4 = match.rut_recibe != recibe;
                boolean b5 = match.fecha_crea != null && !match.fecha_crea.equals(fecha);
                boolean b6 = match.usuario_crea != null && !match.usuario_crea.equals(usuario);
                boolean b7 = match.numero_vale != vale;
                boolean b8 = match.numero_guia_proveedor != guia;
                boolean b9 = match.numero_sello!= sello;
                boolean b10 = match.contador_hr != horometro;
                boolean b11 = match.contador_km != kilometro;
                boolean b12 = match.observaciones != null && !match.observaciones.equals(observaciones);




                if (b || b1 || b2 || b3 || b4 || b5 || b6 || b7 || b8 || b9 || b10 || b11 || b12 ) {

                    Log.i(TAG, "Programando actualización de: " + existingUri);

                    ops.add(ContentProviderOperation.newUpdate(existingUri)
                            .withValue(ContractParaVale.Columnas.MES_PROCESO, match.id_mes_proceso)
                            .withValue(ContractParaVale.Columnas.SURTIDOR, match.id_surtidor)
                            .withValue(ContractParaVale.Columnas.VEHICULO, match.id_vehiculo)
                            .withValue(ContractParaVale.Columnas.OBRA, match.id_obra)
                            .withValue(ContractParaVale.Columnas.RECIBE, match.rut_recibe)
                            .withValue(ContractParaVale.Columnas.USUARIO, match.usuario_crea)
                            .withValue(ContractParaVale.Columnas.FECHA, match.fecha_crea)
                            .withValue(ContractParaVale.Columnas.VALE, match.numero_vale)
                            .withValue(ContractParaVale.Columnas.GUIA, match.numero_guia_proveedor)
                            .withValue(ContractParaVale.Columnas.SELLO, match.numero_sello)
                            .withValue(ContractParaVale.Columnas.HOROMETRO, match.contador_hr)
                            .withValue(ContractParaVale.Columnas.KILOMETRO, match.contador_km)
                            .withValue(ContractParaVale.Columnas.OBSERVACIONES, match.observaciones)
                            .build());
                    syncResult.stats.numUpdates++;
                } else {
                    Log.i(TAG, "No hay acciones para este registro: " + existingUri);
                }
            } else {
                // Debido a que la entrada no existe, es removida de la base de datos
                Uri deleteUri = ContractParaVale.CONTENT_URI.buildUpon()
                        .appendPath(id).build();
                Log.i(TAG, "Programando eliminación de: " + deleteUri);
                ops.add(ContentProviderOperation.newDelete(deleteUri).build());
                syncResult.stats.numDeletes++;
            }
        }
        c.close();

        // Insertar items resultantes
        for (GetVale e : expenseMap.values()) {
            Log.i(TAG, "Programando inserción de: " + e.getId());
            ops.add(ContentProviderOperation.newInsert(ContractParaVale.CONTENT_URI)
                    .withValue(ContractParaVale.Columnas.MES_PROCESO, e.id_mes_proceso)
                    .withValue(ContractParaVale.Columnas.SURTIDOR, e.id_surtidor)
                    .withValue(ContractParaVale.Columnas.VEHICULO, e.id_vehiculo)
                    .withValue(ContractParaVale.Columnas.OBRA, e.id_obra)
                    .withValue(ContractParaVale.Columnas.RECIBE, e.rut_recibe)
                    .withValue(ContractParaVale.Columnas.USUARIO, e.usuario_crea)
                    .withValue(ContractParaVale.Columnas.FECHA, e.fecha_crea)
                    .withValue(ContractParaVale.Columnas.VALE, e.numero_vale)
                    .withValue(ContractParaVale.Columnas.GUIA, e.numero_guia_proveedor)
                    .withValue(ContractParaVale.Columnas.SELLO, e.numero_sello)
                    .withValue(ContractParaVale.Columnas.HOROMETRO, e.contador_hr)
                    .withValue(ContractParaVale.Columnas.KILOMETRO, e.contador_km)
                    .withValue(ContractParaVale.Columnas.OBSERVACIONES, e.observaciones)
                    .withValue(ContractParaVale.Columnas.ID_REMOTA, e.Id)
                    .build());
            syncResult.stats.numInserts++;
        }

        if (syncResult.stats.numInserts > 0 ||
                syncResult.stats.numUpdates > 0 ||
                syncResult.stats.numDeletes > 0) {
            Log.i(TAG, "Aplicando operaciones...");
            try {
                resolver.applyBatch(ContractParaVale.AUTHORITY, ops);
            } catch (RemoteException | OperationApplicationException e) {
                e.printStackTrace();
            }
            resolver.notifyChange(
                    ContractParaVale.CONTENT_URI,
                    null,
                    false);
            Log.i(TAG, "Sincronización finalizada.");

        } else {
            Log.i(TAG, "No se requiere sincronización");
        }

    }
    /**
     * Inicia manualmente la sincronización
     *
     * @param context    Contexto para crear la petición de sincronización
     * @param onlyUpload Usa true para sincronizar el servidor o false para sincronizar el cliente
     */
    public static void sincronizarAhora(Context context, boolean onlyUpload) {
        Log.i(TAG, "Realizando petición de sincronización manual.");
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        if (onlyUpload)
            bundle.putBoolean(ContentResolver.SYNC_EXTRAS_UPLOAD, true);
        ContentResolver.requestSync(obtenerCuentaASincronizar(context),
                context.getString(R.string.provider_authority), bundle);
    }
    /**
     * Crea u obtiene una cuenta existente
     *
     * @param context Contexto para acceder al administrador de cuentas
     * @return cuenta auxiliar.
     */
    public static Account obtenerCuentaASincronizar(Context context) {
        // Obtener instancia del administrador de cuentas
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Crear cuenta por defecto
        Account newAccount = new Account(
                context.getString(R.string.app_name), Constantes.ACCOUNT_TYPE);

        // Comprobar existencia de la cuenta
        if (null == accountManager.getPassword(newAccount)) {

            // Añadir la cuenta al account manager sin password y sin datos de usuario
            if (!accountManager.addAccountExplicitly(newAccount, "", null))
                return null;

        }
        Log.i(TAG, "Cuenta de usuario obtenida.");
        return newAccount;
    }

}