package org.kotemaru.android.gcm_sample2.lib;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.google.firebase.iid.FirebaseInstanceId;

/**
 * 旧 GCMRegister に似せた RegistrationId 管理クラス。
 *
 * @author kotemaru@kotemaru.org
 */
public class GCMRegister {
    private static final String TAG = GCMRegister.class.getSimpleName();
    public static final String GCM_PREF_NAME = "GCM";
    public static final String KEY_REG_ID = "RegistrationId";

    /**
     * GCMの初期化処理。
     * <li>RegistrationId が未登録なら登録処理を行う。</li>
     * @param context
     * @param isForceTokenRefresh 登録済でも登録処理を行う。
     */
    public static void init(Context context, boolean isForceTokenRefresh) {
        final String regId = GCMRegister.getRegistrationId(context);
        if (regId == null || isForceTokenRefresh) {
            onTokenRefresh(context);
        }
    }

    /**
     * 登録済の RegistrationId を返す。
     *
     * @param context
     * @return null=未登録
     */
    public static String getRegistrationId(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(GCM_PREF_NAME, Context.MODE_PRIVATE);
        String regId = prefs.getString(KEY_REG_ID, null);
        Log.d(TAG, "getRegistrationId:" + regId);
        return regId;
    }

    /**
     * RegistrationId の保存。内部利用のみ。
     * @param context
     * @param regId
     */
    private static void setRegistrationId(Context context, String regId) {
        Log.d(TAG, "setRegistrationId:" + regId);
        SharedPreferences prefs = context.getSharedPreferences(GCM_PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(KEY_REG_ID, regId).apply();
    }

    /**
     * GCM3.0の InstanceID から RegistrationId を取得する。
     * <li>RegistrationId は Preferences に保存する。</li>
     * <li>通信をするのでUI-Threadでは実行不可</li>
     *
     * @param context
     * @param senderId アプリのSENDER_ID
     * @return RegistrationId (null=取得失敗)
     */
    private static String registerSync(final Context context, final String senderId) {
        try {
            // for GCM-3.0
            //InstanceID instanceID = InstanceID.getInstance(context);
            //String regId = instanceID.getToken(senderId, GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            // for FCM
            String regId = FirebaseInstanceId.getInstance().getToken();

            Log.d(TAG, "registerSync: " + senderId + ":" + regId);
            return regId;
        } catch (Exception e) {
            Log.e(TAG, "Failed get token:" + senderId, e);
            return null;
        }
    }

    /**
     * RegistrationId の更新処理。
     * <li>通信をするので非同期処理</li>
     * <li>登録処理のコールバックを呼ぶ</li>
     * <li>前の RegistrationId と差し替えになる場合は登録解除のコールバックを呼ぶ。（登録処理の直前に）</li>
     *
     * @param context
     */
    public static void onTokenRefresh(final Context context) {
        Log.d(TAG, "onTokenRefresh: ");
        final GCMListener listener = ((GCMListenerFactory) context.getApplicationContext()).getGCMListener();
        if (listener == null) return;

        AsyncTask<?, ?, ?> task = new AsyncTask<Object, Void, String>() {
            @Override
            protected String doInBackground(Object... params) {
                String oldRegId = getRegistrationId(context);
                String regId = registerSync(context, listener.getSenderId());
                if (regId == null) return null;
                if (oldRegId != null && !oldRegId.equals(regId)) {
                    listener.onUnregistered(context, oldRegId);
                }
                boolean isSuccess = listener.onRegistered(context, regId);
                if (isSuccess) {
                    setRegistrationId(context, regId);
                }
                return regId;
            }
        };
        task.execute();
    }

    public static void onDestroy(Context context) {
    }
}
