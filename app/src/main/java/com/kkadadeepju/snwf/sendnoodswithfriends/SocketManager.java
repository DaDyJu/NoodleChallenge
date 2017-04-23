package com.kkadadeepju.snwf.sendnoodswithfriends;

import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Created by dportetrachsel on 4/20/17.
 */

public class SocketManager {

    private Socket mSocket;

    private MainActivity activity;

    SocketManager(MainActivity act) {
        try {
            mSocket = IO.socket(Constants.SERVER_URL);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        activity = act;
    }

    public void connect() {
        mSocket.on(Socket.EVENT_CONNECT, onConnect);
        mSocket.on(Socket.EVENT_DISCONNECT, onDisconnect);
        mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
        mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);

        mSocket.on(Socket.EVENT_ERROR, onEventError);

        mSocket.on("ackConnected", onAckConnected);
        mSocket.on("joinedRoom", onJoinedRoom);
        mSocket.on("startSession", onStartSession);

        mSocket.connect();

        JSONObject obj = new JSONObject();

        Random rand = new Random();
        rand.setSeed(System.currentTimeMillis());
        try {
            obj.put("clientId", rand.nextInt(10000000));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            obj.put("existing", false);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mSocket.emit("connectClient", obj);
    }

    private Emitter.Listener onAckConnected = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            // If true, find session
            System.out.println("ackConnected call");
            //JSONObject obj = (JSONObject)args[0];

            JSONObject obj = new JSONObject();
            try {
                obj.put("clientId", 1);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            mSocket.emit("findSession", obj);
        }
    };

    private Emitter.Listener onJoinedRoom = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            // If true, find session
            System.out.println("joined room");
            JSONObject obj = (JSONObject) args[0];
        }
    };

    private Emitter.Listener onStartSession = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            // If true, find session
            System.out.println("session started");
            JSONObject obj = (JSONObject) args[0];

            //activity.showLoadingDialog(true);
            Intent myIntent = new Intent(activity.getApplicationContext(), GameActivity.class);
            activity.startActivity(myIntent);
        }
    };

    private Emitter.Listener onEventError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            // If true, find session
            System.out.println("event error");
        }
    };

    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            System.out.println("CONNECTED");
        }
    };

    private Emitter.Listener onDisconnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            System.out.println("DICONNECTED");
        }
    };

    private Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            System.out.println("CONNECT ERROR");
        }
    };
}
