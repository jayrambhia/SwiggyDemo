package com.fenchtose.swiggydemo.restaurants.presenter;

import android.support.annotation.Nullable;

import com.fenchtose.swiggydemo.restaurants.models.RestaurantFeed;

/**
 * Created by Jay Rambhia on 10/21/16.
 */

public interface RestaurantListPresenter {

    void attachView();
    void detachView();

    void load();
    void reload();
    void loadMore();

    @Nullable RestaurantFeed saveInstance();
    void reloadInstance(@Nullable RestaurantFeed feed);

}
