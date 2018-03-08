package com.brandonlehr.whendidiwork.module;

import android.app.Application;

import com.brandonlehr.whendidiwork.Constants;
import com.franmontiel.persistentcookiejar.ClearableCookieJar;
import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.CookieCache;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;

import java.util.Collections;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.CipherSuite;
import okhttp3.ConnectionSpec;
import okhttp3.OkHttpClient;
import okhttp3.TlsVersion;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by blehr on 3/7/2018.
 */
@Module(includes = AppModule.class)
public class RetrofitClientModule {

    public RetrofitClientModule() {}


    @Singleton
    @Provides
    public Retrofit retrofitClient(OkHttpClient httpClient) {
        return new Retrofit.Builder()
                .baseUrl(Constants.API_ROOT)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient)
                .build();
    }

    @Singleton
    @Provides
    public ClearableCookieJar cookieJar(CookieCache cookieCache, SharedPrefsCookiePersistor mSharedPrefsCookiePersistor) {
        return new PersistentCookieJar(cookieCache, mSharedPrefsCookiePersistor);
    }

    @Singleton
    @Provides
    public CookieCache cookieCache() {
        return new SetCookieCache();
    }

    @Singleton
    @Provides
    public SharedPrefsCookiePersistor mSharedPrefsCookiePersistor(@Named("application_context")Application context) {
        return new SharedPrefsCookiePersistor(context);
    }

    @Singleton
    @Provides
    public OkHttpClient httpClient(ConnectionSpec spec, HttpLoggingInterceptor logging, ClearableCookieJar cookieJar) {
        return new OkHttpClient.Builder()
                .addInterceptor(logging)
                .connectionSpecs(Collections.singletonList(spec))
                .cookieJar(cookieJar)
                .build();
    }

    @Singleton
    @Provides
    public ConnectionSpec spec() {
        return new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                .tlsVersions(TlsVersion.TLS_1_2)
                .cipherSuites(
                        CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256,
                        CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256,
                        CipherSuite.TLS_DHE_RSA_WITH_AES_128_GCM_SHA256)
                .build();
    }

    @Singleton
    @Provides
    public HttpLoggingInterceptor logging() {
        return new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY);
    }
//
//    @Provides
//    @Named("mainActivity_viewModel")
//    ViewModel provideMainActivityViewModel(MainActivityViewModel viewModel) {
//        return viewModel;
//    }
//
//    @Provides
//    @Named("createSheet_viewModel")
//    ViewModel provideCreateSheetViewModel(CreateSheetViewModel viewModel) {
//        return viewModel;
//    }
//
//    @Provides
//    @Named("mainActivity_viewModelFactory")
//    ViewModelProvider.Factory provideMainActivityViewModelFactory(
//            ViewModelFactory factory
//    ) {
//        return factory;
//    }
//
//    @Provides
//    @Named("createSheet_viewModelFactory")
//    ViewModelProvider.Factory provideCreateSheetViewModelFactory(
//            CreateSheetViewModelFactory factory
//    ) {
//        return factory;
//    }

}
