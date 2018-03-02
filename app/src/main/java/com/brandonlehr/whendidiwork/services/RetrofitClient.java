package com.brandonlehr.whendidiwork.services;

import android.content.Context;

import com.brandonlehr.whendidiwork.Constants;
import com.franmontiel.persistentcookiejar.ClearableCookieJar;
import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;

import java.util.Collections;

import okhttp3.CipherSuite;
import okhttp3.ConnectionSpec;
import okhttp3.OkHttpClient;
import okhttp3.TlsVersion;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by blehr on 2/6/2018.
 */

public class RetrofitClient {
    private static final String TAG = "RetrofitClient";
    private static Retrofit retrofit = null;


    static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

    public static Retrofit getClient(Context context) {
        ClearableCookieJar cookieJar =
                new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(context));

        // add ciphers
        ConnectionSpec spec = new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                .tlsVersions(TlsVersion.TLS_1_2)
                .cipherSuites(
                        CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256,
                        CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256,
                        CipherSuite.TLS_DHE_RSA_WITH_AES_128_GCM_SHA256)
                .build();

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        // set your desired log level
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        // add logging as last interceptor
        httpClient.addInterceptor(logging);
        httpClient.connectionSpecs(Collections.singletonList(spec));

        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(Constants.API_ROOT)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(httpClient.cookieJar(cookieJar).build())
                    .build();
        }
        return retrofit;
    }

//    private static SSLContext getSSLConfig(Context context) throws CertificateException, IOException,
//            KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
//
//        // Loading CAs from an InputStream
//        CertificateFactory cf = null;
//        cf = CertificateFactory.getInstance("X.509");
//
//        Certificate ca;
//        // I'm using Java7. If you used Java6 close it manually with finally.
//        try (InputStream cert = context.getResources().openRawResource(R.raw.cert)) {
//            ca = cf.generateCertificate(cert);
//        }
//
//        // Creating a KeyStore containing our trusted CAs
//        String keyStoreType = KeyStore.getDefaultType();
//        KeyStore keyStore = KeyStore.getInstance(keyStoreType);
//        keyStore.load(null, null);
//        keyStore.setCertificateEntry("ca", ca);
//
//        // Creating a TrustManager that trusts the CAs in our KeyStore.
//        String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
//        TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
//        tmf.init(keyStore);
//
//        // Creating an SSLSocketFactory that uses our TrustManager
//        SSLContext sslContext = SSLContext.getInstance("TLS");
//        sslContext.init(null, tmf.getTrustManagers(), null);
//
//        return sslContext;
//    }
}
