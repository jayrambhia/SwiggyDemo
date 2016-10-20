package com.fenchtose.swiggydemo.restaurants.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jay Rambhia on 10/21/16.
 */

public class RestaurantFeed implements Parcelable {

    private List<Restaurant> restaurants;

    private transient int pageNum;

    protected RestaurantFeed(Parcel in) {
        restaurants = in.createTypedArrayList(Restaurant.CREATOR);
        pageNum = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(restaurants);
        dest.writeInt(pageNum);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<RestaurantFeed> CREATOR = new Creator<RestaurantFeed>() {
        @Override
        public RestaurantFeed createFromParcel(Parcel in) {
            return new RestaurantFeed(in);
        }

        @Override
        public RestaurantFeed[] newArray(int size) {
            return new RestaurantFeed[size];
        }
    };

    public List<Restaurant> getRestaurants() {
        return restaurants;
    }

    public void add(@NonNull RestaurantFeed feed) {
        if (feed.getRestaurants() != null) {
            if (restaurants == null) {
                restaurants = new ArrayList<>();
            }

            restaurants.addAll(feed.getRestaurants());
        }

        // This should be available in the API
        pageNum++;
    }

    public int getPageNum() {
        return pageNum;
    }
}
