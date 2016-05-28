package org.kotemaru.android.gcm_sample2;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.os.AsyncTask;
import android.util.Log;

/**
 * ユーティリティ。GCMの本質とは無関係。
 *
 * @author @kotemaru.org
 */
public class Util {

    /**
     * (非同期)HTTPのGETリクエスト発行。色々手抜き。
     *
     * @param uri HTTPのURI
     * @return タスク。
     */
    public static AsyncTask<String, Void, String> doGetAsync(String uri) {
        AsyncTask<String, Void, String> task = new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... params) {
                return Util.doGet(params[0]);
            }
        };
        task.execute(uri);
        return task;
    }

    /**
     * HTTPのGETリクエスト発行。
     *
     * @param uri HTTPのURI
     * @return HTTP取得本文
     */
    public static String doGet(String uri) {
        HttpURLConnection con = null;
        try {
            URL url = new URL(uri);

            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setInstanceFollowRedirects(true);
            con.setRequestProperty("Connection", "close");
            con.connect();

            int status = con.getResponseCode();
            if (status != 200) {
                throw new RuntimeException("HTTP error: " + status);
            }

            StringBuilder sbuf = new StringBuilder();
            InputStream in = con.getInputStream();
            try {
                byte[] buff = new byte[1024];
                int n;
                while ((n = in.read(buff)) > 0) {
                    sbuf.append(new String(buff, 0, n, "utf-8"));
                }
            } finally {
                in.close();
            }
            return sbuf.toString();
        } catch (IOException e) {
            Log.e("doGet", e.toString());
            throw new RuntimeException(e);
            //return e.toString();
        } finally {
            if (con != null) con.disconnect();
        }
    }
}
