package io.github.keibai;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

import com.google.gson.Gson;

import io.github.keibai.models.Auction;
import io.github.keibai.models.Event;

public class SaveSharedPreference {

    static final String PREF_USER_ID = "user_id";
    static final String PREF_COOKIES = "cookies";
    static final String PREF_CURRENT_EVENT = "current_event";
    static final String PREF_CURRENT_AUCTION = "current_auction";

    static SharedPreferences getSharedPreferences(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    public static void setUserId(Context ctx, long userId) {
        Editor editor = getSharedPreferences(ctx).edit();
        editor.putLong(PREF_USER_ID, userId);
        editor.apply();
    }

    public static long getUserId(Context ctx) {
        return getSharedPreferences(ctx).getLong(PREF_USER_ID, -1);
    }

    public static void setCookies(Context ctx, String cookies) {
        Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_COOKIES, cookies);
        editor.apply();
    }

    public static String getCookies(Context ctx) {
        return getSharedPreferences(ctx).getString(PREF_COOKIES, "");
    }

    public static void setCurrentEvent(Context ctx, Event event) {
        Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_CURRENT_EVENT, new Gson().toJson(event));
        editor.apply();
    }

    public static Event getCurrentEvent(Context ctx) {
        String jsonEvent = getSharedPreferences(ctx).getString(PREF_CURRENT_EVENT, "{}");
        return new Gson().fromJson(jsonEvent, Event.class);
    }

    public static void setCurrentAuction(Context ctx, Auction auction) {
        Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_CURRENT_AUCTION, new Gson().toJson(auction));
        editor.apply();
    }

    public static Auction getCurrentAuction(Context ctx) {
        String jsonEvent = getSharedPreferences(ctx).getString(PREF_CURRENT_AUCTION, "{}");
        return new Gson().fromJson(jsonEvent, Auction.class);
    }
}

