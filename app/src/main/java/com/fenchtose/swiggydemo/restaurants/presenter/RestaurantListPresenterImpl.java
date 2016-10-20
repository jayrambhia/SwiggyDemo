package com.fenchtose.swiggydemo.restaurants.presenter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fenchtose.swiggydemo.restaurants.RestaurantListView;
import com.fenchtose.swiggydemo.restaurants.api.RestaurantsProvider;
import com.fenchtose.swiggydemo.restaurants.models.RestaurantFeed;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Jay Rambhia on 10/21/16.
 */

public class RestaurantListPresenterImpl implements RestaurantListPresenter {

    private RestaurantListView restaurantListView;
    private RestaurantsProvider restaurantsProvider;

    private RestaurantFeed feed;

    private boolean isLoading = false;

    private CompositeSubscription _subscriptions;

    public RestaurantListPresenterImpl(RestaurantListView restaurantListView,
                                       RestaurantsProvider restaurantsProvider) {
        this.restaurantListView = restaurantListView;
        this.restaurantsProvider = restaurantsProvider;
    }

    @Override
    public void attachView() {
        _subscriptions = new CompositeSubscription();
    }

    @Override
    public void detachView() {
        _subscriptions.unsubscribe();
    }

    @Override
    public void load() {
        if (feed == null) {
            callApi(1);
            return;
        }

    }

    @Override
    public void reload() {
        // TODO implement this
    }

    @Override
    public void loadMore() {
        if (isLoading) {
            return;
        }

        if (feed == null) {
            load();
            return;
        }

        callApi(feed.getPageNum() + 1);
        restaurantListView.showLoadingMore(true);
    }

    private void callApi(int page) {
        // check network connection here

        isLoading = true;
        Observable<RestaurantFeed> observable = restaurantsProvider.getRestaurants(page);
        Subscription subscription = observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<RestaurantFeed>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        restaurantListView.showLoading(false);
                        isLoading = false;
                        // TODO show error
                    }

                    @Override
                    public void onNext(RestaurantFeed restaurantFeed) {
                        if (feed != null) {
                            feed.add(restaurantFeed);
                        } else {
                            feed = restaurantFeed;
                        }

                        setData(feed);
                    }
                });

        _subscriptions.add(subscription);
    }


    private void setData(@NonNull RestaurantFeed feed) {
        restaurantListView.showLoading(false);
        restaurantListView.showLoadingMore(false);
        restaurantListView.setData(feed.getRestaurants());
        isLoading = false;
    }

    @Nullable
    @Override
    public RestaurantFeed saveInstance() {
        return feed;
    }

    @Override
    public void reloadInstance(@NonNull RestaurantFeed savedFeed) {
        feed = savedFeed;
        setData(feed);
    }
}
