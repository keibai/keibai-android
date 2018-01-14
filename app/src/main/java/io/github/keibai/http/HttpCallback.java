package io.github.keibai.http;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;

import io.github.keibai.gson.BetterGson;
import io.github.keibai.models.meta.Error;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okhttp3.ResponseBody;

public abstract class HttpCallback<T> implements Callback {
    Class<T> className;

    public HttpCallback(Class<T> className) {
        this.className = className;
    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        ResponseBody responseBody = response.body();
        if (!response.isSuccessful()) {
            Error error = fromJson(responseBody.string(), Error.class);
            onError(error == null ? customError("Unexpected server response.") : error);
            return;
        }

        T obj = fromJson(responseBody.string(), className);
        if (obj == null) {
            onError(customError("Unexpected object response."));
        } else {
            onSuccess(obj);
        }
    }

    /**
     * Always returns an Error object.
     * The Error message will be taken from the server response, if possible.
     * Otherwise a "Unexpected server response." will be returned in an Error object if the response
     * code is not 200.
     * If the response code is 200 and the object cannot be cast to whatever it was indicated, a
     * "Unexpected object response." Error will be returned.
     * @param error
     * @throws IOException
     */
    public abstract void onError(Error error) throws IOException;

    /**
     * Returns a T object, or null.
     * @param response
     * @throws IOException
     */
    public abstract void onSuccess(T response) throws IOException;

    private Error customError(String errorMessage) {
        Error error = new Error();
        error.error = errorMessage;
        return error;
    }

    private <E> E fromJson(String content, Class<E> className) {
        try {
            return new BetterGson().newInstance().fromJson(content, className);
        } catch (JsonSyntaxException e) {
            return null;
        }
    }
}
