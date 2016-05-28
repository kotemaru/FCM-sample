// Copyright (c) kotemaru.org  (APL/2.0)
package org.kotemaru.android.gcm_sample2.lib;

import android.content.Context;
import android.os.Bundle;

public interface GCMListener {
    String getSenderId();

    /**
     * アプリサーバへregistrationIdを登録する。
     * @param context
     * @param registrationId
     * @return true=登録成功。
     */
    boolean onRegistered(Context context, String registrationId);
    void onUnregistered(Context context, String registrationId);
    void onMessageReceived(Context context, String from, Bundle data);
    void onDeletedMessages(Context context);
    void onMessageSent(Context context, String msgId);
    void onSendError(Context context, String msgId, String error);
}
