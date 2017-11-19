package com.pafoid.utils.geo.api;

import com.pafoid.utils.geo.models.vo.Directions;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Interface that defines the endpoints to be used with the Wikiki REST Web API
 */
public interface DirectionsApiService {

    @GET("directions/json")
    Call<Directions> getDirections(@Query("origin") String origin, @Query("destination") String destination, @Query("sensor") boolean sensor, @Query("mode") String mode, @Query("key") String key);

}
