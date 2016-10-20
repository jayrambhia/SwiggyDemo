package com.fenchtose.swiggydemo.restaurants;

import android.content.res.Configuration;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.fenchtose.swiggydemo.R;
import com.fenchtose.swiggydemo.restaurants.api.RetrofitRestaurantsProvider;
import com.fenchtose.swiggydemo.restaurants.api.SwiggyApi;
import com.fenchtose.swiggydemo.restaurants.models.Restaurant;
import com.fenchtose.swiggydemo.restaurants.models.RestaurantFeed;
import com.fenchtose.swiggydemo.restaurants.presenter.RestaurantListPresenter;
import com.fenchtose.swiggydemo.restaurants.presenter.RestaurantListPresenterImpl;
import com.fenchtose.swiggydemo.utils.RecyclerViewScrollListener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RestaurantListActivity extends AppCompatActivity implements RestaurantListView {

    private static final String SAVED_FEED = "saved_feed";

    @BindView(R.id.recycler_view) RecyclerView recyclerView;
    @BindView(R.id.progress_bar) ProgressBar progressBar;

    private RequestManager glideRequestManager;
    private RestaurantsAdapter adapter;

    private RestaurantListPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        glideRequestManager = Glide.with(this);
        adapter = new RestaurantsAdapter(this, glideRequestManager);
        adapter.setHasStableIds(true);

        RecyclerView.LayoutManager layoutManager;

        int orientation = getResources().getConfiguration().orientation;

        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            layoutManager = new GridLayoutManager(this, 2);
            ((GridLayoutManager)layoutManager).setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    return adapter.isPositionLoader(position) ? 2 : 1;
                }
            });
        } else {
            layoutManager = new LinearLayoutManager(this);
        }

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(new RecyclerViewScrollListener() {
            @Override
            public void onScrollUp() {

            }

            @Override
            public void onScrollDown() {

            }

            @Override
            public void onLoadMore() {
                presenter.loadMore();
            }
        });


        // TODO Use DI for API and Presenter

        Retrofit retrofit = new Retrofit.Builder()
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(SwiggyApi.BASE_URL)
                .build();

        presenter = new RestaurantListPresenterImpl(this, new RetrofitRestaurantsProvider(retrofit));
        presenter.attachView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.load();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        RestaurantFeed feed = presenter.saveInstance();
        if (feed != null) {
            outState.putParcelable(SAVED_FEED, feed);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        RestaurantFeed feed = savedInstanceState.getParcelable(SAVED_FEED);
        if (feed != null) {
            presenter.reloadInstance(feed);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.detachView();
    }

    @Override
    public void showLoading(boolean show) {
        if (show) {
            recyclerView.setVisibility(View.GONE);
        }

        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setData(@NonNull List<Restaurant> restaurants) {
        adapter.setRestaurants(restaurants);
        adapter.notifyDataSetChanged();
        recyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void showLoadingMore(boolean show) {
        adapter.setShowLoading(show);
        adapter.notifyDataSetChanged();
    }
}
