package com.fenchtose.swiggydemo.restaurants.api;

import com.fenchtose.swiggydemo.restaurants.models.RestaurantFeed;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by Jay Rambhia on 10/21/16.
 */

public interface SwiggyApi {

    String BASE_URL = "https://api.myjson.com/";

    @GET("bins/ngcc")
    Observable<RestaurantFeed> getData(@Query("page") int page);

}
