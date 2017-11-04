package io.github.keibai;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

/**
 * Created by eduard on 3/11/17.
 */

public class SaveSharedPreference {

    static final String PREF_USER_ID = "user_id";

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
}
