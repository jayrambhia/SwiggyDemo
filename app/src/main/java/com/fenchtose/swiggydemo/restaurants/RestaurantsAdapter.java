package com.fenchtose.swiggydemo.restaurants;

import android.content.Context;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;
import com.fenchtose.swiggydemo.R;
import com.fenchtose.swiggydemo.restaurants.models.Restaurant;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Jay Rambhia on 10/21/16.
 */

public class RestaurantsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_PHOTO = 1;
    private static final int VIEW_TYPE_LOADER = 2;

    private static final int ITEM_ID_LOADER = -1;

    private Context context;
    private LayoutInflater inflater;
    private List<Restaurant> restaurants;

    private RequestManager imageProvider;
    private boolean showLoading = false;

    @ColorInt private int costHighlightColor;
    @ColorInt private int outletTextColor;
    private int outletTextPadding;

    public static final String RUPEE_SYMBOL = "₹";

    public RestaurantsAdapter(@NonNull Context context, @NonNull RequestManager imageProvider) {
        this.context = context;
        this.imageProvider = imageProvider;
        this.inflater = LayoutInflater.from(context);
        costHighlightColor = ContextCompat.getColor(context, R.color.white_100_percent);
        outletTextColor = ContextCompat.getColor(context, R.color.black_80_percent);
        outletTextPadding = context.getResources().getDimensionPixelOffset(R.dimen.outlet_text_padding);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_LOADER) {
            return new LoaderViewHolder(inflater.inflate(R.layout.item_loader_layout, parent, false));
        } else if (viewType == VIEW_TYPE_PHOTO) {
            return new RestaurantViewHolder(inflater.inflate(R.layout.item_restaurant_layout, parent, false));
        }

        throw new RuntimeException("Invalid view type: " + viewType);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (isPositionLoader(position)) {
            LoaderViewHolder viewHolder = (LoaderViewHolder) holder;
            viewHolder.progressBar.setVisibility(showLoading ? View.VISIBLE : View.GONE);
            return;
        }

        RestaurantViewHolder viewHolder = (RestaurantViewHolder)holder;

        Restaurant restaurant = restaurants.get(position);

        viewHolder.titleView.setText(restaurant.getName());
        viewHolder.cuisineView.setText(restaurant.getCuisines());
        viewHolder.ratingView.setText(restaurant.getAvg_rating());
        viewHolder.costView.setText(getCostString(restaurant.getCostForTwo().length()));

        if (restaurant.getChain() != null && !restaurant.getChain().isEmpty()) {
            viewHolder.outletsView.setText((restaurant.getChain().size() + 1) + " outlets near you");
            viewHolder.outletsView.setVisibility(View.VISIBLE);
            viewHolder.areaView.setVisibility(View.GONE);
            viewHolder.expandActionView.setVisibility(View.VISIBLE);

            if (restaurant.isExpanded()) {
                // Add views here
                viewHolder.expandActionView.setRotation(180);
                viewHolder.outletsContainer.removeAllViews();
                viewHolder.addOutletsViews(restaurant);

            } else {
                viewHolder.expandActionView.setRotation(0);
                viewHolder.outletsContainer.removeAllViews();
            }

        } else {
            viewHolder.areaView.setVisibility(View.VISIBLE);
            viewHolder.areaView.setText(restaurant.getArea());
            viewHolder.outletsView.setVisibility(View.GONE);
            viewHolder.expandActionView.setVisibility(View.GONE);
        }

        imageProvider.load(restaurant.getImageUrl()).into(viewHolder.coverView);
    }

    private CharSequence getCostString(int costCount) {
        SpannableString spannableString = new SpannableString("$$$$");
        spannableString.setSpan(new ForegroundColorSpan(costHighlightColor), 0, costCount, 0);
        return spannableString;
    }

    @Override
    public int getItemCount() {
        if (restaurants == null || restaurants.isEmpty()) {
            return 0;
        }

        int size = restaurants.size();
        if (showLoading) {
            size++;
        }

        return size;
    }

    public boolean isPositionLoader(int position) {
        return showLoading && position == restaurants.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (isPositionLoader(position)) {
            return VIEW_TYPE_LOADER;
        }

        return VIEW_TYPE_PHOTO;
    }

    @Override
    public long getItemId(int position) {
        if (isPositionLoader(position)) {
            return ITEM_ID_LOADER;
        }

        return restaurants.get(position).getCid().hashCode();
    }

    public void setShowLoading(boolean showLoading) {
        this.showLoading = showLoading;
    }

    public void setRestaurants(List<Restaurant> restaurants) {
        this.restaurants = restaurants;
    }

    public class RestaurantViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.title_view) TextView titleView;
        @BindView(R.id.cuisine_view) TextView cuisineView;
        @BindView(R.id.cover_view) ImageView coverView;
        @BindView(R.id.rating_view) TextView ratingView;
        @BindView(R.id.cost_view) TextView costView;
        @BindView(R.id.expand_action_view) ImageView expandActionView;
        @BindView(R.id.area_view) TextView areaView;
        @BindView(R.id.outlets_view) TextView outletsView;
        @BindView(R.id.outlets_container) ViewGroup outletsContainer;

        public RestaurantViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            expandActionView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    Restaurant restaurant = restaurants.get(position);
                    restaurant.setExpanded(!restaurant.isExpanded());
                    outletsContainer.removeAllViews();

                    if (restaurant.isExpanded()) {
                        addOutletsViews(restaurant);
                    }

                    expandActionView.setRotation(restaurant.isExpanded() ? 180 : 0);
                }
            });
        }

        private void addOutletsViews(Restaurant restaurant) {
            for (View view : getOutletsViews(restaurant)) {
                outletsContainer.addView(view);
            }
        }

        private List<View> getOutletsViews(Restaurant restaurant) {
            List<View> views = new ArrayList<>();
            views.add(getOutletView(restaurant));

            if (restaurant.getChain() != null) {
                for (Restaurant chainItem : restaurant.getChain()) {
                    views.add(getOutletView(chainItem));
                }
            }

            return views;
        }

        private TextView getOutletView(Restaurant restaurant) {
            TextView textView = new TextView(context);
            textView.setText(restaurant.getArea());
            textView.setTextSize(16);
            textView.setTextColor(outletTextColor);
            textView.setPadding(outletTextPadding, outletTextPadding, outletTextPadding, outletTextPadding);
            return textView;
        }
    }

    public class LoaderViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.progress_bar) ProgressBar progressBar;

        public LoaderViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
