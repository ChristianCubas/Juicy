package com.example.juicy.network;

import android.content.Context;
import android.graphics.Bitmap;

import androidx.collection.LruCache;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

public class VolleySingleton {

    private static VolleySingleton instance;
    private final RequestQueue requestQueue;
    private final ImageLoader imageLoader;

    private VolleySingleton(Context context) {
        // Cola de peticiones
        requestQueue = Volley.newRequestQueue(context.getApplicationContext());

        // Cache de imágenes
        imageLoader = new ImageLoader(requestQueue, new ImageLoader.ImageCache() {
            private final LruCache<String, Bitmap> cache = new LruCache<>(50);

            @Override
            public Bitmap getBitmap(String url) {
                return cache.get(url);
            }

            @Override
            public void putBitmap(String url, Bitmap bitmap) {
                cache.put(url, bitmap);
            }
        });
    }

    // Obtener instancia
    public static synchronized VolleySingleton getInstance(Context context) {
        if (instance == null) {
            instance = new VolleySingleton(context);
        }
        return instance;
    }

    // Obtener cola de peticiones
    public RequestQueue getRequestQueue() {
        return requestQueue;
    }

    // Obtener ImageLoader
    public ImageLoader getImageLoader() {
        return imageLoader;
    }

    // Metodo que faltaba para enviar peticiones
    public <T> void addToRequestQueue(Request<T> request) {
        requestQueue.add(request);
    }
}
