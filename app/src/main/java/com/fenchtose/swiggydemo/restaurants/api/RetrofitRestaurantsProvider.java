package com.fenchtose.swiggydemo.restaurants.api;

import com.fenchtose.swiggydemo.restaurants.models.RestaurantFeed;

import retrofit2.Retrofit;
import rx.Observable;

/**
 * Created by Jay Rambhia on 10/21/16.
 */

public class RetrofitRestaurantsProvider implements RestaurantsProvider {

    private SwiggyApi swiggyApi;

    public RetrofitRestaurantsProvider(Retrofit retrofit) {
        swiggyApi = retrofit.create(SwiggyApi.class);
    }

    @Override
    public Observable<RestaurantFeed> getRestaurants(int page) {
        return swiggyApi.getData(page);
    }
}
