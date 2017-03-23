package com.example.marcoscardenas.cialproject.Sync;

/**
 * Created by Marcos on 21-02-17.
 */

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.example.marcoscardenas.cialproject.Sync.SyncAdapter;

/**
 * Bound Service que interactua con el sync adapter para correr las sincronizaciones
 */
public class SyncServiceObra extends Service {

    // Instancia del sync adapter
    private static SyncAdapterObra syncAdapter = null;
    // Objeto para prevenir errores entre hilos
    private static final Object lock = new Object();

    @Override
    public void onCreate() {
        synchronized (lock) {
            if (syncAdapter == null) {
                syncAdapter = new SyncAdapterObra(getApplicationContext(), true);
            }
        }
    }

    /**
     * Retorna interfaz de comunicación para que el sistema llame al sync adapter
     */
    @Override
    public IBinder onBind(Intent intent) {
        return syncAdapter.getSyncAdapterBinder();
    }
}