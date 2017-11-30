package io.github.keibai.http;

import android.content.Context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.github.keibai.SaveSharedPreference;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

public class HttpCookieJar implements CookieJar {

    private Context context;
    private List<Cookie> cookies;

    public HttpCookieJar(Context context) {
        this.context = context;
        this.cookies = new ArrayList<>();

        String savedCookies = SaveSharedPreference.getCookies(context);
        if (savedCookies.length() > 0) {
            String[] cookieList = savedCookies.split(",");
            for (String cookiePair : cookieList) {
                String[] cookiePairValues = cookiePair.split("=");
                Cookie cookie = new Cookie.Builder()
                        .name(cookiePairValues[0])
                        .value(cookiePairValues[1])
                        .domain("keibai.herokuapp.com")
                        .build();
                cookies.add(cookie);
            }
        }
    }

    @Override
    public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
        // Save to instance.
        this.cookies = cookies;

        // Save to shared preferences.
        StringBuilder cookieStr = new StringBuilder();
        String delimiter = "";
        for (Cookie cookie : cookies) {
            cookieStr.append(delimiter);
            cookieStr.append(String.format("%s=%s", cookie.name(), cookie.value()));
            delimiter = ",";
        }
        SaveSharedPreference.setCookies(context, cookieStr.toString());
    }

    @Override
    public List<Cookie> loadForRequest(HttpUrl url) {
        if (cookies.size() == 0) {
            return Collections.emptyList();
        }

        return cookies;
    }
}
