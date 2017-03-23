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
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.marcoscardenas.cialproject.Model.ObraGetSet;
import com.example.marcoscardenas.cialproject.Provider.ContractParaObras;
import com.example.marcoscardenas.cialproject.R;
import com.google.gson.Gson;
import com.example.marcoscardenas.cialproject.Constantes;
import com.example.marcoscardenas.cialproject.VolleySingleton;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Maneja la transferencia de datos entre el servidor y el cliente
 */
public class SyncAdapterObra extends AbstractThreadedSyncAdapter {
    private static final String TAG = SyncAdapterObra.class.getSimpleName();

    ContentResolver resolver;
    private Gson gson = new Gson();

    /**
     * Proyección para las consultas
     */
    private static final String[] PROJECTION = new String[]{
            ContractParaObras.Columnas.COD_OBRA,
            ContractParaObras.Columnas.LOCALIDAD,
            ContractParaObras.Columnas.NOMBRE,
            ContractParaObras.Columnas.FINALIZADA,
            ContractParaObras.Columnas.VISIBLE_PETROLEO,
            ContractParaObras.Columnas.ID_REMOTA,

    };

    // Indices para las columnas indicadas en la proyección
    public static final int COLUMNA_COD_OBRA = 0;
    public static final int COLUMNA_ID_REMOTA = 5;
    public static final int COLUMNA_LOCALIDAD = 1;
    public static final int COLUMNA_NOMBRE = 2;
    public static final int COLUMNA_FINALIZADA = 3;
    public static final int COLUMNA_VISIBLE_PETROLEO = 4;



    public SyncAdapterObra(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        resolver = context.getContentResolver();
    }

    /**
     * Constructor para mantener compatibilidad en versiones inferiores a 3.0
     */
    public SyncAdapterObra(
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

        Log.i(TAG, "onPerformSync()... Obra");

        boolean soloSubida = extras.getBoolean(ContentResolver.SYNC_EXTRAS_UPLOAD, false);

        if (!soloSubida) {
            realizarSincronizacionLocal(syncResult);
        } else {
            realizarSincronizacionRemota();
        }
    }

    private void realizarSincronizacionLocal(final SyncResult syncResult) {
        Log.i(TAG, "Actualizando el cliente.");
        VolleySingleton.getInstance(getContext()).addToRequestQueue(
                new JsonObjectRequest(
                        Request.Method.GET,
                        Constantes.GET_BY_OBRA,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                procesarRespuestaGet(response, syncResult);
                                Log.i("Procesa respuesta", "Actualizando el cliente.");
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

    private void realizarSincronizacionRemota() {
        Log.i(TAG, "Actualizando el servidor...");

        iniciarActualizacion();

        Cursor c = obtenerRegistrosSucios();

        Log.i(TAG, "Se encontraron " + c.getCount() + " registros sucios.");

        if (c.getCount() > 0) {
            while (c.moveToNext()) {
                final int idLocal = c.getInt(COLUMNA_COD_OBRA);
                System.out.println("COD_OBRA :"+idLocal);

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
        Uri uri = ContractParaObras.CONTENT_URI_OBRA;
        String selection = ContractParaObras.Columnas.PENDIENTE_INSERCION + "=? AND "
                + ContractParaObras.Columnas.ESTADO + "=?";
        String[] selectionArgs = new String[]{"1", ContractParaObras.ESTADO_SYNC + ""};

        return resolver.query(uri, PROJECTION, selection, selectionArgs, null);
    }

    /**
     * Cambia a estado "de sincronización" el registro que se acaba de insertar localmente
     */
    private void iniciarActualizacion() {
        Uri uri = ContractParaObras.CONTENT_URI_OBRA;
        String selection = ContractParaObras.Columnas.PENDIENTE_INSERCION + "=? AND "
                + ContractParaObras.Columnas.ESTADO + "=?";
        String[] selectionArgs = new String[]{"1", ContractParaObras.ESTADO_OK + ""};

        ContentValues v = new ContentValues();
        v.put(ContractParaObras.Columnas.ESTADO, ContractParaObras.ESTADO_SYNC);

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
        Uri uri = ContractParaObras.CONTENT_URI_OBRA;
        String selection = ContractParaObras.Columnas.COD_OBRA + "=?";
        String[] selectionArgs = new String[]{String.valueOf(idLocal)};

        ContentValues v = new ContentValues();
        v.put(ContractParaObras.Columnas.PENDIENTE_INSERCION, "0");
        v.put(ContractParaObras.Columnas.ESTADO, ContractParaObras.ESTADO_OK);
        v.put(ContractParaObras.Columnas.ID_REMOTA, idRemota);

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
            String idRemota = response.getString(Constantes.ID_OBRA);

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
        Log.i("consulta registros","consulta registros remotos");
        Uri uri = ContractParaObras.CONTENT_URI_OBRA;
        String select = ContractParaObras.Columnas.ID_REMOTA + " IS NOT NULL";
        Cursor c = resolver.query(uri, PROJECTION, select, null, null);
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

            cod_obra         = c.getString(COLUMNA_ID_REMOTA);
            nombre           = c.getString(COLUMNA_NOMBRE);
            localidad        = c.getString(COLUMNA_LOCALIDAD);
            visible_petroleo = c.getInt(COLUMNA_VISIBLE_PETROLEO);
            finalizada       = c.getInt(COLUMNA_FINALIZADA);

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
                resolver.applyBatch(ContractParaObras.AUTHORITY, ops);
            } catch (RemoteException | OperationApplicationException e) {
                e.printStackTrace();
            }
            resolver.notifyChange(
                    ContractParaObras.CONTENT_URI_OBRA,
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