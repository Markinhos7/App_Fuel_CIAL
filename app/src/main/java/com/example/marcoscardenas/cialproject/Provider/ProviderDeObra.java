package com.example.marcoscardenas.cialproject.Provider;

/**
 * Created by Marcos on 21-02-17.
 */

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.example.marcoscardenas.cialproject.DataBase.Conexion;


public class ProviderDeObra extends ContentProvider {

    /**
     * Instancia global del Content Resolver
     */
    private ContentResolver resolver;
    /**
     * Instancia del administrador de BD
     */
    private Conexion conexion;

    @Override
    public boolean onCreate() {
        // Inicializando gestor BD
        conexion = new Conexion(
                getContext()
        );

        resolver = getContext().getContentResolver();

        return true;
    }

    @Override
    public Cursor query(
            Uri uri,
            String[] projection,
            String selection,
            String[] selectionArgs,
            String sortOrder) {

        // Obtener base de datos
        SQLiteDatabase db = conexion.getWritableDatabase();
        // Comparar Uri
        int match = ContractParaObras.uriMatcher.match(uri);

        Cursor c;

        switch (match) {
            case ContractParaObras.ALLROWS:
                // Consultando todos los registros
                c = db.query(ContractParaObras.OBRA, projection,
                        selection, selectionArgs,
                        null, null, sortOrder);
                c.setNotificationUri(
                        resolver,
                        ContractParaObras.CONTENT_URI);
                break;
            case ContractParaObras.SINGLE_ROW:
                // Consultando un solo registro basado en el Id del Uri
                long id_obra = ContentUris.parseId(uri);
                c = db.query(ContractParaObras.OBRA, projection,
                        "cod_obra" + " = " + id_obra,
                        selectionArgs, null, null, sortOrder);
                c.setNotificationUri(
                        resolver,
                        ContractParaObras.CONTENT_URI);
                break;
            default:
                throw new IllegalArgumentException("URI no soportada: " + uri);
        }
        return c;

    }

    @Override
    public String getType(Uri uri) {
        switch (ContractParaObras.uriMatcher.match(uri)) {
            case ContractParaObras.ALLROWS:
                return ContractParaObras.MULTIPLE_MIME;
            case ContractParaObras.SINGLE_ROW:
                return ContractParaObras.SINGLE_MIME;
            default:
                throw new IllegalArgumentException("Tipo de gasto desconocido: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // Validar la uri
        if (ContractParaObras.uriMatcher.match(uri) != ContractParaObras.ALLROWS) {
            throw new IllegalArgumentException("URI desconocida : " + uri);
        }
        ContentValues contentValues;
        if (values != null) {
            contentValues = new ContentValues(values);
        } else {
            contentValues = new ContentValues();
        }

        // Inserción de nueva fila
        SQLiteDatabase db = conexion.getWritableDatabase();
        long rowId = db.insert(ContractParaObras.OBRA, null, contentValues);
        if (rowId > 0) {
            Uri uri_gasto = ContentUris.withAppendedId(
                    ContractParaObras.CONTENT_URI, rowId);
            resolver.notifyChange(uri_gasto, null, false);
            return uri_gasto;
        }
        throw new SQLException("Falla al insertar fila en : " + uri);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        SQLiteDatabase db = conexion.getWritableDatabase();

        int match = ContractParaObras.uriMatcher.match(uri);
        int affected;

        switch (match) {
            case ContractParaObras.ALLROWS:
                affected = db.delete(ContractParaObras.OBRA,
                        selection,
                        selectionArgs);
                break;
            case ContractParaObras.SINGLE_ROW:
                long id_obra = ContentUris.parseId(uri);
                affected = db.delete(ContractParaObras.OBRA,
                        ContractParaObras.Columnas.ID_REMOTA + "=" + id_obra
                                + (!TextUtils.isEmpty(selection) ?
                                " AND (" + selection + ')' : ""),
                        selectionArgs);
                // Notificar cambio asociado a la uri
                resolver.
                        notifyChange(uri, null, false);
                break;
            default:
                throw new IllegalArgumentException("Elemento vale desconocido: " +
                        uri);
        }
        return affected;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        SQLiteDatabase db = conexion.getWritableDatabase();
        int affected;
        switch (ContractParaObras.uriMatcher.match(uri)) {
            case ContractParaObras.ALLROWS:
                affected = db.update(ContractParaObras.OBRA, values,
                        selection, selectionArgs);
                break;
            case ContractParaVale.SINGLE_ROW:
                String id_obra = uri.getPathSegments().get(1);
                Log.i("ID obra",id_obra);
                affected = db.update(ContractParaObras.OBRA, values,
                        ContractParaObras.Columnas.ID_REMOTA + "=" + id_obra
                                + (!TextUtils.isEmpty(selection) ?
                                " AND (" + selection + ')' : ""),
                        selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("URI desconocida: " + uri);
        }
        resolver.notifyChange(uri, null, false);
        return affected;
    }

}

