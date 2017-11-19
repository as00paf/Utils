package com.pafoid.utils.geo.api;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pafoid.utils.geo.models.vo.Directions;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * This class is used to communicate with the Google Directions Service API.
 */
public class DirectionsApiClient {

    private static final String TAG = "DirApiClient";

    public static final String MOBILE_APP_URL = "https://maps.googleapis.com/maps/api/";
    private DirectionsApiService service;

    public DirectionsApiClient(final String token) {

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.connectTimeout(30, TimeUnit.SECONDS);
        httpClient.writeTimeout(1,TimeUnit.MINUTES);
        httpClient.readTimeout(1, TimeUnit.MINUTES);

        if(token != null){
            httpClient.addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request original = chain.request();
                    Request.Builder requestBuilder = original.newBuilder()
                            .header("Authorization", "Bearer " + token);
                    Request request = requestBuilder.build();
                    return chain.proceed(request);
                }
            });
        }

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        httpClient.addInterceptor(logging);


        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZZZZZ")
                .setLenient()
                .setPrettyPrinting()
                .create();


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MOBILE_APP_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(httpClient.build())
                .build();

        service = retrofit.create(DirectionsApiService.class);
    }

    //Shops
    public void getDirections(Callback<Directions> callback, LatLng origin, LatLng destination, String mode, String key){
        Call<Directions> call = service.getDirections(origin.latitude + "," + origin.longitude, destination.latitude + "," + destination.longitude, false, mode, key);
        call.enqueue(callback);
    }

}
