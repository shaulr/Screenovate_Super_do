package com.screenovate.superdo.data;

import android.content.Context;

import com.screenovate.superdo.R;
import com.screenovate.superdo.ui.main.BagListener;

import java.lang.ref.WeakReference;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;

public class SuperDoModel {
    private WeakReference<Context> savedContext;
    private OkHttpClient client;
    private String url;
    private WebSocket websocket;
    private BagListener listener;
    private SuperDoWebSocketListener socketListener;
    private boolean connected = false;
    public SuperDoModel(Context context, BagListener listener) {
        if(context != null) {
            savedContext = new WeakReference<>(context);
        }
        this.listener = listener;
    }

    public void startLoading() {
        if(client == null) {
            initializeWebsocketClient();
        }
        Request request = new Request.Builder().url(url).build();
        socketListener = new SuperDoWebSocketListener(listener);
        websocket = client.newWebSocket(request, socketListener);
        connected = true;
    }

    public void stopLoading() {
        if(websocket != null && client != null) {
            websocket.close(1000, "Normal close");
            client.dispatcher().executorService().shutdown();
            client.connectionPool().evictAll();
            socketListener.stop();
        }
        client = null;
        websocket = null;
    }

    private void initializeWebsocketClient() {
        client = new OkHttpClient();
        url = savedContext.get().getString(R.string.url);
    }
}
