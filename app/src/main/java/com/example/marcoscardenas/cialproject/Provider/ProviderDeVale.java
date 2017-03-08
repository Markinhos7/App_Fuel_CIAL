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

import com.example.marcoscardenas.cialproject.DataBase.Conexion;


public class ProviderDeVale extends ContentProvider {


        private static final String DATABASE_NAME = "Becker.db";
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
                            ContractParaVale.Columnas._ID + " = " + idGasto,
                            selectionArgs, null, null, sortOrder);
                    c.setNotificationUri(
                            resolver,
                            ContractParaVale.CONTENT_URI);
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
                default:
                    throw new IllegalArgumentException("Elemento gasto desconocido: " +
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
                case ContractParaVale.SINGLE_ROW:
                    String idGasto = uri.getPathSegments().get(1);
                    affected = db.update(ContractParaVale.VALE_ENCABEZADO, values,
                            ContractParaVale.Columnas.ID_REMOTA + "=" + idGasto
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

