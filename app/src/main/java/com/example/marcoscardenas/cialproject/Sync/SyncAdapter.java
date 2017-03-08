package com.example.marcoscardenas.cialproject.Sync;

/**
 * Created by Marcos on 21-02-17.
 */
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.DownloadManager;
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

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.marcoscardenas.cialproject.Model.GetVale;
import com.example.marcoscardenas.cialproject.Provider.ContractParaVale;

import com.example.marcoscardenas.cialproject.R;
import com.google.gson.Gson;
import com.example.marcoscardenas.cialproject.Utilidades;
import com.example.marcoscardenas.cialproject.Constantes;

import com.example.marcoscardenas.cialproject.Provider.ContractParaVale;
import com.example.marcoscardenas.cialproject.Model.GetVale;

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
 * Maneja la transferencia de datos entre el servidor y el cliente
 */
public class SyncAdapter extends AbstractThreadedSyncAdapter {
    private static final String TAG = SyncAdapter.class.getSimpleName();

    ContentResolver resolver;
    private Gson gson = new Gson();

    /**
     * Proyección para las consultas
     */
    private static final String[] PROJECTION = new String[]{
            ContractParaVale.Columnas._ID,
            ContractParaVale.Columnas.ID_REMOTA,
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
            ContractParaVale.Columnas.FECHA,
            ContractParaVale.Columnas.KILOMETRO,
            ContractParaVale.Columnas.OBSERVACIONES
};

