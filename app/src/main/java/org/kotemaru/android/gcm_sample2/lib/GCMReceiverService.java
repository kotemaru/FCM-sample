package org.kotemaru.android.gcm_sample2.lib;

import android.os.Bundle;

import com.google.android.gms.gcm.GcmListenerService;
import com.google.android.gms.iid.InstanceIDListenerService;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

/**
 * GCM のメッセージを受け取るサービス。
 *
 * @author kotemaru@kotemaru.org
 */
public class GCMReceiverService
        //extends GcmListenerService
        extends FirebaseMessagingService
{
    private static final String TAG = GCMReceiverService.class.getSimpleName();

    private GCMListener mListener;

    @Override
    public void onCreate() {
        super.onCreate();
        mListener = ((GCMListenerFactory) getApplication()).getGCMListener();
    }

    // for GCM-3.0
    //@Override
    public void onMessageReceived(String from, Bundle data) {
        if (mListener == null) return;
        mListener.onMessageReceived(this, from, data);
    }

    // fot FCM
    @Override
    public void onMessageReceived(RemoteMessage message){
        String from = message.getFrom();
        Map<String,String> data = message.getData();

        Bundle bundle = new Bundle();
        for (Map.Entry<String,String> ent : data.entrySet()) {
            bundle.putString(ent.getKey(), ent.getValue());
        }
        onMessageReceived(from, bundle);
    }

    @Override
    public void onDeletedMessages() {
        if (mListener == null) return;
        mListener.onDeletedMessages(this);
    }

    @Override
    public void onMessageSent(String msgId) {
        if (mListener == null) return;
        mListener.onMessageSent(this, msgId);
    }

    // for GCM-3.0
    //@Override
    //public void onSendError(String msgId, String error) {
    //    if (mListener == null) return;
    //    mListener.onSendError(this, msgId, error);
    //}
}
