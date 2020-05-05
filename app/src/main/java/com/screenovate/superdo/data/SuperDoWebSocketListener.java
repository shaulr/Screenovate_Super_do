package com.screenovate.superdo.data;
import android.util.Log;

import com.screenovate.superdo.ui.main.BagListener;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class SuperDoWebSocketListener extends WebSocketListener {
    private final static String TAG = SuperDoWebSocketListener.class.getSimpleName();
    private Moshi moshi;
    private JsonAdapter<Bag> jsonAdapter;
    private BagListener listener;
    private boolean running = true;

    SuperDoWebSocketListener(BagListener listener) {
        this.listener = listener;
    }
    void stop() {
        running = false;
    }
    @Override
    public void onMessage(@NotNull WebSocket webSocket, @NotNull String text) {
        if(!running) return;
        Log.i(TAG, text);
        try {
            Bag bag = jsonAdapter.fromJson(text);
            listener.onNewBag(bag);
        } catch (IOException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
    }

    @Override
    public void onMessage(@NotNull WebSocket webSocket, ByteString bytes) {
        Log.i(TAG, bytes.toString());

    }
    @Override
    public void onOpen(@NotNull WebSocket webSocket, @NotNull Response response) {
        moshi = new Moshi.Builder()
                    .build();
        jsonAdapter = moshi.adapter(Bag.class);
    }

    @Override
    public void onClosing(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
        Log.i(TAG, "closing code " + code + " " + reason);
    }

    @Override
    public void onFailure(@NotNull WebSocket webSocket, @NotNull Throwable t, @Nullable Response response) {
        Log.e(TAG, Log.getStackTraceString(t));
    }
}
