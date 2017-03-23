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

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.marcoscardenas.cialproject.Model.ObraGetSet;
import com.example.marcoscardenas.cialproject.Model.SurtidorGetSet;
import com.example.marcoscardenas.cialproject.Provider.ContractParaObras;
import com.example.marcoscardenas.cialproject.Provider.ContractParaSurtidor;
import com.example.marcoscardenas.cialproject.Provider.ContractParaVale;
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
public class SyncAdapterSurtidor  {
    private static final String TAG = SyncAdapterSurtidor.class.getSimpleName();

    ContentResolver resolver;
    private Gson gson = new Gson();

    /**
     * Proyección para las consultas
     */
    public static final String[] PROJECTION = new String[]{
            ContractParaSurtidor.Columnas.CODIGO,
            ContractParaSurtidor.Columnas.DESCRIPCION,
            ContractParaSurtidor.Columnas.VIGENTE,
            ContractParaSurtidor.Columnas.ID_REMOTA,

    };

    // Indices para las columnas indicadas en la proyección
    public static final int COLUMNA_CODIGO = 0;
    public static final int COLUMNA_ID_REMOTA = 3;
    public static final int COLUMNA_DESCRIPCION = 1;
    public static final int COLUMNA_VIGENTE = 2;







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

    private void realizarSincronizacionRemota() {
        Log.i(TAG, "Actualizando el servidor...");

        iniciarActualizacion();

        Cursor c = obtenerRegistrosSucios();

        Log.i(TAG, "Se encontraron " + c.getCount() + " registros sucios.");

        if (c.getCount() > 0) {
            while (c.moveToNext()) {
                final int idLocal = c.getInt(COLUMNA_CODIGO);
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
        Log.i("consulta registros Obra","consulta registros remotos");
        Uri uri = ContractParaSurtidor.CONTENT_URI_SURTIDOR;
        String select = ContractParaSurtidor.Columnas.ID_REMOTA + " IS NOT NULL";
        Cursor c = resolver.query(uri, PROJECTION, select, null, null);
        assert c != null;

        Log.i(TAG, "Se encontraron " + c.getCount() + " registros locales.");

        // Encontrar datos obsoletos
        String codigo;
        int vigente;
        String descripcion;

        while (c.moveToNext()) {
            syncResult.stats.numEntries++;

            codigo         = c.getString(COLUMNA_CODIGO);
            descripcion    = c.getString(COLUMNA_DESCRIPCION);
            vigente        = c.getInt(COLUMNA_VIGENTE);

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


                if (b || b1 || b2 ) {

                    Log.i(TAG, "Programando actualización de: " + existingUri);

                    ops.add(ContentProviderOperation.newUpdate(existingUri)
                            .withValue(ContractParaSurtidor.Columnas.CODIGO, match.getCodigo())
                            .withValue(ContractParaSurtidor.Columnas.DESCRIPCION, match.getDescripcion())
                            .withValue(ContractParaSurtidor.Columnas.VIGENTE, match.getVigente())
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