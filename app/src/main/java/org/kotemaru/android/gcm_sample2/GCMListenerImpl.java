// Copyright (c) kotemaru.org  (APL/2.0)
package org.kotemaru.android.gcm_sample2;

import org.kotemaru.android.gcm_sample2.lib.GCMListener;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

public class GCMListenerImpl implements GCMListener {
    private static final String TAG = GCMListenerImpl.class.getSimpleName();

    // https://console.developers.google.comのProject Number。
    public static final String SENDER_ID = "79834248146";
    // アプリサーバーのURL。
    public static final String SERVER_URL = "http://192.168.0.9:8888/";
    // アプリのユーザID。本来はログイン中のユーザとかになるはず。
    public static final String USER_ID = "TarouYamada";


    // for GCM-3.0  FCM では不要(google-services.jsonに有る)
    @Override
    public String getSenderId() {
        return SENDER_ID;
    }

    @Override
    public boolean onRegistered(Context context, String registrationId) {
        Log.i(TAG, "onRegistered: regId = " + registrationId);
        // GCMから発行された端末IDをアプリサーバに登録する。
        try {
            String uri = SERVER_URL + "?action=register"
                    + "&userId=" + USER_ID
                    + "&regId=" + registrationId;
            Util.doGet(uri);
            return true;
        } catch (Throwable t) {
            Log.e(TAG, "onRegistered:", t);
            return false;
        }
    }

    @Override
    public void onUnregistered(Context context, String registrationId) {
        Log.i(TAG, "onUnregistered: regId = " + registrationId);
        // GCMから発行された端末IDをアプリサーバから登録解除する。
        String uri = SERVER_URL + "?action=unregister"
                + "&userId=" + USER_ID
                + "&regId=" + registrationId;
        Util.doGet(uri);
    }

    @Override
    public void onMessageReceived(Context context, String from, Bundle data) {
        String msg = data.getString("msg");
        Log.d(TAG, "onMessageReceived: from=" + from + "  message=" + msg);
    }

    @Override
    public void onDeletedMessages(Context context) {
        Log.d(TAG, "onDeletedMessages:");
    }

    @Override
    public void onMessageSent(Context context, String msgId) {
        Log.d(TAG, "onMessageSent:" + msgId);
    }

    @Override
    public void onSendError(Context context, String msgId, String error) {
        Log.d(TAG, "onSendError:" + msgId + "," + error);
    }
}
