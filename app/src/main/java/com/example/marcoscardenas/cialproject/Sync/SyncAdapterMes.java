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
import com.example.marcoscardenas.cialproject.Provider.ContractParaMes;
import com.example.marcoscardenas.cialproject.Provider.ContractParaObras;
import com.example.marcoscardenas.cialproject.Provider.ContractParaSurtidor;
import com.example.marcoscardenas.cialproject.Provider.ContractParaUsuarios;
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
public class SyncAdapterMes {
    private static final String TAG = SyncAdapterMes.class.getSimpleName();

    ContentResolver resolver;
    private Gson gson = new Gson();

    /**
     * Proyección para las consultas
     */
    public static final String[] PROJECTION = new String[]{
            ContractParaMes.Columnas.ID,
            ContractParaMes.Columnas.PROCESO,
            ContractParaMes.Columnas.ABIERTA,
            ContractParaMes.Columnas.MES,
            ContractParaMes.Columnas.ANO,
            ContractParaMes.Columnas.ID_REMOTA,

    };

    // Indices para las columnas indicadas en la proyección
    public static final int COLUMNA_ID = 0;
    public static final int COLUMNA_ID_REMOTA = 5;
    public static final int COLUMNA_PROCESO = 1;
    public static final int COLUMNA_ABIERTA = 2;
    public static final int COLUMNA_MES = 3;
    public static final int COLUMNA_ANO = 4;



}