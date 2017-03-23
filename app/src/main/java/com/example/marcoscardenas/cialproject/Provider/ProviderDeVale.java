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
import android.widget.Switch;

import com.example.marcoscardenas.cialproject.DataBase.Conexion;


public class ProviderDeVale extends ContentProvider {


    private static final String DATABASE_NAME = "becker.db";
    /**
     * Versión actual de la base de datos
     */
    private static final int DATABASE_VERSION = 1;
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
        int match = ContractParaVale.uriMatcher.match(uri);

        Cursor c;
        Log.d("match asd", Integer.toString(match));
        switch (match) {

            case ContractParaVale.ALLROWS:
                // Consultando todos los registros
                c = db.query(ContractParaVale.VALE_ENCABEZADO, projection,
                        selection, selectionArgs,
                        null, null, sortOrder);
                c.setNotificationUri(
                        resolver,
                        ContractParaVale.CONTENT_URI);
                break;

            case ContractParaVale.SINGLE_ROW:
                // Consultando un solo registro basado en el Id del Uri
                long idGasto = ContentUris.parseId(uri);
                c = db.query(ContractParaVale.VALE_ENCABEZADO, projection,
                        "Id" + " = " + idGasto,
                        selectionArgs, null, null, sortOrder);
                c.setNotificationUri(
                        resolver,
                        ContractParaVale.CONTENT_URI);
                break;
            case ContractParaObras.ALLROWS_OBRA:
                // Consultando todos los registros
                c = db.query(ContractParaObras.OBRA, projection,
                        selection, selectionArgs,
                        null, null, sortOrder);
                c.setNotificationUri(
                        resolver,
                        ContractParaObras.CONTENT_URI_OBRA);
                break;
            case ContractParaObras.SINGLE_ROW_OBRA:
                // Consultando un solo registro basado en el Id del Uri
                long idobra = ContentUris.parseId(uri);
                c = db.query(ContractParaObras.OBRA, projection,
                        "cod_obra" + " = " + idobra,
                        selectionArgs, null, null, sortOrder);
                c.setNotificationUri(
                        resolver,
                        ContractParaObras.CONTENT_URI_OBRA);
                break;
            case ContractParaSurtidor.ALLROWS_SURTIDOR:
                // Consultando todos los registros
                c = db.query(ContractParaSurtidor.SURTIDOR, projection,
                        selection, selectionArgs,
                        null, null, sortOrder);
                c.setNotificationUri(
                        resolver,
                        ContractParaSurtidor.CONTENT_URI_SURTIDOR);
                break;
            case ContractParaSurtidor.SINGLE_ROW_SURTIDOR:
                // Consultando un solo registro basado en el Id del Uri
                long idSurtidor = ContentUris.parseId(uri);
                c = db.query(ContractParaSurtidor.SURTIDOR, projection,
                        "codigo" + " = " + idSurtidor,
                        selectionArgs, null, null, sortOrder);
                c.setNotificationUri(
                        resolver,
                        ContractParaSurtidor.CONTENT_URI_SURTIDOR);
                break;
            default:
                throw new IllegalArgumentException("URI no soportada: " + uri);
        }
        return c;

    }

    @Override
    public String getType(Uri uri) {
        switch (ContractParaVale.uriMatcher.match(uri)) {
            case ContractParaVale.ALLROWS:
                return ContractParaVale.MULTIPLE_MIME;
            case ContractParaVale.SINGLE_ROW:
                return ContractParaVale.SINGLE_MIME;
            default:
                throw new IllegalArgumentException("Tipo de gasto desconocido: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // Validar la uri
        int match = ContractParaVale.uriMatcher.match(uri);
        switch (match) {
            case ContractParaVale.ALLROWS:
                if (ContractParaVale.uriMatcher.match(uri) != ContractParaVale.ALLROWS) {
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
                long rowId = db.insert(ContractParaVale.VALE_ENCABEZADO, null, contentValues);
                if (rowId > 0) {
                    Uri uri_gasto = ContentUris.withAppendedId(
                            ContractParaVale.CONTENT_URI, rowId);
                    resolver.notifyChange(uri_gasto, null, false);
                    return uri_gasto;
                }
                throw new SQLException("Falla al insertar fila en : " + uri);



            case ContractParaObras.ALLROWS_OBRA:
                if (ContractParaVale.uriMatcher.match(uri) != ContractParaVale.ALLROWS_OBRA) {
                    throw new IllegalArgumentException("URI desconocida : " + uri);
                }
                ContentValues contentValues_obra;
                if (values != null) {
                    contentValues_obra = new ContentValues(values);
                } else {
                    contentValues_obra = new ContentValues();
                }

                // Inserción de nueva fila
                SQLiteDatabase db_obra = conexion.getWritableDatabase();
                long rowId_obra = db_obra.insert(ContractParaObras.OBRA, null, contentValues_obra);
                if (rowId_obra > 0) {
                    Uri uri_obra = ContentUris.withAppendedId(
                            ContractParaVale.CONTENT_URI_OBRA, rowId_obra);
                    resolver.notifyChange(uri_obra, null, false);
                    return uri_obra;
                }
                throw new SQLException("Falla al insertar fila en : " + uri);


            case ContractParaSurtidor.ALLROWS_SURTIDOR:
                if (ContractParaSurtidor.uriMatcher.match(uri) != ContractParaSurtidor.ALLROWS_SURTIDOR) {
                    throw new IllegalArgumentException("URI desconocida : " + uri);
                }
                ContentValues contentValues_surtidor;
                if (values != null) {
                    contentValues_surtidor = new ContentValues(values);
                } else {
                    contentValues_surtidor = new ContentValues();
                }

                // Inserción de nueva fila
                SQLiteDatabase db_surtidor = conexion.getWritableDatabase();
                long rowId_surtidor = db_surtidor.insert(ContractParaSurtidor.SURTIDOR, null, contentValues_surtidor);
                if (rowId_surtidor > 0) {
                    Uri uri_surrtidor = ContentUris.withAppendedId(
                            ContractParaSurtidor.CONTENT_URI_SURTIDOR, rowId_surtidor);
                    resolver.notifyChange(uri_surrtidor, null, false);
                    return uri_surrtidor;
                }
                throw new SQLException("Falla al insertar fila en : " + uri);

            default:
                throw new IllegalArgumentException("Tipo de gasto desconocido: " + uri);
        }

    }


        @Override
        public int delete(Uri uri, String selection, String[] selectionArgs) {

            SQLiteDatabase db = conexion.getWritableDatabase();

            int match = ContractParaVale.uriMatcher.match(uri);
            int affected;

            switch (match) {
                case ContractParaVale.ALLROWS:
                    affected = db.delete(ContractParaVale.VALE_ENCABEZADO,
                            selection,
                            selectionArgs);
                    break;
                case ContractParaVale.SINGLE_ROW:
                    long idGasto = ContentUris.parseId(uri);
                    affected = db.delete(ContractParaVale.VALE_ENCABEZADO,
                            ContractParaVale.Columnas.ID_REMOTA + "=" + idGasto
                                    + (!TextUtils.isEmpty(selection) ?
                                    " AND (" + selection + ')' : ""),
                            selectionArgs);
                    // Notificar cambio asociado a la uri
                    resolver.
                            notifyChange(uri, null, false);
                    break;
                case ContractParaObras.ALLROWS_OBRA:
                    affected = db.delete(ContractParaObras.OBRA,
                            selection,
                            selectionArgs);
                    break;
                case ContractParaObras.SINGLE_ROW_OBRA:
                    long idObra = ContentUris.parseId(uri);
                    affected = db.delete(ContractParaObras.OBRA,
                            ContractParaObras.Columnas.ID_REMOTA + "=" + idObra
                                    + (!TextUtils.isEmpty(selection) ?
                                    " AND (" + selection + ')' : ""),
                            selectionArgs);
                    // Notificar cambio asociado a la uri
                    resolver.
                            notifyChange(uri, null, false);
                    break;
                case ContractParaSurtidor.ALLROWS_SURTIDOR:
                    affected = db.delete(ContractParaSurtidor.SURTIDOR,
                            selection,
                            selectionArgs);
                    break;
                case ContractParaSurtidor.SINGLE_ROW_SURTIDOR:
                    long idSurtidor = ContentUris.parseId(uri);
                    affected = db.delete(ContractParaSurtidor.SURTIDOR,
                            ContractParaSurtidor.Columnas.ID_REMOTA + "=" + idSurtidor
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
            switch (ContractParaVale.uriMatcher.match(uri)) {
                case ContractParaVale.ALLROWS:
                    affected = db.update(ContractParaVale.VALE_ENCABEZADO, values,
                            selection, selectionArgs);
                    break;
                case ContractParaVale.ALLROWS_OBRA:
                    affected = db.update(ContractParaObras.OBRA, values,
                            selection, selectionArgs);
                    break;
                case ContractParaVale.SINGLE_ROW:
                    String idGasto = uri.getPathSegments().get(1);
                    Log.i("ID VALE",idGasto);
                    affected = db.update(ContractParaVale.VALE_ENCABEZADO, values,
                            ContractParaVale.Columnas.ID_REMOTA + "=" + idGasto
                                    + (!TextUtils.isEmpty(selection) ?
                                    " AND (" + selection + ')' : ""),
                            selectionArgs);
                    break;
                case ContractParaVale.SINGLE_ROW_OBRA:
                    String idOBRA = uri.getPathSegments().get(1);
                    Log.i("ID OBRA",idOBRA);
                    affected = db.update(ContractParaObras.OBRA, values,
                            ContractParaVale.Columnas.ID_REMOTA + "=" + idOBRA
                                    + (!TextUtils.isEmpty(selection) ?
                                    " AND (" + selection + ')' : ""),
                            selectionArgs);
                    break;
                case ContractParaSurtidor.SINGLE_ROW_SURTIDOR:
                    String idSurtidor = uri.getPathSegments().get(1);
                    Log.i("ID SURTIDOR",idSurtidor);
                    affected = db.update(ContractParaSurtidor.SURTIDOR, values,
                            ContractParaSurtidor.Columnas.ID_REMOTA + "=" + idSurtidor
                                    + (!TextUtils.isEmpty(selection) ?
                                    " AND (" + selection + ')' : ""),
                            selectionArgs);
                    break;
                case ContractParaSurtidor.ALLROWS_SURTIDOR:
                    affected = db.update(ContractParaSurtidor.SURTIDOR, values,
                            selection, selectionArgs);
                    break;

                default:
                    throw new IllegalArgumentException("URI desconocida: " + uri);
            }
            resolver.notifyChange(uri, null, false);
            return affected;
        }

    }

