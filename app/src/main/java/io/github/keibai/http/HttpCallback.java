package io.github.keibai.http;

import com.google.gson.Gson;

import java.io.IOException;

import io.github.keibai.models.Model;
import io.github.keibai.models.meta.Error;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okhttp3.ResponseBody;

public abstract class HttpCallback<T extends Model> implements Callback {

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        ResponseBody responseBody = response.body();
        if (!response.isSuccessful()) {
            Error error = new Gson().fromJson(responseBody.string(), Error.class);
            onError(error);
            return;
        }

        T obj = new Gson().fromJson(responseBody.string(), model());
        onSuccess(obj);
    }

    public abstract Class<T> model();

    public abstract void onError(Error error) throws IOException;

    public abstract void onSuccess(T response) throws IOException;
}
