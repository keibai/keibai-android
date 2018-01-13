package io.github.keibai.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Date;

public class BetterGson {

    public Gson newInstance() {
        return new GsonBuilder().registerTypeAdapter(Date.class, new GsonUTCDateAdapter()).create();
    }
}

