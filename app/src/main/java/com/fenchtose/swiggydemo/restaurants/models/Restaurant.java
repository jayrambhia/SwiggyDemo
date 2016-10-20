package com.fenchtose.swiggydemo.restaurants.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by Jay Rambhia on 10/21/16.
 */

public class Restaurant implements Parcelable {

    String name;
    String city;
    String area;
    String avg_rating;
    String cid;
    List<String> cuisine;
    String costForTwo;
    int deliveryTime;
    boolean closed;

    List<Restaurant> chain;

    private transient String cuisines;
    private transient String imageUrl;
    private transient boolean expanded;

    private static final String IMAGE_URL_PREFIX = "https://res.cloudinary.com/swiggy/image/upload/";

    protected Restaurant(Parcel in) {
        name = in.readString();
        city = in.readString();
        area = in.readString();
        avg_rating = in.readString();
        cid = in.readString();
        cuisine = in.createStringArrayList();
        costForTwo = in.readString();
        deliveryTime = in.readInt();
        closed = in.readByte() != 0;
        chain = in.createTypedArrayList(Restaurant.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(city);
        dest.writeString(area);
        dest.writeString(avg_rating);
        dest.writeString(cid);
        dest.writeStringList(cuisine);
        dest.writeString(costForTwo);
        dest.writeInt(deliveryTime);
        dest.writeByte((byte) (closed ? 1 : 0));
        dest.writeTypedList(chain);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Restaurant> CREATOR = new Creator<Restaurant>() {
        @Override
        public Restaurant createFromParcel(Parcel in) {
            return new Restaurant(in);
        }

        @Override
        public Restaurant[] newArray(int size) {
            return new Restaurant[size];
        }
    };

    public String getName() {
        return name;
    }

    public String getCity() {
        return city;
    }

    public String getArea() {
        return area;
    }

    public String getAvg_rating() {
        return avg_rating;
    }

    public String getCid() {
        return cid;
    }

    public String getImageUrl() {
        if (imageUrl == null) {
            buildImageUrl();
        }

        return imageUrl;
    }

    private void buildImageUrl() {
        imageUrl = IMAGE_URL_PREFIX + cid;
    }

    public List<String> getCuisine() {
        return cuisine;
    }

    public String getCuisines() {
        if (cuisines == null) {
            buildCuisineString();
        }

        return cuisines;
    }

    private void buildCuisineString() {
        if (cuisine == null || cuisine.isEmpty()) {
            cuisines = "";
            return;
        }

        StringBuilder sb = new StringBuilder();
        for (String item : cuisine) {
            sb.append(item);
            sb.append(", ");
        }

        sb.delete(sb.length() - 2, sb.length());
        cuisines = sb.toString();
    }

    public String getCostForTwo() {
        return costForTwo;
    }

    public int getDeliveryTime() {
        return deliveryTime;
    }

    public boolean isClosed() {
        return closed;
    }

    public List<Restaurant> getChain() {
        return chain;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }
}
