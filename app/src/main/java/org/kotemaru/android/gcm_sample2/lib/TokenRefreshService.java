package org.kotemaru.android.gcm_sample2.lib;

//import com.google.android.gms.iid.InstanceIDListenerService;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * RegistrationId の更新タイミングを受け取るだけのサービス。
 *
 * @author kotemaru@kotemaru.org
 */
public class TokenRefreshService
        // extends InstanceIDListenerService  // for GCM-3.0
        extends FirebaseInstanceIdService // for FCM
{
    @Override
    public void onTokenRefresh() {
        GCMRegister.onTokenRefresh(this);
    }
}
