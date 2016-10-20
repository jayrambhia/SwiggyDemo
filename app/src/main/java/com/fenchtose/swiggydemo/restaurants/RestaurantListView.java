package com.fenchtose.swiggydemo.restaurants;

import android.support.annotation.NonNull;

import com.fenchtose.swiggydemo.restaurants.models.Restaurant;

import java.util.List;

/**
 * Created by Jay Rambhia on 10/21/16.
 */

public interface RestaurantListView {

    void showLoading(boolean show);
    void setData(@NonNull List<Restaurant> restaurants);
    void showLoadingMore(boolean show);

}
