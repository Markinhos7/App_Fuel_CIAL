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
import com.example.marcoscardenas.cialproject.Provider.ContractParaVehiculos;
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
public class SyncAdapterVehiculos {
    private static final String TAG = SyncAdapterVehiculos.class.getSimpleName();

    ContentResolver resolver;
    private Gson gson = new Gson();

    /**
     * Proyección para las consultas
     */
    public static final String[] PROJECTION = new String[]{
            ContractParaVehiculos.Columnas.CODIGO,
            ContractParaVehiculos.Columnas.PATENTE,
            ContractParaVehiculos.Columnas.FECHA_TERMINO_CONTRATO,
            ContractParaVehiculos.Columnas.FORMA_PAGO,
            ContractParaVehiculos.Columnas.VALOR_PAGO,
            ContractParaVehiculos.Columnas.ID_REMOTA,

    };

    // Indices para las columnas indicadas en la proyección
    public static final int COLUMNA_CODIGO = 0;
    public static final int COLUMNA_ID_REMOTA = 5;
    public static final int COLUMNA_PATENTE = 1;
    public static final int COLUMNA_FECHA_TERMINO_CONTRATO = 2;
    public static final int COLUMNA_FORMA_PAGO = 3;
    public static final int COLUMNA_VALOR_PAGO = 4;



}