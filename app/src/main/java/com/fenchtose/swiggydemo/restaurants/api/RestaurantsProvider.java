package com.fenchtose.swiggydemo.restaurants.api;

import com.fenchtose.swiggydemo.restaurants.models.RestaurantFeed;

import rx.Observable;

/**
 * Created by Jay Rambhia on 10/21/16.
 */

public interface RestaurantsProvider {

    Observable<RestaurantFeed> getRestaurants(int page);

}