    // Indices para las columnas indicadas en la proyección
    public static final int COLUMNA_ID = 0;
    public static final int COLUMNA_ID_REMOTA = 1;
    public static final int COLUMNA_MES_PROCESO = 2;
    public static final int COLUMNA_SURTIDOR = 3;
    public static final int COLUMNA_VEHICULO = 4;
    public static final int COLUMNA_OBRA = 5;
    public static final int COLUMNA_RECIBE = 6;
    public static final int COLUMNA_USUARIO = 7;
    public static final int COLUMNA_FECHA = 8;
    public static final int COLUMNA_VALE = 9;
    public static final int COLUMNA_GUIA = 10;
    public static final int COLUMNA_SELLO = 11;
    public static final int COLUMNA_HOROMETRO = 12;
    public static final int COLUMNA_KILOMETRO = 13;
    public static final int COLUMNA_OBSERVACIONES = 14;


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
        } else {
            realizarSincronizacionRemota();
        }
    }

    private void realizarSincronizacionLocal(final SyncResult syncResult) {
        Log.i(TAG, "Actualizando el cliente.");
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
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
                        Log.d(TAG, error.networkResponse.toString());
                    }
                }
        );
        VolleySingleton.getInstance(getContext()).addToRequestQueue(jsonObjectRequest
        );

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
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

    private void realizarSincronizacionRemota() {
        Log.i(TAG, "Actualizando el servidor...");

        iniciarActualizacion();

        Cursor c = obtenerRegistrosSucios();

        Log.i(TAG, "Se encontraron " + c.getCount() + " registros sucios.");

        if (c.getCount() > 0) {
            while (c.moveToNext()) {
                final int idLocal = c.getInt(COLUMNA_ID);

                VolleySingleton.getInstance(getContext()).addToRequestQueue(
                        new JsonObjectRequest(
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
                                        Log.d(TAG, "Error Volley: " + error.getMessage());
                                    }
                                }

                        ) {
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
        String selection = ContractParaVale.Columnas._ID + "=?";
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
                    Log.i(TAG, mensaje);
                    finalizarActualizacion(idRemota, idLocal);
                    break;

                case Constantes.FAILED:
                    Log.i(TAG, mensaje);
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
    private void actualizarDatosLocales(JSONObject response, SyncResult syncResult) {

        JSONArray gastos = null;

        try {
            // Obtener array "gastos"
            gastos = response.getJSONArray(Constantes.GASTOS);
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
            expenseMap.put(e.RECIBE, e);
        }

        // Consultar registros remotos actuales
        Uri uri = ContractParaVale.CONTENT_URI;
        String select = ContractParaVale.Columnas.ID_REMOTA + " IS NOT NULL";
        Cursor c = resolver.query(uri, PROJECTION, select, null, null);
        assert c != null;

        Log.i(TAG, "Se encontraron " + c.getCount() + " registros locales.");

        // Encontrar datos obsoletos
        String id;
        int monto;
        String etiqueta;
        String fecha;
        String descripcion;
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

            id = c.getString(COLUMNA_ID_REMOTA);
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

                boolean b = match.MES_PROCESO != mes_proceso;
                boolean b1 = match.OBRA != obra;
                boolean b2 = match.SURTIDOR != surtidor;
                boolean b3 = match.VEHICULO != vehiculo;
                boolean b4 = match.FECHA != null && !match.FECHA.equals(fecha);
                boolean b5 = match.USUARIO != null && !match.USUARIO.equals(usuario);
                boolean b6 = match.VALE != vale;
                boolean b7 = match.GUIA != guia;
                boolean b8 = match.HOROMETRO != horometro;
                boolean b9 = match.KILOMETRO != kilometro;
                boolean b10 = match.OBSERVACIONES != null && !match.OBSERVACIONES.equals(observaciones);




                if (b || b1 || b2 || b3 || b4 || b5 || b6 || b7 || b8 || b9 || b10  ) {

                    Log.i(TAG, "Programando actualización de: " + existingUri);

                    ops.add(ContentProviderOperation.newUpdate(existingUri)
                            .withValue(ContractParaVale.Columnas.MES_PROCESO, match.MES_PROCESO)
                            .withValue(ContractParaVale.Columnas.SURTIDOR, match.SURTIDOR)
                            .withValue(ContractParaVale.Columnas.FECHA, match.FECHA)
                            .withValue(ContractParaVale.Columnas.OBRA, match.OBRA)
                            .withValue(ContractParaVale.Columnas.VEHICULO, match.VEHICULO)
                            .withValue(ContractParaVale.Columnas.USUARIO, match.USUARIO)
                            .withValue(ContractParaVale.Columnas.RECIBE, match.RECIBE)
                            .withValue(ContractParaVale.Columnas.GUIA, match.GUIA)
                            .withValue(ContractParaVale.Columnas.SELLO, match.SELLO)
                            .withValue(ContractParaVale.Columnas.KILOMETRO, match.KILOMETRO)
                            .withValue(ContractParaVale.Columnas.HOROMETRO, match.HOROMETRO)
                            .withValue(ContractParaVale.Columnas.OBSERVACIONES, match.OBSERVACIONES)
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
            Log.i(TAG, "Programando inserción de: " + e.id);
            ops.add(ContentProviderOperation.newInsert(ContractParaVale.CONTENT_URI)

                    .withValue(ContractParaVale.Columnas.MES_PROCESO, e.MES_PROCESO)
                    .withValue(ContractParaVale.Columnas.SURTIDOR, e.SURTIDOR)
                    .withValue(ContractParaVale.Columnas.FECHA, e.FECHA)
                    .withValue(ContractParaVale.Columnas.OBRA, e.OBRA)
                    .withValue(ContractParaVale.Columnas.VEHICULO, e.VEHICULO)
                    .withValue(ContractParaVale.Columnas.USUARIO, e.USUARIO)
                    .withValue(ContractParaVale.Columnas.RECIBE, e.RECIBE)
                    .withValue(ContractParaVale.Columnas.GUIA, e.GUIA)
                    .withValue(ContractParaVale.Columnas.SELLO, e.SELLO)
                    .withValue(ContractParaVale.Columnas.KILOMETRO, e.KILOMETRO)
                    .withValue(ContractParaVale.Columnas.HOROMETRO, e.HOROMETRO)
                    .withValue(ContractParaVale.Columnas.OBSERVACIONES, e.OBSERVACIONES)
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